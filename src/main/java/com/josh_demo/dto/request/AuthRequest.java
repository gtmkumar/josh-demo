package com.josh_demo.dto.request;

import com.josh_demo.utility.validation.AbstractValidatable;
import com.josh_demo.utility.validation.ValidationUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRequest extends AbstractValidatable {
    private String username;
    private String password;

    @Override
    public List<String> validate() {
        List<String> errors = newErrorList();

        // Username validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(username),
                "Username is required");

        // Password validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(password),
                "Password is required");

        return errors;
    }
} 