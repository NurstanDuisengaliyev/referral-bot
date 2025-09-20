package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    public Company findByName(String name);
}
