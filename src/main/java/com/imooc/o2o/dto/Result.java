package com.imooc.o2o.dto;

/**
 * 封装店铺商品类别对象执行后的结果
 * 包括是否成功的标志、商品类别数据、错误信息、状态码
 */
public class Result<T> {

	private boolean success;//是否成功标志
	private T data;//成功时返回的数据
	private String errMsg;//错误信息
	private int errorCode;//状态码
	
	public Result() {}
	//成功时的构造器
	public Result(boolean success,T data) {
		this.success = success;
		this.data = data;
	}
	//错误时的构造器
	public Result(boolean success,int errorCode,String errMsg) {
		this.success = success;
		this.errorCode = errorCode;
		this.errMsg = errMsg;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
