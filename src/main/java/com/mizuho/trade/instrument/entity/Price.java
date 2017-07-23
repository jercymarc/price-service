package com.mizuho.trade.instrument.entity;

import lombok.*;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by Dilip on 18/07/2017.
 */
@Entity
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Price {

    @NonNull
    private PricePk pricePk;
    private String idType;
    private String instrumentType;
    private String priceType;
    @NonNull
    private Date created;
    private BigDecimal bid;
    private BigDecimal ask;
    private boolean isActive;

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
