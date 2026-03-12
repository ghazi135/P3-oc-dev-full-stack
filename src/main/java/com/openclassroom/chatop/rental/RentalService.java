package com.openclassroom.chatop.rental;

import com.openclassroom.chatop.exception.ResourceNotFoundException;
import com.openclassroom.chatop.rental.dtos.RentalListResponseDto;
import com.openclassroom.chatop.rental.dtos.RentalRequestDto;
import com.openclassroom.chatop.rental.dtos.RentalResponseDto;
import com.openclassroom.chatop.user.UserEntity;
import com.openclassroom.chatop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Value("${chatop.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${chatop.upload-base-url:http://localhost:8080}")
    private String uploadBaseUrl;

    /**
     * Enregistre le fichier image et retourne l'URL publique à exposer.
     */
    private String persistImageAndGetUrl(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            throw new IllegalArgumentException("Picture is required");
        }
        try {
            String originalName = picture.getOriginalFilename();
            if (originalName == null || originalName.isBlank()) {
                originalName = "unknown";
            }
            String storedFileName = UUID.randomUUID() + "_" + originalName;
            Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
            }
            Path targetFile = baseDir.resolve(storedFileName);
            picture.transferTo(targetFile.toFile());
            return uploadBaseUrl + "/uploads/" + storedFileName;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store picture", e);
        }
    }

    @Transactional(readOnly = true)
    public RentalListResponseDto getAll() {
        List<RentalResponseDto> list = rentalRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
        return new RentalListResponseDto(list);
    }

    @Transactional(readOnly = true)
    public RentalResponseDto getById(Integer id) {
        RentalEntity entity = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        return mapToDto(entity);
    }

    @Transactional
    public RentalResponseDto create(RentalRequestDto request, String ownerEmail) {
        UserEntity owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        RentalEntity entity = new RentalEntity();
        entity.setName(request.getName());
        entity.setSurface(request.getSurface());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setOwner(owner);
        entity.setPicture(persistImageAndGetUrl(request.getPicture()));

        RentalEntity saved = rentalRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public RentalResponseDto update(Integer id, RentalRequestDto request, String ownerEmail) {
        RentalEntity entity = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        if (!entity.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this rental");
        }

        entity.setName(request.getName());
        entity.setSurface(request.getSurface());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());

        MultipartFile picture = request.getPicture();
        if (picture != null && !picture.isEmpty()) {
            entity.setPicture(persistImageAndGetUrl(picture));
        }

        RentalEntity updated = rentalRepository.save(entity);
        return mapToDto(updated);
    }

    private RentalResponseDto mapToDto(RentalEntity entity) {
        return new RentalResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getSurface(),
                entity.getPrice(),
                entity.getPicture(),
                entity.getDescription(),
                entity.getOwner().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
