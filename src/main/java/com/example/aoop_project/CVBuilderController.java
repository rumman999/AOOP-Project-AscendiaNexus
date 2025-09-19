package com.example.aoop_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CVBuilderController {

    @FXML private TextField fullNameField;
    @FXML private TextField jobTitleField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField linkedinField;
    @FXML private TextArea summaryArea;
    @FXML private TextField skillsField;
    @FXML private TextArea experienceArea;
    @FXML private TextArea educationArea;

    

    @FXML
    void Reset(ActionEvent event) {
        fullNameField.clear();
        jobTitleField.clear();
        emailField.clear();
        phoneField.clear();
        locationField.clear();
        linkedinField.clear();
        summaryArea.clear();
        skillsField.clear();
        experienceArea.clear();
        educationArea.clear();
    }

    PDType1Font helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    PDType1Font helveticaBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    @FXML
    void Submit(ActionEvent event) {
        String fullName = fullNameField.getText().isEmpty() ? "Your Name" : fullNameField.getText();
        String jobTitle = jobTitleField.getText().isEmpty() ? "Professional Title" : jobTitleField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String location = locationField.getText();
        String linkedin = linkedinField.getText();
        String summary = summaryArea.getText();
        String skills = skillsField.getText();
        String experience = experienceArea.getText();
        String education = educationArea.getText();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Colors
                Color primaryBlue = new Color(49, 130, 206);
                Color darkBlue = new Color(26, 54, 93);
                Color lightGray = new Color(247, 250, 252);
                Color accentBlue = new Color(66, 153, 225);

                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();
                float margin = 50;
                float contentWidth = pageWidth - 2 * margin;
                float yPosition = pageHeight - margin;

                // Header background
                contentStream.setNonStrokingColor(primaryBlue);
                contentStream.addRect(0, yPosition - 90, pageWidth, 90);
                contentStream.fill();

                // Full name
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.setFont(helveticaBold, 30);
                float nameWidth = helveticaBold.getStringWidth(fullName) / 1000 * 30;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - nameWidth) / 2, yPosition - 40);
                contentStream.showText(fullName);
                contentStream.endText();

                // Job title
                contentStream.setFont(helvetica, 16);
                float jobWidth = helvetica.getStringWidth(jobTitle) / 1000 * 16;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - jobWidth) / 2, yPosition - 65);
                contentStream.showText(jobTitle);
                contentStream.endText();

                yPosition -= 115;

                // Contact info background
                contentStream.setNonStrokingColor(lightGray);
                contentStream.addRect(margin, yPosition - 100, contentWidth, 100);
                contentStream.fill();

                // Contact info vertically aligned
                contentStream.setNonStrokingColor(darkBlue);
                contentStream.setFont(helvetica, 11);

                float contactY = yPosition - 25; // start position

                if (!email.isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 10, contactY);
                    contentStream.showText("Email: " + email);
                    contentStream.endText();
                    contactY -= 18;
                }
                if (!phone.isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 10, contactY);
                    contentStream.showText("Phone: " + phone);
                    contentStream.endText();
                    contactY -= 18;
                }
                if (!location.isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 10, contactY);
                    contentStream.showText("Location: " + location);
                    contentStream.endText();
                    contactY -= 18;
                }
                if (!linkedin.isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 10, contactY);
                    contentStream.showText("LinkedIn: " + linkedin);
                    contentStream.endText();
                    contactY -= 18;
                }

                yPosition -= 135;

                // Sections
                if (!summary.isEmpty())
                    yPosition = drawSection(contentStream, "PROFESSIONAL SUMMARY", summary,
                            margin, yPosition, contentWidth, primaryBlue, darkBlue, helvetica, 12);

                if (!skills.isEmpty())
                    yPosition = drawSkillsSection(contentStream, skills, margin, yPosition,
                            contentWidth, primaryBlue, accentBlue, Color.WHITE);

                if (!experience.isEmpty())
                    yPosition = drawSection(contentStream, "WORK EXPERIENCE", experience,
                            margin, yPosition, contentWidth, primaryBlue, darkBlue, helvetica, 12);

                if (!education.isEmpty())
                    yPosition = drawSection(contentStream, "EDUCATION", education,
                            margin, yPosition, contentWidth, primaryBlue, darkBlue, helvetica, 12);
            }

            String home = System.getProperty("user.home");
            File pdfFile = new File(home, "Professional_CV_" + fullName.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf");
            document.save(pdfFile);
            System.out.println("CV saved: " + pdfFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float drawSection(PDPageContentStream contentStream, String title, String content,
                              float x, float y, float width, Color titleColor, Color textColor,
                              PDFont font, float fontSize) throws IOException {

        contentStream.setNonStrokingColor(titleColor);
        contentStream.setFont(helveticaBold, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(title);
        contentStream.endText();

        // underline
        contentStream.setStrokingColor(titleColor);
        contentStream.setLineWidth(2);
        contentStream.moveTo(x, y - 5);
        contentStream.lineTo(x + 200, y - 5);
        contentStream.stroke();

        y -= 28;

        contentStream.setNonStrokingColor(textColor);
        contentStream.setFont(font, fontSize);

        for (String line : wrapText(content, font, fontSize, width - 30)) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x + 15, y);
            contentStream.showText(line);
            contentStream.endText();
            y -= (fontSize + 3);
        }
        return y - 35;
    }

    private float drawSkillsSection(PDPageContentStream contentStream, String skills,
                                    float x, float y, float width, Color titleColor,
                                    Color chipColor, Color textColor) throws IOException {

        contentStream.setNonStrokingColor(titleColor);
        contentStream.setFont(helveticaBold, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText("TECHNICAL SKILLS");
        contentStream.endText();

        contentStream.setStrokingColor(titleColor);
        contentStream.setLineWidth(2);
        contentStream.moveTo(x, y - 5);
        contentStream.lineTo(x + 200, y - 5);
        contentStream.stroke();

        y -= 35;

        String[] skillArray = skills.split(",");
        float chipX = x + 10;
        float chipY = y;
        float maxChipWidth = width - 20;

        for (String skill : skillArray) {
            skill = skill.trim();
            if (!skill.isEmpty()) {
                float skillWidth = helvetica.getStringWidth(skill) / 1000 * 11 + 20;
                if (chipX + skillWidth > x + maxChipWidth) {
                    chipX = x + 10;
                    chipY -= 28;
                }

                contentStream.setNonStrokingColor(chipColor);
                contentStream.addRect(chipX, chipY - 15, skillWidth, 20);
                contentStream.fill();

                contentStream.setNonStrokingColor(textColor);
                contentStream.setFont(helvetica, 11);
                contentStream.beginText();
                contentStream.newLineAtOffset(chipX + 7, chipY - 11);
                contentStream.showText(skill);
                contentStream.endText();

                chipX += skillWidth + 12;
            }
        }

        return chipY - 45;
    }

    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return lines;

        for (String paragraph : text.split("\n")) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split("\\s+")) {
                String testLine = line.length() == 0 ? word : line + " " + word;
                if (font.getStringWidth(testLine) / 1000 * fontSize <= maxWidth) {
                    line = new StringBuilder(testLine);
                } else {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                }
            }
            if (line.length() > 0) lines.add(line.toString());
        }
        return lines;
    }

    @FXML
    private void handleDashboard(ActionEvent e){
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
