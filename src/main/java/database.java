package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class database {
    
    private static final String URL = "jdbc:mysql://localhost:3306/users_db";
    private static final String USER = "root";           // seu usuário
    private static final String PASSWORD = "X!"; // a senha que você configurou

     public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
     public static String getPasswordHash(String email) {
        String sql = "SELECT senha_hash FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha_hash"); // pega a senha do banco
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // usuário não encontrado
    }

    public static String getToken(String email) {
        String sql = "SELECT remember_token FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("remember_token"); // pega a senha do banco
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // usuário não encontrado
    }

    public static boolean criarUsuario(String email, String senha) {
        // Gera salt e hash
        String salt = Criptografia.gerarSalt();
        String hash = Criptografia.hashPassword(senha, salt);
        LocalDateTime expira = LocalDateTime.now().plusDays(30);

        String sql = "INSERT INTO usuarios (email, senha_hash, salt, tentativas_login, ultima_tentativa, remember_token, token_expira_em) " +
                     "VALUES (?, ?, ?, 5, NOW(),?,?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, hash);
            stmt.setString(3, salt);
            String token = AuthenticationService.GerarToken(); 
              stmt.setString(4, token);
    stmt.setTimestamp(5, Timestamp.valueOf(expira));

            int linhas = stmt.executeUpdate();
            return linhas > 0; // se inseriu, retorna true

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // deu erro, provavelmente email já existe
        }
    }

    public static String getUserSalt(String email) {
        String sql = "SELECT salt FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("salt"); // pega o salt do banco
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // usuário não encontrado
    }


public static int getUserTrys(String email) {
    String sql = "SELECT tentativas_login FROM usuarios WHERE email = ?";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("tentativas_login"); // pega o número de tentativas
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; // usuário não encontrado ou erro
}

public static void decrementarTentativa(String email) {
    String sql = "UPDATE usuarios SET tentativas_login = tentativas_login - 1, ultima_tentativa = NOW() WHERE email = ?";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        int linhas = stmt.executeUpdate();

        if (linhas > 0) {
            System.out.println("Tentativa decrementada para o usuário: " + email);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void resetarTentativas(String email) {
    String sql = "UPDATE usuarios SET tentativas_login = 5, ultima_tentativa = NULL WHERE email = ?";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        int linhas = stmt.executeUpdate();

        if (linhas > 0) {
            Main.print("Tentativas resetadas para o usuário: " + email);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // retorna true se existir
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // não encontrado ou erro
    }

    public static LocalDateTime getUltimaTentativa(String email) {
        String sql = "SELECT ultima_tentativa FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("ultima_tentativa");
                if (ts != null) {
                    return ts.toLocalDateTime(); // converte para LocalDateTime
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // usuário não encontrado ou sem tentativa registrada
    }
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Conectado ao MySQL!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


