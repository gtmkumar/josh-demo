package com.josh_demo.utility.validation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValidatable implements Validatable {
    @Override
    public boolean isValid() {
        return validate().isEmpty();
    }

    protected List<String> newErrorList() {
        return new ArrayList<>();
    }
} 