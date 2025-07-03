package com.sparta.spartatigers.domain.alarm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeafController {

    @GetMapping("/test")
    public String testPage() {
        return "test"; // templates/test.html
    }
}
