package com.cinejunction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping
    public ResponseEntity<String> test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = authentication != null ? authentication.getName() : "anonymous";
        return ResponseEntity.ok("JWT authentication successful. Authenticated principal: " + principal);
    }
}
