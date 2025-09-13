package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.repository.ReferralRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReferralService {

    private ReferralRepository referralRepository;

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
