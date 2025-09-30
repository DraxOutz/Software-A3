package main.java; // Define o pacote onde essa classe está. Isso ajuda a organizar o código em pastas.

import javax.mail.*; // classes principais para envio de email.
import javax.mail.internet.*; // classes para emails com HTML, anexos, etc.
import java.net.InetAddress; // Pega informações de rede, como o IP local.
import java.time.LocalDateTime; // trabalham com data e hora formatadas.
import java.time.format.DateTimeFormatter; // trabalham com data e hora formatadas.
import java.util.Properties; // Configuração de parâmetros para o servidor de email (SMTP).

public class SendEmail { // Define a classe SendEmail. O código fica organizado dentro dela.

    public static void Send(String email, String code) {
        //email = destinatário.
        //code = código de verificação que será enviado.

        //Email do nosso software para nossos clientes receberem mensagens
        String to = email;
        String from = "X@gmail.com"; // email !!!PORFAVOR NÃO COLOQUE O EMAIL NO GITHUB 
        String password = "X X X X"; // senha de app do Gmail !!!PORFAVOR NÃO COLOQUE A SENHA NO GITHUB 

        //Define o email de destino, o email remetente e a senha de aplicativo (não é a senha real, e sim uma senha gerada no Gmail para apps).

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // exige autenticação.
        props.put("mail.smtp.starttls.enable", "true"); // usa criptografia TLS para proteger os dados.
        props.put("mail.smtp.host", "smtp.gmail.com"); // → endereço do servidor do Gmail.
        props.put("mail.smtp.port", "587"); // porta usada (587 = TLS).

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // Usa o email e a senha do remetente para garantir que só o dono da conta pode enviar.

        try {
            // Pegando IP local
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();

            // Pegando info do sistema
            String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
            String user = System.getProperty("user.name");
            String city = "São Paulo"; // pode automatizar com API externa se quiser o IP público

            // Horário atual
            LocalDateTime now = LocalDateTime.now();
            String formattedTime = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Corpo do email em HTML
            String htmlMessage = "<html>" +
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

            // Usando MimeMultipart para evitar truncamento do email
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlMessage, "text/html; charset=utf-8");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, Main.GetProgramName())); // define quem está enviando.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // define o destinatário.
            message.setSubject("Código de Verificação - " + Main.GetProgramName());//define o assunto do email.
            message.setContent(multipart); //coloca o HTML formatado.

            Transport.send(message); //Envia o email pelo servidor SMTP do Gmail.

            System.out.println("Email enviado com sucesso!"); //Mensagem de sucesso no console para confirmar que foi enviado.

        } catch (Exception e) {
            e.printStackTrace();
            //Se algo der errado (senha errada, internet caída, servidor SMTP fora do ar), o catch pega a exceção e mostra o erro no console.
        }
    }
}
