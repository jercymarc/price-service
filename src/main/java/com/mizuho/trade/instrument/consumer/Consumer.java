package com.mizuho.trade.instrument.consumer;

/**
 * Created by Dilip on 22/07/2017.
 */
public interface Consumer<P> {

    public void consume(P price);

    public String getConsumerName();
}
