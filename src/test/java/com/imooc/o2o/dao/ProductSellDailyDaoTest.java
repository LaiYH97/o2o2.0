package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.ProductSellDaily;
import com.imooc.o2o.entity.Shop;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductSellDailyDaoTest {

	 @Autowired
	    private ProductSellDailyDao productSellDailyDao;

	    @Test
	    public void testAInsertProductSellDaily() throws Exception {
	        int eff = productSellDailyDao.insertProductSellDaily();
	        assertEquals(3, eff);
	    }

	    @Test
	    public void testBQueryProductSellDaily(){
	        // 创建商品日销量统计
	        int effectedNum = productSellDailyDao.insertDefaultProductSellDaily();
	        assertEquals(6, effectedNum);
	    }

	    /**
	     * 测试查询功能
	     *
	     * @throws Exception
	     */
	    @Test
	    @Ignore
	    public void testCQueryProductSellDaily() throws Exception {
	        ProductSellDaily productSellDaily = new ProductSellDaily();
	        // 叠加店铺去查询
	        Shop shop = new Shop();
	        shop.setShopId(1L);
	        productSellDaily.setShop(shop);
	        List<ProductSellDaily> productSellDailyList = productSellDailyDao.queryProductSellDailyList(productSellDaily,
	                null, null);
	        System.out.println(productSellDailyList.size());
	    }
}
