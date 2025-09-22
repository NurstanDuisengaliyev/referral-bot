package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.repository.ApplicantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicantService {

    private ApplicantRepository applicantRepository;

    public void saveApplicant(Applicant applicant) {
        applicantRepository.save(applicant);
    }

    public Applicant findByUserIdWithCompanies(Long userId) {
        return applicantRepository.findByUserIdWithCompanies(userId).orElse(null);
    }

    public Applicant findByUserIdWithReferrals(Long userId) {
        return applicantRepository.findByUserIdWithReferrals(userId).orElse(null);
    }

    public Applicant findByUserId(Long userId) {
        return applicantRepository.findByUserId(userId).orElse(null);
    }


        public String getApplicantProfileSummary(Applicant applicant) {
        String desiredCompaniesText = applicant.getDesiredCompanies()
                .stream()
                .map(Company::getName)
                .collect(Collectors.joining(", "));

        return "Here is your profile summary. Please check if everything is correct:\n"
                + "\uD83D\uDC64 Name: " + applicant.getUser().getName() + '\n'
                + "✉️ Email: " + applicant.getUser().getEmail() + '\n'
                + "\uD83D\uDCE7 Email: " + applicant.getUser().getEmail() + '\n'
                + "\uD83D\uDCBC Desired Companies: " + desiredCompaniesText + "\n\n"
                + "\uD83D\uDCC4 CV uploaded: ✅ (file saved as " + applicant.getCvFileName() + ")\n\n"
                + "Do you confirm this profile?\n\n"
                + "Reply with:\n" +
                "✅ \"Confirm\" – if everything is correct and ready to apply\n" +
                "❌ \"Restart\" – if you want to start filling your profile again";
    }

    public Referral getLatestReferral(Applicant applicant) {
        List<Referral> referrals = applicant.getReferrals();
        Referral latestReferral = null;

        for (Referral referral : referrals) {
            if (latestReferral == null) {
                latestReferral = referral;
            }
            else {
                if (latestReferral.getCreated_at().isBefore(referral.getCreated_at())) {
                    latestReferral = referral;
                }
            }
        }
        return latestReferral;
    }
}
