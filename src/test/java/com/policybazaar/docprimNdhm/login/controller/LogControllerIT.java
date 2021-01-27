package com.policybazaar.docprimNdhm.login.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



/**
 * @author Rahulv
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:dpNdhm-appContextTest.xml"})
@WebAppConfiguration
public class LogControllerIT {

	/**
	 * 
	 */
	@Value("${base.test.coreservices.url}")
	private String BASE_URL;
	
	/**
	 * 
	 */
	@Resource
	private WebApplicationContext webApplicationContext;
	
    /**
     * 
     */
    private MockMvc mockMvc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddErrorLog() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessageByQuery() throws Exception {
		String url = BASE_URL + "/log/get/"+TestRequestParameters._ID;
		mockMvc.perform(get(url)).andExpect(status().isOk());
	}
	
	@Test
	public final void testGetMessageByQueryBadInput() throws Exception{
		String url = BASE_URL + "/log/get/"+TestRequestParameters._ID_BADINPUT;
		mockMvc.perform(get(url)).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void testGetErrors() throws Exception {
		String url = BASE_URL + "/log/getErrors?startDate=01-09-2015&endDate=18-09-2015&appid=1&offset=10&limit=20";
		mockMvc.perform(get(url)).andExpect(status().isOk());
	}

	@Test
	public final void testGetErrorsBadInput() throws Exception{
		String url = BASE_URL + "/log/getErrors?startDate=2015-09-19&endDate=2015-09-25&appid=1&offset=10&limit=20";
		mockMvc.perform(get(url)).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
}
