package com.pakit.kidscafe.repository;

import com.pakit.kidscafe.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByIsRepliedFalseOrderByCreatedAtDesc();

    List<Contact> findByPhoneOrderByCreatedAtDesc(String phone);
}