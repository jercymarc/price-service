package com.mizuho.trade.instrument.controller;

import com.google.common.collect.Sets;
import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.entity.PricePk;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Created by Dilip on 23/07/2017.
 */
public class PriceControllerTest extends MizuhoBaseControllerTest {

    private static final Price testPrice = new Price(new PricePk("instrumentId", "vendorId"), "idType", "instrumentType",
            "priceType", new Date(), BigDecimal.ONE, BigDecimal.TEN, true);

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testPriceController() throws Exception {
        Set<Price> prices = Sets.newHashSet(testPrice);
        String json = jsonUtils.mapToJson(prices);

        String uri = "/prices/create";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json)).andReturn();

        String message = result.getResponse().getContentAsString();
        int status = result.getResponse().getStatus();

        Assert.assertEquals(HttpStatus.OK.value(), status);
        Assert.assertEquals("All prices processed successfully", message);

    }

}
