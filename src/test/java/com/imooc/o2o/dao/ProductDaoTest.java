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

import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.entity.Shop;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductDaoTest {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;
	
	@Test
	@Ignore
	public void testAInsertProduct() throws Exception{
		Shop shop1 = new Shop();
		shop1.setShopId(1L);
		ProductCategory pc1 = new ProductCategory();
		pc1.setProductCategoryId(10L);
		Product product1 = new Product();
		product1.setProductName("商品1");
		product1.setProductDesc("商品1的描述");
		product1.setImgAddr("商品1的缩略图");
		product1.setPriority(1);
		product1.setEnableStatus(1);
		product1.setCreateTime(new Date());
		product1.setLastEditTime(new Date());
		product1.setShop(shop1);
		product1.setProductCategory(pc1);
		
		Product product2 = new Product();
		product2.setProductName("商品2");
		product2.setProductDesc("商品2的描述");
		product2.setImgAddr("商品2的缩略图");
		product2.setPriority(2);
		product2.setEnableStatus(0);
		product2.setCreateTime(new Date());
		product2.setLastEditTime(new Date());
		product2.setShop(shop1);
		product2.setProductCategory(pc1);
		
		Product product3 = new Product();
		product3.setProductName("商品3");
		product3.setProductDesc("商品3的描述");
		product3.setImgAddr("商品3的缩略图");
		product3.setPriority(3);
		product3.setEnableStatus(1);
		product3.setCreateTime(new Date());
		product3.setLastEditTime(new Date());
		product3.setShop(shop1);
		product3.setProductCategory(pc1);
		
		int effectedNum = productDao.insertProduct(product1);
		assertEquals(1,effectedNum);
		effectedNum = productDao.insertProduct(product2);
		assertEquals(1,effectedNum);
		effectedNum = productDao.insertProduct(product3);
		assertEquals(1,effectedNum);
	}
	
	@Test
	public void testBQueryProductList() {
		//product表原有7条数据，商品名包含测试的有7条
		Product productCondition = new Product();
		//分页查询，预期返回3条数据
		List<Product> productList = productDao.queryProductList(productCondition, 0, 3);
		assertEquals(3,productList.size());
		int count = productDao.queryProductCount(productCondition);
		assertEquals(7,count);
		//查询名称为测试的商品总数
		productCondition.setProductName("1");
		productList = productDao.queryProductList(productCondition, 0, 3);
		count = productDao.queryProductCount(productCondition);
		assertEquals(1,count);
	}
	
	@Test
	@Ignore
	public void testCQueryProductByProductId() throws Exception{
		long productId = 13;
		//初始化两个商品详情图实例，作为productId为1的商品下的详情图片
		//批量插入到商品详情图标中
		ProductImg productImg1 = new ProductImg();
		productImg1.setImgAddr("图片1");
		productImg1.setImgDesc("测试图片1");
		productImg1.setPriority(1);
		productImg1.setCreateTime(new Date());
		productImg1.setProductId(productId);
		ProductImg productImg2 = new ProductImg();
		productImg2.setImgAddr("图片2");
		productImg2.setImgDesc("测试图片2");
		productImg2.setPriority(1);
		productImg2.setCreateTime(new Date());
		productImg2.setProductId(productId);
		List<ProductImg> productImgList = new ArrayList<>();
		productImgList.add(productImg1);
		productImgList.add(productImg2);
		int effectedNum = productImgDao.batchInsertProductImg(productImgList);
		assertEquals(2,effectedNum);
		//查询productId为1的商品信息并校验返回的详情图实例列表size是否为2
		Product product = productDao.queryProductById(productId);
		assertEquals(8,product.getProductImgList().size());
		//删除新增的这两个商品详情图示例，形成闭环
		effectedNum = productImgDao.deleteProductImgByProductId(productId);
		assertEquals(8,effectedNum);
	}
	
	@Test
	@Ignore
	public void testDUpdateProduct() {
		Product product = new Product();
		product.setProductId(3L);
		ProductCategory pc = new ProductCategory();
		pc.setProductCategoryId(7L);
		Shop shop = new Shop();
		shop.setShopId(1L);
		
		product.setShop(shop);
		product.setProductCategory(pc);
		product.setProductName("躺着喝的美式咖啡");
		
		int effectedNum = productDao.updateProduct(product);
		assertEquals(1,effectedNum);
	}
	
	@Test
	@Ignore
	public void testEUpdateProductCategoryToNull() {
		//将productCategoryId为2的商品类别下面的商品的商品类别置为空
		int effectNum = productDao.updateProductCategoryToNull(6L);
		assertEquals(effectNum,1);
	}
}
