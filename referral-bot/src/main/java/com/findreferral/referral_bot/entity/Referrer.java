package com.findreferral.referral_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "referrers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Referrer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferrerState currentState;

    @ManyToOne
    @JoinColumn(name = "referral_id", referencedColumnName = "id")
    private Referral currentReferral;

    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Referral> referrals;

    public enum ReferrerState {
        NONE,
        REGISTERING_REFERRER_NAME,
        REGISTERING_REFERRER_COMPANY,
        VIEWING_REFERRALS,
        SELECTING_REFERRAL,
        UPDATING_REFERRAL_STATUS
    }
}
