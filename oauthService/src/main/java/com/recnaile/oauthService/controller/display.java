package com.recnaile.oauthService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class display {

    @GetMapping("/")
    public String display(){
        return "OAuth Service";
    }
}
