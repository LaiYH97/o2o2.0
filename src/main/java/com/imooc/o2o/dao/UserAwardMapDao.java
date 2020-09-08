package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.UserAwardMap;

public interface UserAwardMapDao {

	/**
	 * 根据传入的查询条件分页返回用户兑奖记录信息列表
	 *
	 * @param userAwardCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<UserAwardMap> queryUserAwardMapList(
            @Param("userAwardCondition") UserAwardMap userAwardCondition,
            @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	/**
	 * 根据传入的查询条件分页返回用户兑奖记录数
	 * @param userAwardCondition
	 * @return
	 */
	int queryUserAwardMapCount(
            @Param("userAwardCondition") UserAwardMap userAwardCondition);

	/**
	 * 根据userAwardId返回某条奖品兑换信息
	 * @param userAwardId
	 * @return
	 */
	UserAwardMap queryUserAwardMapById(long userAwardId);

	/**
	 * 添加一条奖品兑换信息
	 * @param userAwardMap
	 * @return
	 */
	int insertUserAwardMap(UserAwardMap userAwardMap);

	/**
	 *  更新一条奖品兑换信息，主要更新奖品兑换状态
	 * @param userAwardMap
	 * @return
	 */
	int updateUserAwardMap(UserAwardMap userAwardMap);
}
