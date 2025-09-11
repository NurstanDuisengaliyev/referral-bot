package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReferralRepository extends JpaRepository<Referral, Long> {}
