package com.findreferral.referral_bot;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;

    private final TelegramClient telegramClient;

    private final TelegramBotService telegramBotService;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       TelegramBotService telegramBotService) {
        this.botToken = botToken;
        telegramClient = new OkHttpTelegramClient(getBotToken());
        this.telegramBotService = telegramBotService;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            long chat_id = update.getMessage().getChatId();

            TelegramBotResponse telegramBotResponse = telegramBotService.process(update);

            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .chatId(chat_id)
                    .text(telegramBotResponse.getText())
                    .replyMarkup(telegramBotResponse.getReplyMarkup())
                    .build();

            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }

}
