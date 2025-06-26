package com.sparta.spartatigers.global.filter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class LoggingTestController {

    @PostMapping("/log")
    public ResponseEntity<String> logTest(@RequestBody String message) {
        return ResponseEntity.ok(message);
    }
}
