package com.bankingsystem.service.impl;

import com.bankingsystem.dto.FeeCalculationDTO;
import com.bankingsystem.entity.SystemSetting;
import com.bankingsystem.repository.SystemSettingRepository;
import com.bankingsystem.service.SystemSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingServiceImpl(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public String getSettingValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public double getDoubleSetting(String key, double defaultValue) {
        String val = getSettingValue(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBigDecimalSetting(String key, BigDecimal defaultValue) {
        String val = getSettingValue(key, defaultValue != null ? defaultValue.toPlainString() : "0.00");
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    @Transactional
    public void updateSetting(String key, String value) {
        systemSettingRepository.findBySettingKey(key).ifPresentOrElse(setting -> {
            setting.setSettingValue(value);
            systemSettingRepository.save(setting);
        }, () -> {
            SystemSetting newSetting = new SystemSetting(key, value, "System Setting: " + key, "GENERAL");
            systemSettingRepository.save(newSetting);
        });
    }

    @Override
    @Transactional
    public void updateSettings(Map<String, String> settingsMap) {
        if (settingsMap != null) {
            settingsMap.forEach(this::updateSetting);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public FeeCalculationDTO calculateFeeAndTax(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new FeeCalculationDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, 0.0);
        }

        double feePct = getDoubleSetting("TRANSFER_FEE_PERCENT", 0.25);
        double taxPct = getDoubleSetting("SERVICE_TAX_PERCENT", 0.10);

        BigDecimal feeMultiplier = BigDecimal.valueOf(feePct).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal taxMultiplier = BigDecimal.valueOf(taxPct).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        BigDecimal serviceFee = amount.multiply(feeMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = amount.multiply(taxMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDebit = amount.add(serviceFee).add(tax);

        return new FeeCalculationDTO(amount, serviceFee, tax, totalDebit, feePct, taxPct);
    }
}
