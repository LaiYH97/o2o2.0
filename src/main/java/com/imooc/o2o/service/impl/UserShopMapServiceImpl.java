package com.imooc.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.UserShopMapDao;
import com.imooc.o2o.dto.UserShopMapExecution;
import com.imooc.o2o.entity.UserShopMap;
import com.imooc.o2o.service.UserShopMapService;
import com.imooc.o2o.util.PageCalculator;

@Service
public class UserShopMapServiceImpl implements UserShopMapService {

	private final UserShopMapDao userShopMapDao;

    @Autowired
    public UserShopMapServiceImpl(UserShopMapDao userShopMapDao) {
        this.userShopMapDao = userShopMapDao;
    }

    @Override
    public UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, int pageIndex, int pageSize) {
        if (userShopMapCondition != null && pageIndex != -1 && pageSize != -1){
            int beginIndex = PageCalculator.calculatorRowIndex(pageIndex, pageSize);
            //根据传入的查询条件分页返回用户积分列表信息
            List<UserShopMap> userShopMapList = userShopMapDao.queryUserShopMapList(userShopMapCondition, beginIndex, pageSize);
            int count = userShopMapDao.queryUserShopMapCount(userShopMapCondition);
            UserShopMapExecution ue = new UserShopMapExecution();
            ue.setUserShopMapList(userShopMapList);
            ue.setCount(count);
            return ue;
        } else {
            return null;
        }
    }

    @Override
    public UserShopMap getUserShopMap(long userId, long shopId) {
        return userShopMapDao.queryUserShopMap(userId, shopId);
    }
}
