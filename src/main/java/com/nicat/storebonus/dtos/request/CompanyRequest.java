package com.nicat.storebonus.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @Size(min = 3, message = "Name must be minimum 3 symbol")
        @Size(max = 45, message = "Name must be minimum 45 symbol")
        @NotBlank(message = "Name can not be null")
        String name,

        @NotBlank(message = "Location can not be null")
        String location
) {
}