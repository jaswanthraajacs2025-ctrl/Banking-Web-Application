package com.bankingsystem.service;

import com.bankingsystem.dto.TransferRequest;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;

public interface TransferService {
    Transaction transferMoney(User senderUser, TransferRequest request, String ipAddress);
}
