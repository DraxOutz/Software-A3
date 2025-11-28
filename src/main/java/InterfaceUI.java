package main.java;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import java.net.URL;
/**
 * Classe InterfaceUI
 *
 * ⚠️ ALTAMENTE IMPORTANTE ⚠️
 * --------------------------------------------------------
 * ESTA É A CAMADA **FRONT-END** DO SISTEMA.
 * 
 * - Responsável por criar as telas de Login, Registro e 2FA.
 * - Toda a validação e segurança é feita em AuthenticationService (back-end).
 * - Aqui só lidamos com a interface visual (Swing).
 * - Nunca faça lógica de segurança dentro da UI.
 * --------------------------------------------------------
 *
 * Fluxo:
 * 1. `CreateInterface()` → cria janela principal.
 * 2. `CreateLogin()` → mostra tela de login.
 * 3. `CreateRegistroPanel()` → mostra tela de registro.
 * 4. `Panel2FA()` → mostra verificação em 2 fatores.
 */
public class InterfaceUI {

    // ===============================
// SISTEMA DE FEED (POSTAGENS)
// ===============================

// Classe que representa um post
static class Post {
    int id; // ✅ AQUI — ESTE É O CAMPO NOVO QUE DEVE SER ADICIONADO

    String author;
    String title;
    String category;
    String text;
    ImageIcon image;

    public Post(String author, String title, String category, String text, ImageIcon image) {
        this.author = author;
        this.title = title;
        this.category = category;
        this.text = text;
        this.image = image;
    }
}

// Lista de posts (vai virar SQL depois)
static java.util.List<Post> posts = new java.util.ArrayList<>();

// FeedPanel precisa ser acessível globalmente
static JPanel feedPanel;
static JPanel trendingColumn;


     // Toolkit para pegar o tamanho da tela do sistema
    static Toolkit toolkit = Toolkit.getDefaultToolkit();
    static Dimension screenSize = toolkit.getScreenSize();

    // Frame principal da aplicação
    static JFrame FrameZin;


