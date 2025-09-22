package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.entity.Referral;
import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.repository.ReferralRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ReferralService {

    private ReferralRepository referralRepository;

    public void save(Referral referral) {
        referralRepository.save(referral);
    }

    public Referral getFirstPendingReferral(Referrer referrer) {
        try {
            return referralRepository.findAllActivePending(referrer).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getReferralText(Referral referral) {
        Applicant applicant = referral.getApplicant();

        if (applicant.getUser().getUsername() != null) {
            return "👤 " + applicant.getUser().getName() + "\n" +
                    "\uD83D\uDCAC @" + applicant.getUser().getUsername() + "\n" +
                    "📧 " + applicant.getUser().getEmail() + "\n" +
                    applicant.getSkills() + "\n" +
                    "📄 " + applicant.getCvFileName() + "(CV uploaded)" + "\n" +
                    "⏰ Expires: " + referral.getExpires_at().toLocalDate() + "\n"
                    + "\nChoose \"Accept\" or \"Reject\"";
        }
        else {
            return "👤 " + applicant.getUser().getName() + "\n" +
                    "📧 " + applicant.getUser().getEmail() + "\n" +
                    applicant.getSkills() + "\n" +
                    "📄 " + applicant.getCvFileName() + "(CV uploaded)" + "\n" +
                    "⏰ Expires: " + referral.getExpires_at().toLocalDate() + "\n"
                    + "\nChoose \"Accept\" or \"Reject\"";
        }


    }

}
