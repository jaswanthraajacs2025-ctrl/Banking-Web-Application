package com.bankingsystem.service;

import com.bankingsystem.dto.FeeCalculationDTO;
import com.bankingsystem.entity.SystemSetting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SystemSettingService {
    String getSettingValue(String key, String defaultValue);
    double getDoubleSetting(String key, double defaultValue);
    BigDecimal getBigDecimalSetting(String key, BigDecimal defaultValue);
    void updateSetting(String key, String value);
    void updateSettings(Map<String, String> settingsMap);
    List<SystemSetting> getAllSettings();
    FeeCalculationDTO calculateFeeAndTax(BigDecimal amount);
}
