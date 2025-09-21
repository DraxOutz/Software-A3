package main.java;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    public static void Send(String email, String code) {

        String to = email;
        String from = "XXXXXX@gmail.com";
        String password = "XXXX XXXX XXXX XXXX"; // senha de app do Gmail

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, Main.GetProgramName())); // nome profissional
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Código de Verificação - "+Main.GetProgramName());

            // Corpo do email em HTML
            String htmlMessage = "<html>" +
                    "<body style='font-family:Arial,sans-serif; color:#333;'>" +
                    "<h2>"+Main.GetProgramName()+"</h2>" +
                    "<p>Olá,</p>" +
                    "<p>Recebemos uma solicitação de verificação para sua conta.</p>" +
                    "<p>Use o código abaixo para concluir o processo:</p>" +
                    "<h1 style='color:#6200EE;'>" + code + "</h1>" +
                    "<p><strong>Atenção:</strong> este código é válido apenas por <strong>30 minutos</strong>. Após esse período, ele será invalidado.</p>" +
                    "<p>Se você não solicitou este código, por favor ignore este email.</p>" +
                    "<br>" +
                    "<p>Atenciosamente,<br><strong>Equipe "+Main.GetProgramName()+"</strong></p>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlMessage, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email enviado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

