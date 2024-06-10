package hu.progmasters.backend.service;

import hu.progmasters.backend.domain.AppUser;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(AppUser appUser) throws MessagingException, UnsupportedEncodingException {
        String subject = "Please verify your email address";
        String senderName = "BLOGMasters";
        String mailContent = "<p>Dear, " + appUser.getUserName() + "</p>" +
                "<p>You have successfully created a <b>" + senderName +
                "</b> account with the following email address: " + appUser.getEmail() + ".</p>" +
                "<p>Please verify your email address by clicking the link below. :) LETS GO BLOGGING </p>";

        String randomCode = RandomString.make(64);
        appUser.setVerificationCode(randomCode);

        String verifyURL = "https://localhost:8080/api/appusers/profile/verify?code=" + randomCode;
        mailContent += "<a href=\"" + verifyURL + "\">VERIFY</a>";

        emailSender(subject, senderName, appUser, mailContent);

    }

    public void sendSuccessfulRegistration(AppUser appUser) throws MessagingException, UnsupportedEncodingException {
        String subject = "LETS DIGGING";
        String senderName = "BLOGMasters";
        String loginURL = "http://localhost:8080/api/appusers/login";
        String mailContent = "<p>Dear, " + appUser.getUserName() + "</p>" +
                "<p>Congratulations on your successful registration and lets digging in blogs. :) <b>" +
                senderName + "</b>!</p>" +
                "<p></p>" +
                "<p><i>Notice! After logging in, please provide your detailed information.</i></p>" +
                "<a href=\"" + loginURL + "\">" + "Log in to your " + senderName + " account</a>" +
                "<p></p>" +
                "<p>Best regards, </p>" +
                "<p>" + senderName + " group</p>";

        emailSender(subject, senderName, appUser, mailContent);
    }

    @Override
    public void deletedAccountNotification(AppUser appUser) throws MessagingException, UnsupportedEncodingException {
        String subject = "Your account has been deleted!";
        String senderName = "BLOGMasters";
        String mailContent = "<p>Dear, " + appUser.getUserName() + "</p>" +
                "<p>It seems you're about to leave our blogsite... <b>" +
                "</b> your account is not active from now on. </p>" +
                "<p>If you are not the one who deleted this account: " + appUser.getUserName() + "\n" +
                "or it has been deleted accidentally contact us at: </p>";

        String adminMail = "noreply.bloggero@gmail.com";
        mailContent += "<a href=\"" + adminMail + "\">ADMIN-MAIL</a>";

        emailSender(subject, senderName, appUser, mailContent);
    }

    private void emailSender(String subject, String senderName, AppUser appUser, String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("noreply.bloggero@gmail.com", senderName);
        helper.setTo(appUser.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);
        mailSender.send(message);
    }
}
