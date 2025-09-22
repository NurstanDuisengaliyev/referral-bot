package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.repository.ReferralRepository;
import com.findreferral.referral_bot.repository.ReferrerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ReferrerRepository referrerRepository;
    private final ReferralRepository referralRepository;
    private final TelegramClient telegramClient;
    private final UserService userService; // if you keep chatId on user, etc.

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Almaty")
    @Transactional
    public void sendDailyReferrerDigests() {
        var referrers = referrerRepository.findByCurrentStates(
                List.of(Referrer.ReferrerState.VIEWING_REFERRALS,
                        Referrer.ReferrerState.UPDATING_REFERRAL_STATUS)
        );

        for (Referrer referrer : referrers) {
            var pendingReferrals = referralRepository.findAllActivePending(referrer);
            if (pendingReferrals.isEmpty()) {
                continue;
            }

            String digest = pendingReferrals.size()
                    + " applicants are waiting for your referral!\n"
                    + "Reply with \"review\" to browse applicants one by one.\n"
                    + "Applications last " + ReferralMatchingService.REFERRAL_DAY_LIMIT + " days";

            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .chatId(referrer.getUser().getChatId())
                    .text(digest)
                    .build();

            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
