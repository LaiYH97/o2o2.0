package com.imooc.o2o.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exception.ProductOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;
	
	/**
	 * 添加商品信息以及图片处理
	 * @param product
	 * @param thumbnail 缩略图
	 * @param productImgList 详情图
	 * @return
	 * @throws ProductOperationException
	 */
	
	/*
	 * 步骤：
	 * 1.处理缩略图，获取缩略图相对路径并赋值给product
	 * 2.往tb_product写入商品信息，获取productId
	 * 3.结合productId批量处理商品详情图
	 * 4.将商品详情图列表批量插入tb_product_img中
	 * 通过spring的事务管理去执行这4不操作，任何一步出错就回滚，不会往表里写入
	 */
	@Override
	@Transactional
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		
		//空值判断
		if(product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			//给商品设置默认属性
			product.setCreateTime(new Date());
			product.setLastEditTime(new Date());
			//默认为上架的状态
			product.setEnableStatus(1);
			//若商品缩略图不为空，则添加缩略图信息到商品
			if(thumbnail != null) {
				addThumbnail(product,thumbnail);
			}
			try {
				int effectedNum = productDao.insertProduct(product);
				if(effectedNum <= 0) {
					throw new ProductOperationException("创建商品失败");
				}
			}catch (Exception e) {
				throw new ProductOperationException("创建商品失败："+e.toString());
			}
			//若商品详情图不为空，则向tb_product_img添加商品详情图
			if(productImgHolderList != null && productImgHolderList.size() >0) {
				addProductImgList(product,productImgHolderList);
			}
			return new ProductExecution(ProductStateEnum.SUCCESS,product);
		}else {
			//传参为空，则返回控制错误信息
			return new ProductExecution(ProductStateEnum.EMPTY_LIST);
		}
	}

	/**
	 * 向product对象添加缩略图
	 * @param product
	 * @param thumbnail
	 */
	private void addThumbnail(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		product.setImgAddr(thumbnailAddr); 
	}
	
	/**
	 * 批量添加图片
	 * @param product
	 * @param productImgHolderList
	 */
	private void addProductImgList(Product product, List<ImageHolder> productImgHolderList) {
		//获取图片存储路径，这里直接存放到相应店铺的文件夹下，同缩略图
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		//遍历，获得详情图片列表
		for(ImageHolder productImgHolder:productImgHolderList) {
			String imgAddr = ImageUtil.generateNormalImg(productImgHolder, dest);
			ProductImg productImg = new ProductImg();
			productImg.setImgAddr(imgAddr);  
			productImg.setProductId(product.getProductId());
			productImg.setCreateTime(new Date());
			productImgList.add(productImg);
		}
		if(productImgList.size() > 0) {
			try {
				int effectedNum = productImgDao.batchInsertProductImg(productImgList);
				if(effectedNum <= 0) {
					throw new ProductOperationException("创建商品详情图片失败");
				}
			}catch (Exception e) {
				throw new ProductOperationException("创建商品详情图片失败："+e.toString());
			}
		}
	}

	/**
	 *通过商品id查询唯一的商品信息
	 */
	@Override
	public Product getProductById(long productId) {
		return productDao.queryProductById(productId);
	}

	/**
	 *修改商品信息以及图片处理
	 */
	@Override
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList)
			throws ProductOperationException {
		//空值判断。存放商品缩略图和详情图需要用到shopId做路径，因此需要针对shop做空值判断
		if(product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			//给商品设置添加默认属性
			product.setLastEditTime(new Date());
			//若商品缩略图不为空且原有缩略图不为空，则删除原有缩略图并添加
			if(thumbnail != null) {
				//先获取一遍原有信息，因为原来的信息里有原图片地址
				Product tempProduct = productDao.queryProductById(product.getProductId());
				if(tempProduct.getImgAddr() != null) {
					//从磁盘中删除缩略图
					ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
				}
				addThumbnail(product,thumbnail);
			}
			//如果有新存入的商品详情图，则将原先的删除，并添加新的图片
			if(productImgList != null && productImgList.size() > 0) {
				deleteProductImgList(product.getProductId());
				addProductImgList(product,productImgList);
			}
			try {
				//更新商品信息
				int effectedNum = productDao.updateProduct(product);
				if(effectedNum <= 0) {
					throw new ProductOperationException("更新商品信息失败");
				}
				return new ProductExecution(ProductStateEnum.SUCCESS,product);
			}catch (Exception e) {
				throw new ProductOperationException("更新商品信息失败："+e.toString());
			}
		}else {
			return new ProductExecution(ProductStateEnum.EMPTY_LIST);
		}
	}
	
	/**
	 * 删除某个商品下的所有详情图
	 * @param productId
	 */
	private void deleteProductImgList(long productId) {
		//根据productId获取原来的图片
		List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
		//从磁盘删除原来的图片
		for(ProductImg productImg:productImgList) {
			ImageUtil.deleteFileOrPath(productImg.getImgAddr());
		}
		//从数据库删除原有图片的信息
		productImgDao.deleteProductImgByProductId(productId);
	}

	/**
	 *获取商品列表及列表总数
	 */
	@Override
	public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
		//页码转换成数据库的行码，并调用dao层取回指定页码的商品列表
		int rowIndex = PageCalculator.calculatorRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
		//基于同样的查询条件返回该查询条件下的商品总数
		int count = productDao.queryProductCount(productCondition);
		ProductExecution pe = new ProductExecution();
		pe.setCount(count);
		pe.setProductList(productList);
		return pe;
	}
}
