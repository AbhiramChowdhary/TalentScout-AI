package com.app.recruiter.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;
    private Long jobId;
    @Column(name = "candidate_name")
    private String candidateName;
    private String email;

    private double matchScore;
    private double interestScore;
    private double finalScore;

    @Column(length = 2000)
    private String reason;

    public Result() {
    }

    public Long getId() {
        return id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobId() {
        return jobId;
    }
    public String getEmail() {
		return email;
	}
    public void setEmail(String email) {
    			this.email = email;
	}    
    

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public double getInterestScore() {
        return interestScore;
    }

    public void setInterestScore(double interestScore) {
        this.interestScore = interestScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}
}