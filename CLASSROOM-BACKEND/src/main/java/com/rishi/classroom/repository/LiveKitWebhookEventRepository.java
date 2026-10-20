package com.rishi.classroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rishi.classroom.model.LiveKitWebhookEvent;

@Repository
public interface LiveKitWebhookEventRepository extends JpaRepository<LiveKitWebhookEvent, Long> {

    Optional<LiveKitWebhookEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);
}