package com.imooc.o2o.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.exception.ProductCategoryOperationException;

public interface ProductCategoryService {

	/**
	 * 查询指定某个店铺下的所有商品类别信息
	 * @param shopId
	 * @return
	 */
	List<ProductCategory> getProductCategoryList(long shopId);
	
	/* 
	* 批量插入商品类别
	* @param productCategoryList
	* @return
	* @throws ProductCategoryOperationException
	*/
	ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList) throws ProductCategoryOperationException;

	/**
	 * 删除商品类别
	 * @param productCategoryId
	 * @param shopId
	 * @return
	 */
	ProductCategoryExecution deleteProductCategory(@Param("productCategoryId")long productCategoryId,@Param("shopId")long shopId);
}
