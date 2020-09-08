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

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonInfoDaoTest {

	@Autowired
	private PersonInfoDao personInfoDao;
	
	@Test
	public void testAQueryPersonInfoById() {
		long userId = 1;
		PersonInfo resultInfo = personInfoDao.queryPersonInfoById(userId);
		System.out.println(resultInfo.getName());
		assertEquals("test", resultInfo.getEmail());
	}
	
	@Test
	@Ignore
	public void testBInsertPersonInfo() {
		PersonInfo personInfo =  new PersonInfo();
		personInfo.setName("lyh");
		personInfo.setCreateTime(new Date());
		personInfo.setLastEditTime(new Date());
		personInfo.setEmail("983849774@qq.com");
		personInfo.setUserType(2);
		personInfo.setEnableStatus(1);
		int effectNum = personInfoDao.insertPersonInfo(personInfo);
		assertEquals(1, effectNum);
	}
}
