package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.ShopCategory;

public interface ShopCategoryDao {
	
	//根据条件检索出符合条件的店铺类别
	List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition") ShopCategory shopCategoryCondition);
	
	//检索出所有的店铺类别
	List<ShopCategory> queryAllShopCategory();
}
