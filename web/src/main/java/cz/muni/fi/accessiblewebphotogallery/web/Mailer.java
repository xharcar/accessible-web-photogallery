package cz.muni.fi.accessiblewebphotogallery.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * A class for sending users e-mails
 */
public class Mailer {

    private static final String client_username = "photogallery422714@gmail.com";
    private static final String client_password = "Photo-gallery_2018";
    private static final Logger log = LogManager.getLogger();
    // throwaway account for the purpose of developing this
    // after deployment, "noreply@photogallery.com" and password goes here
    // more methods to be added if/when required

    public static boolean sendConfirmRegistrationMail(String recipientEmail, String confirmLink) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(client_username, client_password);
            }
        });
        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(client_username));
            email.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject("Confirm Registration");
            email.setText("Dear user, \r\n\r\n An account with this email address has been registered at (photogallery web address). " +
                    "To confirm your registration, please visit the following link: \r\n " + confirmLink +
                    "\r\n If this wasn't you, don't hesitate to contact us at (admin email). " +
                    "\r\n\r\n Kind regards,\r\n Your friendly neighbourhood accessible photogallery");
            // text to be modified as appropriate
            Transport.send(email);
        } catch (MessagingException e) {
            log.catching(e);
            log.error("MessagingException in Mailer.sendConfirmRegistrationMail(" + recipientEmail + "," + confirmLink + "):\r\n" + e.getMessage());
            return false;
        }
        return true;
    }

}
