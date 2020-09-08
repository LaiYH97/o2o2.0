package com.imooc.o2o.service;

import java.util.List;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.exception.ProductOperationException;

public interface ProductService {

	/**
	 * 添加商品信息以及图片处理
	 * @param product
	 * @param thumbnail 缩略图
	 * @param productImgList 详情图
	 * @return
	 * @throws ProductOperationException
	 */
	ProductExecution addProduct(Product product, ImageHolder thumbnail,List<ImageHolder> productImgList) throws ProductOperationException;
	
	/**
	 * 通过商品id查询唯一的商品信息
	 * @param productId
	 * @return
	 */
	Product getProductById(long productId);
	
	/**
	 * 修改商品信息以及图片处理
	 * @param product
	 * @param thumbnail
	 * @param productImgList
	 * @return
	 * @throws ProductOperationException
	 */
	ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList) throws ProductOperationException;
	
	/**
	 * 获取商品列表及列表总数
	 * @param productCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	ProductExecution getProductList(Product productCondition,int pageIndex,int pageSize);
	
}
