package main.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class HaveBeenPawed {
    
    // Método principal que verifica se a senha foi vazada
    public static boolean haveBeenPawed(String password) {
        return getPwnedCount(password) > 5;
    }
    
    // Método que retorna QUANTAS vezes foi vazada
    public static int getPwnedCount(String password) {
        try {
            // Gera hash SHA-1 da senha
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes());
            String sha1 = bytesToHex(hash).toUpperCase();
            
            // Pega os primeiros 5 caracteres do hash
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5);
            
            // Faz requisição para a API do HIBP
            URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java-HaveBeenPawed-Checker");
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Verifica se o sufixo do hash está na lista
                if (line.startsWith(suffix)) {
                    String[] parts = line.split(":");
                    reader.close();
                    return Integer.parseInt(parts[1].trim());
                }
            }
            reader.close();
            
        } catch (Exception e) {
            System.err.println("Erro ao verificar senha: " + e.getMessage());
        }
        return 0;
    }

    // Conversor de bytes para hexadecimal
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // Método principal para testar
    public static boolean VerifyPawed(String senha) {
        
         return haveBeenPawed(senha);
        
    }
}