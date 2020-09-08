package com.imooc.o2o.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ShopAuthMapDao;
import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exception.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;


@Service
public class ShopserviceImpl implements ShopService {

	private Logger logger = (Logger) LoggerFactory.getLogger(ShopserviceImpl.class);

	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ShopAuthMapDao shopAuthMapDao;

	/**
	 * 根据shopCondition分页返回相应店铺列表
	 * 这里的前端页面只能设置页码，而数据库呈现的是行数，所以这里需要工具类PageCalculator进行页码到行数的转换
	 * @param shopCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@Override
	public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
		int rowIndex = PageCalculator.calculatorRowIndex(pageIndex, pageSize);
		List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
		int count = shopDao.queryShopCount(shopCondition);
		ShopExecution se = new ShopExecution();
		if(shopList != null) {
			se.setShopList(shopList);
			se.setCount(count);
		}else {
			se.setState(ShopStateEnum.INNER_ERROR.getState());
		}
		return se;
	}

	/**
	 * 根据shopCondition分页返回相应店铺列表的总数
	 * @param shopCondition
	 * @return
	 */
	@Override
	public int getCount(Shop shopCondition) {
		return shopDao.queryShopCount(shopCondition);
	}
	
	/**
	 * 新增商店
	 * @param shop
	 * @param shopImg
	 * @return
	 */
	@Override
	@Transactional   /* 添加事务控制 */
	public ShopExecution addShop(Shop shop,ImageHolder thumbnail) {
	//1 空值判断
	if (shop == null) {
		logger.warn("shop== null");
		return new ShopExecution(ShopStateEnum.NULL_SHOP_INFO);
	}
	//2 增加对Shop其他引入类非空的判断
	try {
		//2.1 初始化店铺信息初始值（店铺状态、创建时间、更新时间），该值不可通过外力改变 
		shop.setEnableStatus(0);
		shop.setCreateTime(new Date());
		shop.setLastEditTime(new Date());
		//2.2 添加店铺信息
		int effectedNum = shopDao.insertShop(shop);
		logger.warn("添加结果："+effectedNum+"shopId:"+shop.getShopId());
		//2.3 判断插入的店铺是否有效，用ShopOperationException是因为配合事务的回滚机制
		if (effectedNum <= 0) {
			throw new ShopOperationException("店铺创建失败");
		} else {
			//2.4 存储图片
			try {
				if (thumbnail.getImage() != null) {
					addShopImg(shop, thumbnail);
					effectedNum = shopDao.updateShop(shop);
					if (effectedNum <= 0) {
						throw new ShopOperationException("创建图片地址失败");
					}
				}
			} catch (Exception e) {
				throw new ShopOperationException("addShopImg error: "
						+ e.getMessage());
			}
			//2.5 更新店铺的图片地址
			effectedNum = shopDao.updateShop(shop);
			if(effectedNum <= 0) {
				throw new ShopOperationException("更新图片地址失败");
			}
			//执行添加shopAuthMap操作
            ShopAuthMap shopAuthMap = new ShopAuthMap();
            shopAuthMap.setEmployee(shop.getOwner());
            shopAuthMap.setShop(shop);
            shopAuthMap.setTitle("店家");
            shopAuthMap.setTitleFlag(0);
            shopAuthMap.setCreateTime(new Date());
            shopAuthMap.setLastEditTime(new Date());
            shopAuthMap.setEnableStatus(1);
            try {
				effectedNum = shopAuthMapDao.insertShopAuthMap(shopAuthMap);
				if(effectedNum <= 0) {
					throw new ShopOperationException("授权创建失败");
				}
			} catch (Exception e) {
				throw new ShopOperationException("insertShopAuthMap error:" + e.getMessage());
			}
            
		}
	} catch (Exception e) {
		throw new ShopOperationException("addShop error: " + e.getMessage());
	}
	return new ShopExecution(ShopStateEnum.CHECK,shop);
	}

	/**
	 * 获取shop图片目录的相对值路径
	 * @param shop
	 * @param shopImg
	 * @param fileName
	 */
	private void addShopImg(Shop shop, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(shop.getShopId());
		String shopImgAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		shop.setShopImg(shopImgAddr);
	}

	/**
	 * 通过店铺ID获取店铺信息
	 * @param shopId
	 * @return
	 */
	@Override
	public Shop getByShopId(long shopId) {
		return shopDao.queryByShopId(shopId);
	}

	/**
	 * 更新店铺信息，包括对图片的处理
	 * @param shop
	 * @param shopImgInputStream
	 * @param fileName
	 * @return
	 */
	@Override
	public ShopExecution modifyShop(Shop shop, ImageHolder thumbnail)
			throws ShopOperationException {
		
		if (shop == null || shop.getShopId() == null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP_INFO);
		} else {
			try {
				// 1.判断是否需要处理图片
				if (thumbnail.getImage() != null && thumbnail.getImageName() != null && !"".equals(thumbnail.getImageName())) {
					Shop tempShop = shopDao.queryByShopId(shop.getShopId());
					if (tempShop.getShopImg() != null) {
						//编写工具类，删除图片信息 
						ImageUtil.deleteFileOrPath(tempShop.getShopImg());
					}
					addShopImg(shop, thumbnail);
				}
				// 2.更新店铺信息
				shop.setLastEditTime(new Date());
				int effectedNum = shopDao.updateShop(shop);
				if (effectedNum <= 0) {
					return new ShopExecution(ShopStateEnum.INNER_ERROR);
				} else {
					shop = shopDao.queryByShopId(shop.getShopId());
					return new ShopExecution(ShopStateEnum.SUCCESS, shop);
				}
			} catch (Exception e) {
				throw new ShopOperationException("modifyShop error:" + e.getMessage());
			}
		}
	}

}
