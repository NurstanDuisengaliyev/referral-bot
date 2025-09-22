package com.findreferral.referral_bot.repository;

import com.findreferral.referral_bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByTelegramId(Long telegramId);
    User findById(long id);
}
