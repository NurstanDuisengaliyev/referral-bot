package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.entity.Referrer;
import com.findreferral.referral_bot.repository.ReferrerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReferrerService {

    private ReferrerRepository referrerRepository;

    public Referrer saveReferrer(Referrer referrer) {
        return referrerRepository.save(referrer);
    }

    public Referrer findByCompany(Company company) {
        return referrerRepository.findByCompany(company);
    }

    public List<Referrer> getAllReferrers() {
        return referrerRepository.findAll();
    }

    public Referrer getReferrerById(Long Id) {
        return referrerRepository.findById(Id).orElse(null);
    }

    public void deleteReferrer(Long Id) {
        referrerRepository.deleteById(Id);
    }
}
