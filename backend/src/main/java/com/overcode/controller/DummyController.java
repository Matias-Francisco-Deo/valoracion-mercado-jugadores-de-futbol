package com.overcode.controller;

import com.overcode.model.DummyModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DummyController {

    @GetMapping
    public String getDummyMessage() {
        DummyModel model = new DummyModel();

        return "";
    }
}
