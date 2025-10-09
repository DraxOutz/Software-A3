# HexaWarden — Login, Registro e Segurança

## Descrição
O **HexaWarden** é um sistema de autenticação seguro em Java, focado em proteger contas de usuários, gerenciar tentativas de login, permitir registro seguro e autenticação em duas etapas (2FA) via email.

---

## Funcionalidades Principais

### 1. Validação de credenciais
- **Email** no formato correto  
- **Senha segura**:
  - Pelo menos 1 letra maiúscula  
  - Pelo menos 1 letra minúscula  
  - Pelo menos 1 número  
  - Pelo menos 1 caractere especial  
  - Mínimo de 8 e máximo de 64 caracteres  

### 2. Registro de usuário
- Criação de nova conta com email e senha  
- Confirmação da senha (`password2`) para evitar erros de digitação  
- Verifica se email já existe antes de criar  
- Senha armazenada de forma segura usando **SHA-512 + salt único por usuário**  

### 3. Hash de senha + salt
- Senhas nunca são armazenadas em texto puro  
- Cada usuário recebe **salt** único, dificultando ataques de rainbow table  
- Protege contra ataques triviais de força bruta  

### 4. Controle de tentativas de login
- Número de tentativas limitado (ex: 5)  
- Decrementa a cada login incorreto  
- Bloqueio temporário quando tentativas chegam a 0  
- Reset automático após 30 minutos do último login falho  

### 5. Proteção contra SQL Injection
- Todas as consultas ao banco usam **PreparedStatement**  

### 6. Autenticação em duas etapas (2FA)
- Código de **8 caracteres alfanuméricos** gerado aleatoriamente  
- Código enviado por **email** ao usuário registrado  
- **Validade do código:** 30 minutos  
- Código expira automaticamente após o tempo definido  
- Se expirado, novo código é gerado e enviado  

### 7. Envio de email seguro
- Integração com **SMTP (Gmail/Outros)**  
- Mensagem profissional com:
  - Nome da empresa  
  - Código de autenticação  
  - Prazo de 30 minutos para uso  
- Mensagens formatadas em texto limpo ou HTML  

---

## Fluxo de Registro

1. Usuário fornece **email**, **senha** e **confirmação da senha**  
2. Sistema valida email e senha segura  
3. Verifica se email já está cadastrado  
4. Se válido:
   - Gera **salt**
   - Calcula hash da senha + salt
   - Cria o usuário no banco de dados  
5. Caso o registro seja feito com sucesso, usuário pode receber código 2FA (opcional)  

---

## Fluxo de Login Seguro

1. Usuário fornece **email** e **senha**  
2. Sistema valida email e senha segura  
3. Verifica se o usuário existe e consulta:
   - `tentativas_login`
   - `ultima_tentativa`
4. Se tentativas esgotadas:
   - Se **30 minutos se passaram**, reset de tentativas  
   - Caso contrário, login bloqueado  
5. Calcula hash da senha digitada + salt  
6. Compara com hash armazenado no banco  
7. Login correto → reset de tentativas  
   Login incorreto → decremento de tentativas e atualização do timestamp da última tentativa  
8. Se 2FA habilitado, gera código alfanumérico, envia por email e aguarda confirmação  
9. Usuário digita código → sistema valida tempo e valor  
   - Sucesso → usuário logado  
   - Expirado/Incorreto → novo código ou erro  

---

## Fluxo de Código 2FA

1. Usuário recebe email com código de 8 caracteres  
2. Código armazenado **temporariamente em memória** ou **no banco de dados** com timestamp  
3. Usuário digita o código no aplicativo  
4. Sistema valida:
   - Código correto  
   - Código não expirado (≤ 30 minutos)  
5. Caso inválido ou expirado → envia novo código automaticamente  

---

## Exemplos de Resultado do Login/Registro

- `"Sucesso"` → login realizado com sucesso  
- `"Incorrect"` → email ou senha incorretos  
- `"LoginBlocked"` → conta temporariamente bloqueada  
- `"InvalidEmail"` → email inválido  
- `"InvalidPassword"` → senha não atende aos critérios de segurança  
- `"IncorretoSenha"` → senhas de registro não conferem  
- `"Incorrect"` (registro) → email já cadastrado  
- `"Expirado"` → código 2FA expirou, novo código enviado  

---

## Observações
- O sistema garante senhas fortes e contas seguras  
- Bloqueios automáticos e temporários para proteção contra ataques de força bruta  
- 2FA opcional, mas recomendado para segurança extra  
- Código de 2FA pode ser armazenado **em memória** (temporário) ou **no banco** (persistente)  
- Proteção básica contra SQL Injection já implementada  
- Todas as senhas são armazenadas com hash seguro + salt único  

