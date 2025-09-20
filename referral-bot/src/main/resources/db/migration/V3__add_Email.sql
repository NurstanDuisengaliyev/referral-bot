-- ============================================
-- V3__add_applicant_email_state.sql
-- Add REGISTERING_APPLICANT_EMAIL to applicants.current_state check
-- ============================================

ALTER TABLE applicants
DROP CONSTRAINT applicants_current_state_check;

ALTER TABLE applicants
    ADD CONSTRAINT applicants_current_state_check
        CHECK (
            current_state IN (
                              'NONE',
                              'REGISTERING_APPLICANT_NAME',
                              'REGISTERING_APPLICANT_EMAIL',
                              'REGISTERING_APPLICANT_SKILLS',
                              'REGISTERING_APPLICANT_CV',
                              'REGISTERING_APPLICANT_COMPANIES',
                              'APPLYING_FOR_REFERRAL',
                              'WAITING_FOR_REFERRAL'
                )
            );
