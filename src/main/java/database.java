package main.java;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import main.java.InterfaceUI.Post;

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

    public static boolean updatePost(int id, String title, String message, String category, ImageIcon image) {

        String sql = "UPDATE posts SET title = ?, category = ?, message = ?, image = ? WHERE id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, title);
            stmt.setString(2, category);
            stmt.setString(3, message);
    
            if (image != null) {
                // Converte imagem para bytes
                java.awt.Image img = image.getImage();
                java.awt.image.BufferedImage bImage = new java.awt.image.BufferedImage(
                        img.getWidth(null),
                        img.getHeight(null),
                        java.awt.image.BufferedImage.TYPE_INT_RGB
                );
    
                java.awt.Graphics2D g = bImage.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
    
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                javax.imageio.ImageIO.write(bImage, "png", baos);
    
                stmt.setBytes(4, baos.toByteArray());
            } else {
                stmt.setNull(4, java.sql.Types.BLOB);
            }
    
            stmt.setInt(5, id);
    
            return stmt.executeUpdate() > 0;
    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public static boolean addLike(String userEmail, int postId) {
        String sql = "INSERT IGNORE INTO post_likes (user_email, post_id) VALUES (?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, userEmail);
            stmt.setInt(2, postId);
    
            return stmt.executeUpdate() > 0; // só será true se adicionou
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeLike(String userEmail, int postId) {
        String sql = "DELETE FROM post_likes WHERE user_email = ? AND post_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, userEmail);
            stmt.setInt(2, postId);
            stmt.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getLikes(int postId) {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) return rs.getInt(1);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return 0;
    }

    public static boolean userLiked(String email, int postId) {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE user_email = ? AND post_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, email);
            stmt.setInt(2, postId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) return rs.getInt(1) > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
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

    public static void savePost(String author, String title, String category, String message, byte[] imageBytes) {
        String sql = "INSERT INTO posts (author, title, category, message, image) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, author);
            stmt.setString(2, title);
            stmt.setString(3, category);
            stmt.setString(4, message);
            stmt.setBytes(5, imageBytes);
    
            stmt.executeUpdate();
    
            if (DEBUG_MODE) {
                Main.print("✅ Post salvo no banco: " + title);
            }
    
        } catch (SQLException e) {
            if (DEBUG_MODE) {
                Main.print("❌ Erro ao salvar post: " + e.getMessage());
            }
        }
    }

    
    public static List<Post> getAllPosts() {
        List<Post> list = new ArrayList<>();
    
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
    
                int id = rs.getInt("id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                String category = rs.getString("category");
                String message = rs.getString("message");
    
                byte[] imageData = rs.getBytes("image");
                ImageIcon img = null;
    
                if (imageData != null) {
                    img = new ImageIcon(imageData);
                    img = new ImageIcon(img.getImage().getScaledInstance(420, -1, Image.SCALE_SMOOTH));
                }
    
                Post p = new Post(author, title, category, message, img);
                p.id = id;
    
                list.add(p);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return list;
    }
    

    public static List<Post> getPostsByCategory(String category) {
        List<Post> list = new ArrayList<>();
    
        String sql = "SELECT id, author, title, category, message, image FROM posts WHERE category = ? ORDER BY created_at DESC";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, category);
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
    
                int id = rs.getInt("id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                String cat = rs.getString("category");
                String text = rs.getString("message");
    
                ImageIcon image = null;
                byte[] imgBytes = rs.getBytes("image");
                if (imgBytes != null) image = new ImageIcon(imgBytes);
    
                Post p = new Post(author, title, cat, text, image);
                p.id = id;
    
                list.add(p);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }
    
    public static List<Post> searchPostsByTitle(String term) {
        List<Post> list = new ArrayList<>();
    
        String sql = "SELECT id, author, title, category, message, image FROM posts WHERE LOWER(title) LIKE ? ORDER BY created_at DESC";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, "%" + term.toLowerCase() + "%");
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
    
                int id = rs.getInt("id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                String category = rs.getString("category");
                String text = rs.getString("message");
    
                ImageIcon image = null;
                byte[] imgBytes = rs.getBytes("image");
                if (imgBytes != null) image = new ImageIcon(imgBytes);
    
                Post p = new Post(author, title, category, text, image);
                p.id = id;
    
                list.add(p);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }

    public static List<Post> getAllPostsAlphabetically() {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT id, author, title, category, message, image FROM posts ORDER BY LOWER(title) ASC";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                String category = rs.getString("category");
                String text = rs.getString("message");
    
                ImageIcon image = null;
                byte[] imgBytes = rs.getBytes("image");
                if (imgBytes != null) image = new ImageIcon(imgBytes);
    
                Post p = new Post(author, title, category, text, image);
                p.id = id;
    
                list.add(p);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return list;
    }
    
    
public static Map<String, Integer> getTrendingTopics() {
    Map<String, Integer> trends = new HashMap<>();

    String sql = "SELECT category, COUNT(*) AS total FROM posts GROUP BY category ORDER BY total DESC LIMIT 10";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            trends.put(rs.getString("category"), rs.getInt("total"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return trends;
}

public static void banirUsuario(String email, int dias) {
    String sql = "REPLACE INTO usuarios_banidos (email, banido_ate) VALUES (?, DATE_ADD(NOW(), INTERVAL ? DAY))";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        stmt.setInt(2, dias);
        stmt.executeUpdate();

        if (DEBUG_MODE) {
            Main.print("⛔ Usuário banido: " + email + " por " + dias + " dias");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static boolean isUsuarioBanido(String email) {
    String sql = "SELECT banido_ate FROM usuarios_banidos WHERE email = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Timestamp banTime = rs.getTimestamp("banido_ate");
            return banTime.after(new Timestamp(System.currentTimeMillis()));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

public static long getBanRemainingMinutes(String email) {
    String sql = "SELECT banido_ate FROM usuarios_banidos WHERE email = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Timestamp banTime = rs.getTimestamp("banido_ate");
            long diff = banTime.getTime() - System.currentTimeMillis();

            if (diff > 0) {
                return diff / 60000; // converte ms → minutos
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}


public static int getUserStaff(String email) {
    String sql = "SELECT staff FROM usuarios WHERE email = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("staff");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; // padrão = usuário comum
}

public static void deletePost(int postId) {
    String sql = "DELETE FROM posts WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, postId);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
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

            // 4. Cria a tabela de posts
try (Connection conn = getConnection()) {
    String sqlCreatePosts = "CREATE TABLE IF NOT EXISTS posts (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "author VARCHAR(100)," +
            "title VARCHAR(200)," +
            "category VARCHAR(100)," +
            "message TEXT," +
            "image LONGBLOB," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";

    try (PreparedStatement stmt = conn.prepareStatement(sqlCreatePosts)) {
        stmt.executeUpdate();
        if (DEBUG_MODE) {
            Main.print("✅ Tabela 'posts' verificada/criada.");
        }
    }
}
//
// 5. Cria a tabela de likes das postagens
try (Connection conn = getConnection()) {
    String sqlCreateLikes = "CREATE TABLE IF NOT EXISTS post_likes (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "user_email VARCHAR(100) NOT NULL," +
            "post_id INT NOT NULL," +
            "UNIQUE(user_email, post_id)," +
            "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE" +
            ")";

    try (PreparedStatement stmt = conn.prepareStatement(sqlCreateLikes)) {
        stmt.executeUpdate();
        if (DEBUG_MODE) {
            Main.print("✅ Tabela 'post_likes' verificada/criada.");
        }
    }
}

// 6. Cria a tabela de usuários banidos
try (Connection conn = getConnection()) {
    String sqlBanTable = "CREATE TABLE IF NOT EXISTS usuarios_banidos (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "email VARCHAR(100) NOT NULL UNIQUE," +
            "banido_ate TIMESTAMP NOT NULL" +
            ")";

    try (PreparedStatement stmt = conn.prepareStatement(sqlBanTable)) {
        stmt.executeUpdate();
        if (DEBUG_MODE) {
            Main.print("✅ Tabela 'usuarios_banidos' verificada/criada.");
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
