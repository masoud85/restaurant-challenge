package com.midokura.restaurant.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "uniqueName")
public class CustomerGroup {

    @NonNull
    private String uniqueName;
    private int peopleNumber;
    private int lineNumber;
    private Table Table;
    private boolean hasTable;

}
