package com.midokura.restaurant.service;

import com.midokura.restaurant.ApiResponse;
import com.midokura.restaurant.domain.CustomerGroup;
import com.midokura.restaurant.domain.Table;
import com.midokura.restaurant.exception.GroupHasAlreadyLeftException;
import com.midokura.restaurant.repository.TableRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class SeatingManager {

    private static final int GROUPS_MAX_NUMBER = 6;
    static List<Table> tables;
    private final TableRepository tableRepository;
    static Map<Integer, TreeSet<Table>> inventory;
    private static List<CustomerGroup> line = new ArrayList<>();
    private List<CustomerGroup> settledGroup = new ArrayList<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        tables = (List<Table>) tableRepository.findAll();
        inventory = tables.stream()
                .collect(Collectors.groupingBy(
                        Table::getEmptySpace,
                        Collectors.toCollection(() -> new TreeSet<>())
                ));
        System.out.println(1);
    }

    public ApiResponse arrives(CustomerGroup group) {
        int groupsWaiting = checkLine(group);
        if (groupsWaiting > 0) {
            addGroupToQueue(group, groupsWaiting + 1);
            return ApiResponse.builder()
                    .message("There are " + groupsWaiting + " groups ahead of you")
                    .statusCode(1210)
                    .data(groupsWaiting + 1)
                    .build();
        }
        return assignTable(group);
    }

    private static void addGroupToQueue(CustomerGroup group, int groupsWaiting) {
        group.setLineNumber(groupsWaiting);
        line.add(group);
    }

    private ApiResponse assignTable(CustomerGroup group) {
        Optional<Table> availableTable = Optional.empty();
        for (int i = group.getPeopleNumber(); i <= GROUPS_MAX_NUMBER; i++) {
            availableTable = Optional.ofNullable(inventory.get(i)).orElseGet(TreeSet::new).stream()
                    .filter(table -> table.getEmptySpace() >= group.getPeopleNumber())
                    .findFirst();
            if (availableTable.isPresent()) {
                break;
            }
        }
        if (availableTable.isPresent()) {
            settledGroup.add(group);
            updateLine(group);
            availableTable.get().setEmptySpace(availableTable.get().getEmptySpace() - group.getPeopleNumber());
            group.setHasTable(true);
            group.setTable(availableTable.get());
            return ApiResponse.builder()
                    .message("This group set at table " + availableTable.get().getTableNumber())
                    .data(availableTable.get().getTableNumber())
                    .statusCode(1200)
                    .build();
        } else {
            addGroupToQueue(group, 1);
            return ApiResponse.builder().message("Unfortunately there is not empty space now." +
                            " This group is first in the line.")
                    .statusCode(1210)
                    .data(1)
                    .build();
        }
    }

    private void updateLine(CustomerGroup readyCustomerGroup) {
        line.stream().filter(customerGroup -> customerGroup.getPeopleNumber() == readyCustomerGroup.getPeopleNumber()
                        && customerGroup != readyCustomerGroup)
                .peek(customerGroup -> customerGroup.setLineNumber(customerGroup.getLineNumber() - 1));
        line.remove(readyCustomerGroup);
    }


    public ApiResponse leaves(CustomerGroup group) {
        CustomerGroup leavingGroup = settledGroup.stream()
                .filter(customerGroup -> customerGroup.equals(group))
                .findFirst()
                .orElseThrow(() -> new GroupHasAlreadyLeftException());
        settledGroup.remove(group);
        leavingGroup.getTable().setEmptySpace(leavingGroup.getTable().getEmptySpace() + leavingGroup.getPeopleNumber());
        return ApiResponse.builder()
                .message("Group " + leavingGroup.getUniqueName() + " has left.")
                .statusCode(1202)
                .data(leavingGroup.getTable().getTableNumber())
                .build();
    }

    public ApiResponse locate(CustomerGroup group) {
        CustomerGroup seatingGroup = settledGroup.stream().filter(customerGroup -> customerGroup.equals(group))
                .findFirst()
                .orElseThrow(() -> new GroupHasAlreadyLeftException());
        return ApiResponse.builder()
                .message("This group is sitting at " + seatingGroup.getTable().getTableNumber() + " table.")
                .data(seatingGroup.getTable().getTableNumber())
                .statusCode(1211)
                .build();


    }

    public int checkLine(CustomerGroup group) {
        return (int) line.stream()
                .filter(customerGroup -> customerGroup.getPeopleNumber() == group.getPeopleNumber() &&
                        !customerGroup.equals(group)).count();
    }

}
