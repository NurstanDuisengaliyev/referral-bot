package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    // Fetch applicant + desired companies
    @Query("""
        select a from Applicant a
        left join fetch a.desiredCompanies
        where a.user.id = :userId
        """)
    Optional<Applicant> findByUserIdWithCompanies(@Param("userId") Long userId);

    // Fetch applicant + referrals
    @Query("""
        select a from Applicant a
        left join fetch a.referrals
        where a.user.id = :userId
        """)
    Optional<Applicant> findByUserIdWithReferrals(@Param("userId") Long userId);

    // Plain fetch (without relations)
    Optional<Applicant> findByUserId(Long userId);
}