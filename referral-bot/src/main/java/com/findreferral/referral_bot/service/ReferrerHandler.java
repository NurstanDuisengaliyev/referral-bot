package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.Referrer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ReferrerHandler {

    public TelegramBotResponse process (Referrer Referrer, Update update) {
        return null;
    }

}
