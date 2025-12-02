# Guia de Configuração e Execução do Sistema (Versão MongoDB)

Este projeto foi desenvolvido utilizando o **JDK 17** e o banco de dados **MongoDB** (NoSQL). Este guia detalha os passos para configurar o ambiente tanto no **Windows** como no **WSL** (Linux) e executar o projeto utilizando a IDE **IntelliJ IDEA**.

## Visão Geral dos Requisitos

* **IDE:** IntelliJ IDEA (Community ou Ultimate)
* **Java:** JDK 17
* **Banco de Dados:** MongoDB (Community Server)
* **Drivers Java necessários** (devem ser adicionados como biblioteca):
    * `mongodb-driver-sync`
    * `mongodb-driver-core`
    * `bson`
* **Nome do Banco (sugerido):** `restaurantev2`
* **Conexão Padrão:** `mongodb://localhost:27017`

---

## 🖥️ Ambiente Windows

Guia para configurar e rodar o projeto no Windows com IntelliJ IDEA.

### Passo 1: Instalar o JDK 17

1.  **Download:** Baixe o instalador do JDK 17 para Windows do [Eclipse Temurin (Adoptium)](https://adoptium.net/temurin/releases/?version=17).
    * Escolha a versão `.msi` para **Windows x64**.
2.  **Instalação:** Execute o instalador e siga as instruções padrão.
3.  **Verificação:** Abra o Prompt de Comando (CMD) e digite `java --version`.

### Passo 2: Instalar o MongoDB

1.  **Download:** Baixe o [MongoDB Community Server](https://www.mongodb.com/try/download/community).
2.  **Instalação:**
    * Execute o `.msi`.
    * Escolha a opção **"Complete"**.
    * **Importante:** Marque a opção **"Install MongoDB Compass"** (interface gráfica recomendada).
3.  **Verificação:** Abra o navegador em `http://localhost:27017`. Deve aparecer uma mensagem confirmando que o serviço está a rodar.

### Passo 3: Criar o Banco de Dados (Script Inicial)

1.  Crie um arquivo chamado `CriarBancoMongo.js` na sua Área de Trabalho.
2.  Cole o seguinte código dentro dele:

```javascript
// Seleciona o banco
db = db.getSiblingDB('restaurantev2');

// Limpa coleções antigas (Reset)
db.funcionarios.drop();
db.mesas.drop();
db.produtos.drop();
db.itens.drop();
db.pedidos.drop();

// --- INSERÇÃO DE DADOS ---

db.funcionarios.insertMany([
    { _id: 1, nome: 'João Silva', cargo: 'Garçom', salario: 2200.00, telefone: '47999887766' },
    { _id: 2, nome: 'Maria Oliveira', cargo: 'Garçonete', salario: 2250.00, telefone: '47988776655' },
    { _id: 3, nome: 'Carlos Pereira', cargo: 'Gerente', salario: 4500.00, telefone: '47977665544' }
]);

db.mesas.insertMany([
    { _id: 1, numero: 1, capacidade: 4 },
    { _id: 2, numero: 2, capacidade: 2 },
    { _id: 3, numero: 3, capacidade: 6 },
    { _id: 4, numero: 4, capacidade: 4 },
    { _id: 5, numero: 5, capacidade: 8 }
]);

db.produtos.insertMany([
    { _id: 1, nome: 'Batata', unidade_medida: 'Quilogramas', quantidade: 20.0 },
    { _id: 2, nome: 'Óleo de Soja', unidade_medida: 'Litros', quantidade: 10.0 },
    { _id: 3, nome: 'Sal', unidade_medida: 'Gramas', quantidade: 50.0 },
    { _id: 4, nome: 'Pão de Hambúrguer', unidade_medida: 'Unidades', quantidade: 50 },
    { _id: 5, nome: 'Carne de Hambúrguer', unidade_medida: 'Gramas', quantidade: 7500.0 },
    { _id: 6, nome: 'Queijo Cheddar', unidade_medida: 'Gramas', quantidade: 2000.0 },
    { _id: 7, nome: 'Alface', unidade_medida: 'Gramas', quantidade: 1000.0 },
    { _id: 8, nome: 'Tomate', unidade_medida: 'Gramas', quantidade: 2000.0 },
    { _id: 9, nome: 'Refrigerante Cola', unidade_medida: 'Unidades', quantidade: 300 }
]);

db.itens.insertMany([
    {
        _id: 1, nome: 'Batata Frita Média', preco_venda: 15.00, descricao: 'Porção de 300g',
        receita: [ { id_produto: 1, quantidade_necessaria: 0.3 }, { id_produto: 3, quantidade_necessaria: 2.5 } ]
    },
    {
        _id: 2, nome: 'Hambúrguer Clássico', preco_venda: 25.00, descricao: '
````
### Passo 3 Executar:

1. Abra o MongoDB Compass.
2. Conecte-se ao banco local.
3. Clique no ícone MONGOSH (terminal) na parte inferior.
4. Copie e cole o conteúdo do arquivo no terminal e pressione Enter.

### Passo 4: Configurar o Projeto no IntelliJ
1. Drivers: Crie uma pasta drivers dentro do projeto e coloque os arquivos .jar do MongoDB (mongodb-driver-sync, core e bson).
2. Bibliotecas: No IntelliJ, vá em File > Project Structure > Libraries, clique em + e adicione a pasta drivers.
3. Executar: Abra src/view/Menu.java, clique com o botão direito e selecione Run 'Menu.main()'.

### Guia para configurar o projeto usando o subsistema Linux no Windows.

### Passo 1: Instalar JDK e MongoDB no WSL
1.Abra o terminal do WSL e execute:
# 1. Instalar Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# 2. Instalar MongoDB (exemplo para Ubuntu 22.04)
curl -fsSL [https://pgp.mongodb.com/server-7.0.asc](https://pgp.mongodb.com/server-7.0.asc) | \
   sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor

echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] [https://repo.mongodb.org/apt/ubuntu](https://repo.mongodb.org/apt/ubuntu) jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

sudo apt update
sudo apt install -y mongodb-org

### 3. Iniciar o serviço
sudo service mongod start

## Passo 2 Criar o Banco
Crie o arquivo do script:
````
nano CriarBancoMongo.js
````
Cole o conteúdo do script JavaScript (mesmo do passo Windows) e salve (Ctrl+O, Enter, Ctrl+X).

Execute:
````
mongosh CriarBancoMongo.js
````
### Passo 3: Configurar IntelliJ com WSL
1. Abra o projeto localizado no WSL (\\wsl$\...) pelo IntelliJ no Windows.
2. Configure o SDK para usar o OpenJDK 17 do WSL (o IntelliJ deve detectar automaticamente).
3. Adicione os drivers .jar nas bibliotecas do projeto.
4. Execute o Menu.main().