     /**
     * Cria a janela principal (frame) do programa.
     * - Tela cheia, sem bordas, fundo escuro.
     * - Exibe inicialmente o painel de login.
     */
    public static void CreateInterface() {

        JFrame frame = new JFrame(Main.GetProgramName());
        FrameZin = frame;
        frame.setSize(screenSize);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension((int) (screenSize.width * 0.5), (int) (screenSize.height * 0.5)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         // --- Adicionar o Ícone ---
        try {
            // 1. Define o caminho do recurso (relativo ao seu classpath)
            // Ajuste o caminho se a imagem não estiver na pasta 'images'
            URL iconURL = InterfaceUI.class.getResource("/main/resources/logo.png");

            if (iconURL != null) {
                // 2. Carrega a imagem
                Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
                
                // 3. Define o ícone
                frame.setIconImage(icon);
            } else {
                System.err.println("Aviso: Ícone não encontrado no caminho do recurso.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao definir o ícone do JFrame.");
        }
        // -------------------------

        frame.getContentPane().setBackground(new Color(30, 30, 30));
        frame.setLayout(new BorderLayout());

        frame.setVisible(true);

        CreateLogin(frame);

        // se o usuario tiver um token salvo no PC (Remember me), ele ja loga sem senha e email automaticamente !!!ATENÇÃO ESSE TOKEN SE HACKEADO 
        //OU DESCRIPTOGRAFADO QUALQUER UM PODE ACESSAR ESSA CONTA, POR ISSO RESETAR O TOKEN AO MUDAR SENHA OU A CADA 30 DIAS
       if (AuthenticationService.HasTokenSaved() == true ){
        LoginPanel.setVisible(false);
       };
    }

    static JPanel LoginPanel;
     static JPanel CadastroPanel;
      static JPanel backgroundPanel;
      static JPanel Home;
       static JPanel FA2;
       static JPanel ChooseOption;
       static JPanel ResetPanel;


        /**
     * Painel de 2FA (autenticação em duas etapas).
     * Exibe campo para digitar código recebido no e-mail.
     * 
     * @param email e-mail usado para exibir instrução
     */
   
     public static final String SELECTION_STATE = "isSelected";
     public static int TotalSelection =0;

     public static void ChooseOptions() {
        if (ChooseOption == null) {}
    
        JFrame frame = FrameZin;
    
        int totalWidth = (int) (frame.getWidth() * 0.4);
        int totalHeight = (int) (frame.getHeight() * 0.25);
    
        // Dimensões dos botões com base no tamanho do painel
        // Estes cálculos já não são tão críticos, mas mantemos para o confirmButton
        int buttonWidth = (int) (totalWidth / 3.5);
        int buttonHeight = (int) (totalHeight / 3.5);
    
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        ChooseOption = mainPanel;
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
    
        // Título
        JLabel titleLabel = new JLabel("Interesses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(46, 0, 110));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, gbc);
    
        // Painel com botões lado a lado
        gbc.gridy++;
        // ALTERAÇÃO CRÍTICA: Usar GridLayout(1, 3) para forçar 3 colunas e 1 linha.
        // O hgap (15) é o espaçamento horizontal entre os botões.
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 5)); 
        buttonPanel.setOpaque(false);
    
        JButton IARes = new RoundedButton("IA Responsável");
        JButton CiberSeg = new RoundedButton("Cibersegurança");
        JButton Privacidade = new RoundedButton("Privacidade & Ética Digital");
    
        // Definição de uma altura mais adequada para os botões.
        int preferredButtonHeight = (int) (totalHeight * 0.15); 

        // ... dentro do método ChooseOptions() ...
        
        // Cores definidas uma vez, fora do loop
        Color defaultColor = new Color(98, 0, 238); // Cor azul original (não selecionado)
        Color selectedColor = new Color(50, 0, 100); // Cor azul escura (selecionado)
        
        JButton[] buttons = {IARes, CiberSeg, Privacidade};
        for (JButton btn : buttons) {
            // 1. Configurações visuais iniciais
            btn.setBackground(defaultColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // 2. Inicializa o estado de seleção para 'false' (Não selecionado)
            btn.putClientProperty(SELECTION_STATE, false); 
            
            btn.setPreferredSize(new Dimension(0, preferredButtonHeight)); 
            buttonPanel.add(btn);
            
            // 3. Lógica do Toggle (Alternância)
            btn.addActionListener(e -> {
                // Recupera o estado ATUAL armazenado no botão
                boolean isSelected = (boolean) btn.getClientProperty(SELECTION_STATE); 
                
                if (isSelected) {
                    // Se já está selecionado (true) -> DESELECIONAR
                    btn.setBackground(defaultColor); 
                    TotalSelection -=1;
                    // Armazena o novo estado
                    btn.putClientProperty(SELECTION_STATE, false);
                } else if (isSelected == false && TotalSelection <2) {
                    // Se NÃO está selecionado (false) -> SELECIONAR
                    btn.setBackground(selectedColor); 
                    TotalSelection +=1;
                    // Armazena o novo estado
                    btn.putClientProperty(SELECTION_STATE, true);
                }
            });
        }

        gbc.gridwidth = 2;
        // O peso (weightx) garante que o buttonPanel ocupe toda a largura
        gbc.weightx = 1.0; 
        mainPanel.add(buttonPanel, gbc);
        // Reiniciar o peso (weightx)
        gbc.weightx = 0.0; 
    
        // Label de erro
        gbc.gridy++;
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(errorLabel, gbc);
    
        // Botão Confirmar
        gbc.gridy++;
        JButton confirmButton = new RoundedButton("Confirmar");
        confirmButton.setBackground(new Color(46, 125, 50)); // verde escuro
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setPreferredSize(new Dimension((int)(totalWidth * 0.5), buttonHeight));
        gbc.gridwidth = 2;
        mainPanel.add(confirmButton, gbc);

        Map<String, String> mensagens = new HashMap<>();

        mensagens.put("SelecioneUm", "Você precisa selecionar uma opção");
        //Só por deus pra isso aqui funcionar

        confirmButton.addActionListener(e -> {
    
            // 1. Declara e inicializa a lista para armazenar os interesses
            List<String> selectedOptions = new ArrayList<>();
            
            // 2. Coleta os interesses se o TotalSelection estiver dentro do limite (1 ou 2)
            if (TotalSelection > 0 && TotalSelection <= 2) {
                
                for (JButton btn : buttons) {
                    
                    Boolean isSelected = (Boolean) btn.getClientProperty(SELECTION_STATE);
                    
                    if (isSelected != null && isSelected) {
                        // Adiciona o texto do botão à lista
                        selectedOptions.add(btn.getText());
                    }
                }
                
                // Validação final usando o tamanho da lista (redundante, mas seguro)
                if (selectedOptions.size() >= 1 && selectedOptions.size() <= 2) {
                    
                    // 3. CHAMA O SERVIÇO USANDO O NOME CORRETO DA VARIÁVEL (selectedOptions)
                    AuthenticationService.saveInterests(selectedOptions); 
                    
                    // Limpa o label de erro após o sucesso
                    errorLabel.setText("");
                    ChooseOption.setVisible(false);
                    
                    // Aqui você deve adicionar a lógica para avançar a tela ou fechar o painel
                    // Exemplo: hideOptionsPanel(); 
        
                } else {
                    // Caso raro onde TotalSelection > 0, mas a lista está vazia (erro na lógica de clique)
                    String msg = mensagens.get("SelecioneUm");
                    errorLabel.setText(msg);
                }
        
            } else {
                // Bloco de erro para 0 ou mais de 2 seleções
                String Result = "SelecioneUm";
                String msg = mensagens.get(Result);
                errorLabel.setText(msg);
            }
        });
    
        // Adiciona no centro do backgroundPanel
        backgroundPanel.removeAll();
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.add(mainPanel);
        backgroundPanel.revalidate();
        backgroundPanel.repaint();
    }
    
    public static void ResetPasswordFrame() {


        if (LoginPanel != null) LoginPanel.setVisible(false);
        if (CadastroPanel != null) CadastroPanel.setVisible(false);

   if (ResetPanel == null) {
 // frame.getContentPane().removeAll();

 JFrame frame = FrameZin;

 int totalWidth = (int) (frame.getWidth() * 0.5);
 int totalHeight = (int) (frame.getHeight() * 0.5);

// ================= Painel principal =================
 JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
 mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
 mainPanel.setBackground(new Color(0, 0, 0));

 ResetPanel = mainPanel;

 // ================= Painel esquerdo =================
 JPanel leftPanel = new JPanel(new GridBagLayout());
 leftPanel.setBackground(Color.WHITE);
 leftPanel.setPreferredSize(new Dimension(totalWidth/2, totalHeight));

 GridBagConstraints gbc = new GridBagConstraints();
 gbc.insets = new Insets(10, 20, 10, 20);
 gbc.fill = GridBagConstraints.HORIZONTAL;
 gbc.anchor = GridBagConstraints.CENTER;
 gbc.gridx = 0;
 gbc.gridy = 0;
 gbc.gridwidth = 2;

 JLabel titleLabel = new JLabel("Resetar senha em "+Main.GetProgramName()+"!");
 titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
 titleLabel.setForeground(new Color(46, 0, 110));
 titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
 leftPanel.add(titleLabel, gbc);

 // Senha Label
 gbc.gridy++;
 gbc.gridwidth = 2;
 JLabel passwordLabel = new JLabel("Senha:");
 passwordLabel.setForeground(Color.BLACK);
 leftPanel.add(passwordLabel, gbc);

 // Senha Field
 gbc.gridy++;
 gbc.gridwidth = 2;
 JPasswordField passwordField = new JPasswordField();
 passwordField.setPreferredSize(new Dimension(250, 30));
 passwordField.setBackground(Color.WHITE);
 passwordField.setForeground(Color.BLACK);
 passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
 leftPanel.add(passwordField, gbc);

  // Senha Label
 gbc.gridy++;
 gbc.gridwidth = 2;
 JLabel passwordLabel2 = new JLabel("Repita a senha:");
 passwordLabel2.setForeground(Color.BLACK);
 leftPanel.add(passwordLabel2, gbc);

 // Senha Field
 gbc.gridy++;
 gbc.gridwidth = 2;
 JPasswordField passwordField2 = new JPasswordField();
 passwordField2.setPreferredSize(new Dimension(250, 30));
 passwordField2.setBackground(Color.WHITE);
 passwordField2.setForeground(Color.BLACK);
 passwordField2.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
 leftPanel.add(passwordField2, gbc);

  // Espaço para erro
 gbc.gridx = 0;
 gbc.gridy++;
 gbc.gridwidth = 2;
 gbc.anchor = GridBagConstraints.CENTER;
 JLabel errorLabel = new JLabel("");
 errorLabel.setForeground(Color.RED);
 errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
 leftPanel.add(errorLabel, gbc);

 // Botões Login e Cadastrar
 gbc.gridy++;
 gbc.fill = GridBagConstraints.HORIZONTAL;
 JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
 buttonsPanel.setBackground(Color.WHITE);
 buttonsPanel.setPreferredSize(new Dimension(250, 40));

         JButton registerButton = new RoundedButton("Cadastrar-se");
 registerButton.setBackground(new Color(98, 0, 238));
 registerButton.setForeground(Color.WHITE);
 registerButton.setFocusPainted(false);
 registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
 registerButton.setBorder(BorderFactory.createEmptyBorder());
 buttonsPanel.add(registerButton);

 JButton loginButton = new RoundedButton("Voltar");
 loginButton.setBackground(new Color(98, 0, 238));
 loginButton.setForeground(Color.WHITE);
 loginButton.setFocusPainted(false);
 loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
 loginButton.setBorder(BorderFactory.createEmptyBorder());
 buttonsPanel.add(loginButton);


 leftPanel.add(buttonsPanel, gbc);

 // ================= Painel direito =================
 JPanel rightPanel = new JPanel();
 rightPanel.setBackground(new Color(98, 0, 238));
 rightPanel.setLayout(new BorderLayout());

 JLabel templateLabel = new JLabel("<html><div style='color:white; padding:20px; font-size:16px;'>Aqui você pode colocar uma imagem ou texto de boas-vindas, promoções ou instruções do sistema.</div></html>");
 templateLabel.setHorizontalAlignment(SwingConstants.CENTER);
 rightPanel.add(templateLabel, BorderLayout.CENTER);

 // ================= Monta painel principal =================
 mainPanel.add(leftPanel);
 mainPanel.add(rightPanel);
 backgroundPanel.add(mainPanel);

  loginButton.addActionListener(e -> {
     CadastroPanel.setVisible(false);
     if (LoginPanel == null) {
           CreateLogin(frame);
     } else {
         LoginPanel.setVisible(true);
     }
 });

  Map<String, String> mensagens = new HashMap<>();
   mensagens.put("Nulo", "Todos os campos devem ser preenchidos");
     mensagens.put("InvalidEmail", "Formato de email inválido");
     mensagens.put("InvalidPassword", "Formato de senha inválido");
    mensagens.put("IncorretoSenha", "as senhas não coincidem");
     mensagens.put("Incorrect", "Já existe um cadastro com esse email");
      mensagens.put("TryAgainLater", "Aguarde um pouco");
      mensagens.put("PwnedPassword", "Senha encontrada em vazamentos, tente outra");

registerButton.addActionListener(e -> {
String password = new String(passwordField.getPassword());
String password2 = new String(passwordField2.getPassword());

// roda em outra thread pra não travar a UI
new Thread(() -> {
 String Result = AuthenticationService.ResetPassword(password, password2);
 Main.print(Result);

 SwingUtilities.invokeLater(() -> {
     errorLabel.setText("");
     if (Result.equals("Sucesso")) {
         errorLabel.setText("Cadastrado com sucesso");
         CadastroPanel.setVisible(false);
         // aqui você pode abrir o próximo painel, se quiser
         // proximaTela.setVisible(true);
     } else {
         String msg = mensagens.get(Result);
         errorLabel.setText(msg);
     }
 });
}).start();
});


   } else {
    ResetPanel.setVisible(true);
   }

    }

    

        public static void Panel2FA(String email) {

                // Esconde login e registro
    if (LoginPanel != null) LoginPanel.setVisible(false);
    if (CadastroPanel != null) CadastroPanel.setVisible(false);

              if (FA2 == null) {
 
             JFrame frame = FrameZin;

      int totalWidth = (int) (frame.getWidth() * 0.3);
    int totalHeight = (int) (frame.getHeight() * 0.3);


    // Painel principal (box no meio)
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
    mainPanel.setBackground(Color.WHITE);
    FA2 = mainPanel;

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;

    JLabel titleLabel = new JLabel("Verificação em duas etapas");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(new Color(46, 0, 110));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    mainPanel.add(titleLabel, gbc);

    // Instrução
    gbc.gridy++;
    JLabel infoLabel = new JLabel("Digite o código enviado para " + email);
    infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    mainPanel.add(infoLabel, gbc);

    // Campo de código
    gbc.gridy++;
    gbc.gridwidth = 2;
    JTextField codeField = new JTextField();
    codeField.setPreferredSize(new Dimension(200, 30));
    codeField.setHorizontalAlignment(SwingConstants.CENTER);
    codeField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
    mainPanel.add(codeField, gbc);

    // Label de erro
    gbc.gridy++;
    JLabel errorLabel = new JLabel("");
    errorLabel.setForeground(Color.RED);
    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    mainPanel.add(errorLabel, gbc);

    // Botão confirmar
    gbc.gridy++;
    gbc.gridwidth = 1;
    JButton confirmButton = new RoundedButton("Confirmar");
    confirmButton.setBackground(new Color(46, 125, 50)); // verde escuro
    confirmButton.setForeground(Color.WHITE);
    confirmButton.setFocusPainted(false);
    confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    mainPanel.add(confirmButton, gbc);

    // Botão cancelar
    gbc.gridx = 1;
    JButton cancelButton = new RoundedButton("Cancelar");
    cancelButton.setBackground(Color.GRAY);
    cancelButton.setForeground(Color.WHITE);
    cancelButton.setFocusPainted(false);
    cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    mainPanel.add(cancelButton, gbc);

    // Adiciona no background
    backgroundPanel.add(mainPanel);
    backgroundPanel.revalidate();
    backgroundPanel.repaint();

    // Eventos

    cancelButton.addActionListener(e -> {
    if (LoginPanel != null) {
        LoginPanel.setVisible(true); }
    FA2.setVisible(false);
});


 confirmButton.addActionListener(e -> {
    String codigoDigitado = codeField.getText();

    // roda em outra thread pra não travar a UI
    new Thread(() -> {
        String Result = AuthenticationService.VerifyCode(codigoDigitado);

        SwingUtilities.invokeLater(() -> {
            errorLabel.setText("");
            if (!Result.equals("Sucesso")) {
                Map<String, String> mensagens = new HashMap<>();

                mensagens.put("Incorreto", "O código informado está incorreto");
                mensagens.put("Invalido", "Formato do código é inválido");
                mensagens.put("Nulo", "Nenhum código informado");
                mensagens.put("Expirado", "Código expirado! Um novo código foi enviado.");
                mensagens.put("LoginBlocked", "Login bloqueado!");

                String msg = mensagens.get(Result);
                errorLabel.setText(msg);

            } else {
                FA2.setVisible(false);
                // aqui você pode chamar o painel seguinte, se quiser
                // proximaTela.setVisible(true);
            }
        });
    }).start();
});


    cancelButton.addActionListener(e -> {
        mainPanel.setVisible(false);
        if (LoginPanel != null) LoginPanel.setVisible(true);
    }); } else {
                FA2.setVisible(true);
    }

            };

 /**
     * Cria painel de Registro (cadastro de novo usuário).
     * 
     * @param frame janela principal
     */

     // ========================================
// ADICIONAR UM POST AO FEED
// ========================================
// ========================================
// ADICIONAR UM POST AO FEED (CORRIGIDO)
// ========================================

public static void openEditPostPopup(JFrame frame, JPanel feedPanel, Post post) {

    // Fundo escuro
    JDialog dialog = new JDialog(frame, true);
    dialog.setUndecorated(true);
    dialog.setLayout(new GridBagLayout());
    dialog.setBackground(new Color(0, 0, 0, 150));

    // Card central
    JPanel card = new JPanel();
    card.setPreferredSize(new Dimension(620, 460));
    card.setBackground(Color.WHITE);
    card.setLayout(new GridBagLayout());
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ================== BOTÃO X ==================
    JButton closeBtn = new RoundedButton("X");
    closeBtn.setFocusable(false);
    closeBtn.setBorderPainted(false);
    closeBtn.setContentAreaFilled(false);
    closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
    closeBtn.setForeground(Color.GRAY);
    closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) { closeBtn.setForeground(Color.RED); }
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) { closeBtn.setForeground(Color.GRAY); }
    });

    closeBtn.addActionListener(e -> dialog.dispose());

    JPanel closeWrapper = new JPanel(new BorderLayout());
    closeWrapper.setOpaque(false);
    closeWrapper.add(closeBtn, BorderLayout.EAST);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    card.add(closeWrapper, gbc);

    // ================== Nome do usuário ==================
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    JLabel userLabel = new JLabel("@" + post.author);
    userLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
    userLabel.setForeground(new Color(40, 40, 40));
    card.add(userLabel, gbc);

    // ================== Categoria ==================
    String[] categories = {"IA Responsável", "Cibersegurança", "Privacidade & Ética Digital"};
    JComboBox<String> categoryBox = new JComboBox<>(categories);
    categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    categoryBox.setSelectedItem(post.category); // ⭐ carregar categoria existente

    gbc.gridy = 1;
    gbc.gridwidth = 2;
    card.add(categoryBox, gbc);

    // ================== Título ==================
    JTextField titleField = new JTextField(post.title);
    titleField.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleField.setForeground(Color.BLACK);
    titleField.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

    gbc.gridy = 2;
    card.add(titleField, gbc);

    // ================== Texto ==================
    JTextArea textArea = new JTextArea(post.text);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    textArea.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

    JScrollPane scroll = new JScrollPane(textArea);
    scroll.setPreferredSize(new Dimension(550, 140));

    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.BOTH;
    card.add(scroll, gbc);

    // ================== Imagem atual ==================
    final ImageIcon[] selectedImage = {post.image};

    JButton addImageBtn = new RoundedButton("Alterar imagem");
    addImageBtn.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            ImageIcon img = new ImageIcon(chooser.getSelectedFile().getAbsolutePath());
            selectedImage[0] = img;
        }
    });

    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    card.add(addImageBtn, gbc);

    // ================== BOTÃO SALVAR ==================
    JButton saveBtn = new RoundedButton("Salvar alterações");
    saveBtn.setBackground(new Color(98, 0, 238));
    saveBtn.setForeground(Color.WHITE);
    saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    card.add(saveBtn, gbc);

    // ================== AÇÃO DO BOTÃO SALVAR ==================
    saveBtn.addActionListener(e -> {

        String newTitle = titleField.getText().trim();
        String newText = textArea.getText().trim();
        String newCategory = (String) categoryBox.getSelectedItem();
        ImageIcon newImage = selectedImage[0];

        if (newTitle.isEmpty() || newText.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Título e texto não podem estar vazios!");
            return;
        }

        // Atualiza no banco
        database.updatePost(post.id, newTitle, newText, newCategory, newImage);

        // Remove o post antigo do feed
        feedPanel.removeAll();

        // Recarrega todos
        for (Post p : database.getAllPosts()) {
            addPostToFeed(feedPanel, p);
        }

        feedPanel.revalidate();
        feedPanel.repaint();

        dialog.dispose();
    });

    dialog.add(card);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
}


