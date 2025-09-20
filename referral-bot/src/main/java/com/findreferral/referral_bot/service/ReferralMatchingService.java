package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ReferralMatchingService {

    private final ReferralService referralService;

    @Transactional
    public List<Referral> matchApplicant(Applicant applicant) {
        List<Referral> createdReferrals = new ArrayList<>();
        final int REFERRAL_DAY_LIMIT = 7;

        for (Company company : applicant.getDesiredCompanies()) {

            List<Referrer> referrers = getReferrers(company);

            for (Referrer referrer : referrers) {
                Referral referral = new Referral();

                referral.setApplicant(applicant);
                referral.setReferrer(referrer);

                referral.setStatus(Referral.ReferralStatus.PENDING);
                referral.setCreated_at(LocalDateTime.now());
                referral.setExpires_at(LocalDateTime.now().plusDays(REFERRAL_DAY_LIMIT));

                referralService.save(referral);
                createdReferrals.add(referral);
            }

        }

        return createdReferrals;
    }

    private List<Referrer> getReferrers(Company company) {
        // TODO: Probably, will have to add some limiting logic for referrers number.
        return company.getReferrers();
    }

}
