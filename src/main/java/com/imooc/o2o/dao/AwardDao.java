package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.Award;

public interface AwardDao {

	/**
	 * 依据传入的条件分页显示奖品信息列表
	 *
	 * @param awardCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<Award> queryAwardList(@Param("awardCondition") Award awardCondition,
							   @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	/**
	 * 依据传入的条件返回奖品数
	 * @param awardCondition
	 * @return
	 */
	int queryAwardCount(@Param("awardCondition") Award awardCondition);

	/**
	 * 依据awardId获得奖品信息
	 * @param awardId
	 * @return
	 */
	Award queryAwardByAwardId(long awardId);

	/**
	 * 添加奖品信息
	 * @param award
	 * @return
	 */
	int insertAward(Award award);

	/**
	 * 更新奖品信息
	 * @param award
	 * @return
	 */
	int updateAward(Award award);

	/**
	 * 删除奖品信息
	 * @param awardId
	 * @param shopId
	 * @return
	 */
	int deleteAward(@Param("awardId") long awardId,@Param("shopId")long shopId);
}
