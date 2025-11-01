package com.innowise.paymentservice.client;

import com.innowise.paymentservice.exception.custom.RandomOrgNotAvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class RandomOrgClient {

    private final RestClient restClient;

    @Value("${randomorg.api.url}")
    private String apiUrl;

    public int getRandomInteger(int min, int max) {
        String uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("num", 1)
                .queryParam("min", min)
                .queryParam("max", max)
                .queryParam("col", 1)
                .queryParam("base", 10)
                .queryParam("format", "plain")
                .queryParam("rnd", "new")
                .toUriString();

        try {
            String response = restClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            return Integer.parseInt(response.trim());
        } catch (Exception e) {
            throw new RandomOrgNotAvailableException("Failed to fetch random number from Random.org", e);
        }
    }
}