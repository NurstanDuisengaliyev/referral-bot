package com.findreferral.referral_bot.Config;

import com.findreferral.referral_bot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient telegramWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.telegram.org")
                .build();
    }

    @Bean
    public TelegramClient telegramClient (@Value("${bot.token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

}