public static void addPostToFeed(JPanel feedPanel, Post post) {

    JPanel postCard = new JPanel();
    postCard.setLayout(new BoxLayout(postCard, BoxLayout.Y_AXIS));
    postCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
    ));
    postCard.setBackground(new Color(30, 30, 35));

    // --- Labels com ALINHAMENTO CORRIGIDO ---
    JLabel authorLabel = new JLabel("@" + post.author);
    authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    authorLabel.setForeground(new Color(98, 0, 238));
    authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
    postCard.add(authorLabel);
    
    // Espaço para separar autor e título
    postCard.add(Box.createRigidArea(new Dimension(0, 5))); 

    JLabel titleLabel = new JLabel(post.title);
    titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
    postCard.add(titleLabel);

    JLabel categoryLabel = new JLabel("Categoria: " + post.category);
    categoryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    categoryLabel.setForeground(Color.LIGHT_GRAY);
    categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
    postCard.add(categoryLabel);

    // Espaço entre categoria e texto
    postCard.add(Box.createRigidArea(new Dimension(0, 10))); 

    JLabel textLabel = new JLabel("<html>" + post.text.replace("\n", "<br>") + "</html>");
    textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    textLabel.setForeground(Color.WHITE);
    textLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
    postCard.add(textLabel);
    
    // Espaço entre texto e imagem/ações
    postCard.add(Box.createRigidArea(new Dimension(0, 10))); 

    if (post.image != null) {
        JLabel imageLabel = new JLabel(post.image);
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
        postCard.add(imageLabel);
        postCard.add(Box.createRigidArea(new Dimension(0, 10))); 
    }

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
    actions.setOpaque(false);
    actions.setAlignmentX(Component.LEFT_ALIGNMENT); // ✅ CORREÇÃO APLICADA
    
    // ✅ LIKE CORRENTE
    // [O restante da lógica de botões de Like e Delete permanece a mesma]
    int currentLikes = database.getLikes(post.id);
    JButton likeBtn = new RoundedButton("❤️ " + currentLikes);
    JButton delBtn = new RoundedButton("Excluir");

    // Email do usuário logado
    String userEmail = AuthenticationService.getLoggedEmail();

    // ✅ SISTEMA ANTI LIKE INFINITO
    likeBtn.addActionListener(e -> {

        if (!database.userLiked(userEmail, post.id)) {

            database.addLike(userEmail, post.id); // adiciona like
            int newLikes = database.getLikes(post.id); // recarrega total
            likeBtn.setText("❤️ " + newLikes);

        } else {
            JOptionPane.showMessageDialog(null, "Você já deu like nessa postagem!");
        }
    });


    String loggedEmail = AuthenticationService.getLoggedEmail();
    String loggedUser = AuthenticationService.getLoggedUsername();

    // ✅ PERMISSÃO PARA DELETAR (autor OU staff)
    int staff = database.getUserStaff(loggedEmail);

    if (post.author.equals(loggedUser) || staff > 0) {   // ✅ ALTERADO AQUI
        delBtn.addActionListener(e -> {
            database.deletePost(post.id);
            feedPanel.remove(postCard);
            feedPanel.revalidate();
            feedPanel.repaint();
        });
    } else {
        delBtn.setVisible(false);
    }

    JButton banBtn = new RoundedButton("Banir");

