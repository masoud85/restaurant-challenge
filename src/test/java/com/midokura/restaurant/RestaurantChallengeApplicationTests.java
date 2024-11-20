package com.midokura.restaurant;

import com.midokura.restaurant.domain.CustomerGroup;
import com.midokura.restaurant.domain.Table;
import com.midokura.restaurant.repository.TableRepository;
import com.midokura.restaurant.service.SeatingManager;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestaurantChallengeApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    SeatingManager seatingManager;

    @MockBean
    TableRepository tableRepository;

    @Test
    @Order(1)
    void shouldReturnTable1For2People() {
//        given: all the following tables are empty
        List<Table> availableTables = List.of(
                Table.builder().id(101L).capacity(2).emptySpace(2).tableNumber(1).build(),
                Table.builder().id(102L).capacity(2).emptySpace(2).tableNumber(2).build(),
                Table.builder().id(103L).capacity(3).emptySpace(3).tableNumber(3).build(),
                Table.builder().id(104L).capacity(4).emptySpace(4).tableNumber(4).build(),
                Table.builder().id(105L).capacity(4).emptySpace(4).tableNumber(5).build(),
                Table.builder().id(106L).capacity(5).emptySpace(5).tableNumber(6).build(),
                Table.builder().id(107L).capacity(6).emptySpace(6).tableNumber(7).build()
                );
        when(tableRepository.findAll()).thenReturn(availableTables);
        seatingManager.init();

//        when: a group of 2 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(2)
                                .uniqueName("group1")
                                .build(), ApiResponse.class);

//        then: we expect to get table 1 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 1);
        assertEquals(apiResponseResponseEntity.getBody().getStatusCode(), 1200);
    }

    @Test
    @Order(2)
     void shouldReturnTable2For2People() {
//        given: table 2 for 2 people is available

//        when: a group of 2 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(2)
                                .uniqueName("group2")
                                .build(), ApiResponse.class);

//        then: we expect to get table 2 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 2);
    }

    @Test
    @Order(3)
     void shouldReturnTable6For5People() {
//        given: table 6 for 5 people is available

//        when: a group of 5 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(5)
                                .uniqueName("group3")
                                .build(), ApiResponse.class);

//        then: we expect to get table 6 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 6);
    }

    @Test
    @Order(4)
     void shouldReturnTable7For5People() {
//        given: table 7 for 5 people is available

//        when: a group of 5 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(5)
                                .uniqueName("group4")
                                .build(), ApiResponse.class);

//        then: we expect to get table 6 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 7);
    }

    @Test
    @Order(5)
     void shouldReturnThereIsNotEnoughSpaceError() {
//        given: there is not enough space for 6 people

//        when: a group of 6 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(6)
                                .uniqueName("group5")
                                .build(), ApiResponse.class);

//        then: we expect to get table 6 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 1);
        assertEquals(apiResponseResponseEntity.getBody().getMessage(), "Unfortunately there is not empty space now." +
                " This group is first in the line.");
        assertEquals(apiResponseResponseEntity.getBody().getStatusCode(), 1210);
    }

    @Test
    @Order(6)
     void shouldReturnTable7WhenGroup4Leaves() {
//        given: table 7 is occupied by 6 people(group4)
//        when: group4 leave
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/leave",
                        CustomerGroup.builder()
                                .uniqueName("group4")
                                .build(), ApiResponse.class);

//        then: we expect to get table 7 available again
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 7);
        assertEquals(apiResponseResponseEntity.getBody().getStatusCode(), 1202);
    }

    @Test
    @Order(7)
    void shouldReturnTable7For6People() {
//        given: group4 left and now table 7 is available for group5
//        and: this is the same group which was in the line.

//        when: a group of 6 people come in
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .peopleNumber(6)
                                .uniqueName("group5")
                                .build(), ApiResponse.class);

//        then: we expect to get table 6 for them
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 7);
    }

    @Test
    @Order(8)
    void shouldReturnProperMessgeIfGroup4LeaveTwice() {
//        given: table 7 is occupied by 6 people(group4)

//        when: group4 leave
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/leave",
                        CustomerGroup.builder()
                                .uniqueName("group4")
                                .build(), ApiResponse.class);

//        then: we expect to get table 7 available again
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getMessage(), "Group has already gone.");
    }

    @Test
    @Order(9)
    void shouldReturnYouAreInLine() {
//        given: There is not enough space for 6 people

//        when: a group of 6 arrive

        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .uniqueName("group6")
                                .peopleNumber(6)
                                .build(), ApiResponse.class);

//        then: we get proper message and give them first number in line
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 1);
        assertEquals(apiResponseResponseEntity.getBody().getMessage(), "Unfortunately there is not empty space now." +
                " This group is first in the line.");
        assertEquals(apiResponseResponseEntity.getBody().getStatusCode(), 1210);
    }

    @Test
    @Order(10)
    void shouldReturnYouAre2ndInLine() {
//        given: There is not enough space for 6 people

//        when: second group of 6 arrive
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/arrive",
                        CustomerGroup.builder()
                                .uniqueName("group7")
                                .peopleNumber(6)
                                .build(), ApiResponse.class);

//        then: we get proper message and give them 2nd number in line
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 2);
        assertEquals(apiResponseResponseEntity.getBody().getMessage(), "There are 1 groups ahead of you");
        assertEquals(apiResponseResponseEntity.getBody().getStatusCode(), 1210);
    }

    @Test
    @Order(11)
    void locatingGroup2ShouldReturnTable2() {
//        given: group2 is still on table 2 from shouldReturnTable2For2People tests and they have not left yet.

//        when: we check for their table number
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/locate",
                        CustomerGroup.builder()
                                .uniqueName("group2")
                                .build(), ApiResponse.class);

//        then:we get 2 as table number
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), 2);

    }

    @Test
    @Order(12)
    void locatingGroup4ShouldReturnNull() {
//        given: group2 is still on table 2 from shouldReturnTable2For2People tests and they have not left yet.

//        when: we check for their table number
        ResponseEntity<ApiResponse> apiResponseResponseEntity = testRestTemplate
                .postForEntity("http://localhost:" + port + "/locate",
                        CustomerGroup.builder()
                                .uniqueName("group4")
                                .build(), ApiResponse.class);

//        then:we get 2 as table number
        assertEquals(apiResponseResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponseResponseEntity.getBody().getData(), null);

    }


}
