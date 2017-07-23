package com.mizuho.trade.instrument.endpoint;

import com.mizuho.trade.instrument.entity.Price;
import com.mizuho.trade.instrument.service.PriceService;
import com.mizuho.trade.instrument.util.PriceConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by Dilip on 23/07/2017.
 */
public class JmsPriceEndpoint<P extends Price> extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JmsPriceEndpoint.class);

    private final Set<String> jmsEndpoints;
    private final Class<P> priceClass;
    private final GsonDataFormat format;
    @Autowired
    private PriceService<P> priceService;


    public JmsPriceEndpoint(Set<String> jmsEndpoints,
                            Class<P> priceClass,
                            String dateFormat) {
        super();
        this.format = new GsonDataFormat(priceClass);
        this.priceClass = priceClass;
        this.jmsEndpoints = jmsEndpoints;
        format.setDateFormatPattern(dateFormat);
    }

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel(PriceConstants.DEAD_LETTER_CHANNEL)
                .maximumRedeliveries(3).redeliveryDelay(5000));


        for (String jms : jmsEndpoints) {
            LOG.info("Setting publish route for " + jms);
            from(jms).unmarshal(format).process(new Processor() {

                @Override
                public void process(Exchange exchange) throws Exception {
                    P price = exchange.getIn().getBody(priceClass);
                    LOG.info("Received price via jms: " + price.getPricePk().getVendorId() + ", " +
                            price.getPricePk().getInstrumentId());
                }
            }).bean(priceService, "addOrUpdate");
        }
    }
}
