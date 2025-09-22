package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.User;
import com.findreferral.referral_bot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findByTelegramId (Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public User findById (Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
