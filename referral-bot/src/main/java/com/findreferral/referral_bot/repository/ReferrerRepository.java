package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferrerRepository extends JpaRepository<Referrer, Long> {}
