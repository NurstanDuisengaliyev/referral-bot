package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class NoneUserHandler {

    public static final String registerApplicantButtonText = "I am looking for a referral";
    public static final String registerReferrerButtonText = "I wanna be a referrer";

    private final UserService userService;
    private final ApplicantService applicantService;
    private final ReferrerService referrerService;

    public TelegramBotResponse process (User user, Update update) {
        switch (update.getMessage().getText()) {
            case registerApplicantButtonText -> {
                user.setRole(User.UserRole.APPLICANT);

                Applicant applicant = new Applicant();
                applicant.setUser(user);
                applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_NAME);
                user.setApplicant(applicant);

                userService.saveUser(user);
                applicantService.saveApplicant(applicant);

                String askApplicantNameBotText = "Please enter your full name so we can create your applicant profile.";
                return new TelegramBotResponse(askApplicantNameBotText, null);
            }
            case registerReferrerButtonText -> {
                user.setRole(User.UserRole.REFERRER);

                Referrer referrer = new Referrer();
                referrer.setUser(user);
                referrer.setCurrentState(Referrer.ReferrerState.REGISTERING_REFERRER_NAME);
                user.setReferrer(referrer);

                userService.saveUser(user);
                referrerService.saveReferrer(referrer);

                String askReferrerNameBotText = "Please enter your full name so applicants know who referred them.";
                return new TelegramBotResponse(askReferrerNameBotText, null);
            }
            default -> {
                return new TelegramBotResponse("Some Error happened, start all over again(", null);
            }
        }
    }

}
