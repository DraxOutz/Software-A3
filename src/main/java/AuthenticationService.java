package main.java;


import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.Duration;




/**
 * Classe AuthenticationService
 * 
 * Fornece métodos para validar e autenticar credenciais de usuário,
 * incluindo validação de e-mail e verificação de senha segura.
 */
public class AuthenticationService {

    // Expressão regular para validar e-mails
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Verifica se o e-mail fornecido é válido.
     *
     * @param email O e-mail a ser validado.
     * @return true se o e-mail estiver no formato correto, false caso contrário.
     */
    public static boolean isEmailValido(String email) {
        if (email == null) return false; // retorna false se o e-mail for nulo
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches(); // verifica se o e-mail corresponde ao padrão
    }

    // Expressão regular para validar senhas seguras
    private static final String SENHA_REGEX =
            "^(?=.*[a-z])" +        // pelo menos 1 letra minúscula
            "(?=.*[A-Z])" +         // pelo menos 1 letra maiúscula
            "(?=.*\\d)" +           // pelo menos 1 número
            "(?=.*[@#$%^&+=!])" +   // pelo menos 1 caractere especial
            "(?=\\S+$)" +           // sem espaços em branco
              ".{8,64}$";                // mínimo de 8 e maximo 64 caracteres

    /**
     * Verifica se a senha fornecida é segura.
     *
     * @param senha A senha a ser validada.
     * @return true se a senha atender aos critérios de segurança, false caso contrário.
     */
    public static boolean isSenhaSegura(String senha) {
        if (senha == null) return false; // retorna false se a senha for nula
        return senha.matches(SENHA_REGEX); // verifica se a senha atende à expressão regular
    }

    /**
     * Verifica as credenciais do usuário e retorna o resultado da autenticação.
     *
     * @param email    O e-mail do usuário.
     * @param password A senha do usuário.
     * @param Check    Um booleano adicional (Utilizado para lembrar login).
     * @return Uma string indicando o resultado da validação:
     *         "InvalidEmail" - se o e-mail for inválido
     *         "InvalidPassword" - se a senha não for segura
     *         "Sucesso" - se e-mail e senha forem válidos
     */

     static long minutosRestantes;
     static long lastRegisterAttempt = 0; 
     static Boolean Verified = false;
     static String Code;
     static String LoginOuRegister;
     static boolean Logged = false;
     static LocalDateTime CodeGeneratedTime;

     static String emaill,senha;

    

     public static void SendCode() {
    // Gera um código alfanumérico de 8 caracteres
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 8; i++) {
        sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    Code = sb.toString();

    // Marca o horário de geração
    CodeGeneratedTime = LocalDateTime.now();

    Main.print("Código gerado: " + Code);

    // Envia o código por email
    SendEmail.Send(emaill, Code);
}


public static String VerifyCode(String codigoDigitado) {
    if (codigoDigitado == null || codigoDigitado.isEmpty()) {
        return "Nulo"; // Campo vazio
    }

    // Verifica se o código expirou
    Duration duration = Duration.between(CodeGeneratedTime, LocalDateTime.now());
    if (duration.toMinutes() > 30) {
        SendCode(); // gera um novo código
        return "Expirado"; // código expirou
    }

    // Compara o código digitado com o gerado
    if (codigoDigitado.equals(Code)) {
        Verified = true;

        if (LoginOuRegister.equals("Cadastro")) {
            RegistroCriar(emaill, senha, senha);
            Main.print("Usuario criado no registro.");
        } else {
            Logged = true;
            Main.print("Usuario logado no registro.");
        }

        return "Sucesso"; // Código correto
    } else {
        return "Incorreto"; // Código errado
    }
}


     public static String PegarTempoRestante(){
        String vlr = minutosRestantes+" minutos";
        return vlr;
     }

    public static String RegistroCriar(String email, String password, String password2) {
         String Result = "Nulo"; // valor inicial do resultado
         long agoratick = System.currentTimeMillis();

            if (agoratick - lastRegisterAttempt < 2000) {
        // se a última tentativa foi há menos de 2 segundos, bloqueia
        return Result = "TryAgainLater"; // você cria uma mensagem apropriada
         }

  lastRegisterAttempt = agoratick;

        if (!isEmailValido(email)) {
            Result = "InvalidEmail"; // e-mail inválido
        }

        if (!isSenhaSegura(password)) {
            Result = "InvalidPassword"; // senha inválida
        }

        if (!password.trim().equals(password2.trim())) {
            Result = "IncorretoSenha";
        }

         if (database.userExists(email)) {
             Result = "Incorrect";
         };

         if (Result.equals("Nulo") && Verified == true ) {
          database.criarUsuario(email,password);
          Result = "Sucesso";
         } else if (Result.equals("Nulo")) {
            emaill = email;
            senha = password;
            LoginOuRegister = "Cadastro";
            SendCode();
             InterfaceUI.Panel2FA(email);
         };

        return Result;
    };

    public static String LoginCheck(String email, String password, boolean Check) {
        Main.print(email + password); // imprime e-mail e senha para debug (remover em produção)

        long agoratick = System.currentTimeMillis();


        String Result = "Nulo"; // valor inicial do resultado

                if (agoratick - lastRegisterAttempt < 2000) {
        // se a última tentativa foi há menos de 2 segundos, bloqueia
        return Result = "TryAgainLater"; // você cria uma mensagem apropriada
         }

         lastRegisterAttempt = agoratick;

        if (!isEmailValido(email)) {
            Result = "InvalidEmail"; // e-mail inválido
        }

        if (!isSenhaSegura(password)) {
            Result = "InvalidPassword"; // senha inválida
        }
    
       if (isEmailValido(email) && !database.userExists(email)) {
        Result = "Incorrect";
         Main.print("email não existe.");
       } else if (database.userExists(email)) {

         int Tentativas = database.getUserTrys(email);
        LocalDateTime ultima = database.getUltimaTentativa(email);

        Main.print("User existe.");

         LocalDateTime desbloqueio = ultima.plusMinutes(30); // hora que vai desbloquear
         LocalDateTime agora = LocalDateTime.now();
          minutosRestantes = java.time.Duration.between(agora, desbloqueio).toMinutes();

         Main.print("O user tem :"+Tentativas+" tentativas restantes.");
         Main.print("Ultima tentiva de login realizado as: "+ultima);

         if (ultima.plusMinutes(30).isBefore(LocalDateTime.now())) {
             database.resetarTentativas(email);
             Tentativas = 5;
         }
         //
         if (Tentativas <=0) {
            Result = "LoginBlocked";
         };
         //
       };
        // Se ambos forem válidos e o resultado ainda estiver "Nulo"
        if (isSenhaSegura(password) && isEmailValido(email) && Result.equals("Nulo")) {
          // Se os email e senhas forem válidos, realiza a busca no banco de dados para comparar o hash da senha

           String hashArmazenado = database.getPasswordHash(email);
           String saltArmazenado = database.getUserSalt(email);

           Main.print(hashArmazenado+ " hash armazenado");
           password = Criptografia.hashPassword(password,saltArmazenado);

           Main.print(hashArmazenado+password);
          


           if (Criptografia.verifyPassword(password.trim(),hashArmazenado.trim())) {
            Result = "Sucesso"; // login bem-sucedido
             LoginOuRegister = "Login";
               emaill = email; 
             SendCode();
              InterfaceUI.Panel2FA(email);
            } else { 
                database.decrementarTentativa(email);
              Main.print("Hash incorreto.");
              Result = "Incorrect";};
        }

        return Result; // retorna o resultado final
    }
}
