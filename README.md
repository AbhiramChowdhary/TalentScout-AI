# TalentScout AI

> 🤖 **AI-Powered Talent Scouting & Engagement Agent** | Automate candidate screening, ranking, and outreach

TalentScout AI helps recruiters reduce manual screening effort by automatically analyzing job descriptions, ranking candidates, estimating candidate interest, and generating personalized outreach messages. Built as an intelligent recruiter assistant, the platform enables hiring teams to move faster with data-backed shortlists.

**Built for:** Catalyst AI Hackathon 2026

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Project Structure](#project-structure)
- [Scoring Logic](#scoring-logic)
- [Usage Guide](#usage-guide)
- [Sample Input & Output](#sample-input--output)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Why TalentScout AI Stands Out](#why-talentscout-ai-stands-out)
- [Author](#author)
- [License](#license)

---

## Problem Statement

Recruiters spend hours on repetitive tasks:

- 📄 Reviewing resumes manually
- 🔍 Matching candidates to job requirements
- ⭐ Prioritizing top applicants
- ✉️ Reaching out individually
- 📊 Measuring candidate interest

**TalentScout AI automates this entire workflow** using AI-driven candidate evaluation, allowing recruiters to focus on relationship-building instead of admin work.

---

## Key Features

### 🧠 Smart Job Description Parsing
Automatically extracts:
- Required skills
- Experience level
- Role expectations
- Key qualifications

### 🎯 AI Candidate Ranking
Every candidate receives:
- **Match Score** (0–100): How well candidate skills align with job requirements
- **Interest Score** (0–100): Estimated probability of candidate interest in the role
- **Final Weighted Score**: Combined score for prioritization

### 💡 Explainable AI Decisions
Each ranked result includes reasoning explaining why the candidate matches (or doesn't match) the role.

### 📧 Personalized Outreach
Generates recruiter-ready:
- Outreach messages
- Email drafts
- Engagement templates

### 🔄 AI Reliability Layer
If Google Gemini API becomes unavailable, the system automatically switches to a fallback scoring engine to ensure continuous operation.

### 📊 Dashboard UI
Premium recruiter dashboard featuring:
- Top candidate insights
- Metrics and statistics cards
- Ranked shortlist with scores
- One-click email generation
- Loading states and error handling

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend** | Java, Spring Boot, Spring MVC, Spring Data JPA, Hibernate |
| **Frontend** | HTML, CSS, Bootstrap 5, Thymeleaf |
| **Database** | MySQL 8.0+ |
| **AI Integration** | Google Gemini API |
| **Build Tool** | Maven/Gradle |

---

## Project Architecture

```
┌─────────────────┐
│ Recruiter Input │ (Job Title + Job Description)
│     (Web UI)    │
└────────┬────────┘
         │
         ↓
┌─────────────────────────┐
│ Spring Boot Controller  │ (Handles HTTP requests)
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Gemini AI Service      │ (Job parsing & scoring)
│  + Fallback Engine      │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│ Candidate Database      │ (MySQL)
│ (Fetch candidates)      │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Scoring Engine         │ (Calculate scores)
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Results Dashboard      │ (Display rankings)
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│ Email Generation        │ (Create outreach)
└─────────────────────────┘
```

---

## Prerequisites

Before running TalentScout AI, ensure you have:

- **Java Development Kit (JDK)** 11 or higher
  - Verify: `java -version`
- **MySQL Server** 8.0 or higher
  - Verify: `mysql --version`
- **Maven** 3.6+ (or Gradle 7.0+)
  - Verify: `mvn -version`
- **Git** for cloning the repository
- **Google Gemini API Key** (sign up at [Google AI Studio](https://aistudio.google.com))

---

## Installation & Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/AbhiramChowdhary/TalentScout-AI.git
cd TalentScout-AI
```

### Step 2: Set Up MySQL Database

```bash
mysql -u root -p
```

Then run:

```sql
CREATE DATABASE talentscout_db;
USE talentscout_db;

-- Sample candidates table (schema will auto-create via Hibernate)
-- Ensure your application.properties configures Hibernate to create tables
```

### Step 3: Configure Environment Variables

Create a `.env` file in the project root or configure in Eclipse/IDE:

**Option A: Using `application.properties` (Recommended)**

Create or update `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/talentscout_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Gemini AI API Key
gemini.api.key=your_gemini_api_key_here

# Server Configuration
server.port=8080
```

**Option B: Using Eclipse Run Configuration**

1. Right-click project → **Run Configurations**
2. Go to **Arguments** tab
3. Add to **VM arguments**:
   ```
   -DDB_URL=jdbc:mysql://localhost:3306/talentscout_db
   -DDB_USERNAME=root
   -DDB_PASSWORD=yourpassword
   -DGEMINI_API_KEY=your_api_key
   ```

### Step 4: Build & Run Project

```bash
# Build with Maven
mvn clean install

# Run the application
mvn spring-boot:run
```

Or run directly from IDE:
- Locate and right-click `TalentscoutAiApplication.java`
- Select **Run As** → **Java Application**

### Step 5: Access Application

Open your browser and navigate to:

```
http://localhost:8080
```

You should see the TalentScout AI dashboard.

---

## Project Structure

```
TalentScout-AI/
├── src/
│   ├── main/
│   │   ├── java/com/catalyst/talentscout/
│   │   │   ├── TalentscoutAiApplication.java      # Main Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── RecruiterController.java       # Handles HTTP requests
│   │   │   ├── service/
│   │   │   │   ├── GeminiService.java             # Google Gemini API integration
│   │   │   │   ├── ScoringService.java            # Scoring logic & ranking
│   │   │   │   └── EmailService.java              # Email draft generation
│   │   │   ├── repository/
│   │   │   │   ├── CandidateRepository.java       # Database queries
│   │   │   │   └── JobRepository.java             # Job data persistence
│   │   │   ├── model/
│   │   │   │   ├── Candidate.java                 # Candidate entity
│   │   │   │   ├── Job.java                       # Job entity
│   │   │   │   └── ScoringResult.java             # Scoring result DTO
│   │   │   └── util/
│   │   │       └── FallbackScoringEngine.java     # Fallback AI engine
│   │   └── resources/
│   │       ├── application.properties             # Configuration file
│   │       ├── templates/
│   │       │   ├── index.html                     # Main dashboard
│   │       │   ├── results.html                   # Results page
│   │       │   └── components/                    # Reusable components
│   │       └── static/
│   │           ├── css/
│   │           │   └── style.css                  # Bootstrap + custom styles
│   │           └── js/
│   │               └── script.js                  # Frontend logic
│   └── test/
│       └── java/                                  # Unit tests
├── pom.xml                                        # Maven dependencies
├── README.md                                      # This file
└── .gitignore
```

---

## Scoring Logic

### Final Score Formula

```
Final Score = (0.85 × Match Score) + (0.15 × Interest Score)
```

**Example:**
- Match Score: 80/100 (good skill alignment)
- Interest Score: 90/100 (high likelihood to engage)
- Final Score: (0.85 × 80) + (0.15 × 90) = 68 + 13.5 = **81.5/100**

### Scoring Components

| Score | Calculation | Impact |
|-------|-----------|--------|
| **Match Score** | Skill overlap + Experience level match | 85% |
| **Interest Score** | Career progression fit + Compensation alignment | 15% |
| **Final Score** | Weighted combination | Ranking |

---

## Usage Guide

### Step 1: Enter Job Details
1. Navigate to the dashboard
2. Enter **Job Title** (e.g., "Java Backend Developer")
3. Paste **Job Description** with requirements

### Step 2: Analyze Candidates
1. Click **Analyze Candidates**
2. System parses job requirements using Gemini AI
3. Scores all candidates in database against job requirements
4. Ranks candidates by Final Score

### Step 3: Review Rankings
- View ranked shortlist with explanations
- Click on each candidate to see detailed scoring breakdown
- Read AI explanations for each score

### Step 4: Generate Outreach
- Click **Generate Email** for any candidate
- Review personalized outreach message
- Copy to email client or download as draft

---

## Sample Input & Output

### Sample Input

**Job Title:** Java Backend Developer

**Job Description:**
```
Looking for experienced Java developer with:
- 3+ years backend development experience
- Spring Boot and Spring MVC expertise
- REST API design
- MySQL database design
- Microservices architecture knowledge
```

### Sample Output

| Rank | Candidate | Match | Interest | Final | Reason |
|------|-----------|-------|----------|-------|--------|
| 1 | Rahul Kumar | 78 | 92 | 80.1 | Excellent Spring Boot experience, 4 years in backend, seeking growth |
| 2 | Priya Singh | 68 | 70 | 68.3 | Good Java skills, 2.5 years experience, open to opportunities |
| 3 | Arjun Patel | 52 | 45 | 50.8 | Intermediate skills, willing to learn microservices |

---

## Troubleshooting

### Issue: MySQL Connection Error
**Error:** `java.sql.SQLException: Communications link failure`

**Solution:**
1. Verify MySQL server is running: `mysql -u root -p`
2. Check database name matches `application.properties`
3. Verify username/password are correct
4. Ensure port 3306 is not blocked

### Issue: Gemini API Key Invalid
**Error:** `403 Forbidden: Invalid API Key`

**Solution:**
1. Get a new API key from [Google AI Studio](https://aistudio.google.com)
2. Update `application.properties` with correct key
3. Restart application

### Issue: Application won't start
**Error:** `Port 8080 already in use`

**Solution:**
1. Change port in `application.properties`: `server.port=8081`
2. Or kill process using port 8080:
   - **Linux/Mac:** `lsof -ti:8080 | xargs kill -9`
   - **Windows:** `netstat -ano | findstr :8080` then `taskkill /PID <PID> /F`

### Issue: No candidates displaying
**Solution:**
1. Verify candidate data exists in database
2. Check tables were created by Hibernate
3. Run sample SQL insert to add test candidates
4. Check application logs for errors

---

## Future Enhancements

- 📄 **Resume PDF Upload** - Parse PDFs directly instead of manual entry
- 🔗 **LinkedIn Integration** - Import candidate profiles from LinkedIn
- 📅 **Interview Scheduling Agent** - Auto-schedule interviews with candidates
- 📊 **Response Tracking** - Monitor candidate responses and engagement metrics
- 🎯 **Multi-Role Recommendation** - Suggest candidates for multiple open positions simultaneously
- 🔐 **Role-Based Access Control** - Admin, recruiter, and viewer roles
- 📱 **Mobile Dashboard** - Responsive design for recruitment on-the-go
- 🌐 **Multi-Language Support** - Support for global recruitment teams

---

## Why TalentScout AI Stands Out

Unlike simple job portals, TalentScout AI acts as an **intelligent recruiter copilot** by combining:

✅ **Automated Candidate Matching** - AI-driven skill alignment  
✅ **Interest Estimation** - Predicts likelihood of candidate engagement  
✅ **Explainable AI** - Understand why scores are assigned  
✅ **Automated Engagement** - Generate personalized outreach instantly  
✅ **Reliability Layer** - Fallback engine ensures uptime  
✅ **Data-Backed Decisions** - Move faster with confidence  

---

## Author

**Built for:** Catalyst AI Hackathon 2026  
**Developer:** [Abhiram Chowdhary](https://github.com/AbhiramChowdhary)

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## Support & Contact

For issues, questions, or suggestions:
- 🐛 [Report Issues](https://github.com/AbhiramChowdhary/TalentScout-AI/issues)
- 💬 [Start a Discussion](https://github.com/AbhiramChowdhary/TalentScout-AI/discussions)

---

**Made with ❤️ for the Catalyst AI Hackathon 2026**
