package com.pakit.kidscafe.controller;

import com.pakit.kidscafe.dto.ContactRequest;
import com.pakit.kidscafe.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createContact(
            @Valid @RequestBody ContactRequest request
    ) {
        contactService.createContact(request);
        return ResponseEntity.ok(Map.of("message", "문의가 접수되었습니다"));
    }
}