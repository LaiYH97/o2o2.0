package com.imooc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exception.ShopOperationException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopServiceTest {

	@Autowired
	private ShopService shopService;

	/**
	 * 测试商铺列表分页展示的service实现
	 */
	@Test
	public void testGetShopList() {
		Shop shopCondition = new Shop();
		ShopCategory sc = new ShopCategory();
		sc.setShopCategoryId(1L);
		shopCondition.setShopCategory(sc);
		ShopExecution se = shopService.getShopList(shopCondition, 1, 3);
		System.out.println("店铺列表总数："+se.getShopList().size());
		System.out.println("店铺总数为："+se.getCount());
	}
	
	/**
	 * 测试添加商铺的service实现
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testAddShop() throws Exception {
		Shop shop = new Shop();
		PersonInfo personInfo = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		
		personInfo.setUserId(1L);
		area.setAreaId(1);
		shopCategory.setShopCategoryId(1L);
		
		shop.setOwner(personInfo);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("嘤嘤嘤咖啡厅");
		shop.setShopDesc("mytest2");
		shop.setShopAddr("testaddr2");
		shop.setPhone("15217811067");
		shop.setPriority(66);
		shop.setCreateTime(new Date());
		shop.setLastEditTime(new Date());
		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
		shop.setAdvice("审核中...");
		
		File shopImg = new File("D:/Javaproject/img/2.jpg"); 
		
		InputStream is = new FileInputStream(shopImg);
		ImageHolder imageHolder = new ImageHolder(shopImg.getName(), is);
		ShopExecution se = shopService.addShop(shop, imageHolder);
		assertEquals(ShopStateEnum.CHECK.getState(),se.getState());
	}
	
	/**
	 *  测试修改商铺的service实现
	 * @throws ShopOperationException
	 * @throws FileNotFoundException
	 */
	@Test
	@Ignore
	public void testModifyShop() throws ShopOperationException,FileNotFoundException{
		Shop shop = new Shop();
		shop.setShopId(1L);
		shop.setShopName("海上音乐厅");
		File shopImg = new File("D:/Javaproject/img/4.jpg");
		InputStream is = new FileInputStream(shopImg);
		ImageHolder imageHolder = new ImageHolder("4.jpg", is);
		ShopExecution shopExecution = shopService.modifyShop(shop, imageHolder);
		System.out.println("新的图片地址："+shopExecution.getShop().getShopImg());
	}
}
