package main.java;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Criptografia {

     // Gera hash SHA-512 da senha (alterar para uma mais moderna)
    public static String hashPassword(String senha, String salt) {
    try {
        String senhaComSalt = salt + senha; // adiciona o salt
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashBytes = md.digest(senhaComSalt.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("SHA-512 não disponível", e);
    }
}
  
  public static String gerarSalt() {
        SecureRandom sr = new SecureRandom();   // gerador seguro
        byte[] salt = new byte[16];             // 16 bytes = 128 bits
        sr.nextBytes(salt);                     // preenche com bytes aleatórios
        return Base64.getEncoder().encodeToString(salt); // converte para String
}

    // Verifica se a senha digitada confere
   public static boolean verifyPassword(String hashDigitado, String hashArmazenado) {
    return hashDigitado.equals(hashArmazenado);
}


}

