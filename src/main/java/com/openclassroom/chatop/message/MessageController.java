package com.openclassroom.chatop.message;

import com.openclassroom.chatop.message.dtos.MessageRequestDto;
import com.openclassroom.chatop.message.dtos.MessageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Envoyer un message au propriétaire d'une location")
    @ApiResponse(responseCode = "201", description = "Message envoyé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    @ApiResponse(responseCode = "404", description = "Location ou utilisateur introuvable")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(
            @Valid @RequestBody MessageRequestDto request,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        MessageResponseDto result = messageService.create(request, currentUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
