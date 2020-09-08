package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.ProductImg;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductImgDaoTest {

	@Autowired
	private ProductImgDao productImgDao;
	
	@Test
	public void testABatchInsertProductImg() throws Exception{

		ProductImg productImg1 = new ProductImg();
		productImg1.setProductId(3L);
		productImg1.setImgAddr("图片1");
		productImg1.setImgDesc("测试图片1");
		productImg1.setPriority(1);
		productImg1.setCreateTime(new Date());
		
		ProductImg productImg2 = new ProductImg();
		productImg2.setProductId(3L);
		productImg2.setImgAddr("图片2");
		productImg2.setImgDesc("测试图片2");
		productImg2.setPriority(1);
		productImg2.setCreateTime(new Date());
		
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		productImgList.add(productImg1);
		productImgList.add(productImg2);
		
		int effectedNum = productImgDao.batchInsertProductImg(productImgList);
		assertEquals(2,effectedNum);
	}
	
	@Test
	@Ignore
	public void testBQueryProductImgList() {
		List<ProductImg> productImgList = productImgDao.queryProductImgList(1L);
		assertEquals(2,productImgList.size());
	}
	
	@Test
	@Ignore
	public void testCDeleteProductImgByProductId() throws Exception{
		long productId =1;
		int effectedNum = productImgDao.deleteProductImgByProductId(productId);
		assertEquals(2,effectedNum);
	}
}
