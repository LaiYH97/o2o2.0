package com.imooc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.dto.WechatAuthExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.enums.WechatAuthStateEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatAuthServiceTest {

	@Autowired
	private WechatAuthService wechatAuthService;
	
	@Test
	public void testRegister() {
		WechatAuth wechatAuth = new WechatAuth();
		PersonInfo personInfo = new PersonInfo();
		String openId = "openIDlyh";
		
		personInfo.setCreateTime(new Date());
		personInfo.setName("laiyanhong");
		personInfo.setUserType(1);
		
		wechatAuth.setPersonInfo(personInfo);
		wechatAuth.setOpenId(openId);
		wechatAuth.setCreateTime(new Date());
		
		WechatAuthExecution wae = wechatAuthService.register(wechatAuth);
		assertEquals(WechatAuthStateEnum.SUCCESS.getState(), wae.getState());
		
		wechatAuth  = wechatAuthService.getWechatAuthByOpenId(openId);
		System.out.println(wechatAuth.getPersonInfo().getName());
		
	}
}
