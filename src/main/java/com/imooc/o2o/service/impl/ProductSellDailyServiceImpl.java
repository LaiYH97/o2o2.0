package com.imooc.o2o.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.ProductSellDailyDao;
import com.imooc.o2o.entity.ProductSellDaily;
import com.imooc.o2o.service.ProductSellDailyService;

@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {

	@Autowired
	private ProductSellDailyDao productSellDailyDao;
    
	private static Logger logger = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);
    
    @Override
    public void dailyCalculate() {
        logger.info("Quratz Running!");
        //System.out.print("Quratz Running!");
        productSellDailyDao.insertProductSellDaily();
        //统计余下销量为0的商品
        productSellDailyDao.insertDefaultProductSellDaily();
    }

    @Override
    public List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime, Date endTime) {
        return productSellDailyDao.queryProductSellDailyList(productSellDailyCondition, beginTime, endTime);
    }
}
