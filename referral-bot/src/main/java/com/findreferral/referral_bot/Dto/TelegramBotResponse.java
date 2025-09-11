package com.findreferral.referral_bot.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramBotResponse {
    private String text = null;
    private InlineKeyboardMarkup replyMarkup = null;
}
