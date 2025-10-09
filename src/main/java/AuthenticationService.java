package main.java;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe AuthenticationService
 *
 * ⚠️ ALTAMENTE IMPORTANTE ⚠️
 * --------------------------------------------------------
 * Esta classe controla LOGIN, REGISTRO e AUTENTICAÇÃO.
 *
 * - Nunca armazene senhas em texto puro.
 * - Sempre use hash + salt (Criptografia.hashPassword).
 * - Tokens e códigos de verificação precisam ser protegidos.
 * - Nunca faça logs com senha real em produção.
 *
 * Qualquer descuido aqui pode comprometer TODO o sistema.
 * --------------------------------------------------------
 *
 * <p>Funções principais:</p>
 * <ul>
 *   <li>Validar formato de e-mail.</li>
 *   <li>Validar se a senha atende aos critérios de segurança.</li>
 *   <li>Gerar tokens e códigos temporários (2FA).</li>
 *   <li>Gerenciar autenticação de login e cadastro.</li>
 *   <li>Controlar tentativas de login e bloqueios temporários.</li>
 *   <li>Lembrar login (persistência com Preferences).</li>
 * </ul>
 */
public class AuthenticationService {

    // DEBUG MODE - Configuração flexível
  

    // -----------------------------
    // REGEX PARA VALIDAR EMAIL
    // -----------------------------
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Verifica se o e-mail fornecido é válido.
     *
     * @param email O e-mail a ser validado.
     * @return true se o e-mail estiver no formato correto, false caso contrário.
     */
    public static boolean isEmailValido(String email) {
        if (email == null) return false;
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    // -----------------------------
    // REGEX PARA VALIDAR SENHAS
    // -----------------------------
    private static final String SENHA_REGEX =
            "^(?=.*[a-z])" +        // pelo menos 1 letra minúscula
            "(?=.*[A-Z])" +         // pelo menos 1 letra maiúscula
            "(?=.*\\d)" +           // pelo menos 1 número
            "(?=.*[@#$%^&+=!])" +   // pelo menos 1 caractere especial
            "(?=\\S+$)" +           // sem espaços em branco
            ".{8,64}$";             // mínimo 8 e máximo 64 caracteres

    /**
     * Verifica se a senha fornecida é segura.
     *
     * @param senha A senha a ser validada.
     * @return true se atender aos critérios de segurança, false caso contrário.
     */
    public static boolean isSenhaSegura(String senha) {
        if (senha == null) return false;
        return senha.matches(SENHA_REGEX);
    }

    // -----------------------------
    // VARIÁVEIS DE CONTROLE
    // -----------------------------
    private static long minutosRestantes;
    private static long lastRegisterAttempt = 0;
    private static boolean verified = false; // ✅ boolean primitivo
    private static String Code;
    private static String LoginOuRegister;
    static boolean Logged = false;
    private static LocalDateTime CodeGeneratedTime;
    private static boolean rememberMe; // ✅ boolean primitivo
    static public String TokenVerifiy;

    private static String emaill, senha;

    // Gerador de números aleatórios seguros
    private static final SecureRandom random = new SecureRandom();

    // Preferences (armazenamento local para lembrar login)
    private static final Preferences prefs = Preferences.userRoot().node("meuapp");

    /**
     * Evento para redefinir senha (em construção).
     *
     * @param email Email do usuário.
     * @return Resultado da operação.
     */
    public static String ResetPasswordEvent(String email) {
        String Result = "Nulo";
        //
        if (!isEmailValido(email) || !database.userExists(email)) {
            Result = "InvalidEmail";
        }
        //
        if (Result.equals("Nulo")) {
                 //
                 emaill = email;
                 LoginOuRegister = "ResetPassword";
                 InterfaceUI.Panel2FA(email);
                 SendCode();
                 
                 //
        };
        //
        return Result;
    }

    /**
     * Verifica se há token salvo para o usuário.
     *
     * @return true se houver token válido, false caso contrário.
     */

     public static void OpenChoseOptions() {
        int userId = database.getUserId(emaill); 
        if (!database.userHasInterests(userId)) { // ✅ boolean primitivo
        InterfaceUI.ChooseOptions();
        } else {
         Main.print("O user ja possui um interesse, prossiga a home.");
        }
     }

    public static Boolean HasTokenSaved() {
        boolean Vlr = false;

        String email = prefs.get("remember_email", null);

        if (email != null) {
            String salt = database.getUserSalt(email);

            String Token = prefs.get("remember_token", null);
            Token = Criptografia.hashPassword(Token, salt);

            String TokenArmazenado = database.getToken(email);
            TokenArmazenado = Criptografia.hashPassword(TokenArmazenado, salt);

            // Verifica se o token bate
            if (Criptografia.verifyPassword(Token, TokenArmazenado)) {
                LoginOuRegister = "Login";
                Logged = true;
                Vlr = true;
                Main.print("O usuário possui token salvo.");
                
                OpenChoseOptions();
            }
        }

        return Vlr;
    }

    /**
     * Gera e envia um código de verificação (2FA).
     * Código expira em 30 minutos.
     * Bloqueia regeneração em menos de 5 minutos.
     */
    public static void SendCode() {
        if (CodeGeneratedTime != null) {
            Duration duration = Duration.between(CodeGeneratedTime, LocalDateTime.now());
            if (duration.toMinutes() < 5) {
                Main.print("Código já gerado recentemente. Aguarde " + (5 - duration.toMinutes()) + " minutos.");
                return;
            }
        }

        // Gera código alfanumérico de 8 caracteres
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        Code = sb.toString();
        CodeGeneratedTime = LocalDateTime.now();

        // ✅ DEBUG MODE - Só mostra código se debug ativo
        
            Main.print("🔐 [DEBUG] Código gerado: " + Code);
            Main.print("⏰ [DEBUG] Expira em: " + CodeGeneratedTime.plusMinutes(30));
        

        // Envia o código por e-mail
        try {
        SendEmail.Send(emaill, Code);}
        catch (Exception e) {
           Main.print("Algum erro na api.");
        };
    }

    /**
     * Gera um token seguro de 256 bits.
     *
     * @return Token em formato Base64 URL Safe.
     */
    public static String GerarToken() {
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);

        String Token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        TokenVerifiy = Token;

        return Token;
    }

