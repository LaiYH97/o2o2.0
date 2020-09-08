package com.imooc.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ProductCategoryDao;
import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.exception.ProductCategoryOperationException;
import com.imooc.o2o.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductCategoryDao productCategoryDao;
	
	@Autowired
	private ProductDao productDao;

	/**
	 * 查询指定某个店铺下的所有商品类别信息
	 * @param shopId
	 * @return
	 */
	@Override
	public List<ProductCategory> getProductCategoryList(long shopId) {
		return productCategoryDao.queryProductCategoryList(shopId);
	}

	/**
	 *批量插入商品类别
	 */
	@Override
	@Transactional
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryOperationException {
		if(productCategoryList != null && productCategoryList.size() >0) {
			try {
				int effectedNum = productCategoryDao.batchInsertProductCategory(productCategoryList);
				if(effectedNum <= 0) {
					throw new ProductCategoryOperationException("店铺类别创建失败"); 
				}else {
					return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
				}
			
			}catch (Exception e) {
				throw new ProductCategoryOperationException("batchAddProductCategory error:" + e.getMessage());
			}
		}else {
			return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		}
	}

	/**
	 *删除商品类别
	 *并将该类别下的商品类别置为空，以后添加
	 */
	@Override
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId) {
		//将tb_product表中的该商品类别id置为空
		try {
			int effectedNum = productDao.updateProductCategoryToNull(productCategoryId);
			if(effectedNum <0) {
				throw new ProductCategoryOperationException("商品类别更新失败");
			}
		}catch(Exception e) {
			throw new ProductCategoryOperationException("deleteProductCategory error:"+e.getMessage());
		}
		//再从tb_product_category中删除商品类别
		try {
			int effectedNum = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if(effectedNum <= 0) {
				throw new ProductCategoryOperationException("商品类别删除失败");
			}else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		}catch(Exception e) {
			throw new ProductCategoryOperationException("delete ProductCategory error:"+e.getMessage());
		}
	}
	
	
}
