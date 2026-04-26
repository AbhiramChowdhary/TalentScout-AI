package com.app.recruiter.controller;

import com.app.recruiter.entities.Candidate;
import com.app.recruiter.entities.Job;
import com.app.recruiter.repository.CandidateRepository;
import com.app.recruiter.repository.JobRepository;
import com.app.recruiter.service.GeminiService;
import com.app.recruiter.entities.Result;
import com.app.recruiter.repository.ResultRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class RecruiterController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private GeminiService geminiService;
    
    @Autowired
    private ResultRepository resultRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("job", new Job());
        return "index";
    }

    @PostMapping("/submitJob")
    public String submitJob(@ModelAttribute Job job, Model model) {


        if(job.getTitle() == null || job.getTitle().trim().isEmpty() ||
           job.getDescription() == null || job.getDescription().trim().isEmpty()) {

            model.addAttribute("errorTitle", "Invalid Input");
            model.addAttribute("errorMessage",
                    "Please enter both Job Title and Job Description.");

            return "error";
        }
    	
    	
        jobRepository.save(job);

     
        // Optional AI JD parsing
        String parsedJD = geminiService.askGemini(
                "Extract skills, experience and role from this JD in one short line: "
                        + job.getDescription());

        List<Candidate> candidates = candidateRepository.findAll();
        List<Map<String, Object>> rankedList = new ArrayList<>();

        for (Candidate c : candidates) {

            // AI reason + outreach prompt
        	String prompt =
        		    "You are an AI recruiter.\n" +
        		    "Job Requirements: " + parsedJD +
        		    "\nCandidate Name: " + c.getName() +
        		    "\nCandidate Skills: " + c.getSkills() +
        		    "\nExperience: " + c.getExperience() +
        		    "\nReturn ONLY in format:\n" +
        		    "MatchScore: number 0 to 100\n" +
        		    "InterestScore: number 0 to 100\n" +
        		    "Reason: one short sentence\n" +
        		    "Outreach: short recruiter message";

            String aiResponse = geminiService.askGemini(prompt);

            double matchScore = extractScore(
                    aiResponse,
                    "MatchScore",
                    calculateLocalMatch(job.getDescription(), c));

            double interestScore = extractScore(
                    aiResponse,
                    "InterestScore",
                    75);

            String reply = simulateReply();

            // Better ranking weight
            double finalScore =
                    (0.85 * matchScore) + (0.15 * interestScore);

            String reason = buildReason(aiResponse, c, matchScore);
            String emailPrompt =
                    "Write a professional recruiter email to candidate " +
                    c.getName() +
                    " for the role " + job.getTitle() +
                    ". Use this hiring reason: " + reason +
                    ". Keep email under 80 words. Friendly and professional.";

            String generatedEmail =
                    geminiService.askGemini(emailPrompt);
            String outreach = buildOutreach(aiResponse, c, job, matchScore);

            Map<String, Object> row = new HashMap<>();

            row.put("name", c.getName());
            row.put("email", c.getEmail());
            row.put("skills", c.getSkills());
            row.put("experience", c.getExperience());
            row.put("matchScore", matchScore);
            row.put("interestScore", interestScore);
            row.put("finalScore", finalScore);
            row.put("reason", reason);
            row.put("message", outreach);
            row.put("reply", reply);
            row.put("generatedEmail", generatedEmail);
            
            
            Result result = new Result();

            result.setCandidateId(c.getId());
            result.setCandidateName(c.getName());
            result.setEmail(c.getEmail());
            result.setJobId(job.getId());
            
            result.setMatchScore(matchScore);
            result.setInterestScore(interestScore);
            result.setFinalScore(finalScore);

            result.setReason(reason);

            resultRepository.save(result);

            rankedList.add(row);
            
        }

        rankedList.sort((a, b) ->
                Double.compare((Double) b.get("finalScore"),
                        (Double) a.get("finalScore")));

        model.addAttribute("results", rankedList);
        model.addAttribute("jobTitle", job.getTitle());

        model.addAttribute("aiStatus",
        	    geminiService.isFallbackUsed() ? "Fallback Mode" : "AI Live");
        return "results";
    }

    // ---------- Reliable Match Scoring ----------

    private double extractScore(String text,
            String key,
            double fallback) {

try {

int start = text.indexOf(key + ":");

if (start == -1)
return fallback;

start = start + key.length() + 1;

String num =
text.substring(start)
.trim()
.split("\\s+")[0]
.replaceAll("[^0-9.]", "");

return Double.parseDouble(num);

} catch (Exception e) {

return fallback;
}
}

	private double calculateLocalMatch(String jd, Candidate c) {

        double score = 0;

        String desc = jd.toLowerCase();
        String skills = c.getSkills().toLowerCase();

        if (desc.contains("java") && skills.contains("java")) score += 30;
        if (desc.contains("spring") && skills.contains("spring")) score += 30;
        if (desc.contains("mysql") && skills.contains("mysql")) score += 20;
        if (desc.contains("react") && skills.contains("react")) score += 15;
        if (desc.contains("python") && skills.contains("python")) score += 25;

        if (c.getExperience() >= 3) score += 20;

        return Math.min(score, 100);
    }

    // ---------- Candidate Interest ----------

    private String simulateReply() {

        String[] replies = {
                "Yes, I'm interested.",
                "Please share more details.",
                "Open to opportunities.",
                "Currently not looking."
        };

        return replies[new Random().nextInt(replies.length)];
    }

    private double getInterestScore(String reply) {

        if (reply.contains("interested")) return 90;
        if (reply.contains("details")) return 75;
        if (reply.contains("Open")) return 80;

        return 30;
    }

    // ---------- AI Text Parsing ----------

    private String extractReason(String text) {

        try {
            int start = text.indexOf("Reason:");
            int end = text.indexOf("Outreach:");

            if (start == -1)
                return "Strong candidate based on skill alignment.";

            if (end == -1)
                end = text.length();

            return text.substring(start + 7, end).trim();

        } catch (Exception e) {
            return "Strong candidate based on skill alignment.";
        }
    }

    private String extractOutreach(String text) {

        try {
            int start = text.indexOf("Outreach:");

            if (start == -1)
                return "Hi Candidate, your profile matches our opportunity.";

            return text.substring(start + 9).trim();

        } catch (Exception e) {
            return "Hi Candidate, your profile matches our opportunity.";
        }
    }
    private String buildReason(String ai, Candidate c, double score) {

        if (ai.contains("Reason:"))
            return extractReason(ai);

        if (score >= 90)
            return "Excellent fit with " + c.getSkills()
                    + " and relevant experience.";

        if (score >= 70)
            return "Good match with most required technical skills.";

        if (score >= 40)
            return "Partial fit. Some required skills are missing.";

        return "Limited alignment for this specific role.";
    }
    private String buildOutreach(String ai, Candidate c, Job job, double score) {

        if (ai.contains("Outreach:"))
            return extractOutreach(ai);

        if (score >= 70) {
            return "Hi " + c.getName()
                    + ", your profile was shortlisted for our "
                    + job.getTitle()
                    + " opportunity. Would you be open to a quick discussion?";
        }

        if (score >= 40) {
            return "Hi " + c.getName()
                    + ", we found parts of your profile relevant for our "
                    + job.getTitle()
                    + " role. Interested in learning more?";
        }

        return "Hi " + c.getName()
                + ", we reviewed your profile and would like to stay connected for future relevant opportunities.";
    }
}