    /**
     * Verifica o código digitado pelo usuário (2FA).
     *
     * @param codigoDigitado Código inserido pelo usuário.
     * @return Resultado da verificação ("Sucesso", "Expirado", "Incorreto"...).
     */

       public static void saveInterests(List<String> interests) {
     // 1. OBTÉM O ID do usuário logado (usando o email que deve estar acessível)
     // O 'emaill' deve ser uma variável de instância ou estática acessível neste ponto.
      int userId = database.getUserId(emaill); 
    
       // 2. CHAMA o método de persistência do banco de dados (que já criamos)
       // Passando o ID e a lista de interesses.
     database.saveUserInterests(userId, interests);
        }

    public static String ResetPassword(String password1, String password2) {
     String Result = "Nulo";
     //
     if (!password1.equals(password2)) {
       Result = "IncorretoSenha";
     }
     //
     if (!isSenhaSegura(password1)) {
        Result = "InvalidPassword";
     };
     //
     if (HaveBeenPawed.VerifyPwned(password1)) { // ✅ Nome corrigido
        Result = "PwnedPassword";
     };
     //
     if (Result.equals("Nulo")  && !(emaill == null)) {
        //
        database.updateUsuario(emaill, password1);
        OpenChoseOptions();
        SendEmail.Send(emaill,"Reset?");
        //
     }
     //
     return Result;
    };

    public static String VerifyCode(String codigoDigitado) {
        if (codigoDigitado == null || codigoDigitado.isEmpty()) {
            return "Nulo"; // Campo vazio
        }

        // Verifica expiração
        Duration duration = Duration.between(CodeGeneratedTime, LocalDateTime.now());
        if (duration.toMinutes() > 30) {
            SendCode();
            return "Expirado";
        }

        // Se for login, checa tentativas - ✅ CORRIGIDO: equals()
        if ("Login".equals(LoginOuRegister)) {
            int Tentativas = database.getUserTrys(emaill);
            if (Tentativas <= 0) {
                return "LoginBlocked";
            }
        }

        // Comparação do código
        if (codigoDigitado.equals(Code)) {
            verified = true;

            // ✅ CORRIGIDO: equals() em todas as comparações
            if ("Cadastro".equals(LoginOuRegister)) {
                RegistroCriar(emaill, senha, senha);
                Main.print("Usuário criado no registro.");
                OpenChoseOptions();
            } else if ("Login".equals(LoginOuRegister)) {
                Logged = true;
                Main.print("Usuário logado.");
                OpenChoseOptions();
                // ✅ CORRIGIDO: boolean primitivo
                if (rememberMe) {
                    
                  if (TokenVerifiy == null) {
                    TokenVerifiy = database.getToken(emaill);
                  }

                    prefs.put("remember_email", emaill);
                    prefs.put("remember_token", TokenVerifiy);
                }
            } else {
                Main.print("Pedido de reset de senha efetuado.");
                InterfaceUI.ResetPasswordFrame();
            }

            return "Sucesso";
        } else {
            // ✅ CORRIGIDO: equals()
            if ("Login".equals(LoginOuRegister) || "ResetPassword".equals(LoginOuRegister)) {
                database.decrementarTentativa(emaill);
            }
            return "Incorreto";
        }
    }

