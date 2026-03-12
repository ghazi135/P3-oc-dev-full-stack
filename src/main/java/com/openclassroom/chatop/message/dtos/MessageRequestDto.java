package com.openclassroom.chatop.message.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDto {

    @JsonProperty("rental_id")
    @NotNull
    private Integer rentalId;

    /**
     * Présent pour respecter le contrat de l'API / Mockoon,
     * mais IGNORÉ côté back : on utilise toujours l'utilisateur
     * authentifié (JWT) pour déterminer l'expéditeur réel.
     */
    @JsonProperty("user_id")
    @NotNull
    private Integer userId;

    @NotBlank
    private String message;
}
