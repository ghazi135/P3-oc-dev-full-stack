package com.openclassroom.chatop.message;

import com.openclassroom.chatop.exception.ResourceNotFoundException;
import com.openclassroom.chatop.message.dtos.MessageRequestDto;
import com.openclassroom.chatop.message.dtos.MessageResponseDto;
import com.openclassroom.chatop.rental.RentalEntity;
import com.openclassroom.chatop.rental.RentalRepository;
import com.openclassroom.chatop.user.UserEntity;
import com.openclassroom.chatop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public MessageResponseDto create(MessageRequestDto request, String currentUserEmail) {
        UserEntity sender = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RentalEntity rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        MessageEntity entity = new MessageEntity();
        entity.setUser(sender);
        entity.setRental(rental);
        entity.setMessage(request.getMessage());
        messageRepository.save(entity);

        return new MessageResponseDto("Message sent with success");
    }
}
