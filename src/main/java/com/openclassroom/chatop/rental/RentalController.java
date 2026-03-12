package com.openclassroom.chatop.rental;

import com.openclassroom.chatop.message.dtos.MessageResponseDto;
import com.openclassroom.chatop.rental.dtos.RentalListResponseDto;
import com.openclassroom.chatop.rental.dtos.RentalRequestDto;
import com.openclassroom.chatop.rental.dtos.RentalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "Liste de toutes les locations")
    @ApiResponse(responseCode = "200", description = "Liste retournée")
    @GetMapping
    public ResponseEntity<RentalListResponseDto> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAll());
    }

    @Operation(summary = "Détail d'une location par id")
    @ApiResponse(responseCode = "200", description = "Location trouvée")
    @ApiResponse(responseCode = "404", description = "Location introuvable")
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDto> getRentalById(@PathVariable Integer id) {
        return ResponseEntity.ok(rentalService.getById(id));
    }

    @Operation(summary = "Création d'une nouvelle location")
    @ApiResponse(responseCode = "201", description = "Location créée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> createRental(
            @Valid @ModelAttribute RentalRequestDto request,
            Authentication authentication
    ) {
        String ownerEmail = authentication.getName();
        rentalService.create(request, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("Rental created !"));
    }

    @Operation(summary = "Mise à jour d'une location existante")
    @ApiResponse(responseCode = "200", description = "Location mise à jour")
    @ApiResponse(responseCode = "403", description = "Non propriétaire")
    @ApiResponse(responseCode = "404", description = "Location introuvable")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> updateRental(
            @PathVariable Integer id,
            @ModelAttribute RentalRequestDto request,
            Authentication authentication
    ) {
        String ownerEmail = authentication.getName();
        rentalService.update(id, request, ownerEmail);
        return ResponseEntity.ok(new MessageResponseDto("Rental updated !"));
    }
}
