package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe responsável por toda a comunicação com o banco de dados MySQL.
 * 
 * <p>Ela contém métodos para:</p>
 * <ul>
 *   <li>Criar usuários (com senha + salt + token de login).</li>
 *   <li>Validar se um usuário existe.</li>
 *   <li>Buscar senha, salt, token e tentativas de login.</li>
 *   <li>Gerenciar tentativas de login (resetar/decrementar).</li>
 *   <li>Testar a conexão com o banco de dados.</li>
 * </ul>
 */
public class database {
    
    // DEBUG MODE - Configuração flexível
    private static final boolean DEBUG_MODE = 
        "true".equals(System.getenv("DEBUG_MODE")) || 
        "true".equals(System.getProperty("debug.mode"));
    
    // URL de conexão com o banco de dados MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/users_db";
    
    // Usuário do banco (root no caso local, mas em produção seria outro usuário com menos permissões)
    private static final String USER = "root";
    
    // Senha do banco
    private static final String PASSWORD = security.Password.PASSWORDSQL;  //!!!PORFAVOR NÃO COLOQUE A SENHA NO GITHUB 

    /**
     * Retorna uma conexão válida com o banco de dados.
     * 
     * @return Connection ativa com o MySQL.
     * @throws SQLException se houver erro ao conectar.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Busca o hash da senha de um usuário pelo email.
     * 
     * @param email Email do usuário.
     * @return Hash da senha ou null se não encontrado.
     */
    public static String getPasswordHash(String email) {
        String sql = "SELECT senha_hash FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email); // substitui o ? pelo email
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha_hash"); // retorna o hash
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar hash: " + e.getMessage());
            }
        }

        return null; // usuário não encontrado
    }

    /**
     * Busca o token de login salvo para um usuário.
     * 
     * @param email Email do usuário.
     * @return Token ou null se não existir.
     */
    public static String getToken(String email) {
        String sql = "SELECT remember_token FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("remember_token");
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar token: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Cria um novo usuário no banco com email, senha (hash + salt),
     * token de autenticação e data de expiração.
     * 
     * @param email Email do usuário.
     * @param senha Senha em texto puro (será convertida para hash+salt).
     * @return true se o usuário foi criado com sucesso, false se deu erro.
     */
    //Staff em valor int pois terá hierarquia 1 Suporte, 2 Administrador, 3 CEO
    public static boolean criarUsuario(String email, String senha) {
        // Gera salt e hash da senha
        String salt = Criptografia.gerarSalt();
        String hash = Criptografia.hashPassword(senha, salt);
        
        // Token expira em 30 dias
        LocalDateTime expira = LocalDateTime.now().plusDays(30);

        String sql = "INSERT INTO usuarios (email, senha_hash, salt, tentativas_login, ultima_tentativa, remember_token, token_expira_em, staff) " +
                     "VALUES (?, ?, ?, 5, NOW(), ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, hash);
            stmt.setString(3, salt);

            // Gera token de autenticação
            String token = AuthenticationService.GerarToken();
            stmt.setString(4, token);

            // Define data de expiração do token
            stmt.setTimestamp(5, Timestamp.valueOf(expira));
            stmt.setInt(6, 0);

            int linhas = stmt.executeUpdate();
            
            if (DEBUG_MODE && linhas > 0) {
                Main.print("✅ Usuário criado: " + email);
            }
            
            return linhas > 0; // true se inseriu corretamente

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao criar usuário: " + e.getMessage());
            }
            return false; // provavelmente email já existe
        }
    }

    public static boolean updateUsuario(String email, String senha) {
        // Gera salt e hash da senha
        String salt = Criptografia.gerarSalt();
        String hash = Criptografia.hashPassword(senha, salt);
    
        // Gera token de autenticação
        String token = AuthenticationService.GerarToken();
    
        // Token expira em 30 dias
        LocalDateTime expira = LocalDateTime.now().plusDays(30);
    
        String sql = "UPDATE usuarios SET senha_hash = ?, salt = ?, remember_token = ?, token_expira_em = ?, staff = ? WHERE email = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, hash);                       // senha_hash
            stmt.setString(2, salt);                       // salt
            stmt.setString(3, token);                      // remember_token
            stmt.setTimestamp(4, Timestamp.valueOf(expira)); // token_expira_em
            stmt.setInt(5, 0);                             // staff (exemplo: 0)
            stmt.setString(6, email);                      // WHERE email = ?
    
            int linhas = stmt.executeUpdate();
            
            if (DEBUG_MODE && linhas > 0) {
                Main.print("✅ Senha atualizada para: " + email);
            }
            
            return linhas > 0; // true se atualizou corretamente
    
        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao atualizar usuário: " + e.getMessage());
            }
            return false;
        }
    }
    

    public static void saveUserInterests(int userId, List<String> interests) {
        // 1. Abertura da Conexão
        try (Connection conn = getConnection()) {
            
            // PASSO A: DELETA os interesses antigos. 
            // Isso garante que se ele mudou de 2 para 1, ou mudou quais escolheu, a base reflete o atual.
            String sqlDelete = "DELETE FROM usuario_interesses WHERE usuario_id = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, userId);
                stmtDelete.executeUpdate();
            }

            // PASSO B: INSERE os novos interesses em lote (BATCH).
            String sqlInsert = "INSERT INTO usuario_interesses (usuario_id, interesse) VALUES (?, ?)";
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                
                for (String interesse : interests) {
                    // Prepara os valores para cada linha: ID do usuário e o texto do interesse
                    stmtInsert.setInt(1, userId);
                    stmtInsert.setString(2, interesse);
                    
                    // Adiciona este comando de INSERT ao lote (batch)
                    stmtInsert.addBatch(); 
                }
                
                // Executa todas as inserções de uma vez
                int[] results = stmtInsert.executeBatch();
                
                if (DEBUG_MODE) {
                    Main.print("✅ Interesses salvos para usuário " + userId + ". Total: " + results.length);
                }
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao salvar interesses: " + e.getMessage());
            }
        }
    }

    /**
     * Retorna o salt do usuário (usado para validar a senha).
     * 
     * @param email Email do usuário.
     * @return Salt armazenado ou null se não existir.
     */
    public static String getUserSalt(String email) {
        String sql = "SELECT salt FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("salt");
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar salt: " + e.getMessage());
            }
        }

        return null;
    }

    public static int getUserId(String email) {
        String sql = "SELECT id FROM usuarios WHERE email = ?";
    
        try (Connection conn = getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("id"); 
            }
    
        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar ID: " + e.getMessage());
            }
        }
    
        return -1; 
    }

    /**
     * Retorna o número de tentativas de login restantes de um usuário.
     * 
     * @param email Email do usuário.
     * @return Número de tentativas restantes (ou 0 se não encontrado).
     */
    public static int getUserTrys(String email) {
        String sql = "SELECT tentativas_login FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("tentativas_login");
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar tentativas: " + e.getMessage());
            }
        }

        return 0;
    }

    /**
     * Decrementa em 1 as tentativas de login do usuário.
     * Também atualiza a data da última tentativa.
     * 
     * @param email Email do usuário.
     */
    public static void decrementarTentativa(String email) {
        String sql = "UPDATE usuarios SET tentativas_login = tentativas_login - 1, ultima_tentativa = NOW() WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            int linhas = stmt.executeUpdate();

            if (DEBUG_MODE && linhas > 0) {
                Main.print("🔻 Tentativa decrementada para: " + email);
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao decrementar tentativa: " + e.getMessage());
            }
        }
    }

    /**
     * Reseta o número de tentativas de login do usuário para 5.
     * 
     * @param email Email do usuário.
     */
    public static void resetarTentativas(String email) {
        String sql = "UPDATE usuarios SET tentativas_login = 5, ultima_tentativa = NULL WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            int linhas = stmt.executeUpdate();

            if (DEBUG_MODE && linhas > 0) {
                Main.print("🔄 Tentativas resetadas para: " + email);
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao resetar tentativas: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica se um usuário existe pelo email.
     * 
     * @param email Email do usuário.
     * @return true se o usuário existe, false caso contrário.
     */
    
    public static boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao verificar usuário: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Retorna a data/hora da última tentativa de login do usuário.
     * 
     * @param email Email do usuário.
     * @return LocalDateTime da última tentativa ou null se não houver.
     */
    public static LocalDateTime getUltimaTentativa(String email) {
        String sql = "SELECT ultima_tentativa FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("ultima_tentativa");
                if (ts != null) {
                    return ts.toLocalDateTime();
                }
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao buscar última tentativa: " + e.getMessage());
            }
        }

        return null;
    }

    public static boolean userHasInterests(int userId) {
        // Conta quantas linhas existem na tabela de interesses para este usuário
        String sql = "SELECT COUNT(*) FROM usuario_interesses WHERE usuario_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se COUNT(*) for maior que 0, o usuário tem interesses
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao verificar interesses: " + e.getMessage());
            }
            return false; 
        }
        return false;
    }

    public static void inicializarBanco() {
        try {
            // 1. Conecta sem banco específico
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sqlCreateDb = "CREATE DATABASE IF NOT EXISTS users_db";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCreateDb)) {
                    stmt.executeUpdate();
                    if (DEBUG_MODE) {
                        Main.print("✅ Banco 'users_db' verificado/criado.");
                    }
                }
            }

            // 2. Conecta ao banco de dados recém-criado
            try (Connection conn = getConnection()) {
                String sqlCreateTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "email VARCHAR(100) NOT NULL UNIQUE," +
                        "senha_hash VARCHAR(255) NOT NULL," +
                        "salt VARCHAR(255) NOT NULL," +
                        "tentativas_login INT DEFAULT 5," +
                        "ultima_tentativa TIMESTAMP NULL," +
                        "remember_token VARCHAR(255)," +
                        "token_expira_em TIMESTAMP," +
                        "staff INT DEFAULT 0" +
                        ")";

                try (PreparedStatement stmt = conn.prepareStatement(sqlCreateTable)) {
                    stmt.executeUpdate();
                    if (DEBUG_MODE) {
                        Main.print("✅ Tabela 'usuarios' verificada/criada.");
                    }
                }
            }

            // 3. Cria a tabela de interesses
            try (Connection conn = getConnection()) {
                String sqlCreateTableInterests = "CREATE TABLE IF NOT EXISTS usuario_interesses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "usuario_id INT NOT NULL," +
                    "interesse VARCHAR(100) NOT NULL," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                    ")";

                try (PreparedStatement stmt = conn.prepareStatement(sqlCreateTableInterests)) {
                    stmt.executeUpdate();
                    if (DEBUG_MODE) {
                        Main.print("✅ Tabela 'usuario_interesses' verificada/criada.");
                    }
                }
            }

        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao inicializar banco: " + e.getMessage());
            }
        }
    }

    public static void StartDataBase() {
        inicializarBanco();

        try (Connection conn = getConnection()) {
            if (conn != null) {
                Main.print("✅ Conectado ao MySQL!");
                
                // Só criar usuário se não existir
                if (!userExists("devhexawarden@gmail.com")) {
                    criarUsuario("devhexawarden@gmail.com", "DevWarden122!");
                } else if (DEBUG_MODE) {
                    Main.print("ℹ️ Usuário de teste já existe.");
                }
            }
        } catch (SQLException e) {
            Main.print("❌ Erro na conexão: " + e.getMessage());
        }
    }
}