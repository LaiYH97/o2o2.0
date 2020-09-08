package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.WechatAuth;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatAuthDaoTest {

	@Autowired
	private WechatAuthDao wechatAuthDao;
	
	@Test	
	@Ignore
	public void testAqueryWechatInfoByOpenId() {
		String openId = "lyh";
		WechatAuth wechatAuth = wechatAuthDao.queryWechatInfoByOpenId(openId);
		System.out.println(wechatAuth.getPersonInfo().getName());
	}
	
	@Test
	public void testBinserWechatAuth() {
		PersonInfo personInfo=new PersonInfo();
		WechatAuth wechatAuth=new WechatAuth();
		//设置的是userId关联tb_person_info的哪一行
		personInfo.setUserId(1L);
		wechatAuth.setPersonInfo(personInfo);
		wechatAuth.setOpenId("lyh");
		wechatAuth.setCreateTime(new Date());
		int insertWechatAuth = wechatAuthDao.insertWechatAuth(wechatAuth);
		assertEquals(1, insertWechatAuth);
	}
	
}
