package com.mizuho.trade.instrument.controller;

import com.mizuho.trade.instrument.MizuhoPriceServiceApplication;
import com.mizuho.trade.instrument.util.JSONUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Dilip on 23/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MizuhoPriceServiceApplication.class)
@WebAppConfiguration
public class MizuhoBaseControllerTest {

    protected MockMvc mockMvc;

    protected JSONUtils jsonUtils;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jsonUtils = new JSONUtils();
    }
}
