package com.findreferral.referral_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applicants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    private String skills; // Json of Skills

    private String cvPath; // Url path to the CV

    @ManyToMany
    @JoinTable(
            name = "applicant_desired_companies",
            joinColumns = @JoinColumn(name = "applicant_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Company> desiredCompanies = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicantState currentState = ApplicantState.NONE;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Referral> referrals;

    public enum ApplicantState {
        NONE,
        REGISTERING_APPLICANT_NAME,
        REGISTERING_APPLICANT_SKILLS,
        REGISTERING_APPLICANT_CV,
        REGISTERING_APPLICANT_COMPANIES,
        APPLYING_FOR_REFERRAL,
        WAITING_FOR_REFERRAL,
    }
}
