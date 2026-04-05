# Motor de Busca Bancário - EDA2

## Objetivo do Projeto
O objetivo deste projeto é implementar um motor de busca de alta performance em memória para uma base de dados bancária. O sistema utiliza estruturas de dados para realizar consultas eficientes através de diferentes chaves de acesso, simulando a camada de processamento de dados de uma instituição financeira.

## Características dos Dados e Arquitetura

* **Chave Primária Composta:** A identificação única e principal de cada conta bancária no sistema é garantida pela junção do **Número da Agência** com o **Número da Conta**.
* **Ordenação Externa (Fora do Escopo):** O sistema em Java assume como premissa que o arquivo `contas_bancarias.csv` já é fornecido **previamente ordenado** pela chave primária. A ordenação respeita a hierarquia da chave composta (ordena-se primeiro pela agência e, em seguida, pela conta de forma estável). O processo de ordenação em si não faz parte do escopo desta aplicação.
* **Geração dos Dados:** Toda a massa de dados é gerada de forma externa por um algoritmo em Python (com possibilidade de uso de IA para mock de dados).

## Divisão de Responsabilidades

Conforme detalhado no documento *Esboço de Arquitetura e Escopo* (`docs/`), o desenvolvimento foi fatiado da seguinte maneira:

### Davi
* Geração do arquivo de dados e algoritmo estrutural (Python).
* Implementação do algoritmo de busca de conta pelo **Nome do Titular**.
* Implementação do algoritmo de busca pela **Chave Primária** (Agência + Conta).

### Mateus
* Implementação do algoritmo de busca de conta por **CPF**.
* Implementação do algoritmo de busca de conta por **Chave Pix**. (A fazer)
* Implementação da interface gráfica base.

### Responsabilidade Compartilhada
* Uso da interface gráfica no projeto e integração do motor de busca com a biblioteca visual importada.

## 💻 Como Executar

O projeto é dividido em duas etapas fundamentais: a geração da massa de dados e a execução do motor de processamento.

### 🐍 1. Gerando a Massa de Dados (Python-Opcional)
Antes de rodar a aplicação em Java, é necessário gerar o banco de dados simulado (`.csv`).

**Pré-requisitos:** Python 3 instalado.

No terminal, a partir da raiz do projeto, siga os passos:

1. Acesse o diretório de scripts:
   `cd scripts`
2. Crie um ambiente virtual para isolar as dependências:
   `python3 -m venv venv`
3. Ative o ambiente virtual:
   * **Mac/Linux:** `source venv/bin/activate`
   * **Windows:** `venv\Scripts\activate`
4. Instale as bibliotecas necessárias:
   `pip install -r requirements.txt`
5. Execute o gerador de dados:
   `python gerador_dados.py`

*O script criará automaticamente a pasta `data/` na raiz do projeto e salvará o arquivo `contas_bancarias.csv` perfeitamente ordenado.*

### ☕ 2. Executando o Motor de Busca (Java) - EM DESENVOLVIMENTO
Com o arquivo de dados devidamente gerado e posicionado em `data/contas_bancarias.csv`, o motor está pronto para uso.

**Pré-requisitos:** JDK 25 ou superior.

1. Abra o projeto na sua IDE de preferência (IntelliJ IDEA, Eclipse, VS Code).
2. Certifique-se de que a pasta `src/` está marcada como o diretório de fontes (Sources Root).
3. Navegue até o arquivo `src/Main.java`.
4. Execute a classe `Main`. O sistema carregará a massa de dados para a memória (Heap) instantaneamente e inicializará a interface/terminal para as buscas.

## Equipe de Desenvolvimento

| <img src="docs/assets/fotos/Davi-UnB.png" width="120px;" alt="Davi Freitas"/><br />**Davi Freitas** | <img src="docs/assets/fotos/Mateus0xC.png" width="120px;" alt="Mateus Barreto"/><br />**Mateus Barreto** |
| :---: | :---: |
| Matrícula: **241011018** | Matrícula: **241011466** |
| <img src="https://github.com/Davi-UnB.png" width="16px;"/> [`@Davi-UnB`](https://github.com/Davi-UnB) | <img src="https://github.com/Mateus0xC.png" width="16px;"/> [`@Mateus0xC`](https://github.com/Mateus0xC) <img src="https://github.com/gecko1205.png" width="16px;"/> [`@gecko1205`](https://github.com/gecko1205) |