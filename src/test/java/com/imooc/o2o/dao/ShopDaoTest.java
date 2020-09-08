package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopDaoTest {
	
	@Autowired
	private ShopDao shopDao;
	
	@Test
	@Ignore
	public void testQueryShopList() {
		Shop shopCondition = new Shop();
		PersonInfo owner = new PersonInfo();
		owner.setUserId(1L);
		shopCondition.setOwner(owner);
		List<Shop> shopList = shopDao.queryShopList(shopCondition, 0, 5);
			
		System.out.println("OwnerID为1的第一页的数量："+shopList.size());
		int count = shopDao.queryShopCount(shopCondition);
		System.out.println("OwnerID为1的店铺总数："+count);
		for(Shop shop:shopList) {
			System.out.print(shop.getShopId());
			System.out.println(shop.getEnableStatus());
		}
		//shopCategoryId设置为1
		ShopCategory shopCategory = new ShopCategory();
		shopCategory.setShopCategoryId(1L);
		shopCondition.setShopCategory(shopCategory);
		List<Shop> shopList2 = shopDao.queryShopList(shopCondition, 0, 5);
		System.out.println("OwnerID为1且店铺类别为1时，第一页店铺："+shopList2.size());
		int count2 = shopDao.queryShopCount(shopCondition);
		System.out.println("OwnerID为1且店铺类别为1的店铺总数"+count2);
	}
	
	/**
	 * 测试根据店铺id查询店铺信息
	 */
	@Test
	@Ignore
	public void testQueryShopById() {
		long shopId= 1;
		Shop shop = shopDao.queryByShopId(shopId);
		System.out.println(shop.getArea().getAreaId());
		System.out.println(shop.getArea().getAreaName());
		
	}
	/**
	 * 新增店铺的测试
	 */
	@Test
	@Ignore
	public void testInsertShop() {
		Shop shop = new Shop();
		PersonInfo owner = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(1L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("测试的店铺");
		shop.setShopDesc("test");
		shop.setShopAddr("test");
		shop.setPhone("test");
		shop.setShopImg("test");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(1);
		shop.setAdvice("审核中");
		int effectedNum = shopDao.insertShop(shop);
		assertEquals(1, effectedNum);
	}
	
	/**
	 * 更新店铺信息的测试
	 */
	@Test
	@Ignore
	public void testUpdateShop() {
		Shop shop = new Shop();
		shop.setShopId(1L);
		shop.setShopDesc("测试店铺的描述");
		shop.setShopAddr("测试店铺的地址");
		shop.setLastEditTime(new Date());
		int effectedNum = shopDao.updateShop(shop);
		assertEquals(1, effectedNum);
	}
	
	/**
	 * 测试前端组合查询店铺列表
	 */
	@Test
	public void testCategory() {
		Shop shopCondition = new Shop();
		ShopCategory childCategory = new ShopCategory();
		ShopCategory parentCategory = new ShopCategory();
		parentCategory.setShopCategoryId(1L);
		childCategory.setParent(parentCategory);
		shopCondition.setShopCategory(childCategory);
		List<Shop>  shopList = shopDao.queryShopList(shopCondition, 0, 6);
		int count = shopDao.queryShopCount(shopCondition);
		System.out.println(shopList.size());
		System.out.println(count);
		System.out.println(shopCondition.getShopCategory().getParent().getShopCategoryId());
	}
}
