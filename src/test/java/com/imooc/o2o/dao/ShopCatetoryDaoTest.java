package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.ShopCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopCatetoryDaoTest {

	@Autowired
	private ShopCategoryDao shopCategoryDao;
	
	@Test
	@Ignore
	public void testQueryShopCategory() {
			List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(new ShopCategory());
			assertEquals(2, shopCategoryList.size());
			ShopCategory testCategory = new ShopCategory();
			ShopCategory parentCategory = new ShopCategory();
			parentCategory.setShopCategoryId(1L);
			testCategory.setParent(parentCategory);
			shopCategoryList = shopCategoryDao.queryShopCategory(testCategory);
			assertEquals(1, shopCategoryList.size());
			System.out.print(shopCategoryList.get(0).getShopCategoryName());
	}
	
	@Test
	public void testQueryShopCategory2() {
		List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(null);
		assertEquals(shopCategoryList.size(),0);
	}
}
