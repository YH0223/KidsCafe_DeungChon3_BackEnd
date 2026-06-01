package com.pakit.kidscafe.repository;

import com.pakit.kidscafe.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByIsActiveTrueOrderByCreatedAtDesc();

    List<Notice> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);

    List<Notice> findAllByIsActiveTrueOrderByCreatedAtDesc();
}