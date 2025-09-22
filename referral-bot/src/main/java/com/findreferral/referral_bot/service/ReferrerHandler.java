package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.TelegramBot;
import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReferrerHandler {

    private final ReferrerService referrerService;
    private final UserService userService;
    private final CompanyService companyService;
    private final ReferralService referralService;
    private final TelegramClient telegramClient;

    @Transactional
    public TelegramBotResponse process (Long userId, Update update) {
        Referrer referrer = referrerService.findByUserIdWithCompany(userId);

        if (referrer == null) {
            return new TelegramBotResponse(
                    "Something went wrong!\n"
                            + "Send \\start command to re-start.",
                    null
            );
        }

        switch (referrer.getCurrentState()) {
            case REGISTERING_REFERRER_NAME -> {
                String text = update.getMessage().getText();

                referrer.getUser().setName(text);
                userService.saveUser(referrer.getUser());

                referrer.setCurrentState(Referrer.ReferrerState.REGISTERING_REFERRER_COMPANY);
                referrerService.saveReferrer(referrer);

                String companyNames = companyService.getAllCompanies()
                        .stream()
                        .map(Company::getName)
                        .collect(Collectors.joining(", "));

                String botResponseText = "Which company do you work at? This will link you to applicants who want referrals there.\n"
                        + "Right now, there are referrers from these companies: " + companyNames
                        + "\n\nIf you don't see your company name, just write it anyways, it will be added to the list.";

                return new TelegramBotResponse(botResponseText, null);
            }
            case REGISTERING_REFERRER_COMPANY -> {
                String text = update.getMessage().getText();

                Company company = companyService.findByName(text);
                if (company == null) {
                    company = new Company();
                    company.setName(text);
                    companyService.save(company);
                }

                referrer.setCompany(company);
                referrer.setCurrentState(Referrer.ReferrerState.VIEWING_REFERRALS);
                referrerService.saveReferrer(referrer);

                String botResponseText =
                        "✅ All set! You’ll receive a daily digest of active applicants at 12:00 (Astana).\n" +
                                "You can also type \"review\" anytime to browse candidates now.";


                ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                        true,
                        true,
                        "review"
                );

                return new TelegramBotResponse(botResponseText, replyKeyboardMarkup);
            }
            case VIEWING_REFERRALS -> {
                referrer = referrerService.findByUserIdWithReferrals(userId);
                String text = update.getMessage().getText();

                if (text.equalsIgnoreCase("review")) {
                    TelegramBotResponse botResponse = getNewReferralApplicationResponse(referrer);

                    if (referrer.getCurrentReferral() != null) {
                        referrer.setCurrentState(Referrer.ReferrerState.UPDATING_REFERRAL_STATUS);
                        referrerService.saveReferrer(referrer);
                    }

                    return botResponse;
                }
                else {
                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            true,
                            true,
                            "review"
                    );
                    return new TelegramBotResponse("Type \"review\"", replyKeyboardMarkup);
                }
            }
            case UPDATING_REFERRAL_STATUS -> {
                referrer = referrerService.findByUserIdWithReferrals(userId);
                String text = update.getMessage().getText();
                Referral currentReferral = referrer.getCurrentReferral();
                LocalDateTime now = LocalDateTime.now();

                if (currentReferral == null
                        || currentReferral.getStatus() != Referral.ReferralStatus.PENDING
                        || currentReferral.getExpires_at().isBefore(now)) {

                    referrer.setCurrentReferral(null);
                    referrer.setCurrentState(Referrer.ReferrerState.VIEWING_REFERRALS);
                    referrerService.saveReferrer(referrer);

                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            true,
                            true,
                            "review"
                    );

                    return new TelegramBotResponse(
                            "⚠️ This application expired or was already processed.\n"
                            + "Type \"review\" to browse new candidates now.",
                            replyKeyboardMarkup
                    );
                }

                if (text.equalsIgnoreCase("Accept")) {
                    currentReferral.setStatus(Referral.ReferralStatus.APPROVED);
                    notifyApplicantAboutReferral(currentReferral);
                }
                else if (text.equalsIgnoreCase("Reject")) {
                    currentReferral.setStatus(Referral.ReferralStatus.REJECTED);
                }

                referralService.save(currentReferral);
                TelegramBotResponse botResponse = getNewReferralApplicationResponse(referrer);

                if (referrer.getCurrentReferral() == null) {
                    referrer.setCurrentState(Referrer.ReferrerState.VIEWING_REFERRALS);
                    referrerService.saveReferrer(referrer);
                }

                return botResponse;
            }
            default -> {
                ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                        true,
                        true,
                        "review"
                );
                return new TelegramBotResponse("Type \"review\"", replyKeyboardMarkup);
            }
        }
    }

    public TelegramBotResponse getNewReferralApplicationResponse (Referrer referrer) {
        Referral firstPendingReferral = referralService.getFirstPendingReferral(referrer);

        if (firstPendingReferral == null) {
            referrer.setCurrentReferral(null);
            referrerService.saveReferrer(referrer);
            return new TelegramBotResponse("⚠️ You have no active application to review.\nTry to \"review\" later!", null);
        }

        referrer.setCurrentReferral(firstPendingReferral);
        referrerService.saveReferrer(referrer);

        String botResponseText = referralService.getReferralText(firstPendingReferral);

        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                false,
                true,
                "Accept", "Reject"
        );

        return new TelegramBotResponse(
                botResponseText,
                replyKeyboardMarkup,
                new InputFile(firstPendingReferral.getApplicant().getCvFileId())
        );
    }

    private void notifyApplicantAboutReferral(Referral currentReferral) {
        var applicantUser = currentReferral.getApplicant().getUser();
        var referrer = currentReferral.getReferrer();
        String notification = "✅ Your referral request was *ACCEPTED* by "
                + referrer.getUser().getName()
                + (referrer.getUser().getUsername() != null ? " (@" + referrer.getUser().getUsername() + ")" : "")
                + "!\n\n💬 Please contact them directly to discuss further details.";

        SendMessage message = SendMessage
                .builder()
                .chatId(applicantUser.getChatId())
                .text(notification)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