// Apenas administradores podem banir (staff >= 2)
if (staff >= 2) {

    banBtn.addActionListener(e -> {

        String[] opcoes = {
            "24 horas",
            "7 dias",
            "31 dias",
            "Permanente"
        };

        String escolha = (String) JOptionPane.showInputDialog(
            null,
            "Selecione a duração do banimento:",
            "Banir usuário",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0]
        );

        if (escolha != null) {
            int dias = 0;

            switch (escolha) {
                case "24 horas": dias = 1; break;
                case "7 dias": dias = 7; break;
                case "31 dias": dias = 31; break;
                case "Permanente": dias = 99999; break;
            }

            database.banirUsuario(post.author, dias);

            JOptionPane.showMessageDialog(null,
                    "Usuário @" + post.author + " foi banido por: " + escolha);
        }
    });

} else {
    banBtn.setVisible(false);
}

actions.add(banBtn);


    JButton editBtn = new RoundedButton("Editar");

    if (post.author.equals(loggedUser)) {
    
        editBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(feedPanel);
            openEditPostPopup(frame, feedPanel, post);
        });
    
    } else {
        editBtn.setVisible(false);
    }
    
    actions.add(editBtn);
    

    actions.add(likeBtn);
    actions.add(delBtn);

    postCard.add(actions);

    // Adiciona no topo
    feedPanel.add(postCard, 0);

    feedPanel.revalidate();
    feedPanel.repaint();
}
// ========================================
// ATUALIZAR TRENDING TOPICS
// ========================================
public static void updateTrendingTopics(JPanel trendingColumn) {

    trendingColumn.removeAll();
    trendingColumn.setLayout(new BoxLayout(trendingColumn, BoxLayout.Y_AXIS));
    trendingColumn.setBackground(new Color(20, 20, 25));

    // Título
    JLabel trendingTitle = new JLabel("Assuntos do Momento");
    trendingTitle.setForeground(Color.WHITE);
    trendingTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    trendingTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    trendingColumn.add(trendingTitle);

    // ✅ BUSCAR TRENDING REAL DO BANCO
    Map<String, Integer> trends = database.getTrendingTopics();

    if (trends.isEmpty()) {
        JLabel none = new JLabel("Nenhum tópico ainda");
        none.setForeground(Color.GRAY);
        none.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        none.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        trendingColumn.add(none);

        trendingColumn.revalidate();
        trendingColumn.repaint();
        return;
    }

    // ✅ Ordenar por maior quantidade
    List<Map.Entry<String, Integer>> sorted = new ArrayList<>(trends.entrySet());
    sorted.sort((a, b) -> b.getValue() - a.getValue());

    // ✅ Mostrar só os 5 mais relevantes
    int top = Math.min(5, sorted.size());
    for (int i = 0; i < top; i++) {
        String name = sorted.get(i).getKey();
        int count = sorted.get(i).getValue();

        JLabel lbl = new JLabel("#" + name + "  •  " + count + " posts");
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        trendingColumn.add(lbl);
    }

    trendingColumn.revalidate();
    trendingColumn.repaint();
}

  


  
   public static void CreateRegistroPanel(JFrame frame) {
       // frame.getContentPane().removeAll();

        int totalWidth = (int) (frame.getWidth() * 0.5);
        int totalHeight = (int) (frame.getHeight() * 0.5);

    // ================= Painel principal =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        mainPanel.setBackground(new Color(0, 0, 0));

        CadastroPanel = mainPanel;

        // ================= Painel esquerdo =================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(totalWidth/2, totalHeight));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Cadastrar em "+Main.GetProgramName()+"!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(46, 0, 110));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(titleLabel, gbc);

        // Email Label
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.BLACK);
        leftPanel.add(emailLabel, gbc);

        // Email Field
        gbc.gridy++;
        gbc.gridwidth = 2;
        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(Color.BLACK);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
        leftPanel.add(emailField, gbc);

        // Senha Label
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(Color.BLACK);
        leftPanel.add(passwordLabel, gbc);

        // Senha Field
        gbc.gridy++;
        gbc.gridwidth = 2;
        // Campo de senha
