package main.java;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * Classe responsável por toda a parte de criptografia de senhas.
 *
 * ⚠️ ALTAMENTE IMPORTANTE ⚠️
 * ---------------------------------------------
 * Nunca armazene senhas em texto puro!
 * Sempre use hash + salt para proteger as credenciais dos usuários.
 * O uso de PBKDF2 com HmacSHA512 garante alta segurança contra ataques de força bruta.
 * ---------------------------------------------
 *
 * <p>Principais funções:</p>
 * <ul>
 *   <li>Gerar hash seguro de senha usando PBKDF2 com HmacSHA512.</li>
 *   <li>Gerar salt aleatório para cada usuário.</li>
 *   <li>Verificar se o hash da senha digitada bate com o armazenado.</li>
 * </ul>
 */
public class Criptografia {

    /**
     * Número de iterações do algoritmo PBKDF2.
     * Quanto maior, mais seguro (mas também mais lento).
     */
    private static final int ITERATIONS = 65536; 
    
    /**
     * Tamanho da chave/hash em bits.
     * Aqui usamos 256 bits = padrão forte de segurança.
     */
    private static final int KEY_LENGTH = 256;   

    /**
     * Gera um hash seguro da senha com PBKDF2 (Password-Based Key Derivation Function 2).
     * O salt é usado para garantir que duas senhas iguais não gerem o mesmo hash.
     *
     * @param senha Senha em texto puro (digitada pelo usuário).
     * @param salt  Salt gerado para este usuário.
     * @return Hash da senha codificado em Base64.
     */
    public static String hashPassword(String senha, String salt) {
        try {
            // Define os parâmetros para gerar o hash
            PBEKeySpec spec = new PBEKeySpec(
                senha.toCharArray(),      // converte senha em array de chars
                salt.getBytes(),          // adiciona o salt
                ITERATIONS,               // número de iterações
                KEY_LENGTH                // tamanho da chave
            );

            // Usa PBKDF2 com HmacSHA512 (forte e resistente a brute force)
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hashBytes = skf.generateSecret(spec).getEncoded();

            // Converte os bytes do hash em string Base64 (fácil de armazenar no banco)
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo não disponível", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Gera um salt aleatório em Base64.
     * O salt evita que dois usuários com a mesma senha tenham o mesmo hash.
     *
     * @return String Base64 representando o salt.
     */
    public static String gerarSalt() {
        SecureRandom sr = new SecureRandom(); // gerador de números aleatórios seguros
        byte[] salt = new byte[16];           // tamanho de 16 bytes (128 bits)
        sr.nextBytes(salt);                   // preenche o array com bytes aleatórios
        return Base64.getEncoder().encodeToString(salt); // retorna em formato Base64
    }

    /**
     * Verifica se o hash da senha digitada confere com o hash armazenado.
     * (Aqui assume-se que o hashDigitado já foi gerado com o mesmo salt).
     *
     * @param hashDigitado   Hash gerado a partir da senha digitada.
     * @param hashArmazenado Hash salvo no banco de dados.
     * @return true se os dois forem iguais, false caso contrário.
     */
    public static boolean verifyPassword(String hashDigitado, String hashArmazenado) {
        return hashDigitado.equals(hashArmazenado);
    }
}
