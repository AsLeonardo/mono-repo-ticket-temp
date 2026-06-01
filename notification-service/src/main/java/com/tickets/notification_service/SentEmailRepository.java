package com.tickets.notification_service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SentEmailRepository extends JpaRepository<SentEmail, Long> {
}