package com.overcode.persistency.repositories.impl;

import com.overcode.persistency.repositories.interfaces.DummyRepository;

public class DummyRepositoryImpl implements DummyRepository {

    @Override
    public String findValue() {
        return "dummy repository value";
    }
}
