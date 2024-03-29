package com.project.anyahajo.auth;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
class EmailService implements EmailSender{

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void send(String to, String email, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("dd@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("** failed to send email\n" + e.getMessage());
            throw new IllegalStateException("failed to send email");
        }
    }

}
