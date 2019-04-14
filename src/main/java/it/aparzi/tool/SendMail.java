package it.aparzi.tool;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.*;

public class SendMail {

    private final static String USERNAME = "XXX@gmail.com";
    private final static String PASSWORD = "XXXXXXXXXX";

    private final static String FROM = "XXXX@gmail.com";
    private final static String TO = "XXXX@gmail.com";
    private final static String SUBJECT = "Automatic Email";
    private final static String BODY = "This is a important message with attachment.";

    private final static String PATH_DIRECTORY = "C:/temp/";

    public static void main(String[] args) {
        SendMail demo = new SendMail();
        demo.sendEmail();
    }

    private void sendEmail() {
        File folder = new File(PATH_DIRECTORY);
        File[] listOfFiles = folder.listFiles();

        Optional.ofNullable(listOfFiles).ifPresent(lof -> {
            List<File> files = Arrays.asList(lof);
            files.forEach(file -> {
                try {
                    InternetAddress fromAddress = new InternetAddress(FROM);
                    InternetAddress toAddress = new InternetAddress(TO);

                    // Create an Internet mail msg.
                    MimeMessage msg = new MimeMessage(getSession());
                    msg.setFrom(fromAddress);
                    msg.setRecipient(Message.RecipientType.TO, toAddress);
                    msg.setSubject(SUBJECT);
                    msg.setSentDate(new Date());

                    // Set the email msg text.
                    MimeBodyPart messagePart = new MimeBodyPart();
                    messagePart.setText(BODY);

                    // Set the email attachment file
                    FileDataSource fileDataSource = new FileDataSource(file.getAbsolutePath());

                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(fileDataSource));
                    attachmentPart.setFileName(fileDataSource.getName());

                    // Create Multipart E-Mail.
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(messagePart);
                    multipart.addBodyPart(attachmentPart);

                    msg.setContent(multipart);

                    // Send the msg with attachment.
                    System.out.println("*************** ATTENDERE ***************");
                    Transport.send(msg);
                    System.out.println("*************** EMAIL INVIATA CORRETTAMENTE *************** \n\n");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        return session;
    }

}
