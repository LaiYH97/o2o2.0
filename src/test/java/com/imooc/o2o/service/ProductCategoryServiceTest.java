package com.imooc.o2o.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.ProductCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryServiceTest {

	@Autowired
	ProductCategoryService productCategoryService;
	
	@Test
	public void testGetProductCategoryList() {
		long shopId = 1;
		List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(shopId);
		for(ProductCategory pc:productCategoryList) {
			System.out.printf("%s的权重：%s    ",pc.getProductCategoryName(),pc.getPriority());
		}
	}
}
