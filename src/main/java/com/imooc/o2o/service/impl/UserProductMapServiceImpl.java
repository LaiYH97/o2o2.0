package com.imooc.o2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.UserProductMapDao;
import com.imooc.o2o.dao.UserShopMapDao;
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserProductMap;
import com.imooc.o2o.entity.UserShopMap;
import com.imooc.o2o.enums.UserProductMapStateEnum;
import com.imooc.o2o.exception.UserProductMapOperationException;
import com.imooc.o2o.service.UserProductMapService;
import com.imooc.o2o.util.PageCalculator;

@Service
public class UserProductMapServiceImpl implements UserProductMapService {

	@Autowired
    private UserProductMapDao userProductMapDao;

    @Autowired
    private UserShopMapDao userShopMapDao;

    @Override
    public UserProductMapExecution listUserProductMap(UserProductMap userProductMapCondition, Integer pageIndex, Integer pageSize) {
        if (userProductMapCondition != null && pageIndex != null && pageSize != null) {
            int beginIndex = PageCalculator.calculatorRowIndex(pageIndex, pageSize);
            List<UserProductMap> userProductMapList = userProductMapDao.queryUserProductMapList(userProductMapCondition, beginIndex, pageSize);
            int count = userProductMapDao.queryUserProductMapCount(userProductMapCondition);
            UserProductMapExecution ue = new UserProductMapExecution();
            ue.setUserProductMapList(userProductMapList);
            ue.setCount(count);
            return ue;
        } else {
            return null;
        }
    }

    @Override
    public UserProductMapExecution addUserProductMap(UserProductMap userProductMap) throws UserProductMapOperationException {
        if (userProductMap != null && userProductMap.getUser().getUserId() != null
            && userProductMap.getShop().getShopId() != null && userProductMap.getOperator().getUserId() != null) {
            userProductMap.setCreateTime(new Date());
            try {
                int effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
                if (effectedNum <= 0) {
                    throw new UserProductMapOperationException("添加消费记录失败");
                }

                //若本次消费能够积分
                if (userProductMap.getPoint() != null && userProductMap.getPoint() > 0) {
                    UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userProductMap.getUser().getUserId(),
                            userProductMap.getShop().getShopId());
                    //如果以前在本店消费过
                    if (userShopMap != null && userShopMap.getUserShopId() != null) {
                        userShopMap.setPoint(userShopMap.getPoint() + userProductMap.getPoint());;
                        effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
                        if (effectedNum <= 0) {
                            throw new UserProductMapOperationException("更新积分信息失败");
                        }
                    } else {
                        // 在店铺没有过消费记录，添加一条店铺积分信息(就跟初始化会员一样)
                        userShopMap = compactUserShopMap4Add(userProductMap.getUser().getUserId(),
                                userProductMap.getShop().getShopId(), userProductMap.getPoint());
                        effectedNum = userShopMapDao.insertUserShopMap(userShopMap);
                        if (effectedNum <= 0) {
                            throw new UserProductMapOperationException("积分信息创建失败");
                        }
                    }
                }
                return new UserProductMapExecution(UserProductMapStateEnum.SUCCESS, userProductMap);
            } catch (Exception e) {
                throw new UserProductMapOperationException("添加授权失败:" + e.toString());
            }
        } else {
            return new UserProductMapExecution(UserProductMapStateEnum.NULL_USERPRODUCT_INFO);
        }
    }

    /**
     * 封装顾客积分信息
     *
     * @param userId
     * @param shopId
     * @param point
     * @return
     */
    private UserShopMap compactUserShopMap4Add(Long userId, Long shopId, Integer point) {
        UserShopMap userShopMap = null;
        // 空值判断
        if (userId != null && shopId != null) {
            userShopMap = new UserShopMap();
            PersonInfo customer = new PersonInfo();
            customer.setUserId(userId);
            Shop shop = new Shop();
            shop.setShopId(shopId);
            userShopMap.setUser(customer);
            userShopMap.setShop(shop);
            userShopMap.setCreateTime(new Date());
            userShopMap.setPoint(point);
        }
        return userShopMap;
    }
}
