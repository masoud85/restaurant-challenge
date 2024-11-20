package com.midokura.restaurant.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Entity(name = "restaurant_table")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Table implements Comparable<Table> {

    @Id
    @GeneratedValue
    private Long id;
    private int tableNumber;
    private int capacity;
    private int emptySpace;

    public Table() {
    }

    @Override
    public int compareTo(Table other) {
        return Comparator.comparingInt(Table::getEmptySpace)
                .thenComparing(Table::getCapacity)
                .thenComparing(Table::getTableNumber)
                .compare(this, other);
    }
}
