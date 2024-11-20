package com.midokura.restaurant.controller;

import com.midokura.restaurant.ApiResponse;
import com.midokura.restaurant.domain.CustomerGroup;
import com.midokura.restaurant.service.SeatingManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final SeatingManager seatingManager;

    @PostMapping("/leave")
    public ApiResponse leave(@RequestBody CustomerGroup group) {
        return seatingManager.leaves(group);
    }

    @PostMapping("/arrive")
    public ApiResponse arrive(@RequestBody CustomerGroup group) {
        return seatingManager.arrives(group);
    }

    @PostMapping("/locate")
    public ApiResponse locate(@RequestBody CustomerGroup group) {
        return seatingManager.locate(group);
    }

}
