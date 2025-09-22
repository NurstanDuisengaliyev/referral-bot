package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByName(String name);

    @Query("""
    SELECT DISTINCT c 
    FROM Company c 
    LEFT JOIN FETCH c.referrers r
    """)
    List<Company> findAllWithReferrers();
}
