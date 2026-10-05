package com.bankingsystem.repository;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Card;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUserOrderByCreatedAtDesc(User user);

    List<Card> findByAccount(Account account);

    Optional<Card> findByIdAndUser(Long id, User user);

    long countByStatus(CardStatus status);

    Page<Card> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
