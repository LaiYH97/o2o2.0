package com.imooc.o2o.util;

/**
 * 实现将前端页面的页码数转换成数据库数据的行数的工具类
 * @author Administrator
 *
 */
public class PageCalculator {
	
	public static int calculatorRowIndex(int pageIndex,int pageSize) {
		return (pageIndex > 0)?(pageIndex - 1) * pageSize:0;    
	}
	
}
