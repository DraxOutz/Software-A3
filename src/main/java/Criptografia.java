package main.java;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Criptografia {

    // Parâmetros do PBKDF2
    private static final int ITERATIONS = 65536; // mais iterações = mais seguro, mas mais lento
    private static final int KEY_LENGTH = 256;   // tamanho do hash em bits

    // Gera hash seguro da senha (usa salt)
    public static String hashPassword(String senha, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                senha.toCharArray(),
                salt.getBytes(),
                ITERATIONS,
                KEY_LENGTH
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hashBytes = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo não disponível", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    // Gera salt aleatório
    public static String gerarSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Verifica se o hash da senha digitada confere com o armazenado
    public static boolean verifyPassword(String hashDigitado, String hashArmazenado) {
        return hashDigitado.equals(hashArmazenado);
    }
}
