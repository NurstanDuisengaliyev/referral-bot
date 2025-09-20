package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.repository.ReferralRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReferralService {

    private ReferralRepository referralRepository;

    public void save(Referral referral) {
        referralRepository.save(referral);
    }

    public void createReferral(Referral referral) {
        referral.setStatus(Referral.ReferralStatus.PENDING);
        referral.setCreated_at(LocalDateTime.now());
        referral.setExpires_at(LocalDateTime.now().plusWeeks(1)); // Example expiry
    }

    public Referral updateStatus(Long referralId, Referral.ReferralStatus status) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new RuntimeException("Referral not found"));
        referral.setStatus(status);
        return referralRepository.save(referral);
    }

}