JPasswordField passwordField = new JPasswordField();
passwordField.setPreferredSize(new Dimension(250, 30));
passwordField.setBackground(Color.WHITE);
passwordField.setForeground(Color.BLACK);
passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));

// Botão olho
JButton showHideButton = new JButton("\uD83D\uDC41"); // emoji de olho
showHideButton.setPreferredSize(new Dimension(30, 30));
showHideButton.setFocusPainted(false);
showHideButton.setBorder(BorderFactory.createEmptyBorder());
showHideButton.setContentAreaFilled(false);

// Flag para controle
final boolean[] isPasswordVisible = {false};
showHideButton.addActionListener(e -> {
    if (isPasswordVisible[0]) {
        passwordField.setEchoChar('•'); // volta a esconder
        isPasswordVisible[0] = false;
    } else {
        passwordField.setEchoChar((char)0); // mostra a senha
        isPasswordVisible[0] = true;
    }
});

// JPanel para manter o layout e campo com estilo
JPanel passwordPanel = new JPanel(new BorderLayout());
passwordPanel.setBackground(Color.WHITE); // mantém a cor de fundo do campo
passwordPanel.add(passwordField, BorderLayout.CENTER);
passwordPanel.add(showHideButton, BorderLayout.EAST);

// Adiciona ao leftPanel no GridBagConstraints
leftPanel.add(passwordPanel, gbc);


         // Senha Label
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel passwordLabel2 = new JLabel("Repita a senha:");
        passwordLabel2.setForeground(Color.BLACK);
        leftPanel.add(passwordLabel2, gbc);

        // Senha Field
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPasswordField passwordField2 = new JPasswordField();
        passwordField2.setPreferredSize(new Dimension(250, 30));
        passwordField2.setBackground(Color.WHITE);
        passwordField2.setForeground(Color.BLACK);
        passwordField2.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
        leftPanel.add(passwordField2, gbc);

        // Data de Nascimento Label
gbc.gridy++;
gbc.gridwidth = 2;
JLabel birthLabel = new JLabel("Data de Nascimento:");
birthLabel.setForeground(Color.BLACK);
leftPanel.add(birthLabel, gbc);

// Data de Nascimento Field (dia, mês, ano separados)
gbc.gridy++;
gbc.gridwidth = 2;

JPanel birthPanel = new JPanel(new GridLayout(1, 3, 10, 0));
birthPanel.setBackground(Color.WHITE);

// Campo Dia
JTextField dayField = new JTextField();
dayField.setPreferredSize(new Dimension(60, 30));
dayField.setBackground(Color.WHITE);
dayField.setForeground(Color.BLACK);
dayField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
birthPanel.add(dayField);

// Campo Mês
JTextField monthField = new JTextField();
monthField.setPreferredSize(new Dimension(60, 30));
monthField.setBackground(Color.WHITE);
monthField.setForeground(Color.BLACK);
monthField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
birthPanel.add(monthField);

// Campo Ano
JTextField yearField = new JTextField();
yearField.setPreferredSize(new Dimension(80, 30));
yearField.setBackground(Color.WHITE);
yearField.setForeground(Color.BLACK);
yearField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
birthPanel.add(yearField);

