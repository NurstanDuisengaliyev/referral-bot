package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.repository.ReferrerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReferrerService {

    private ReferrerRepository referrerRepository;

    public void saveReferrer(Referrer referrer) {
        referrerRepository.save(referrer);
    }

    public Referrer findByUserIdWithReferrals(Long userId) {
        return referrerRepository.findByUserIdWithReferrals(userId).orElse(null);
    }

    public Referrer findByUserIdWithCompany(Long userId) {
        return referrerRepository.findByUserIdWithCompany(userId).orElse(null);
    }

}
