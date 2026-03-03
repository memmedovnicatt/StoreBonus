package com.nicat.storebonus.dtos.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployerRequest(
        @NotNull
        String name,

        @NotNull
        String surname,

        @NotNull()
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        @Size(min = 11, max = 70)
        String mail,

        @NotNull
        @Pattern(regexp = "^\\+994(50|51|55|70|77|99)\\d{7}$", message = "Phone number must be valid '+9940000000000'.Example: +994516125092")
        @Column(columnDefinition = "VARCHAR(20)")
        String phoneNumber,

        @NotNull
        byte age,

        @NotNull
        Long positionId
) {
}