leftPanel.add(birthPanel, gbc);


         // Espaço para erro
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(errorLabel, gbc);

        // Botões Login e Cadastrar
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setPreferredSize(new Dimension(250, 40));

                JButton registerButton = new RoundedButton("Cadastrar-se");
        registerButton.setBackground(new Color(98, 0, 238));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(registerButton);

        JButton loginButton = new RoundedButton("Voltar");
        loginButton.setBackground(new Color(98, 0, 238));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(loginButton);


        leftPanel.add(buttonsPanel, gbc);

        // ================= Painel direito =================
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(98, 0, 238));
        rightPanel.setLayout(new BorderLayout());

        URL iconURL = InterfaceUI.class.getResource("/main/resources/painel-cadastro.png");
        
        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            
            JLabel templateLabel = new JLabel();
            templateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
            // Adiciona o JLabel ao painel (antes de redimensionar)
            rightPanel.add(templateLabel, BorderLayout.CENTER);
        
            // Listener para redimensionar a imagem quando o rightPanel mudar de tamanho
            rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    int width = rightPanel.getWidth();
                    int height = rightPanel.getHeight();
                    if (width > 0 && height > 0) {
                        // Redimensiona a imagem para o tamanho do rightPanel
                        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        templateLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
            });
        
        } else {
            System.err.println("Imagem não encontrada!");
        }

        // ================= Monta painel principal =================
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        backgroundPanel.add(mainPanel);

         loginButton.addActionListener(e -> {
            CadastroPanel.setVisible(false);
            if (LoginPanel == null) {
                  CreateLogin(frame);
            } else {
                LoginPanel.setVisible(true);
            }
        });

         Map<String, String> mensagens = new HashMap<>();
          mensagens.put("Nulo", "Todos os campos devem ser preenchidos");
            mensagens.put("InvalidEmail", "Formato de email inválido");
            mensagens.put("InvalidPassword", "Formato de senha inválido");
           mensagens.put("IncorretoSenha", "as senhas não coincidem");
            mensagens.put("Incorrect", "Já existe um cadastro com esse email");
             mensagens.put("TryAgainLater", "Aguarde um pouco");
              mensagens.put("PwnedPassword", "Senha encontrada em vazamentos, tente outra");
                  mensagens.put("UnderAge", "É necessário ser maior de 13 anos");

       registerButton.addActionListener(e -> {
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());
    String password2 = new String(passwordField2.getPassword());

    // roda em outra thread pra não travar a UI
    new Thread(() -> {
        String Result = AuthenticationService.RegistroCriar(email, password, password2);

          if (Result.equals("Sucesso")) {
// Obtém a data atual
java.util.Calendar hoje = java.util.Calendar.getInstance();
int anoAtual = hoje.get(java.util.Calendar.YEAR);
int mesAtual = hoje.get(java.util.Calendar.MONTH) + 1;
int diaAtual = hoje.get(java.util.Calendar.DAY_OF_MONTH);

// Calcula a idade
int idade = anoAtual - Integer.parseInt(yearField.getText());

// Ajusta se ainda não fez aniversário este ano
if (Integer.parseInt(monthField.getText()) > mesAtual || 
    (Integer.parseInt(monthField.getText()) == mesAtual && 
     Integer.parseInt(dayField.getText()) > diaAtual)) {
    idade--;
}

// Verifica se é maior de 13 anos
if (idade < 13) {
   String msg = mensagens.get("UnderAge");
    errorLabel.setText(msg);
    return;
}
          };

        Main.print(Result);

        SwingUtilities.invokeLater(() -> {
            errorLabel.setText("");
            if (Result.equals("Sucesso")) {
                errorLabel.setText("Cadastrado com sucesso");
                CadastroPanel.setVisible(false);
                // aqui você pode abrir o próximo painel, se quiser
                // proximaTela.setVisible(true);
            } else {
                String msg = mensagens.get(Result);
                errorLabel.setText(msg);
            }
        });
    }).start();
});

    
       
   };
   /**
     * Cria painel de Login.
     * - Usuário digita e-mail e senha.
     * - Pode marcar "lembrar de mim".
     * - Pode recuperar senha.
     * - Pode ir para cadastro.
     *
     * @param frame janela principal
     */
    public static void CreateLogin(JFrame frame) {
       // frame.getContentPane().removeAll();

        int totalWidth = (int) (frame.getWidth() * 0.5);
        int totalHeight = (int) (frame.getHeight() * 0.5);

        // ================= Painel de fundo =================
         backgroundPanel = new JPanel() { //Configuração de fundo gradiente
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(46, 0, 110),
                        0, getHeight(), new Color(23, 0, 56)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // colocar o estilo de layout
        frame.setContentPane(backgroundPanel);

        // ================= Painel principal =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        mainPanel.setBackground(new Color(0, 0, 0)); // cor do fundo RGB

        LoginPanel = mainPanel;

        // ================= Painel esquerdo =================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(totalWidth/2, totalHeight));

        GridBagConstraints gbc = new GridBagConstraints(); //Define as regras de posicionamento (restrições)
        gbc.insets = new Insets(10, 20, 10, 20); //10 (topo), 20 (esquerda), 10 (baixo), 20 (direita).
        gbc.fill = GridBagConstraints.HORIZONTAL; //Como o componente deve preencher o espaço da célula.
        gbc.anchor = GridBagConstraints.CENTER; //alinhamento dentro da célula caso o componente não preencha tudo.
        gbc.gridx = 0; //coluna (eixo X) onde o componente vai ser colocado
        gbc.gridy = 0; //linha (eixo Y) onde o componente vai ser colocado
        gbc.gridwidth = 2; //Faz o componente ocupar 2 colunas ao invés de só 1.

        JLabel titleLabel = new JLabel("Entrar em "+Main.GetProgramName()+"!"); //Entrar em <nome do programa>!
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); //Define a fonte do texto
        titleLabel.setForeground(new Color(46, 0, 110)); //Define a cor do texto.
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); //Alinha o texto horizontalmente no centro do JLabel.
        leftPanel.add(titleLabel, gbc); //Adiciona o titleLabel dentro do painel leftPanel.

        // Email Label
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.BLACK);
        leftPanel.add(emailLabel, gbc);

        // Email Field
        gbc.gridy++;
        gbc.gridwidth = 2;
        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(Color.BLACK);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
        leftPanel.add(emailField, gbc);

        // Senha Label
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(Color.BLACK);
        leftPanel.add(passwordLabel, gbc);

        // Senha Field
        gbc.gridy++;
        gbc.gridwidth = 2;
        // Campo de senha
JPasswordField passwordField = new JPasswordField();
passwordField.setPreferredSize(new Dimension(250, 30));
passwordField.setBackground(Color.WHITE);
passwordField.setForeground(Color.BLACK);
passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));

// Botão olho
JButton showHideButton = new JButton("\uD83D\uDC41"); // emoji de olho
showHideButton.setPreferredSize(new Dimension(30, 30));
showHideButton.setFocusPainted(false);
showHideButton.setBorder(BorderFactory.createEmptyBorder());
showHideButton.setContentAreaFilled(false);

// Flag para controle
final boolean[] isPasswordVisible = {false};
showHideButton.addActionListener(e -> {
    if (isPasswordVisible[0]) {
        passwordField.setEchoChar('•'); // volta a esconder
        isPasswordVisible[0] = false;
    } else {
        passwordField.setEchoChar((char)0); // mostra a senha
        isPasswordVisible[0] = true;
    }
});

// JPanel para manter o layout e campo com estilo
JPanel passwordPanel = new JPanel(new BorderLayout());
passwordPanel.setBackground(Color.WHITE); // mantém a cor de fundo do campo
passwordPanel.add(passwordField, BorderLayout.CENTER);
passwordPanel.add(showHideButton, BorderLayout.EAST);

