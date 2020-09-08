package com.imooc.o2o.entity;

import java.util.Date;

/**
 * 顾客已领取的奖品映射
 */
public class UserAwardMap {

	private Long userAwardId;

    private Date createTime;

    //使用状态 0 未兑换 1已兑换
    private Integer usedStatus;

    //领取奖品所消耗的积分
    private Integer point;

    //顾客信息实体类
    private PersonInfo user;

    //奖品信息
    private Award award;

    //操作员信息实体类
    private PersonInfo operator;

    private Shop shop;

	public Long getUserAwardId() {
		return userAwardId;
	}

	public void setUserAwardId(Long userAwardId) {
		this.userAwardId = userAwardId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getUsedStatus() {
		return usedStatus;
	}

	public void setUsedStatus(Integer usedStatus) {
		this.usedStatus = usedStatus;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public PersonInfo getUser() {
		return user;
	}

	public void setUser(PersonInfo user) {
		this.user = user;
	}

	public Award getAward() {
		return award;
	}

	public void setAward(Award award) {
		this.award = award;
	}

	public PersonInfo getOperator() {
		return operator;
	}

	public void setOperator(PersonInfo operator) {
		this.operator = operator;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
    
    
}
