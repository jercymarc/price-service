package com.mizuho.trade.instrument.controller;

import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Dilip on 23/07/2017.
 */
@RestController
@RequestMapping("/prices")
public class PriceController {

    private static final Logger LOG = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    private PriceService<Price> priceService;

    @RequestMapping(value = "/vendor/{vendorId}/list", method = RequestMethod.GET)
    public Set<Price> getVendorPrices(@PathVariable("vendorId") String vendorId) {
        LOG.info("Retrieving prices for vendor " + vendorId);
        Set<Price> prices;
        try {
            prices = priceService.getPricesForVendor(vendorId);
            LOG.info("Prices retrieved successfully");
        } catch (Exception e) {
            LOG.error("Unable to get prices for vendor " + vendorId, e);
            prices = Collections.emptySet();
        }
        return prices;
    }

    @RequestMapping(value = "/instrument/{instrumentId}/list", method = RequestMethod.GET)
    public Set<Price> getInstrumentIdPrices(@PathVariable("instrumentId") String instrumentId) {
        LOG.info("Fetching prices for instrument id " + instrumentId);
        Set<Price> prices;
        try {
            prices = priceService.getPricesForInstrumentId(instrumentId);
            LOG.info("Prices retrieved successfully");
        } catch (Exception e) {
            LOG.error("Unable to get prices for instrument id " + instrumentId, e);
            prices = Collections.emptySet();
        }
        return prices;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<String> createPrices(@RequestBody Set<Price> prices) {
        LOG.info("Creating " + prices.size() + " prices.");
        int created = 0;
        ResponseEntity<String> response = null;
        for (Price p : prices) {
            try {
                priceService.addOrUpdate(p);
                ++created;
            } catch (Exception e) {
                LOG.error("Unable to create price", e);
            }
        }
        if (created == prices.size()) {
            String message = "All prices processed successfully";
            LOG.info(message);
            response = new ResponseEntity<String>(message, HttpStatus.OK);
        } else {
            String message = created + " out of " + prices.size() + " prices created.";
            LOG.warn(message);
            response = new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }


}
