# TalentScout AI

## AI Talent Scouting & Engagement Agent

TalentScout AI helps recruiters reduce manual screening effort by automatically analyzing job descriptions, ranking candidates, estimating candidate interest, and generating personalized outreach messages.

Built as an intelligent recruiter assistant, the platform enables hiring teams to move faster with data-backed shortlists.

---

# Problem Statement

Recruiters spend hours:

- Reviewing resumes manually
- Matching candidates to job requirements
- Prioritizing top applicants
- Reaching out individually
- Measuring candidate interest

TalentScout AI automates this workflow using AI-driven candidate evaluation.

---

# Key Features

## Smart Job Description Parsing

Extracts required:

- Skills
- Experience
- Role expectations

## AI Candidate Ranking

Every candidate receives:

- Match Score (0–100)
- Interest Score (0–100)
- Final Weighted Score

## Explainable AI Decisions

Each ranked result includes a short reason explaining why the candidate matches (or doesn't).

## Personalized Outreach

Generates recruiter-ready outreach messages and email drafts automatically.

## AI Reliability Layer

If Gemini API becomes unavailable, the system automatically switches to a fallback scoring engine.

## Dashboard UI

Premium recruiter dashboard with:

- Top candidate insights
- Metrics cards
- Ranked shortlist
- Clickable emails
- Loading states

---

# Tech Stack

## Backend

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate

## Frontend

- HTML
- CSS
- Bootstrap 5
- Thymeleaf

## Database

- MySQL

## AI Integration

- Google Gemini API

---

# Project Workflow

1. Recruiter enters Job Title + Job Description
2. AI parses requirements
3. Candidates fetched from database
4. Each candidate evaluated on Match + Interest
5. Final score generated
6. Ranked shortlist displayed
7. Outreach email drafts created instantly

---

# Scoring Logic

## Final Score Formula

```text
Final Score = (0.85 × Match Score) + (0.15 × Interest Score)
