import csv
import random
import os
from faker import Faker


def gerar_massa_bancaria(quantidade=100000): #Quantidade padrão definida: 100.000 (1.000.000 gera 94 mb; 10M gera 956 mb....)
    # Inicializa o Faker com localização brasileira para CPFs e Nomes reais
    fake = Faker('pt_BR')
    contas_geradas = []

    print(f"[*] Gerando {quantidade} registros bancários. Isso pode levar alguns segundos...")

    for _ in range(quantidade):
        agencia = f"{random.randint(1000, 4051)}"
        conta = f"{random.randint(10000, 99999)}-{random.randint(0, 9)}"
        cpf = fake.cpf()

        # Geração dos nomes com uma maior diversidade de tamanho 2-4.
        quantidade_sobrenomes = random.randint(1, 3)
        partes_do_nome = [fake.first_name()]
        for _ in range(quantidade_sobrenomes):
            partes_do_nome.append(fake.last_name())
        titular = " ".join(partes_do_nome)


        tipo_conta = random.choice(["Corrente", "Poupança", "Salário"])
        saldo = round(random.uniform(0.0, 150000.0), 2)

        # Varia as chaves Pix para simular o mundo real
        tipo_pix = random.choice(["cpf", "email", "telefone", "aleatoria"])

        if tipo_pix == "cpf":
            pix = cpf


        elif tipo_pix == "email":
            # Pega o primeiro nome (que pode ser composto), tira acentos e remove espaços
            primeiro_nome = partes_do_nome[0].lower()
            mapa_acentos = str.maketrans("áàãâéêíóôõúç", "aaaaeeiooouc")
            nome_limpo = primeiro_nome.translate(mapa_acentos).replace(" ", "")
            # Sorteia um provedor real e um número para dar naturalidade
            dominio = random.choice(["@gmail.com", "@hotmail.com", "@yahoo.com.br", "@outlook.com"])
            numero = random.randint(1, 999)
            pix = f"{nome_limpo}{numero}{dominio}"

        elif tipo_pix == "telefone":
            # Força o padrão exato de celular brasileiro: (XX) 9XXXX-XXXX
            ddd = random.randint(11, 99)
            prefixo = random.randint(1000, 9999)
            sufixo = random.randint(1000, 9999)
            pix = f"({ddd}) 9{prefixo}-{sufixo}"

        else:
            pix = fake.uuid4()

        contas_geradas.append({
            'agencia': agencia,
            'conta': conta,
            'cpf': cpf,
            'titular': titular,
            'tipo': tipo_conta,
            'saldo': saldo,
            'pix': pix
        })

    print("[*] Ordenando os dados pela chave primária composta (Agência -> Conta)...")
    # A mágica da ordenação dupla do Python acontece aqui:
    contas_geradas.sort(key=lambda x: (x['agencia'], x['conta']))

    # Resolve o caminho para salvar exatamente em ../data/contas_bancarias.csv
    diretorio_script = os.path.dirname(os.path.abspath(__file__))
    caminho_arquivo = os.path.join(diretorio_script, '..', 'data', 'contas_bancarias.csv')

    # Cria a pasta data/ caso ela não exista
    os.makedirs(os.path.dirname(caminho_arquivo), exist_ok=True)

    print(f"[*] Exportando para o arquivo: {caminho_arquivo}")
    with open(caminho_arquivo, mode='w', newline='', encoding='utf-8') as arquivo_csv:
        # Usando ';' como delimitador para não conflitar com vírgulas em números/textos
        writer = csv.DictWriter(arquivo_csv, fieldnames=['agencia', 'conta', 'cpf', 'titular', 'tipo', 'saldo', 'pix'],
                                delimiter=';')
        writer.writeheader()
        writer.writerows(contas_geradas)

    print(f"[+] Sucesso! {quantidade} registros ordenados e prontos para o Java.")


if __name__ == '__main__':
    gerar_massa_bancaria()