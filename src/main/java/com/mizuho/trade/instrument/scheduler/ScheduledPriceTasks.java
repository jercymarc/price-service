package com.mizuho.trade.instrument.scheduler;

import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Dilip on 18/07/2017.
 */
@Component
public class ScheduledPriceTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledPriceTasks.class);
    @Autowired
    private PriceService priceService;
    private ReadWriteLock priceLock = new ReentrantReadWriteLock(true);
    private int cacheAgeInDays = 30;

    @Scheduled(cron = "*/5 * * * * *")
    public void scheduleTaskUsingCronExpression() {
        try {
            lockPriceObject();
            priceService.getAllPrices();
            Set<Price> prices = priceService.getPricesForLast30Days();
            unlockPriceObject();
            if (!prices.isEmpty()) {
                LOG.info("Deleting " + prices.size() + " old prices");
                for (Price p : prices) {
                    priceService.delete(p);
                }
                LOG.info("Prices cleaned successfully");
            } else {
                LOG.info("No prices to clean");
            }
        } catch (Exception e) {
            LOG.error("Unable to delete old prices", e);
            try {
                unlockPriceObject();
            } catch (Exception ignore) {
                // will get an exception if the readlock is not locked
            }
        }

    }

    private void unlockPriceObject() {
        priceLock.readLock().unlock();
    }

    private void lockPriceObject() {
        priceLock.readLock().lock();
    }
}
