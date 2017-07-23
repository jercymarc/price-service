package com.mizuho.trade.instrument.service;

import com.mizuho.trade.instrument.entity.Price;

import java.util.Set;

/**
 * Created by Dilip on 18/07/2017.
 */
public interface PriceService<P extends Price> {
    public Set<Price> getPricesForLast30Days();

    public Set<P> getPricesForVendor(String vendor);

    public Set<P> getPricesForInstrumentId(String instrument);

    public Set<P> getAllPrices();

    public void addOrUpdate(P price);

    public void publish(P price);

    public void delete(P price);

}
