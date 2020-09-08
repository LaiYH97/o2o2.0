package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.entity.ProductImg;

public interface ProductImgDao {

	/**
	 * 批量添加商品详情图片
	 * @param productImgList
	 * @return
	 */
	int batchInsertProductImg(List<ProductImg> productImgList);
	
	/**
	 * 查询商品详情图片
	 * @param productId
	 * @return
	 */
	List<ProductImg> queryProductImgList(long productId);
	
	/**
	 * 删除商品详情图片
	 * @param productId
	 * @return
	 */
	int deleteProductImgByProductId(long productId);
}
