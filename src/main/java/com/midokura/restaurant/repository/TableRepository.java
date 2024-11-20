package com.midokura.restaurant.repository;

import com.midokura.restaurant.domain.Table;
import org.springframework.data.repository.CrudRepository;

public interface TableRepository extends CrudRepository<Table, Long> {
}
