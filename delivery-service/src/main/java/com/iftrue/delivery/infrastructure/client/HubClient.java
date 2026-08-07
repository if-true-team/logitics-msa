package com.iftrue.delivery.infrastructure.client;

import com.iftrue.delivery.infrastructure.client.dto.HubRouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/v1/internal/hub-routes/path")
    HubRouteResponse getShortestRoute(
            @RequestParam("departureHubId") String departureHubId,
            @RequestParam("arrivalHubId") String arrivalHubId
    );
}