// Adiciona ao leftPanel no GridBagConstraints
leftPanel.add(passwordPanel, gbc);


       

        // Remember me + Forgot
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JCheckBox rememberCheck = new JCheckBox("Lembrar de mim");
        rememberCheck.setBackground(Color.WHITE);
        rememberCheck.setForeground(Color.BLACK);
        leftPanel.add(rememberCheck, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton forgotButton = new RoundedButton("Esqueceu a senha?");
        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setForeground(new Color(153, 0, 238));
        forgotButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(forgotButton, gbc);

        // Espaço para erro
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(errorLabel, gbc);

       

        // Botões Login e Cadastrar
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setPreferredSize(new Dimension(250, 40));

        JButton loginButton = new RoundedButton("Login");
        loginButton.setBackground(new Color(98, 0, 238));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(loginButton);

        JButton registerButton = new RoundedButton("Cadastrar-se");
        registerButton.setBackground(new Color(98, 0, 238));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(registerButton);

        leftPanel.add(buttonsPanel, gbc);

        // ================= Painel direito =================
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(98, 0, 238));
        rightPanel.setLayout(new BorderLayout());

        URL iconURL = InterfaceUI.class.getResource("/main/resources/painel-login.png");
        
        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            
            JLabel templateLabel = new JLabel();
            templateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
            // Adiciona o JLabel ao painel (antes de redimensionar)
            rightPanel.add(templateLabel, BorderLayout.CENTER);
        
            // Listener para redimensionar a imagem quando o rightPanel mudar de tamanho
            rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    int width = rightPanel.getWidth();
                    int height = rightPanel.getHeight();
                    if (width > 0 && height > 0) {
                        // Redimensiona a imagem para o tamanho do rightPanel
                        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        templateLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
            });
        
        } else {
            System.err.println("Imagem não encontrada!");
        }

        // ================= Monta painel principal =================
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        backgroundPanel.add(mainPanel);

        frame.revalidate();
        frame.repaint();
        
         Map<String, String> mensagens = new HashMap<>();
            mensagens.put("Nulo", "Todos os campos devem ser preenchidos");
            mensagens.put("Incorrect", "Senha ou email incorreto");
            mensagens.put("InvalidEmail", "Formato de email inválido");
            mensagens.put("InvalidPassword", "Formato de senha inválido");
            mensagens.put("LoginBlocked", "Login bloqueado! Tente novamente em: ");
            mensagens.put("LoginBanned", "Você foi banido! Seu acesso foi bloqueado.");
            mensagens.put("TryAgainLater", "Aguarde um pouco");
            mensagens.put("PwnedPassword", "Senha encontrada em vazamentos, tente outra");

        // Eventos

        forgotButton.addActionListener(e -> {
              String email = emailField.getText();
    new Thread(() -> {
       
      String Result = AuthenticationService.ResetPasswordEvent(email);

        SwingUtilities.invokeLater(() -> {
            errorLabel.setText("");
              if (Result.equals("Sucesso")) {

              }else{
                String msg = mensagens.get(Result);
                 errorLabel.setText(msg);
              }
        });
    }).start();
});

        loginButton.addActionListener(e -> {
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());
    boolean checked = rememberCheck.isSelected();

    // roda em outra thread pra não travar a UI
    new Thread(() -> {
        String Result = AuthenticationService.LoginCheck(email, password, checked);
        Main.print(Result);

        SwingUtilities.invokeLater(() -> {
            if (Result.equals("Sucesso")) {
                errorLabel.setText("Logado");
                // aqui você pode abrir o próximo painel, se quiser
                // proximaTela.setVisible(true);
            } else {
                String msg = mensagens.get(Result);
                if (Result.equals("LoginBlocked")) {
                    msg += AuthenticationService.PegarTempoRestante();
                }
                errorLabel.setText(msg);
            }
        });
    }).start();
});


        registerButton.addActionListener(e -> {
            LoginPanel.setVisible(false);
            if (CadastroPanel == null) {
                  CreateRegistroPanel(frame);
            } else {
                CadastroPanel.setVisible(true);
            }
            // implementar cadastro
        });
    }

