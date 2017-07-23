package com.mizuho.trade.instrument.consumer;

import com.google.common.collect.ImmutableMap;
import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.util.PriceConstants;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dilip on 22/07/2017.
 */
public class JMSPriceConsumer<P extends Price> implements Consumer<P> {

    public static final String PRICE_SERVICE_JMS_CONSUMER = "Price Service JMS Consumer";
    private static final Logger LOG = LoggerFactory.getLogger(JMSPriceConsumer.class);
    @Produce(uri = PriceConstants.TOPIC_INTERNAL_FEED_PRICE)
    private ProducerTemplate producerTemplate;

    @Override
    public void consume(Price price) {
        LOG.info("Publishing price to " + PriceConstants.TOPIC_INTERNAL_FEED_PRICE);
        producerTemplate.sendBodyAndHeaders(price, ImmutableMap.of("vendorId", price.getPricePk().getVendorId(),
                "instrumentId", price.getPricePk().getInstrumentId()));
    }

    @Override
    public String getConsumerName() {
        return PRICE_SERVICE_JMS_CONSUMER;
    }

}
