package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDtoBooking {
    private Long id;
    @NotBlank
    private String name;
}
