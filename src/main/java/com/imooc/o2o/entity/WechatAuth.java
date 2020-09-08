package com.imooc.o2o.entity;

import java.util.Date;

/*
 *  用户分为微信账号和本地账号  
 *  以下是微信账号实体类
 * */
public class WechatAuth {
	//微信账号ID
	private Long wechatAuthId;
	//微信账号微信ID
	private String openId;
	//创建时间
	private Date createTime;
	//用户实体（id）
	private PersonInfo personInfo;
	
	public Long getWechatAuthId() {
		return wechatAuthId;
	}
	public void setWechatAuthId(Long wechatAuthId) {
		this.wechatAuthId = wechatAuthId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public PersonInfo getPersonInfo() {
		return personInfo;
	}
	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}
		
}
