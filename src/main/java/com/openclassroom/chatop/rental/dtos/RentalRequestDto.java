package com.openclassroom.chatop.rental.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RentalRequestDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer surface;

    @NotNull
    @Min(1)
    private Integer price;

    @NotNull(message = "Picture is required")
    private MultipartFile picture;

    @NotBlank
    private String description;


}
