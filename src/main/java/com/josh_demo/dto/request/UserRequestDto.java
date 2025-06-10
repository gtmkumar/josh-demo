package com.josh_demo.dto.request;

import com.josh_demo.utility.validation.AbstractValidatable;
import com.josh_demo.utility.validation.ValidationUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestDto extends AbstractValidatable {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;

    @Override
    public List<String> validate() {
        List<String> errors = newErrorList();

        // Username validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(userName),
                "Username is required");
        ValidationUtil.addErrorIfTrue(errors,
                userName != null && userName.length() < 3,
                "Username must be at least 3 characters long");

        // Password validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(password),
                "Password is required");
        ValidationUtil.addErrorIfTrue(errors,
                password != null && password.length() < 6,
                "Password must be at least 6 characters long");

        // Name validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(firstName),
                "First name is required");
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(lastName),
                "Last name is required");

        // Email validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(email),
                "Email is required");
        ValidationUtil.addErrorIfTrue(errors,
                !ValidationUtil.isValidEmail(email),
                "Invalid email format");

        // Phone validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(phone),
                "Phone number is required");
        ValidationUtil.addErrorIfTrue(errors,
                !ValidationUtil.isValidPhone(phone),
                "Invalid phone number format");

        // Role validation
        ValidationUtil.addErrorIfTrue(errors,
                ValidationUtil.isNullOrEmpty(role),
                "Role is required");

        return errors;
    }
}