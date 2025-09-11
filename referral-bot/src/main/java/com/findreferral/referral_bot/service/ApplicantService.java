package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Applicant;
import com.findreferral.referral_bot.repository.ApplicantRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicantService {

    private ApplicantRepository applicantRepository;

    public Applicant saveApplicant(Applicant applicant) {
        return applicantRepository.save(applicant);
    }

    public List<Applicant> getAllApplicants() {
        return applicantRepository.findAll();
    }

    public Applicant getApplicantById(Long id) {
        return applicantRepository.findById(id).orElse(null);
    }

    public void deleteApplicant(Long id) {
        applicantRepository.deleteById(id);
    }
}
