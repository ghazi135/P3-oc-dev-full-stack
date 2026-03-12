package com.openclassroom.chatop.rental.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RentalListResponseDto {

    private List<RentalResponseDto> rentals;
}