    /**
     * Retorna o tempo restante de bloqueio.
     *
     * @return String com minutos restantes.
     */
    public static String PegarTempoRestante() {
        String vlr = minutosRestantes + " minutos";
        return vlr;
    }

    /**
     * Realiza o registro de um novo usuário.
     *
     * @param email    Email do usuário.
     * @param password Senha do usuário.
     * @param password2 Confirmação da senha.
     * @return Resultado da operação ("Sucesso", "InvalidEmail", etc.).
     */
    public static String RegistroCriar(String email, String password, String password2) {
        String Result = "Nulo";
        long agoratick = System.currentTimeMillis();

        if (agoratick - lastRegisterAttempt < 2000) {
            return "TryAgainLater";
        }

        lastRegisterAttempt = agoratick;

        if (!isEmailValido(email)) {
            Result = "InvalidEmail";
        }

        if (!isSenhaSegura(password)) {
            Result = "InvalidPassword";
        }

        if (!password.trim().equals(password2.trim())) {
            Result = "IncorretoSenha";
        }

        if (database.userExists(email)) {
            Result = "Incorrect";
        }

        if (HaveBeenPawed.VerifyPwned(password)) { // ✅ Nome corrigido
            Result = "PwnedPassword";
        }

        // ✅ CORRIGIDO: boolean primitivo
        if (Result.equals("Nulo") && verified) {
            database.criarUsuario(email, password);
            Result = "Sucesso";
        } else if (Result.equals("Nulo")) {
            emaill = email;
            senha = password;
            LoginOuRegister = "Cadastro";
                        InterfaceUI.Panel2FA(email);
            SendCode();
        }

        return Result;
    }

    /**
     * Realiza a verificação de login.
     *
     * @param email    Email do usuário.
     * @param password Senha digitada.
     * @param Check    Se verdadeiro, ativa a opção "lembrar login".
     * @return Resultado ("Sucesso", "InvalidEmail", "InvalidPassword", etc.).
     */
    public static String LoginCheck(String email, String password, boolean Check) {
        // ⚠️ IMPORTANTE: Nunca logar senha em produção
        Main.print("Tentativa de login para: " + email);

        long agoratick = System.currentTimeMillis();
        String Result = "Nulo"; // por padrão nulo

        if (agoratick - lastRegisterAttempt < 2000) {
            return "TryAgainLater";
        }

        lastRegisterAttempt = agoratick;

        if (!isEmailValido(email)) {
            Result = "InvalidEmail"; // verifica se o formato de email é valido
        }

        if (!isSenhaSegura(password)) {
            Result = "InvalidPassword";// verifica se o formato de senha é valido
        }

        // Verifica se usuário existe, por padrão incorret para não saber se o email ou senha é incorreto (boas práticas)
        if (isEmailValido(email) && !database.userExists(email)) {
            Result = "Incorrect";
            Main.print("Email não existe.");
        } else if (database.userExists(email)) {
            int Tentativas = database.getUserTrys(email);
            LocalDateTime ultima = database.getUltimaTentativa(email);

            Main.print("Usuário existe.");
            LocalDateTime desbloqueio = ultima.plusMinutes(30);
            LocalDateTime agora = LocalDateTime.now();
            minutosRestantes = java.time.Duration.between(agora, desbloqueio).toMinutes();

            if (ultima.plusMinutes(30).isBefore(LocalDateTime.now())) {
                database.resetarTentativas(email);
                Tentativas = 5; // seta por padrão 5 tentativas após o tempo de segurança de 30 minutos
            }

            if (Tentativas <= 0) {
                Result = "LoginBlocked"; //Se o usuario realizou todas as tentativas, o bloqueia para evitar ataques de força  bruta
            }
        }

        // Se tudo válido
        // Essas validações evitam qualquer problema com SQL Injection ou ataques de força  bruta
        if (isSenhaSegura(password) && isEmailValido(email) && Result.equals("Nulo")) {
            String hashArmazenado = database.getPasswordHash(email);
            String saltArmazenado = database.getUserSalt(email);

            password = Criptografia.hashPassword(password, saltArmazenado);

            if (Criptografia.verifyPassword(password.trim(), hashArmazenado.trim())) {
                Result = "Sucesso";
                LoginOuRegister = "Login";
                emaill = email;
                rememberMe = Check; // ✅ boolean primitivo
                                InterfaceUI.Panel2FA(email);
                SendCode();
            } else {
                database.decrementarTentativa(email);
                Main.print("Hash incorreto.");
                Result = "Incorrect";
            }
        }

        return Result;
    }
}