package com.findreferral.referral_bot;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
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
                       TelegramClient telegramClient,
                       TelegramBotService telegramBotService) {
        this.botToken = botToken;
        this.telegramClient = telegramClient;
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
        if (update.hasMessage()) {

            long chat_id = update.getMessage().getChatId();

            TelegramBotResponse telegramBotResponse = telegramBotService.process(update);

            if (telegramBotResponse.getInputFile() == null) {
                SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text(telegramBotResponse.getText())
                        .replyMarkup(telegramBotResponse.getReplyKeyboard())
                        .build();
                try {
                    telegramClient.execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else {
                SendDocument sendDoc = new SendDocument(
                        String.valueOf(chat_id),
                        telegramBotResponse.getInputFile()
                );

                sendDoc.setCaption(telegramBotResponse.getText());
                sendDoc.setReplyMarkup(telegramBotResponse.getReplyKeyboard());

                try {
                    telegramClient.execute(sendDoc); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            // check callbackData.equals("")
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }

}
