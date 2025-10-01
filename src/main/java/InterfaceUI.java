package main.java;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
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


        /**
     * Painel de 2FA (autenticação em duas etapas).
     * Exibe campo para digitar código recebido no e-mail.
     * 
     * @param email e-mail usado para exibir instrução
     */
   
     

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
    JButton confirmButton = new JButton("Confirmar");
    confirmButton.setBackground(new Color(98, 0, 238));
    confirmButton.setForeground(Color.WHITE);
    confirmButton.setFocusPainted(false);
    confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    mainPanel.add(confirmButton, gbc);

    // Botão cancelar
    gbc.gridx = 1;
    JButton cancelButton = new JButton("Cancelar");
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

                JButton registerButton = new JButton("Cadastrar-se");
        registerButton.setBackground(new Color(98, 0, 238));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(registerButton);

        JButton loginButton = new JButton("Voltar");
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
        JButton forgotButton = new JButton("Esqueceu a senha?");
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

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(98, 0, 238));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.add(loginButton);

        JButton registerButton = new JButton("Cadastrar-se");
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

        JLabel templateLabel = new JLabel("<html><div style='color:white; padding:20px; font-size:16px;'>Colocar uma imagem ou texto de boas-vindas, promoções ou instruções do sistema.</div></html>");
        templateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(templateLabel, BorderLayout.CENTER);

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
