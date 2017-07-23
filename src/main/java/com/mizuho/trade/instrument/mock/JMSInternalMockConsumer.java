package com.mizuho.trade.instrument.mock;

import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.util.PriceConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dilip on 23/07/2017.
 */
public class JMSInternalMockConsumer extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JMSInternalMockConsumer.class);


    @Override
    public void configure() throws Exception {
        from(PriceConstants.TOPIC_INTERNAL_FEED_PRICE).process(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {

                Price price = exchange.getIn().getBody(Price.class);
                LOG.info("Received price on internal JMS feed: " + price);
            }
        });

    }


}
