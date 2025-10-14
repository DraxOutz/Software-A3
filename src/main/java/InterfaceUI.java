package main.java;

import java.awt.*;
import java.util.ArrayList;
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

       registerButton.addActionListener(e -> {
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());
    String password2 = new String(passwordField2.getPassword());

    // roda em outra thread pra não travar a UI
    new Thread(() -> {
        String Result = AuthenticationService.RegistroCriar(email, password, password2);
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
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
        leftPanel.add(passwordField, gbc);

       

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
}
