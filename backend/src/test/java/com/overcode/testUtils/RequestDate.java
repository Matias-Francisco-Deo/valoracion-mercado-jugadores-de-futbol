package com.overcode.testUtils;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
public class RequestDate {
        private volatile LocalDate date = LocalDate.now(ZoneOffset.UTC);

    }