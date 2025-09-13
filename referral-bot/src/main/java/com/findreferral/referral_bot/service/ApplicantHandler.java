package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.Applicant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class ApplicantHandler {

    private final ApplicantService applicantService;
    private final UserService userService;
    private final FileService fileService;

    public TelegramBotResponse process (Applicant applicant, Update update) {

        switch (applicant.getCurrentState()) {

            case REGISTERING_APPLICANT_NAME -> {
                String text = update.getMessage().getText();

                applicant.getUser().setName(text);
                userService.saveUser(applicant.getUser());

                applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_CV);
                applicantService.saveApplicant(applicant);

                String botResponseText = "Upload your CV file here. Supported formats: PDF, DOCX.";

                return new TelegramBotResponse(botResponseText, null);
            }
            case REGISTERING_APPLICANT_CV -> {
                Document document = update.getMessage().getDocument();
                if (document == null) {
                    return new TelegramBotResponse("Please upload a CV file (PDF or DOCX).", null);
                }

                try {
                    Path filePath = fileService.downloadAndSaveCv(
                            document.getFileId(),
                            document.getFileName()
                    );

                    applicant.setCvPath(filePath.toString());
                    applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_SKILLS);
                    applicantService.saveApplicant(applicant);

                    String botResponseText = "Your CV has been saved ✅\n" +
                            "Now, tell us briefly about your key skills, and why you deserve a referral.\n" +
                            "Please keep it under 500 characters (≈3–5 sentences).";

                    return new TelegramBotResponse(botResponseText, null);

                } catch (Exception e) {
                    return new TelegramBotResponse(
                            "❌ Sorry, something went wrong while saving your CV.\n" +
                                    "💡 Please try again, and if the file is too large, compress it before sending.",
                            null
                    );
                }
            }
            case REGISTERING_APPLICANT_SKILLS -> {
                String text = update.getMessage().getText();

                if (text.length() > 500) {
                    return new TelegramBotResponse(
                            "Your answer is too long. Please keep it under 500 characters (≈3–5 sentences).",
                            null
                    );
                }

                applicant.setSkills(text);
                applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_COMPANIES);
                applicantService.saveApplicant(applicant);

                String botResponseText = "";

                // TODO: Come up with a logic for choosing several companies.

                return new TelegramBotResponse(botResponseText, null);
            }
            case REGISTERING_APPLICANT_COMPANIES -> {}
            case APPLYING_FOR_REFERRAL -> {}
            case WAITING_FOR_REFERRAL -> {}
            case NONE -> {}
            default -> {}

        }

    }

}
