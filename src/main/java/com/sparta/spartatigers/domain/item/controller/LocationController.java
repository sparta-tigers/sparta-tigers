package com.sparta.spartatigers.domain.item.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import com.sparta.spartatigers.domain.item.service.LocationService;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ApiResponse<Void> createLocation(@Valid @RequestBody LocationRequestDto request) {

        locationService.createLocation(request);

        return ApiResponse.ok(null);
    }
}
