package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.o2o.entity.LocalAuth;
import com.imooc.o2o.entity.PersonInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalAuthDaoTest {

	@Autowired
	private LocalAuthDao localAuthDao;
	
	private static final String USERNAME = "username";
	
	private static final String PASSWORD = "password";
	
	@Test
	@Ignore
	public void testAinsertLocalAuth() {
		LocalAuth localAuthConditionAuth = new LocalAuth();
		PersonInfo personInfo = new PersonInfo();
		personInfo.setUserId(2L);
		localAuthConditionAuth.setPersonInfo(personInfo);
		localAuthConditionAuth.setUsername("username");
		localAuthConditionAuth.setPassword("password");
		localAuthConditionAuth.setCreateTime(new Date());
		localAuthConditionAuth.setLastEditTime(new Date());
		int effectNum = localAuthDao.insertLocalAuth(localAuthConditionAuth);
		assertEquals(1, effectNum);
	}
	
	@Test
	@Ignore
	public void testBqueryLocalByUserNameAndPwd() {
		LocalAuth localAuth = localAuthDao.queryLocalByUserNameAndPwd(USERNAME, PASSWORD);
		System.out.println(localAuth.getUsername());
	}
	
	@Test
	public void testCqueryLocalByUserId() {
		LocalAuth localAuth = localAuthDao.queryLocalByUserId(1L);
		System.out.println(localAuth.getUsername());
	}
	
	@Test
	@Ignore
	public void testDupdateLocalAuth() {
		long userId = 1L;
		String username = "username";
		String password = "password";
		String newPassword = "newpassword";
		Date lastEditTime = new Date();
		int effectNum = localAuthDao.updateLocalAuth(userId, username, password, newPassword, lastEditTime);
		assertEquals(1, effectNum);
		LocalAuth localAuth = localAuthDao.queryLocalByUserId(1L);
		System.out.println(localAuth.getPassword());
	}
}
