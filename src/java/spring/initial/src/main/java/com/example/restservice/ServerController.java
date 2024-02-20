package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planetnine.sessionless.SessionlessImpl;

@RestController
public class ServerController {
    
    private static final String template = "Hello!";

    @GetMapping("/register")
    public User user(@RequestParam(value = "foo") String foo) {
        String fo = new SessionlessImpl().generateUUID();
        return new User("bar", fo);
    }
}
