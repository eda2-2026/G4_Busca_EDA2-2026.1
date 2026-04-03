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
* Implementação do algoritmo de busca de conta por **Chave Pix**.
* Desenvolvimento e disponibilização da biblioteca visual prévia.

### Responsabilidade Compartilhada
* Implementação do código da interface gráfica no projeto e integração do motor de busca com a biblioteca visual importada.

## Equipe de Desenvolvimento

<div style="display: flex; gap: 20px; flex-wrap: wrap;">

<div style="border: 1px solid #e0e0e0; border-radius: 10px; padding: 20px; width: 250px; text-align: center; box-shadow: 0 4px 6px rgba(0,0,0,0.05); background: var(--md-code-bg-color);">
    <div style="width: 120px; height: 120px; background-color: #ddd; border-radius: 30%; margin: 0 auto 15px; overflow: hidden;">
         <img src="docs/assets/fotos/Davi-UnB.png" alt="Foto de Davi" style="width: 100%; height: 100%; object-fit: cover;">
    </div>
    <h3 style="margin: 0 0 5px; font-size: 1.2em;">Davi Freitas</h3>
    <p style="margin: 0 0 15px; font-size: 0.9em; font-weight: 500;">Matrícula: 241011018</p>
    <div style="display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 0.9em;">
        <img src="https://github.com/Davi-UnB.png" alt="GitHub Avatar" style="width: 16px; height: 16px; border-radius: 50%; display: block;">
        <a href="https://github.com/Davi-UnB" target="_blank" style="text-decoration: none; opacity: 0.8; line-height: 16px;">@Davi-UnB</a>
    </div>
</div>

<div style="border: 1px solid #e0e0e0; border-radius: 10px; padding: 20px; width: 250px; text-align: center; box-shadow: 0 4px 6px rgba(0,0,0,0.05); background: var(--md-code-bg-color);">
    <div style="width: 120px; height: 120px; background-color: #ddd; border-radius: 30%; margin: 0 auto 15px; overflow: hidden;">
         <img src="docs/assets/fotos/Mateus0xC.png" alt="Foto de Mateus" style="width: 100%; height: 100%; object-fit: cover;">
    </div>
    <h3 style="margin: 0 0 5px; font-size: 1.2em;">Mateus Barreto</h3>
    <p style="margin: 0 0 15px; font-size: 0.9em; font-weight: 500;">Matrícula: 241011466</p>
    <div style="display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 0.9em;">
        <img src="https://github.com/Mateus0xC.png" alt="GitHub Avatar" style="width: 16px; height: 16px; border-radius: 50%; display: block;">
        <a href="https://github.com/Mateus0xC" target="_blank" style="text-decoration: none; opacity: 0.8; line-height: 16px;">@Mateus0xC</a>
    </div>
</div>

</div>