package com.imooc.o2o.service;

import com.imooc.o2o.entity.PersonInfo;

public interface PersonInfoService {

	/**
	 * 根据用户Id获取PersonInfo用户信息
	 * @param userId
	 * @return
	 */
	PersonInfo getPersonInfoById(Long userId);
}
