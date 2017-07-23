package com.mizuho.trade.instrument.entity;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Created by Dilip on 18/07/2017.
 */

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PricePk implements Serializable {
    @NonNull
    private String instrumentId;
    @NonNull
    private String vendorId;
}
