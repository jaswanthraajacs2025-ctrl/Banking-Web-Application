package com.bankingsystem.service;

import com.bankingsystem.dto.CardLimitRequest;
import com.bankingsystem.dto.CardRequest;
import com.bankingsystem.entity.Card;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CardService {
    Card requestCard(User user, CardRequest request, String ipAddress);
    List<Card> getCardsForUser(User user);
    Optional<Card> getCardByIdAndUser(Long id, User user);
    void toggleCardStatus(User user, Long cardId, CardStatus newStatus, String ipAddress);
    void updateCardLimits(User user, CardLimitRequest request, String ipAddress);
    Page<Card> getAllCardsAdmin(Pageable pageable);
    void adminUpdateCardStatus(Long cardId, CardStatus status, String adminEmail, String ipAddress);
    long countActiveCards();
}
