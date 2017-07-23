package com.mizuho.trade.instrument.repository;

import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.entity.PricePk;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


/**
 * Created by Dilip on 18/07/2017.
 */
@Repository
@CacheConfig(cacheNames = {"pricesByVendorId", "pricesByInstrumentId"})
public interface PriceRepository extends JpaRepository<Price, PricePk> {

}