public static void openTweetPopup(JFrame frame) {

    // Fundo escuro
    JDialog dialog = new JDialog(frame, true);
    dialog.setUndecorated(true);
    dialog.setLayout(new GridBagLayout());
    dialog.setBackground(new Color(0, 0, 0, 150));

    // Card central
    JPanel card = new JPanel();
    card.setPreferredSize(new Dimension(620, 460));
    card.setBackground(Color.WHITE);
    card.setLayout(new GridBagLayout());
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ================== BOTÃO X ==================
    JButton closeBtn = new RoundedButton("X");
    closeBtn.setFocusable(false);
    closeBtn.setBorderPainted(false);
    closeBtn.setContentAreaFilled(false);
    closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
    closeBtn.setForeground(Color.GRAY);
    closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) { closeBtn.setForeground(Color.RED); }
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) { closeBtn.setForeground(Color.GRAY); }
    });

    closeBtn.addActionListener(e -> dialog.dispose());

    JPanel closeWrapper = new JPanel(new BorderLayout());
    closeWrapper.setOpaque(false);
    closeWrapper.add(closeBtn, BorderLayout.EAST);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    card.add(closeWrapper, gbc);

    // ================== Nome do usuário ==================
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    JLabel userLabel = new JLabel("@" + AuthenticationService.loggedUserName);
    userLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
    userLabel.setForeground(new Color(40, 40, 40));
    card.add(userLabel, gbc);

    // ================== Categoria ==================
    String[] categories = {"IA Responsável", "Cibersegurança", "Privacidade & Ética Digital"};
    JComboBox<String> categoryBox = new JComboBox<>(categories);
    categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

    gbc.gridy = 1;
    gbc.gridwidth = 2;
    card.add(categoryBox, gbc);

    // ================== Título ==================
    JTextField titleField = new JTextField("Digite o título...");
    titleField.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleField.setForeground(Color.GRAY);
    titleField.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

    titleField.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (titleField.getText().equals("Digite o título...")) {
                titleField.setText("");
                titleField.setForeground(Color.BLACK);
            }
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (titleField.getText().trim().isEmpty()) {
                titleField.setForeground(Color.GRAY);
                titleField.setText("Digite o título...");
            }
        }
    });

    gbc.gridy = 2;
    card.add(titleField, gbc);

    // ================== Mensagem ==================
    JTextArea textArea = new JTextArea(5, 20);
    textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    textArea.setForeground(Color.GRAY);
    textArea.setText("Digite sua mensagem...");
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    textArea.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (textArea.getText().equals("Digite sua mensagem...")) {
                textArea.setText("");
                textArea.setForeground(Color.BLACK);
            }
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (textArea.getText().trim().isEmpty()) {
                textArea.setForeground(Color.GRAY);
                textArea.setText("Digite sua mensagem...");
            }
        }
    });

    JScrollPane scroll = new JScrollPane(textArea);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

    gbc.gridy = 3;
    card.add(scroll, gbc);

    // ================== Botões ==================
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonRow.setBackground(Color.WHITE);

    int buttonWidth = 140;
    int buttonHeight = 40;

    JButton btnUpload = new RoundedButton("Imagem");
    btnUpload.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnUpload.setBackground(Color.WHITE);
    btnUpload.setForeground(Color.BLACK);
    btnUpload.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
    btnUpload.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

    JButton btnPublish = new RoundedButton("Publicar");
    btnPublish.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnPublish.setBackground(new Color(120, 0, 255));
    btnPublish.setForeground(Color.WHITE);
    btnPublish.setBorder(BorderFactory.createLineBorder(new Color(120, 0, 255), 2, true));
    btnPublish.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

    buttonRow.add(btnUpload);
    buttonRow.add(btnPublish);

    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.EAST;
    card.add(buttonRow, gbc);

    // ================== Upload de imagem ==================
    File[] selectedImage = {null};
    btnUpload.addActionListener(e -> {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagens", "png", "jpg", "jpeg"));
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedImage[0] = fc.getSelectedFile();
            btnUpload.setText("✅ Upload feito");
        }
    });

    // ================== Publicar ==================
    btnPublish.addActionListener(e -> {
        String rawTitle = titleField.getText().trim();
        String rawMsg = textArea.getText().trim();
    
        boolean invalidTitle = rawTitle.isEmpty() || rawTitle.equals("Digite o título...");
        boolean invalidMsg = rawMsg.isEmpty() || rawMsg.equals("Digite sua mensagem...");
    
        // ✅ Validação de tamanho
        int MAX_TITLE = 100;
        int MAX_MSG = 300;
    
        if (rawTitle.length() > MAX_TITLE) {
            JOptionPane.showMessageDialog(frame, "Título muito longo! Máx: " + MAX_TITLE + " caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (rawMsg.length() > MAX_MSG) {
            JOptionPane.showMessageDialog(frame, "Mensagem muito longa! Máx: " + MAX_MSG + " caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (invalidTitle || invalidMsg) {
            if (invalidTitle) titleField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            if (invalidMsg) scroll.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    
            JOptionPane.showMessageDialog(frame, "Preencha o título e a mensagem!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // restaura bordas
        titleField.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
    
        String category = categoryBox.getSelectedItem().toString();
    
        // Converte imagem para bytes
        byte[] imageBytes = null;
        if (selectedImage[0] != null) {
            try {
                imageBytes = java.nio.file.Files.readAllBytes(selectedImage[0].toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao ler a imagem!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
        // Salva post no banco
        database.savePost(AuthenticationService.loggedUserName, rawTitle, category, rawMsg, imageBytes);
    
        // Atualiza feed
        feedPanel.removeAll();
        List<Post> postsDB = database.getAllPosts();
        Collections.reverse(postsDB);
    
        for (Post post : postsDB) {
            addPostToFeed(feedPanel, post);
        }
    
        feedPanel.revalidate();
        feedPanel.repaint();
    
        // Atualiza trending
        updateTrendingTopics(trendingColumn);
    
        dialog.dispose();
    });
    
    dialog.add(card);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
}

    


    public static void Home() {

        // Oculta outros painéis
        if (LoginPanel != null) LoginPanel.setVisible(false);
        if (CadastroPanel != null) CadastroPanel.setVisible(false);
        if (ChooseOption != null) ChooseOption.setVisible(false);
        if (FA2 != null) FA2.setVisible(false);
    
        JFrame frame = FrameZin;
    
        // Painel principal (3 colunas)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        Home = mainPanel;
    
        // =======================================================
        // ✅ COLUNA ESQUERDA — MENU TWITTER
        // =======================================================
        JPanel leftMenu = new JPanel();
        leftMenu.setPreferredSize(new Dimension(220, frame.getHeight()));
        leftMenu.setBackground(new Color(15, 15, 20));
        leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
    
        JButton btnHome = new RoundedButton("Página Inicial");
        JButton btnFiltros = new RoundedButton("Filtros");
        JButton btnTweetar = new RoundedButton("Publicar");
        JButton btnLogout = new RoundedButton("Deslogar");
    
        Color defaultBg = new Color(20, 20, 25);
        Color defaultFg = Color.WHITE;
    
        btnTweetar.setBackground(new Color(120, 0, 255));
        btnTweetar.setForeground(Color.WHITE);
    
        btnLogout.setBackground(new Color(200, 30, 30));
        btnLogout.setForeground(Color.WHITE);

        btnLogout.addActionListener(e -> {
            Home.setVisible(false);
            AuthenticationService.ClearLogin();
            CreateLogin(FrameZin); // recria o painel de login e troca o contentPane
        });

        btnHome.addActionListener(e -> {
    feedPanel.removeAll();
    
    // Carrega todos os posts novamente
    List<Post> postsDB = database.getAllPosts();
    Collections.reverse(postsDB); // Mais recentes em cima

    for (Post p : postsDB) {
        addPostToFeed(feedPanel, p);
    }

    feedPanel.revalidate();
    feedPanel.repaint();
});

        
    
        JButton[] menuButtons = {btnHome, btnFiltros, btnTweetar, btnLogout};
    
        for (JButton b : menuButtons) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 45));
    
            if (b != btnTweetar && b != btnLogout) {
                b.setBackground(defaultBg);
                b.setForeground(defaultFg);
            }
    
            leftMenu.add(b);
            leftMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    
      // =======================================================
// ✅ FILTROS DE CATEGORIA
// =======================================================
JPanel filterPanel = new JPanel();
filterPanel.setBackground(new Color(15, 15, 20));
filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
filterPanel.setVisible(false);

String[] categories = {
        "IA Responsável",
        "Cibersegurança",
        "Privacidade & Ética Digital",
        "Ordem ALfabética",
};

for (String cat : categories) {
    JButton fb = new JButton(cat);
    fb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    fb.setForeground(Color.WHITE);
    fb.setBackground(new Color(30, 30, 35));
    fb.setFocusPainted(false);
    fb.setAlignmentX(Component.CENTER_ALIGNMENT);
    fb.setMaximumSize(new Dimension(200, 35));

    fb.addActionListener(e -> {
        feedPanel.removeAll();
        filterPanel.setVisible(false);
        List<Post> postsDB;

        if (cat.equals("Ordem ALfabética")) {
            // Pega todos os posts e ordena pelo título A-Z
            postsDB = database.getAllPostsAlphabetically();
        } else {
            postsDB = database.getPostsByCategory(cat);
            Collections.reverse(postsDB); // Mantém a ordem original
        }

        for (Post p : postsDB) {
            addPostToFeed(feedPanel, p);
        }

        feedPanel.revalidate();
        feedPanel.repaint();
    });

    filterPanel.add(fb);
    filterPanel.add(Box.createRigidArea(new Dimension(0, 5)));
}

leftMenu.add(Box.createRigidArea(new Dimension(0, 10)));
leftMenu.add(filterPanel);

btnFiltros.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

// =======================================================
// ✅ COLUNA CENTRAL — FEED
// =======================================================
JPanel feedColumn = new JPanel(new BorderLayout());
feedColumn.setBackground(new Color(25, 25, 30));

feedPanel = new JPanel();
feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
feedPanel.setBackground(new Color(25, 25, 30));

JScrollPane scroll = new JScrollPane(feedPanel);
scroll.setBorder(null);
scroll.getVerticalScrollBar().setUnitIncrement(16);

feedColumn.add(scroll, BorderLayout.CENTER);

// ✅ Tweetar abre pop-up
btnTweetar.addActionListener(e -> openTweetPopup(frame));

// =======================================================
// ✅ CARREGAR POSTS DO BANCO DE DADOS
// =======================================================
feedPanel.removeAll();
List<Post> postsDB = database.getAllPosts();

// Mantém a ordem do mais recente para o mais antigo
Collections.reverse(postsDB);

for (Post p : postsDB) {
    addPostToFeed(feedPanel, p);
}

feedPanel.revalidate();
feedPanel.repaint();

        // =======================================================
        // ✅ COLUNA DIREITA — TRENDING + BUSCA
        // =======================================================
        trendingColumn = new JPanel();
        trendingColumn.setPreferredSize(new Dimension(300, frame.getHeight()));
        trendingColumn.setBackground(new Color(20, 20, 25));
        trendingColumn.setLayout(new BoxLayout(trendingColumn, BoxLayout.Y_AXIS));
    
        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    
        trendingColumn.add(Box.createRigidArea(new Dimension(0, 15)));
        trendingColumn.add(searchField);
        trendingColumn.add(Box.createRigidArea(new Dimension(0, 15)));
    
        searchField.addActionListener(e -> {
            String term = searchField.getText().toLowerCase();
    
            trendingColumn.removeAll();
            trendingColumn.add(searchField);
            trendingColumn.add(Box.createRigidArea(new Dimension(0, 15)));
    
            List<Post> searchResults = database.searchPostsByTitle(term);
    
            for (Post p : searchResults) {
                JLabel r = new JLabel("• " + p.title);
                r.setForeground(Color.WHITE);
                r.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                trendingColumn.add(r);
            }
    
            trendingColumn.revalidate();
            trendingColumn.repaint();
        });
    
        // ✅ TRENDING DO BANCO
        updateTrendingTopics(trendingColumn);
    
        // =======================================================
        // ✅ MONTAGEM FINAL
        // =======================================================
        mainPanel.add(leftMenu, BorderLayout.WEST);
        mainPanel.add(feedColumn, BorderLayout.CENTER);
        mainPanel.add(trendingColumn, BorderLayout.EAST);
    
        backgroundPanel.removeAll();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(mainPanel);
        backgroundPanel.revalidate();
        backgroundPanel.repaint();
    }
    
}
