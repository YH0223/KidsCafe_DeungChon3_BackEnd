package com.pakit.kidscafe.service;

import com.pakit.kidscafe.dto.ContactRequest;
import com.pakit.kidscafe.entity.Contact;
import com.pakit.kidscafe.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public Contact createContact(ContactRequest request) {
        Contact contact = Contact.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .category(request.getCategory())
                .message(request.getMessage())
                .isReplied(false)
                .build();

        return contactRepository.save(contact);
    }
}