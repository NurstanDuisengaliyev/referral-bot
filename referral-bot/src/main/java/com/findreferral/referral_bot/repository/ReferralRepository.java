package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReferralRepository extends JpaRepository<Referral, Long> {

    @Query("""
      select r
      from Referral r
      where r.referrer = :referrer
        and r.status = com.findreferral.referral_bot.entity.Referral.ReferralStatus.PENDING
        and r.expires_at > CURRENT_TIMESTAMP
      order by r.created_at asc
    """)
    List<Referral> findAllActivePending(Referrer referrer);
}
