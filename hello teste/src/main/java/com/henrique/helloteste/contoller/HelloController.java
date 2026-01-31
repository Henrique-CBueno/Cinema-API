package com.henrique.helloteste.contoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping
    public String hello() {
        System.out.println("EU RECEBI");
        return "HELLO WORLD";
    }
}
