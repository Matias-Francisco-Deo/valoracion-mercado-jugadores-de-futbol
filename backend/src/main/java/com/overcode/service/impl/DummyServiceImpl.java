package com.overcode.service.impl;

import com.overcode.service.interfaces.DummyService;

public class DummyServiceImpl implements DummyService {

    @Override
    public String execute() {
        return "dummy service result";
    }
}
