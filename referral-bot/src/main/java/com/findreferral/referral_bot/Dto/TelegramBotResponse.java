package com.findreferral.referral_bot.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

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

    public static ReplyKeyboard createReplyKeyboard(boolean setOneTimeKeyboard,
                                             boolean setResizeKeyboard,
                                             String... buttonTexts) {
        List<KeyboardRow> keyboard = new ArrayList<>(
                List.of(new KeyboardRow(buttonTexts))
        );
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(setOneTimeKeyboard);
        replyKeyboardMarkup.setResizeKeyboard(setResizeKeyboard);
        return replyKeyboardMarkup;
    }
}
