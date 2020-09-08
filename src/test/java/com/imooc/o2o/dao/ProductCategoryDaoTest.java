package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.ProductCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryDaoTest {

	@Autowired
	private ProductCategoryDao productCategoryDao;
	
	@Test
	@Ignore
	public void testDelectProductCategory() {
		long shopId = 1;
		List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategoryList(shopId);
		for(ProductCategory pc:productCategoryList) {
			if("批量插入商品类别1".equals(pc.getProductCategoryName())||"批量插入商品类别2".equals(pc.getProductCategoryName())) {
				int count = productCategoryDao.deleteProductCategory(pc.getProductCategoryId(), shopId);
				assertEquals(2,count);
			}
		}
	}
	/**
	 * 测试批量添加商品分类
	 */
	@Test
	@Ignore
	public void testBatchInsertProductCategory() {
		ProductCategory productCategory1 = new ProductCategory();
		productCategory1.setProductCategoryName("批量插入商品类别1");
		productCategory1.setPriority(1);
		productCategory1.setCreateTime(new Date());
		productCategory1.setShopId(1L);
		ProductCategory productCategory2 = new ProductCategory();
		productCategory2.setProductCategoryName("批量插入商品类别2");
		productCategory2.setPriority(2);
		productCategory2.setCreateTime(new Date());
		productCategory2.setShopId(1L);
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		productCategoryList.add(productCategory1);
		productCategoryList.add(productCategory2);
		int count = productCategoryDao.batchInsertProductCategory(productCategoryList);
		assertEquals(2,count);
	}
	
	/**
	 * 测试展示指定店铺id的展示商品类别
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void testQueryProductCategoryList() throws Exception{
		long shopId = 1L;
		List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategoryList(shopId);
		System.out.println("该店铺自定义商品类别数为：" + productCategoryList.size());
	}

}
