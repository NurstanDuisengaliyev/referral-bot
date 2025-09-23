package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.Dto.TelegramBotResponse;
import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApplicantHandler {

    private final ApplicantService applicantService;
    private final UserService userService;
    private final CompanyService companyService;
    private final ReferralMatchingService referralMatchingService;
    private final ReferralService referralService;

    @Transactional
    public TelegramBotResponse process (Long userId, Update update) {

        Applicant applicant = applicantService.findByUserId(userId);

        if (applicant == null) {
            User user = userService.findById(userId);
            user.setRole(User.UserRole.NONE);
            userService.saveUser(user);

            return new TelegramBotResponse(
                    "Something went wrong!\n"
                            + "Send \\start command to re-start.",
                    null
            );
        }

        switch (applicant.getCurrentState()) {

            case REGISTERING_APPLICANT_NAME -> {
                String text = update.getMessage().getText();

                applicant.getUser().setName(text);
                userService.saveUser(applicant.getUser());

                applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_EMAIL);
                applicantService.saveApplicant(applicant);

                String botResponseText = "Write your email.";

                return new TelegramBotResponse(botResponseText, null);
            }
            case REGISTERING_APPLICANT_EMAIL -> {
                String text = update.getMessage().getText();

                applicant.getUser().setEmail(text);
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

                applicant.setCvFileName(document.getFileName());
                applicant.setCvFileId(document.getFileId());
                applicant.setCvFileUniqueId(document.getFileUniqueId());

                applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_SKILLS);
                applicantService.saveApplicant(applicant);

                String botResponseText = "Your CV has been saved ✅\n" +
                        "Now, tell us briefly about your key skills, and why you deserve a referral.\n" +
                        "Please keep it under 500 characters (≈3–5 sentences).";

                return new TelegramBotResponse(botResponseText, null);
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

                List<Company> availableCompanies = companyService.getAvailableCompanies();
                String companyNames = availableCompanies.stream()
                        .map(Company::getName)
                        .collect(Collectors.joining(", "));

                String botResponseText =
                        "Great! Now choose which companies you’d like to apply for.\n\n" +
                                "Here are the available companies:\n" +
                                companyNames + "\n\n" +
                                "👉 Please reply with the company names, separated by commas.\n\n" +
                                "Example: Google, Bloomberg, Jane Street\n\n" +
                                "Or, just type \"all\"";

                ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                        true,
                        true,
                        "all"
                );

                return new TelegramBotResponse(botResponseText, replyKeyboardMarkup);
            }
            case REGISTERING_APPLICANT_COMPANIES -> {
                applicant = applicantService.findByUserIdWithCompanies(userId);
                String text = update.getMessage().getText();

                if (text.equals("all")) {
                    text = companyService.getAvailableCompanies()
                            .stream()
                            .map(Company::getName)
                            .collect(Collectors.joining(", "));
                }

                String[] company_names = text.split(", ");
                List<Company> desired_companies = new ArrayList<>();

                for (String company_name : company_names) {
                    Company company = companyService.findByName(company_name);
                    if (company == null) {
                        continue;
                    }
                    desired_companies.add(company);
                }

                if (desired_companies.isEmpty()) {
                    return new TelegramBotResponse("Try to write company names correctly and send me again.", null);
                }

                applicant.setDesiredCompanies(desired_companies);
                applicant.setCurrentState(Applicant.ApplicantState.APPLYING_FOR_REFERRAL);
                applicantService.saveApplicant(applicant);

                String botResponseText = applicantService.getApplicantProfileSummary(applicant);

                ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                        true,
                        true,
                        "Confirm", "Restart"
                );

                return new TelegramBotResponse(botResponseText, replyKeyboardMarkup);
            }
            case APPLYING_FOR_REFERRAL -> {
                applicant = applicantService.findByUserIdWithCompanies(userId);
                String text = update.getMessage().getText();

                if (text.equals("Confirm")) {
                    List<Referral> createdReferrals = referralMatchingService.matchApplicant(applicant);

                    applicant.setCurrentState(Applicant.ApplicantState.WAITING_FOR_REFERRAL);
                    applicantService.saveApplicant(applicant);

                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            false,
                            true,
                            "check"
                    );

                    return new TelegramBotResponse(
                            "Your profile was sent to " + createdReferrals.size() + " referrers.\n"
                            + "You will be notified about the results in a week!\n"
                            + "Or, you can check the real-time results with \"check\" command!",
                            replyKeyboardMarkup
                    );
                }
                else if (text.equals("Restart")) {
                    applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_NAME);
                    applicantService.saveApplicant(applicant);
                    return new TelegramBotResponse("Okay.\nPlease enter your full name so we can create your applicant profile.", null);
                }
                else {
                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            true,
                            true,
                            "Confirm", "Restart"
                    );
                    return new TelegramBotResponse("\"Confirm\" Or \"Restart\"", replyKeyboardMarkup);
                }
            }
            case WAITING_FOR_REFERRAL -> {
                applicant = applicantService.findByUserIdWithReferrals(userId);
                List<Referral> referrals = applicant.getReferrals();
                Referral latestReferral = applicantService.getLatestReferral(applicant);
                String text = update.getMessage().getText();

                int pending = 0, rejected = 0, approved = 0;

                for (Referral referral : referrals) {
                    switch (referral.getStatus()) {
                        case PENDING -> pending++;
                        case APPROVED -> approved++;
                        case REJECTED -> rejected++;
                    }
                }

                String botResponseText = "";
                LocalDateTime now = LocalDateTime.now();

                if (latestReferral == null || now.isAfter(latestReferral.getExpires_at())) {
                    if (text.equals("apply again")) {
                        applicant.setCurrentState(Applicant.ApplicantState.REGISTERING_APPLICANT_NAME);
                        applicantService.saveApplicant(applicant);
                        return new TelegramBotResponse(
                                "Okay! Let's start all over again\n\nWhat is your full name?",
                                null);
                    }

                    botResponseText += String.format("Pending: %d, Approved: %d, Rejected: %d\n", pending, approved, rejected)
                                    + "\nAlso, you can apply again to all available companies!\n"
                                    + "Use command \"apply again\"";


                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            true,
                            true,
                            "apply again"
                    );

                    return new TelegramBotResponse(botResponseText, replyKeyboardMarkup);
                }
                else {
                    int daysBetween = (int) ChronoUnit.DAYS.between(now, latestReferral.getExpires_at());

                    botResponseText += String.format("Pending: %d, Approved: %d, Rejected: %d\n", pending, approved, rejected)
                            + "\nAlso, you can apply once again after " + daysBetween + " days!";

                    ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) TelegramBotResponse.createReplyKeyboard(
                            false,
                            true,
                            "check"
                    );

                    return new TelegramBotResponse(botResponseText, replyKeyboardMarkup);
                }
            }
            default -> {
                return new TelegramBotResponse(
                        "Something went wrong!\n"
                                + "Send \\start command to re-start.",
                        null
                );
            }

        }

    }

}
