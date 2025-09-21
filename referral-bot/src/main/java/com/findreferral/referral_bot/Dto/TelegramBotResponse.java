package com.findreferral.referral_bot.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramBotResponse {
    private String text = null;
    private ReplyKeyboard replyKeyboard = null;
    private InputFile inputFile = null;

    public TelegramBotResponse(String text, ReplyKeyboard replyKeyboard) {
        this.text = text;
        this.replyKeyboard = replyKeyboard;
    }
}
