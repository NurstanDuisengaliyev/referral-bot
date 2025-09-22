package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReferrerRepository extends JpaRepository<Referrer, Long> {
    List<Referrer> findByCurrentState(Referrer.ReferrerState state);

    @Query("""
        select r from Referrer r
        left join fetch r.referrals refs
        left join fetch refs.applicant a
        where r.user.id = :userId
        """)
    Optional<Referrer> findByUserIdWithReferrals(@Param("userId") Long userId);

    @Query("""
        select r from Referrer r
        left join fetch r.company
        where r.user.id = :userId
        """)
    Optional<Referrer> findByUserIdWithCompany(@Param("userId") Long userId);
}
