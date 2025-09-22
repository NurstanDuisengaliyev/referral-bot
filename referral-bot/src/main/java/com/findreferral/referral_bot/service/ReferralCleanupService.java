package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.repository.ReferralRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReferralCleanupService {
    private final ReferralRepository referralRepository;

    @Scheduled(cron = "0 0 0 * * SUN,WED", zone = "Asia/Almaty")
    @Transactional
    public void cleanupExpiredReferrals() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(5);
        referralRepository.deleteAllExpired(threshold);
    }

}
