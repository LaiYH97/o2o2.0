package com.imooc.o2o.entity;

import java.util.Date;

/*
 *  商品缩略图实体类
 * */
public class ProductImg {
	//商品缩略图ID
	private Long productImgId;
	//商品缩略图链接
	private String imgAddr;
	//商品缩略图描述
	private String imgDesc;
	//商品缩略图权重
	private Integer priority;
	//创建时间
	private Date createTime;
	//商品实体（商品ID，与Product相关）
	private Long productId;
	
	public Long getProductImgId() {
		return productImgId;
	}
	public void setProductImgId(Long productImgId) {
		this.productImgId = productImgId;
	}
	public String getImgAddr() {
		return imgAddr;
	}
	public void setImgAddr(String imgAddr) {
		this.imgAddr = imgAddr;
	}
	public String getImgDesc() {
		return imgDesc;
	}
	public void setImgDesc(String imgDesc) {
		this.imgDesc = imgDesc;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
}
