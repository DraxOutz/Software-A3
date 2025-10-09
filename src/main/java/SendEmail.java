package main.java; // Define o pacote onde essa classe está. Isso ajuda a organizar o código em pastas.

import javax.mail.*; // classes principais para envio de email.
import javax.mail.internet.*; // classes para emails com HTML, anexos, etc.
import java.net.InetAddress; // Pega informações de rede, como o IP local.
import java.time.LocalDateTime; // trabalham com data e hora formatadas.
import java.time.format.DateTimeFormatter; // trabalham com data e hora formatadas.
import java.util.Properties; // Configuração de parâmetros para o servidor de email (SMTP).

public class SendEmail { // Define a classe SendEmail. O código fica organizado dentro dela.
     
  public static void main(String[] args) {
      Send("devhexawarden@gmail.com", "Reset?");
  }

    public static void Send(String email, String code) {

          if (email == null || email.trim().isEmpty()) {
         Main.print("Email não pode ser vazio");
         return;
             }

        String to = email;
        String from = security.Password.EMAIL; 
        String password = security.Password.PASSWORDEMAIL; 
    
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
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
    
            String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
            String user = System.getProperty("user.name");
            String city = "São Paulo";
    
            LocalDateTime now = LocalDateTime.now();
            String formattedTime = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    
            String htmlMessage;
    
            if (!code.equals("Reset?")) {
                // Mensagem padrão
                htmlMessage = "<html>" +
                        "<body style='font-family:Arial,sans-serif; color:#333;'>" +
                        "<h2>" + Main.GetProgramName() + "</h2>" +
                        "<p>Olá " + user + ",</p>" +
                        "<p>Recebemos uma solicitação de verificação para sua conta.</p>" +
                        "<p>Use o código abaixo para concluir o processo:</p>" +
                        "<h1 style='color:#6200EE;'>" + code + "</h1>" +
                        "<p><strong>Atenção:</strong> este código é válido apenas por <strong>30 minutos</strong>. Após esse período, ele será invalidado.</p>" +
                        "<p>Se você não solicitou este código, por favor ignore este email.</p>" +
                        "<hr>" +
                        "<p>Informações do dispositivo:</p>" +
                        "<ul>" +
                        "<li>IP Local: " + ip + "</li>" +
                        "<li>Cidade: " + city + "</li>" +
                        "<li>Dispositivo/OS: " + os + "</li>" +
                        "<li>Horário: " + formattedTime + "</li>" +
                        "</ul>" +
                        "<br>" +
                        "<p>Atenciosamente,<br><strong>Equipe " + Main.GetProgramName() + "</strong></p>" +
                        "</body>" +
                        "</html>";
            } else {
                // Mensagem para caso de Reset
                htmlMessage = "<html>" +
                        "<body style='font-family:Arial,sans-serif; color:#333;'>" +
                        "<h2>" + Main.GetProgramName() + "</h2>" +
                        "<p>Olá " + user + ",</p>" +
                        "<p>Sua senha foi alterada recentemente.</p>" +
                        "<p>Se você fez essa alteração, pode ignorar esta mensagem.</p>" +
                        "<p>Se não reconhece essa mudança, recomendamos que altere sua senha imediatamente e revise sua segurança.</p>" +
                        "<hr>" +
                        "<p>Informações do dispositivo onde a alteração ocorreu:</p>" +
                        "<ul>" +
                        "<li>IP Local: " + ip + "</li>" +
                        "<li>Cidade: " + city + "</li>" +
                        "<li>Dispositivo/OS: " + os + "</li>" +
                        "<li>Horário: " + formattedTime + "</li>" +
                        "</ul>" +
                        "<br>" +
                        "<p>Atenciosamente,<br><strong>Equipe " + Main.GetProgramName() + "</strong></p>" +
                        "</body>" +
                        "</html>";
            }
    
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlMessage, "text/html; charset=utf-8");
    
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
    
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, Main.GetProgramName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Código de Verificação - " + Main.GetProgramName());
            message.setContent(multipart);
    
            Transport.send(message);
    
            System.out.println("Email enviado com sucesso!");
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
