package hu.progmasters.backend.service;

import hu.progmasters.backend.domain.AppUser;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendEmail(AppUser appUser) throws MessagingException, UnsupportedEncodingException;

    void deletedAccountNotification(AppUser appUser) throws MessagingException, UnsupportedEncodingException;

    void sendSuccessfulRegistration(AppUser appUser) throws MessagingException, UnsupportedEncodingException;
}
