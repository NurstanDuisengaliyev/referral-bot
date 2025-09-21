package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TelegramBotService {

    private final UserService userService;

    private NoneUserHandler noneUserHandler;
    private ApplicantHandler applicantHandler;
    private ReferrerHandler referrerHandler;

    public TelegramBotResponse process(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        User user = userService.findByTelegramId(telegramId);

        if (user == null) {
            user = new User();
            user.setUsername(update.getMessage().getFrom().getUserName());
            user.setTelegramId(telegramId);
            user.setChatId(update.getMessage().getChatId());
            user.setRole(User.UserRole.NONE);
            user.setName(update.getMessage().getFrom().getFirstName() + ' ' + update.getMessage().getFrom().getLastName());
            userService.saveUser(user);

            return createInitialBotResponse();
        }

        return switch (user.getRole()) {
            case NONE -> noneUserHandler.process(user, update);
            case APPLICANT -> applicantHandler.process(user.getApplicant(), update);
            case REFERRER -> referrerHandler.process(user.getReferrer(), update);
        };

    }

    private TelegramBotResponse createInitialBotResponse() {
        TelegramBotResponse response = new TelegramBotResponse();

        String initialMessage = """
                Hello! Welcome to the Referral Bot.\s
                
                You can either apply for a referral or become a referrer.\s
                
                Please choose an option below:""";


        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(NoneUserHandler.registerApplicantButtonText);
        row1.add(NoneUserHandler.registerReferrerButtonText);
        keyboard.add(row1);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        response.setText(initialMessage);
        response.setReplyKeyboard(replyKeyboardMarkup);

        return response;
    }
}
