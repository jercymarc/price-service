package com.mizuho.trade.instrument.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Dilip on 22/07/2017.
 */
public class PriceConstants {

    public static final String TOPIC_INTERNAL_FEED_PRICE = "activemq:topic:com.feed.price.internal";
    public static final String DEAD_LETTER_CHANNEL = "activemq:queue:dead";
    public static final java.lang.String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final List<String> TEST_ISINS = Lists.newArrayList("DE000DG6CF68", "DE000JPM85H5", "XS1237672412", "XS1289335736", "XS1289354877");

}
