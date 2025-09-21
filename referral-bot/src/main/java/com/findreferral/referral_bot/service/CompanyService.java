package com.findreferral.referral_bot.service;

import com.findreferral.referral_bot.entity.Company;
import com.findreferral.referral_bot.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    void save(Company company) {
        companyRepository.save(company);
    }

    List<Company> getAvailableCompanies() {
        return companyRepository.findAll()
                .stream()
                .filter(company -> !company.getReferrers().isEmpty())
                .toList();
    }

    List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    Company findByName(String name) {
        return companyRepository.findByName(name);
    }

}
