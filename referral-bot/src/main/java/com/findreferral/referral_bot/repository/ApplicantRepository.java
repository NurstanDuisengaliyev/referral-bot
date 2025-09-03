package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> { }
