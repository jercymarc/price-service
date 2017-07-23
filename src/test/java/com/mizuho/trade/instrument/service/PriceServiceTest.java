package com.mizuho.trade.instrument.service;

import com.google.common.collect.Sets;
import com.mizuho.trade.instrument.consumer.Consumer;
import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.entity.PricePk;
import com.mizuho.trade.instrument.repository.PriceRepository;
import com.mizuho.trade.instrument.service.impl.PriceServiceImpl;
import org.apache.camel.ValidationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Created by Dilip on 20/07/2017.
 */
public class PriceServiceTest {


    private static final Date DATE_31_DAYS_AGO = new Date(
            System.currentTimeMillis() - (24 * 3600L * 31));
    private static final Price testPrice = new Price(new PricePk("instrumentId", "vendorId"), "idType", "instrumentType",
            "priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
    private static final Price testPrice2 = new Price(new PricePk(null, "vendorId"), "idType", "instrumentType", "priceType",
            DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
    private static final Price testPrice3 = new Price(new PricePk("instrumentId", null), "idType", "instrumentType",
            "priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
    private static final Price testPrice4 = new Price(new PricePk("instrumentId", "vendorId"), "idType", "instrumentType",
            "priceType", null, BigDecimal.ONE, BigDecimal.TEN, true);
    private final Set<Price> pricesConsumed = Sets.newHashSet();
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private PriceService priceService;

    @Before
    public void setup() {
        priceRepository.save(testPrice);
        priceService = new PriceServiceImpl(30, 60, 60, Collections.singleton(new Consumer<Price>() {
            public void consume(Price p) {
                pricesConsumed.add(p);
            }

            @Override
            public String getConsumerName() {
                return "Test Consumer";
            }
        }));

    }

    @Test
    public void testPriceService() {
        // Test that priming has happened
        Set<Price> prices = priceService.getPricesForInstrumentId(testPrice.getPricePk().getInstrumentId());
        Assert.assertTrue("Cache was primed", prices.size() == 1);
        Assert.assertTrue("Cache was primed", prices.contains(testPrice));

        prices = priceService.getPricesForVendor(testPrice.getPricePk().getVendorId());
        Assert.assertTrue("Cache was primed", prices.size() == 1);
        Assert.assertTrue("Cache was primed", prices.contains(testPrice));

        prices = priceService.getAllPrices();
        Assert.assertTrue("Cache was primed", prices.size() == 1);
        Assert.assertTrue("Cache was primed", prices.contains(testPrice));

        Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
        Assert.assertTrue("Price is active as published", pricesConsumed.iterator().next().isActive());

        // Test deletion
        pricesConsumed.clear();
        priceService.delete(testPrice);
        Assert.assertTrue("Price was deleted", priceService.getAllPrices().isEmpty());
        Assert.assertTrue("Price was deleted", priceService.getPricesForVendor(testPrice.getPricePk().getVendorId()).isEmpty());
        Assert.assertTrue("Price was deleted", priceService.getPricesForInstrumentId(testPrice.getPricePk().getInstrumentId()).isEmpty());
        Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
        Assert.assertFalse("Price is inactive as published", pricesConsumed.iterator().next().isActive());

        // Test addOrUpdate method
        pricesConsumed.clear();
        testPrice.setActive(true);
        priceService.addOrUpdate(testPrice);
        prices = priceService.getPricesForInstrumentId(testPrice.getPricePk().getInstrumentId());
        Assert.assertTrue("Price was added", prices.size() == 1);
        Assert.assertTrue("Price was added", prices.contains(testPrice));
        Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
        Assert.assertTrue("Price is active as published", pricesConsumed.iterator().next().isActive());

        prices = priceService.getPricesForVendor(testPrice.getPricePk().getVendorId());
        Assert.assertTrue("Price was cached by vendor", prices.size() == 1);
        Assert.assertTrue("Price was cached by vendor", prices.contains(testPrice));

        prices = priceService.getAllPrices();
        Assert.assertTrue("Price was added", prices.size() == 1);
        Assert.assertTrue("Price was added", prices.contains(testPrice));


    }

    @Test(expected = ValidationException.class)
    public void testValidationFailsWithNullVendorId() {
        priceService.addOrUpdate(testPrice2);
    }

    @Test(expected = ValidationException.class)
    public void testValidationFailsWithNullInstrumentId() {
        priceService.addOrUpdate(testPrice3);
    }

    @Test(expected = ValidationException.class)
    public void testValidationFailsWithNullCreatedDate() {
        priceService.addOrUpdate(testPrice4);
    }

}
