package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferrerRepository extends JpaRepository<Referrer, Long> {
    Referrer findByCompany(Company company);
    List<Referrer> findByCurrentState(Referrer.ReferrerState state);
}
