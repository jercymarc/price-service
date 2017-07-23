package com.mizuho.trade.instrument.service.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mizuho.trade.instrument.consumer.Consumer;
import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.repository.PriceRepository;
import com.mizuho.trade.instrument.service.PriceService;
import com.mizuho.trade.instrument.service.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


/**
 * Created by Dilip on 20/07/2017.
 */
@Service
public class PriceServiceImpl implements PriceService<Price>, TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(PriceServiceImpl.class);
    private final Multimap<String, Price> priceMapByVendor = HashMultimap.create();
    private final Multimap<String, Price> priceMapByInstrument = HashMultimap.create();
    private final Set<Consumer<Price>> consumers;
    private final long timerDelaySeconds;
    private final long timerPeriodSeconds;
    @Autowired
    private PriceRepository priceRepository;
    private ReadWriteLock priceLock = new ReentrantReadWriteLock(true);
    private long cacheAgeInDays;

    public PriceServiceImpl(long cacheAgeInDays, long timerDelaySeconds, long timerPeriodSeconds,
                            Set<Consumer<Price>> priceConsumers) {

        LOG.info("Cache age in days = " + cacheAgeInDays);
        LOG.info("Timer delay in seconds = " + timerDelaySeconds);
        LOG.info("Timer period in seconds = " + timerPeriodSeconds);
        this.consumers = priceConsumers;

        this.cacheAgeInDays = cacheAgeInDays;
        this.timerDelaySeconds = timerDelaySeconds;
        this.timerPeriodSeconds = timerPeriodSeconds;
    }

    @Override
    public Set<Price> getPricesForVendor(String vendor) {
        Set<Price> prices;
        LOG.info("Getting prices for Vendor " + vendor);
        try {
            lockPriceObject();
            prices = new HashSet<Price>(priceMapByVendor.get(vendor));
        } catch (Exception e) {
            LOG.error("Unable to get price for vendor " + vendor);
            throw e;
        } finally {
            unlockPriceObject();
        }

        return prices;
    }

    @Override
    public Set<Price> getPricesForInstrumentId(String instrument) {
        Set<Price> prices;
        LOG.info("Getting prices for instrument " + instrument);
        try {
            lockPriceObject();
            prices = new HashSet<Price>(priceMapByInstrument.get(instrument));
        } catch (Exception e) {
            LOG.error("Unable to get price for instrument id " + instrument);
            throw e;
        } finally {
            unlockPriceObject();
        }

        return prices;
    }

    @Override
    public Set<Price> getAllPrices() {
        LOG.info("Getting all prices");
        Set<Price> prices;
        try {
            lockPriceObject();
            prices = new HashSet<Price>(priceMapByVendor.values());
            LOG.info("Prices retrieved successfully");
        } catch (Exception e) {
            LOG.error("Unable to retrieve all prices", e);
            throw e;
        } finally {
            unlockPriceObject();
        }

        return prices;
    }

    @Override
    public void addOrUpdate(Price price) {
        LOG.info("Adding price with primary key " + price.getPricePk());
        try {
            lockPriceObject();
            priceRepository.save(price);
            addPriceToMaps(price);
            LOG.info("Price added successfully");
        } catch (Exception e) {
            LOG.error("Unable to add price with pk " + price.getPricePk(), e);
            throw e;
        } finally {
            try {
                unlockPriceObject();
            } catch (Exception ignore) {
                //Exception is thrown if lock is not locked
            }
        }
        publish(price);
    }

    @Override
    public void runTimerTask() {
        LOG.info("Cleaning prices older than " + cacheAgeInDays + " days");
        final Date dateBefore = new Date(System.currentTimeMillis() - cacheAgeInDays * 24 * 36 * 100L);
        try {
            lockPriceObject();
            Set<Price> prices = getPricesForLast30Days();
            unlockPriceObject();
            if (!prices.isEmpty()) {
                LOG.info("Deleting " + prices.size() + " old prices");
                for (Price p : prices) {
                    delete(p);
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

    public Set<Price> getPricesForLast30Days() {
        final Date dateBefore = new Date(System.currentTimeMillis() - cacheAgeInDays * 24 * 36 * 100L);
        Set<Price> prices = getAllPrices()
                .stream()
                .filter(price -> price.getCreated().before(dateBefore))
                .collect(Collectors.toSet());
        return prices;
    }

    @Override
    public void publish(Price price) {
        for (Consumer<Price> consumer : consumers) {
            LOG.info("Publishing to " + consumer.getConsumerName());
            try {
                consumer.consume(price);
            } catch (Exception e) {
                LOG.error("Unable to publish to " + consumer.getConsumerName(), e);
            }
        }
    }

    @Override
    public void delete(Price price) {
        LOG.info("Deleting price with primary key " + price.getPricePk());
        try {
            lockPriceObject();
            priceRepository.delete(price);
            removePriceFromMaps(price);
            LOG.info("Price removed successfully");
        } catch (Exception e) {
            LOG.error("Unable to remove price with pk " + price.getPricePk(), e);
            throw e;
        } finally {
            unlockPriceObject();
        }
        LOG.info("Publishing inactive price downstream");
        price.setActive(false);
        publish(price);
    }

    private void addPriceToMaps(Price price) {
        priceMapByVendor.put(price.getPricePk().getVendorId(), price);
        priceMapByInstrument.put(price.getPricePk().getInstrumentId(), price);
    }

    private void removePriceFromMaps(Price price) {
        priceMapByVendor.remove(price.getPricePk().getVendorId(), price);
        priceMapByInstrument.remove(price.getPricePk().getInstrumentId(), price);
    }

    private void unlockPriceObject() {
        priceLock.readLock().unlock();
    }

    private void lockPriceObject() {
        priceLock.readLock().lock();
    }

    @Override
    public String getServicename() {
        return "Price Service";
    }

    @Override
    public long getDelayInSeconds() {
        return timerDelaySeconds;
    }

    @Override
    public long getPeriodInSeconds() {
        return timerPeriodSeconds;
    }

}