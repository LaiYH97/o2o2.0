package com.imooc.o2o.service;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.exception.ShopOperationException;

public interface ShopService {

	/**
	 * 根据shopCondition分页返回相应店铺列表
	 * 这里的前端页面只能设置页码，而数据库呈现的是行数，所以这里需要工具类PageCalculator进行页码到行数的转换
	 * @param shopCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);
	
	/**
	 * 根据shopCondition分页返回相应店铺列表的总数
	 * @param shopCondition
	 * @return
	 */
	public int getCount(Shop shopCondition);
	
	/**
	 * 新增商店
	 * @param shop
	 * @param thumbnail
	 * @return
	 */
	ShopExecution addShop(Shop shop, ImageHolder thumbnail);
	
	/**
	 * 通过店铺ID获取店铺信息
	 * @param shopId
	 * @return
	 */
	Shop getByShopId(long shopId);
	
	/**
	 * 更新店铺信息，包括对图片的处理
	 * @param shop
	 * @param shopImgInputStream
	 * @param fileName
	 * @return
	 */
	ShopExecution modifyShop(Shop shop,ImageHolder thumbnail) throws ShopOperationException;
	
}
