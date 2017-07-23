package com.mizuho.trade.instrument.service;

/**
 * Created by Dilip on 18/07/2017.
 */
public interface TimerTask {
    void runTimerTask();

    String getServicename();

    long getDelayInSeconds();

    long getPeriodInSeconds();
}
