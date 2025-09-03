package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

    List<Referral> findByReferrer(Referrer referrer);
    List<Referral> findByApplicant(Applicant applicant);
}
