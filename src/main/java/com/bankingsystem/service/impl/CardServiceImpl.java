package com.bankingsystem.service.impl;

import com.bankingsystem.dto.CardLimitRequest;
import com.bankingsystem.dto.CardRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.exception.ResourceNotFoundException;
import com.bankingsystem.repository.CardRepository;
import com.bankingsystem.service.AuditLogService;
import com.bankingsystem.service.CardService;
import com.bankingsystem.service.NotificationService;
import com.bankingsystem.util.GeneratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public CardServiceImpl(CardRepository cardRepository,
                           NotificationService notificationService,
                           AuditLogService auditLogService) {
        this.cardRepository = cardRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Card requestCard(User user, CardRequest request, String ipAddress) {
        Account account = user.getAccount();
        if (account == null) {
            throw new ResourceNotFoundException("No active account associated with user.");
        }

        Card card = new Card();
        card.setUser(user);
        card.setAccount(account);
        card.setCardType(request.getCardType() != null ? request.getCardType() : CardType.DEBIT);
        card.setCardNumberMasked(GeneratorUtils.generateCardNumberMasked());
        card.setCardHolderName(user.getFullName().toUpperCase());
        card.setExpiryDate(GeneratorUtils.generateExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setCardNetwork(request.getCardNetwork() != null ? request.getCardNetwork() : "VISA");

        BigDecimal limit = request.getSpendingLimit() != null ? request.getSpendingLimit() : new BigDecimal("5000.00");
        card.setSpendingLimit(limit);
        card.setDailyLimit(limit.multiply(new BigDecimal("0.4")).setScale(2, RoundingMode.HALF_UP));

        Card savedCard = cardRepository.save(card);

        auditLogService.log(user.getEmail(), AuditAction.CARD_REQUESTED,
                "Issued new " + savedCard.getCardType() + " card (" + savedCard.getCardNumberMasked() + ")", ipAddress, "SUCCESS");
        notificationService.createNotification(user, "New Card Issued",
                "Your " + savedCard.getCardType() + " card (" + savedCard.getCardNumberMasked() + ") has been activated successfully.", "SUCCESS", "/customer/cards");

        return savedCard;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Card> getCardsForUser(User user) {
        return cardRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Card> getCardByIdAndUser(Long id, User user) {
        return cardRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public void toggleCardStatus(User user, Long cardId, CardStatus newStatus, String ipAddress) {
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found."));

        card.setStatus(newStatus);
        cardRepository.save(card);

        AuditAction action = newStatus == CardStatus.BLOCKED ? AuditAction.CARD_BLOCKED : AuditAction.CARD_UNBLOCKED;
        auditLogService.log(user.getEmail(), action,
                "Card " + card.getCardNumberMasked() + " status changed to " + newStatus, ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Card Status Changed",
                "Card " + card.getCardNumberMasked() + " is now " + newStatus + ".",
                newStatus == CardStatus.ACTIVE ? "SUCCESS" : "WARNING", "/customer/cards");
    }

    @Override
    @Transactional
    public void updateCardLimits(User user, CardLimitRequest request, String ipAddress) {
        Card card = cardRepository.findByIdAndUser(request.getCardId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found."));

        card.setSpendingLimit(request.getSpendingLimit());
        card.setDailyLimit(request.getDailyLimit());
        cardRepository.save(card);

        auditLogService.log(user.getEmail(), AuditAction.CARD_LIMIT_UPDATED,
                "Updated limits for card " + card.getCardNumberMasked() + " (Spending: $" + request.getSpendingLimit() + ", Daily: $" + request.getDailyLimit() + ")",
                ipAddress, "SUCCESS");
        notificationService.createNotification(user, "Card Limits Updated",
                "Your spending limit for card " + card.getCardNumberMasked() + " has been updated.", "INFO", "/customer/cards");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getAllCardsAdmin(Pageable pageable) {
        return cardRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    @Transactional
    public void adminUpdateCardStatus(Long cardId, CardStatus status, String adminEmail, String ipAddress) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        card.setStatus(status);
        cardRepository.save(card);

        AuditAction action = status == CardStatus.BLOCKED ? AuditAction.CARD_BLOCKED : AuditAction.CARD_UNBLOCKED;
        auditLogService.log(adminEmail, action, "Admin updated card " + card.getCardNumberMasked() + " status to " + status, ipAddress, "SUCCESS");

        if (card.getUser() != null) {
            notificationService.createNotification(card.getUser(), "Card Status Notice",
                    "Your card " + card.getCardNumberMasked() + " was set to " + status + " by administrator.", "WARNING", "/customer/cards");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveCards() {
        return cardRepository.countByStatus(CardStatus.ACTIVE);
    }
}
