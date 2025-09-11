package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.User;
import com.findreferral.referral_bot.repository.UserRepository;
import kotlin.random.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@AllArgsConstructor
public class TelegramBotService {

    private final UserRepository userRepository;

    public TelegramBotResponse process(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        User user = userRepository.findByTelegramId(telegramId);

        if (user == null) {
            user = new User();
            user.setTelegramId(telegramId);
            user.setRole(User.UserRole.NONE);
            user.setName(update.getMessage().getFrom().getFirstName() + ' ' + update.getMessage().getFrom().getLastName());
            userRepository.save(user);

            return null

        }

        return switch (user.getRole()) {
            case NONE -> ;
            case APPLICANT -> ;
            case REFERRER -> ;
        };

    }
}
