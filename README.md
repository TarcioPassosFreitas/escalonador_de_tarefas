# Escalonador de Tarefas Round Robin

![Java Version](https://img.shields.io/badge/Java-17-blue.svg)
![Gradle Build](https://img.shields.io/badge/Build-Gradle-brightgreen.svg)

## Índice

1. [Iniciando](#como-usar)
   - [Pré-requisitos](#pré-requisitos)
   - [Construir o Projeto](#construir-o-projeto)
   - [Executar o Projeto](#executar-o-projeto)
   - [Estrutura de Diretórios](#estrutura-de-diretórios)
3. [Introdução](#introdução)
4. [Fundamentos Teóricos](#fundamentos-teóricos)
    - [Processos e Estados](#processos-e-estados)
    - [Escalonamento de Processos](#escalonamento-de-processos)
    - [Algoritmo Round Robin](#algoritmo-round-robin)
    - [Bloco de Controle de Processo (BCP)](#bloco-de-controle-de-processo-bcp)
5. [Descrição do Sistema](#descrição-do-sistema)
    - [Estrutura do Projeto](#estrutura-do-projeto)
    - [Classes Principais](#classes-principais)
    - [Fluxo de Execução](#fluxo-de-execução)
6. [Implementação](#implementação)
    - [ProcessLoader](#processloader)
    - [Scheduler](#scheduler)
    - [Logger](#logger)
    - [Main](#main)
7. [Execução e Logs](#execução-e-logs)
    - [Configuração Inicial](#configuração-inicial)
    - [Exemplo de Execução](#exemplo-de-execução)
    - [Análise do Log Gerado](#análise-do-log-gerado)
8. [Resultados e Avaliação](#resultados-e-avaliação)
    - [Métricas Coletadas](#métricas-coletadas)
    - [Impacto do Quantum](#impacto-do-quantum)
    - [Recomendações](#recomendações)
9. [Conclusão](#conclusão)
10. [Referências](#referências)


---
## Como Usar

### Pré-requisitos

- **Java 17** instalado
- **Gradle** instalado
- **Lombok** plugin configurado no seu IDE

### Construir o Projeto

Clone o repositório e navegue até o diretório do projeto:

```bash
git clone https://github.com/TarcioPassosFreitas/escalonador_de_tarefas.git
cd escalonador_de_tarefas
````
Compile o projeto usando Gradle:

````bash
gradle build
````

### Executar o Projeto

Certifique-se de que o arquivo quantum.txt está configurado corretamente em src/main/resources/quantum/quantum.txt e os arquivos de entrada que deseja estejam em src/main/resources/inserts.
Execute a aplicação:

````bash
gradle run
````

Os logs serão gerados no diretório src/main/resources/logs com o formato logXX_timestamp.txt.

### Estrutura de Diretórios
````plaintext
├── src
│   ├── main
│   │   ├── java
│   │   │   └── br
│   │   │       └── com
│   │   │           └── escalonadorTarefas
│   │   │               ├── enums
│   │   │               │   └── ProcessState.java
│   │   │               ├── models
│   │   │               │   └── ProcessControlBlock.java
│   │   │               ├── services
│   │   │               │   ├── Logger.java
│   │   │               │   ├── ProcessLoader.java
│   │   │               │   └── Scheduler.java
│   │   │               └── Main.java
│   │   └── resources
│   │       ├── inserts
│   │       │   ├── prog_01.txt
│   │       │   ├── prog_02.txt
│   │       │   └── ... (outros programas)
│   │       ├── logs
│   │       └── quantum
│   │           └── quantum.txt
├── build.gradle
└── README.md

````

---

## Introdução

Este projeto implementa um **escalonador de tarefas de tempo compartilhado** utilizando o algoritmo **Round Robin**. Desenvolvido em **Java 17** com **Gradle** como sistema de build, o escalonador simula a execução de processos em uma máquina fictícia equipada com registradores de uso geral e um conjunto limitado de instruções. O objetivo principal é demonstrar conceitos fundamentais de **Sistemas Operacionais**, como gerenciamento de processos, escalonamento e chamadas de sistema.

---

## Fundamentos Teóricos

### Processos e Estados

**Processo** é uma instância de um programa em execução. Cada processo possui seu próprio espaço de endereçamento, registradores e recursos. Em Sistemas Operacionais, um processo pode estar em um dos seguintes estados:

- **Pronto (READY):** O processo está apto para executar e aguarda alocação da CPU.
- **Executando (RUNNING):** O processo está atualmente utilizando a CPU.
- **Bloqueado (BLOCKED):** O processo está aguardando a conclusão de uma operação de E/S ou outro evento.
- **Terminando (TERMINATED):** O processo concluiu sua execução e está sendo removido da memória.

### Escalonamento de Processos

O **escalonamento de processos** é a tarefa do sistema operacional de decidir qual processo na fila de prontos será alocado para a CPU. O objetivo é otimizar a utilização da CPU, minimizar o tempo de resposta e garantir justiça entre os processos.

### Algoritmo Round Robin

O **Round Robin** é um algoritmo de escalonamento preemptivo que aloca um **quantum** de tempo fixo para cada processo na fila de prontos. Após o quantum expirar, o processo é interrompido e colocado no final da fila, permitindo que o próximo processo seja executado. Este método garante que todos os processos recebam uma quantidade justa de tempo de CPU.

### Bloco de Controle de Processo (BCP)

O **Bloco de Controle de Processo (BCP)** é uma estrutura de dados que mantém todas as informações necessárias para gerenciar e executar um processo. Inclui:

- **Identificação do Processo:** ID e nome.
- **Contador de Programa (PC):** Indica a próxima instrução a ser executada.
- **Registradores:** A, B, C, D.
- **Estado:** READY, RUNNING, BLOCKED, TERMINATED.
- **Instruções:** Referência ao código do processo.
- **Tempo de Espera:** Quantos quanta o processo deve aguardar antes de retornar à fila de prontos.
- **Contador de Interrupções:** Número de vezes que o processo foi interrompido.

---

## Descrição do Sistema

### Estrutura do Projeto

O projeto está organizado nos seguintes pacotes e classes:

- `br.com.escalonadorTarefas.enums`
    - `ProcessState`: Enumeração dos estados dos processos.
- `br.com.escalonadorTarefas.models`
    - `ProcessControlBlock`: Representa o BCP.
- `br.com.escalonadorTarefas.services`
    - `ProcessLoader`: Carrega processos a partir de arquivos.
    - `Scheduler`: Implementa o algoritmo Round Robin.
    - `Logger`: Gera e gerencia os logs de execução.
- `br.com.escalonadorTarefas`
    - `Main`: Classe principal que inicializa e executa o sistema.

### Classes Principais

- **ProcessControlBlock (BCP):** Mantém o estado e informações de cada processo.
- **ProcessLoader:** Responsável por ler arquivos de texto que contêm os programas e inicializar os BCPs.
- **Scheduler:** Gerencia as filas de prontos e bloqueados, executa os processos conforme o algoritmo Round Robin e coleta estatísticas.
- **Logger:** Registra eventos importantes da execução, como carregamento de processos, interrupções, E/S, terminações e estatísticas finais.

### Fluxo de Execução

1. **Carregamento de Processos:** O `ProcessLoader` lê os arquivos de programa e inicializa os BCPs, adicionando-os à fila de prontos.
2. **Inicialização do Scheduler:** O `Scheduler` recebe os processos e começa a executá-los de acordo com o algoritmo Round Robin.
3. **Execução dos Processos:** Cada processo executa até o limite do quantum ou até encontrar uma instrução de E/S ou SAIDA.
4. **Interrupções e Bloqueios:** Ao final do quantum ou ao encontrar E/S, o processo é interrompido e movido para a fila apropriada.
5. **Terminação:** Quando um processo executa SAIDA, ele é removido da memória e suas estatísticas são registradas.
6. **Registro de Logs:** O `Logger` captura todos os eventos e gera um arquivo de log detalhado.
7. **Cálculo de Estatísticas:** Ao final, o sistema calcula médias de trocas de processo e instruções por quantum, registrando no log.

---

## Implementação

### ProcessLoader

Responsável por carregar os processos a partir dos arquivos de texto localizados no diretório `src/main/resources/inserts`. Cada arquivo representa um programa com até 21 instruções, iniciando com o nome do processo.

**Funcionamento:**

- Lê o nome do processo da primeira linha.
- Lê as instruções subsequentes até SAIDA.
- Inicializa o BCP com as informações lidas.

**Exemplo de Arquivo de Programa (`prog_02.txt`):**

```plaintext
PROG-2
A=8
E/S
COM
COM
COM
E/S
B=10
C=2
COM
COM
E/S
COM
COM
E/S
COM
COM
E/S
D=3
A=9
B=1
COM
COM
SAIDA
````


### Scheduler

Implementa o algoritmo Round Robin, gerenciando as filas de prontos e bloqueados.

**Componentes:**

- **Filas:**
    - `readyQueue`: Fila de processos prontos para execução.
    - `blockedQueue`: Fila de processos bloqueados aguardando E/S.
- **Listas:**
    - `terminatedProcesses`: Lista de processos que concluíram a execução.
- **Estatísticas:**
    - `contextSwitchCount`: Número total de trocas de contexto.
    - `totalQuantumExecutions`: Total de quanta executados.
    - `totalInstructionsInQuantums`: Total de instruções executadas nos quanta.

**Funcionamento:**

1. **Adicionar Processos:** Adiciona processos à fila de prontos.
2. **Executar Processos:** Executa processos na ordem da fila, respeitando o quantum.
3. **Interrupções:** Move processos para a fila de bloqueados ou para a fila de prontos após interrupção.
4. **Desbloquear Processos:** Decrementa o tempo de espera dos processos bloqueados e os move para a fila de prontos quando o tempo expira.
5. **Coleta de Estatísticas:** Atualiza contadores de interrupções e instruções executadas.

### Logger

Responsável por registrar todos os eventos durante a execução do escalonador.

**Características:**

- Gera arquivos de log com o formato `logXX_timestamp.txt`, onde `XX` é o valor do quantum e `timestamp` é o momento da execução.
- Registra eventos como carregamento de processos, execuções, interrupções, operações de E/S e terminações.
- Ao final, registra as estatísticas médias solicitadas.

**Exemplo de Log:**

```plaintext
Carregando PROG-1
Carregando PROG-2
...
Executando PROG-1
Interrompendo PROG-1 após 4 instruções
E/S iniciada em PROG-1
...
PROG-1 terminado. A=5. B=2. C=2. D=10
...
MÉDIA DE TROCAS: 5,70
MÉDIA DE INSTRUÇÕES: 2,28
QUANTUM: 4

````




### Main

Classe principal que orquestra todo o processo de escalonamento.

**Passos:**

1. **Leitura do Quantum:** Lê o valor do quantum a partir do arquivo `quantum.txt`.
2. **Inicialização do Logger:** Cria um arquivo de log baseado no quantum e timestamp.
3. **Carregamento de Processos:** Utiliza o `ProcessLoader` para carregar os processos a partir dos arquivos de programa.
4. **Inicialização do Scheduler:** Cria o `Scheduler` e adiciona os processos à fila de prontos.
5. **Execução do Scheduler:** Inicia o escalonamento e execução dos processos.
6. **Cálculo de Estatísticas:** Calcula e registra as estatísticas finais no log.
7. **Fechamento do Logger:** Fecha o arquivo de log para garantir que todos os dados sejam salvos corretamente.

---

## Execução e Logs

### Configuração Inicial

- **Diretórios:**
    - `src/main/resources/inserts`: Contém os arquivos de programas (`prog_01.txt`, `prog_02.txt`, etc.).
    - `src/main/resources/logs`: Onde os arquivos de log serão gerados.
    - `src/main/resources/quantum`: Contém o arquivo `quantum.txt` com o valor do quantum.

- **Arquivos de Programa:** Cada arquivo `.txt` representa um programa com até 21 instruções, iniciando com o nome do processo.

### Exemplo de Execução

Considerando o quantum definido em `quantum.txt` como **4**, a execução gerou um arquivo de log denominado `log04_1738076613947.txt`.

**Conteúdo do Log:**

```plaintext
Carregando PROG-1
Carregando PROG-2
Carregando PROG-3
Carregando PROG-4
Carregando PROG-5
Carregando PROG-6
Carregando PROG-7
Carregando PROG-8
Carregando PROG-9
Carregando PROG-10
Executando PROG-1
Interrompendo PROG-1 após 4 instruções
Executando PROG-2
Interrompendo PROG-2 após 1 instruções
E/S iniciada em PROG-2
Executando PROG-3
Interrompendo PROG-3 após 1 instruções
E/S iniciada em PROG-3
Executando PROG-4
Interrompendo PROG-4 após 3 instruções
E/S iniciada em PROG-4
Executando PROG-5
Interrompendo PROG-5 após 2 instruções
E/S iniciada em PROG-5
Executando PROG-6
Interrompendo PROG-6 após 3 instruções
E/S iniciada em PROG-6
Executando PROG-7
Interrompendo PROG-7 após 3 instruções
E/S iniciada em PROG-7
Executando PROG-8
Interrompendo PROG-8 após 4 instruções
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
E/S iniciada em PROG-9
Executando PROG-10
Interrompendo PROG-10 após 4 instruções
Executando PROG-1
Interrompendo PROG-1 após 2 instruções
E/S iniciada em PROG-1
Executando PROG-8
Interrompendo PROG-8 após 4 instruções
Executando PROG-2
Interrompendo PROG-2 após 3 instruções
E/S iniciada em PROG-2
Executando PROG-10
Interrompendo PROG-10 após 4 instruções
Executando PROG-3
Interrompendo PROG-3 após 4 instruções
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-8
Interrompendo PROG-8 após 4 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-6
Interrompendo PROG-6 após 4 instruções
Executando PROG-10
Interrompendo PROG-10 após 1 instruções
E/S iniciada em PROG-10
Executando PROG-7
Interrompendo PROG-7 após 4 instruções
Executando PROG-3
Interrompendo PROG-3 após 4 instruções
Executando PROG-9
Interrompendo PROG-9 após 4 instruções
Executando PROG-8
Interrompendo PROG-8 após 4 instruções
Executando PROG-1
Interrompendo PROG-1 após 3 instruções
E/S iniciada em PROG-1
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
E/S iniciada em PROG-6
Executando PROG-2
Interrompendo PROG-2 após 4 instruções
Executando PROG-7
Interrompendo PROG-7 após 4 instruções
Executando PROG-3
Interrompendo PROG-3 após 4 instruções
Executando PROG-9
Interrompendo PROG-9 após 4 instruções
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-8
Interrompendo PROG-8 após 4 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 0 instruções
E/S iniciada em PROG-2
Executando PROG-10
PROG-10 terminado. A=0. B=0. C=0. D=0
Executando PROG-7
Interrompendo PROG-7 após 0 instruções
E/S iniciada em PROG-7
Executando PROG-3
Interrompendo PROG-3 após 1 instruções
E/S iniciada em PROG-3
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
E/S iniciada em PROG-9
Executando PROG-8
PROG-8 terminado. A=5. B=10. C=12. D=1
Executando PROG-1
Interrompendo PROG-1 após 1 instruções
E/S iniciada em PROG-1
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
E/S iniciada em PROG-6
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
E/S iniciada em PROG-2
Executando PROG-7
PROG-7 terminado. A=3. B=5. C=1. D=2
Executando PROG-3
PROG-3 terminado. A=3. B=91. C=10. D=4
Executando PROG-9
PROG-9 terminado. A=0. B=0. C=0. D=0
Executando PROG-1
Interrompendo PROG-1 após 1 instruções
E/S iniciada em PROG-1
Executando PROG-6
PROG-6 terminado. A=9. B=5. C=0. D=0
Executando PROG-4
Interrompendo PROG-4 após 3 instruções
E/S iniciada em PROG-4
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
E/S iniciada em PROG-2
Executando PROG-1
PROG-1 terminado. A=5. B=2. C=2. D=10
Executando PROG-4
PROG-4 terminado. A=2. B=5. C=3. D=9
Executando PROG-5
PROG-5 terminado. A=0. B=3. C=2. D=0
Executando PROG-2
Interrompendo PROG-2 após 4 instruções
Executando PROG-2
PROG-2 terminado. A=9. B=1. C=2. D=3
MÉDIA DE TROCAS: 5,70
MÉDIA DE INSTRUÇÕES: 2,28
QUANTUM: 4


````


Considerando o quantum definido em `quantum.txt` como **2**, a execução gerou um arquivo de log denominado `log02_1738109588222.txt`.

**Conteúdo do Log:**

```plaintext
Carregando PROG-1
Carregando PROG-2
Carregando PROG-3
Carregando PROG-4
Carregando PROG-5
Carregando PROG-6
Carregando PROG-7
Carregando PROG-8
Carregando PROG-9
Carregando PROG-10
Executando PROG-1
Interrompendo PROG-1 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 1 instruções
E/S iniciada em PROG-2
Executando PROG-3
Interrompendo PROG-3 após 1 instruções
E/S iniciada em PROG-3
Executando PROG-4
Interrompendo PROG-4 após 2 instruções
Executando PROG-5
Interrompendo PROG-5 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
Executando PROG-7
Interrompendo PROG-7 após 2 instruções
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-10
Interrompendo PROG-10 após 2 instruções
Executando PROG-1
Interrompendo PROG-1 após 2 instruções
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-5
Interrompendo PROG-5 após 0 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 1 instruções
E/S iniciada em PROG-6
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-7
Interrompendo PROG-7 após 1 instruções
E/S iniciada em PROG-7
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-9
Interrompendo PROG-9 após 0 instruções
E/S iniciada em PROG-9
Executando PROG-10
Interrompendo PROG-10 após 2 instruções
Executando PROG-1
Interrompendo PROG-1 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 1 instruções
E/S iniciada em PROG-2
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
Executando PROG-10
Interrompendo PROG-10 após 2 instruções
Executando PROG-7
Interrompendo PROG-7 após 2 instruções
Executando PROG-1
Interrompendo PROG-1 após 0 instruções
E/S iniciada em PROG-1
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
Executando PROG-10
Interrompendo PROG-10 após 2 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-7
Interrompendo PROG-7 após 2 instruções
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-1
Interrompendo PROG-1 após 2 instruções
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
Executando PROG-10
Interrompendo PROG-10 após 1 instruções
E/S iniciada em PROG-10
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-7
Interrompendo PROG-7 após 2 instruções
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 0 instruções
E/S iniciada em PROG-2
Executando PROG-1
Interrompendo PROG-1 após 1 instruções
E/S iniciada em PROG-1
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 0 instruções
E/S iniciada em PROG-6
Executando PROG-7
Interrompendo PROG-7 após 2 instruções
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-10
PROG-10 terminado. A=0. B=0. C=0. D=0
Executando PROG-3
Interrompendo PROG-3 após 2 instruções
Executando PROG-4
Interrompendo PROG-4 após 2 instruções
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-5
Interrompendo PROG-5 após 1 instruções
E/S iniciada em PROG-5
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-7
Interrompendo PROG-7 após 0 instruções
E/S iniciada em PROG-7
Executando PROG-1
Interrompendo PROG-1 após 1 instruções
E/S iniciada em PROG-1
Executando PROG-9
Interrompendo PROG-9 após 2 instruções
Executando PROG-6
Interrompendo PROG-6 após 2 instruções
Executando PROG-3
Interrompendo PROG-3 após 1 instruções
E/S iniciada em PROG-3
Executando PROG-4
Interrompendo PROG-4 após 1 instruções
E/S iniciada em PROG-4
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 0 instruções
E/S iniciada em PROG-2
Executando PROG-5
PROG-5 terminado. A=0. B=3. C=2. D=0
Executando PROG-9
Interrompendo PROG-9 após 0 instruções
E/S iniciada em PROG-9
Executando PROG-6
Interrompendo PROG-6 após 0 instruções
E/S iniciada em PROG-6
Executando PROG-7
PROG-7 terminado. A=3. B=5. C=1. D=2
Executando PROG-1
Interrompendo PROG-1 após 1 instruções
E/S iniciada em PROG-1
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-3
PROG-3 terminado. A=3. B=91. C=10. D=4
Executando PROG-4
PROG-4 terminado. A=2. B=5. C=3. D=9
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-9
PROG-9 terminado. A=0. B=0. C=0. D=0
Executando PROG-8
Interrompendo PROG-8 após 2 instruções
Executando PROG-6
PROG-6 terminado. A=9. B=5. C=0. D=0
Executando PROG-1
PROG-1 terminado. A=5. B=2. C=2. D=10
Executando PROG-2
Interrompendo PROG-2 após 0 instruções
E/S iniciada em PROG-2
Executando PROG-8
PROG-8 terminado. A=5. B=10. C=12. D=1
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-2
Interrompendo PROG-2 após 2 instruções
Executando PROG-2
PROG-2 terminado. A=9. B=1. C=2. D=3
MÉDIA DE TROCAS: 8,90
MÉDIA DE INSTRUÇÕES: 1,46
QUANTUM: 2


````

### Análise do Log Gerado

- **Carregamento de Processos:** Indica a ordem em que os processos foram carregados na fila de prontos.
- **Execução e Interrupções:** Mostra qual processo está sendo executado e quantas instruções foram executadas até a interrupção (por quantum, E/S ou SAIDA).
- **Operações de E/S:** Indica quando um processo iniciou uma operação de E/S, movendo-se para a fila de bloqueados.
- **Terminações:** Indica quando um processo concluiu sua execução, incluindo os valores finais dos registradores.
- **Estatísticas Finais:**
    - **MÉDIA DE TROCAS:** Média de interrupções por processo.
    - **MÉDIA DE INSTRUÇÕES:** Média de instruções executadas por quantum.
    - **QUANTUM:** Valor do quantum utilizado na execução.

---

## Resultados e Avaliação

### Métricas Coletadas

Durante as execuções do escalonador com diferentes valores de quantum (`n`), foram coletadas as seguintes métricas:

- **Número Médio de Trocas de Processo por Processo (`MÉDIA DE TROCAS`):** Representa a média de vezes que cada processo foi interrompido durante sua execução.
- **Número Médio de Instruções Executadas por Quantum (`MÉDIA DE INSTRUÇÕES`):** Indica a média de instruções executadas antes de uma interrupção ocorrer (por quantum, E/S ou SAIDA).

### Impacto do Quantum

O valor do quantum (`n`) influencia diretamente no comportamento do escalonador e nas métricas coletadas:

- **Quantum Pequeno (`n` baixo):**

    **Prós:**
    - Maior responsividade, processos recebem mais vezes a CPU.
    - Redução do tempo de espera para processos interativos.

    **Contras:**
    - Maior número de trocas de contexto, aumentando a sobrecarga do sistema.
    - Maior média de instruções por quantum, podendo levar a execuções mais fragmentadas.

- **Quantum Grande (`n` alto):**

    **Prós:**
    - Menor número de trocas de contexto, reduzindo a sobrecarga.
    - Execução mais contínua dos processos, eficiente para processos de longa duração.

    **Contras:**
    - Menor responsividade para processos interativos.
    - Maior tempo de espera para processos que chegam após os primeiros.

### Recomendações

Com base nas métricas coletadas, recomenda-se escolher um valor de quantum que balanceie a **responsividade** e a **eficiência** do sistema. Um valor intermediário de `n` geralmente proporciona um bom equilíbrio, minimizando o número de trocas de contexto sem comprometer a responsividade dos processos.

**Exemplo de Seleção:**

- **Quantum = 4:**
    - **MÉDIA DE TROCAS:** 5,70
    - **MÉDIA DE INSTRUÇÕES:** 2,28
    - **Conclusão:** Valor equilibrado que oferece boa responsividade e controle de sobrecarga.

---

## Conclusão

A implementação do **escalonador de tarefas Round Robin** atendeu aos requisitos propostos, demonstrando a aplicação prática de conceitos fundamentais de **Sistemas Operacionais**, como gerenciamento de processos, escalonamento e gerenciamento de estados. A coleta e análise das métricas permitiram avaliar o impacto do valor do quantum na eficiência e responsividade do sistema, fornecendo insights valiosos para a otimização do escalonamento.

O sistema desenvolve uma simulação robusta, permitindo a análise detalhada do comportamento do algoritmo Round Robin em diferentes cenários, contribuindo para a compreensão aprofundada dos mecanismos de escalonamento em Sistemas Operacionais.

---

## Referências

- Silberschatz, A., Galvin, P. B., & Gagne, G. (2018). *Operating System Concepts*. Wiley.
- Tanenbaum, A. S., & Bos, H. (2015). *Modern Operating Systems*. Pearson.

---
