package it.aparzi.tool;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.*;

public class SendMail {

    public static void main(String[] args) {
        SendMail demo = new SendMail();
        try {
            demo.sendEmail();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("********* ERRORE LETTURA PATH DIRECTORY *********");
        }
    }

    private void sendEmail() throws IOException {
        Properties extProp = getExternalProperties();
        if (Objects.isNull(extProp)) {
            System.out.println("********* NON E' STATO POSSIBILE CARICARE PROPRIETà ESTERNE *********");
            return;
        }

        System.out.print("Inserire il PATH della directory dove risiedono i file: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String path = reader.readLine();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        Optional.ofNullable(listOfFiles).ifPresent(lof -> {
            List<File> files = Arrays.asList(lof);
            files.forEach(file -> {
                try {
                    InternetAddress fromAddress = new InternetAddress(extProp.getProperty("mail.from"));
                    InternetAddress toAddress = new InternetAddress(extProp.getProperty("mail.to"));

                    // Create an Internet mail msg.
                    MimeMessage msg = new MimeMessage(getSession(extProp));
                    msg.setFrom(fromAddress);
                    msg.setRecipient(Message.RecipientType.TO, toAddress);
                    msg.setSubject(extProp.getProperty("mail.subject"));
                    msg.setSentDate(new Date());

                    // Set the email msg text.
                    MimeBodyPart messagePart = new MimeBodyPart();
                    messagePart.setText(extProp.getProperty("mail.body"));

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

    private Session getSession(Properties extProp) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(extProp.getProperty("mail.username"), extProp.getProperty("mail.password"));
            }
        });

        return session;
    }

    private Properties getExternalProperties() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
