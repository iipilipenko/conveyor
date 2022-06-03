package com.pilipenko.creditconveyor.controller;

import com.pilipenko.creditconveyor.service.ConveyorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConveyorControllerTest {


    @Mock
    private ConveyorService conveyorService;

    @InjectMocks
    ConveyorController sut;

    @Test
    void getLoanOffers() {
        //prepare
//        when(conveyorService.createLoanOffers().thenReturn());
    }

    @Test
    void getCreditDTO() {
    }
}