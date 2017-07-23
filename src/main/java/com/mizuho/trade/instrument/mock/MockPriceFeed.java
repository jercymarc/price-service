package com.mizuho.trade.instrument.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.entity.PricePk;
import com.mizuho.trade.instrument.service.TimerTask;
import com.mizuho.trade.instrument.util.JSONUtils;
import com.mizuho.trade.instrument.util.PriceConstants;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Dilip on 23/07/2017.
 */
public class MockPriceFeed implements TimerTask {

    public static final String ACTIVEMQ_TOPIC_COM_FEED_PRICE_THOMSON_REUTERS = "activemq:topic:com.feed.price.thomsonreuters";
    public static final String ACTIVEMQ_TOPIC_COM_FEED_PRICE_BLOOMBERG = "activemq:topic:com.feed.price.bloomberg";
    private static final Logger LOG = LoggerFactory.getLogger(MockPriceFeed.class);
    private final long delayInSeconds;
    private final long periodInSeconds;
    private final JSONUtils jsonUtils = new JSONUtils();
    @Produce(uri = ACTIVEMQ_TOPIC_COM_FEED_PRICE_THOMSON_REUTERS)
    private ProducerTemplate producerTemplateReuters;
    @Produce(uri = ACTIVEMQ_TOPIC_COM_FEED_PRICE_BLOOMBERG)
    private ProducerTemplate producerTemplateBloomberg;


    public MockPriceFeed(String vendorId, long delayInSeconds, long periodInSeconds) {
        super();
        this.delayInSeconds = delayInSeconds;
        this.periodInSeconds = periodInSeconds;
    }

    @Override
    public void runTimerTask() {
        try {
            Price price = createMockPrice("Bloomberg");
            producerTemplateBloomberg.sendBodyAndHeader(jsonUtils.mapToJson(price), "vendorId", "Bloomberg");
            price = createMockPrice("Reuters");
            producerTemplateReuters.sendBodyAndHeader(jsonUtils.mapToJson(price), "vendorId", "Reuters");
        } catch (CamelExecutionException | JsonProcessingException e) {
            LOG.error("Unable to parse price", e);
        }

    }

    private Price createMockPrice(String vendorId) {
        int position = (int) (Math.random() * 5);
        String isin = PriceConstants.TEST_ISINS.get(position);
        PricePk pricePk = new PricePk(isin, vendorId);
        return new Price(pricePk, "ISIN", "Government Bond", "Clean Price",
                new Date(), generatePrice(), generatePrice(), true);
    }

    private BigDecimal generatePrice() {
        return new BigDecimal(100 * Math.random()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    @Override
    public String getServicename() {
        return "Mock price feed";
    }

    @Override
    public long getDelayInSeconds() {
        return delayInSeconds;
    }

    @Override
    public long getPeriodInSeconds() {
        return periodInSeconds;
    }

}
