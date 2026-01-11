package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.client.RandomOrgClient;
import com.innowise.paymentservice.service.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomNumberServiceImpl implements RandomNumberService {

    private final RandomOrgClient randomOrgClient;

    @Override
    public int getRandomInteger(int min, int max) {
        return randomOrgClient.getRandomInteger(min, max);
    }

}
