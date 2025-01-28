package br.com.escalonadorTarefas.services;

import br.com.escalonadorTarefas.models.ProcessControlBlock;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsável por registrar eventos do escalonador em um arquivo de log.
 * <p>
 * O nome do arquivo de log é gerado com base no valor do quantum e um timestamp,
 * para evitar sobrescrita de logs quando rodamos várias vezes.
 * <p>
 * Os arquivos de log serão criados no diretório "src/main/resources/logs".
 */
public class Logger {

    /**
     * Caminho completo onde o log será gravado.
     * -- GETTER --
     *

     */
    @Getter
    private final String logFileName;

    /**
     * Responsável por escrever no arquivo de log.
     */
    private BufferedWriter writer;

    /**
     * Construtor do Logger.
     * Cria um arquivo de log dentro de "src/main/resources/logs" com nome único,
     *
     * @param quantum Valor do quantum utilizado, para inclusão no nome do arquivo de log.
     */
    public Logger(int quantum) {
        String logsPath = "src/main/resources/logs";

        File dirLogs = new File(logsPath);
        if (!dirLogs.exists()) {
            dirLogs.mkdirs();
        }

        String uniqueName = String.format("log%02d_%d.txt", quantum, System.currentTimeMillis());

        this.logFileName = logsPath + File.separator + uniqueName;

        try {
            this.writer = new BufferedWriter(new FileWriter(logFileName));
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo de log: " + e.getMessage());
        }
    }

    /**
     * Método interno para escrever no arquivo de log.
     *
     * @param message Mensagem a ser registrada no log.
     */
    private void log(String message) {
        try {
            if (writer != null) {
                writer.write(message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de log: " + e.getMessage());
        }
    }

    /**
     * Registra o carregamento de um processo.
     *
     * @param processName Nome do processo carregado.
     */
    public void logProcessLoaded(String processName) {
        log("Carregando " + processName);
    }

    /**
     * Registra o início da execução de um processo.
     *
     * @param processName Nome do processo que entrou em execução.
     */
    public void logProcessExecution(String processName) {
        log("Executando " + processName);
    }

    /**
     * Registra a interrupção de um processo após a execução de um número específico de instruções.
     *
     * @param processName          Nome do processo interrompido.
     * @param instructionsExecuted Número de instruções executadas antes da interrupção.
     */
    public void logProcessInterruption(String processName, int instructionsExecuted) {
        log("Interrompendo " + processName + " após " + instructionsExecuted + " instruções");
    }

    /**
     * Registra o início de uma operação de E/S em um processo.
     *
     * @param processName Nome do processo que iniciou a E/S.
     */
    public void logProcessIOStart(String processName) {
        log("E/S iniciada em " + processName);
    }

    /**
     * Registra a finalização de um processo, incluindo os valores finais de seus registradores.
     *
     * @param pcb Bloco de Controle do Processo finalizado.
     */
    public void logProcessTermination(ProcessControlBlock pcb) {
        log(String.format("%s terminado. A=%d. B=%d. C=%d. D=%d",
                pcb.getProcessName(),
                pcb.getRegisterA(),
                pcb.getRegisterB(),
                pcb.getRegisterC(),
                pcb.getRegisterD()));
    }

    /**
     * Registra as estatísticas finais do escalonador.
     *
     * @param averageSwitches     Número médio de trocas de processo por processo.
     * @param averageInstructions Número médio de instruções executadas por processo.
     * @param quantum             Valor do quantum utilizado.
     */
    public void logFinalStatistics(double averageSwitches, double averageInstructions, int quantum) {
        log(String.format("MÉDIA DE TROCAS: %.2f", averageSwitches));
        log(String.format("MÉDIA DE INSTRUÇÕES: %.2f", averageInstructions));
        log("QUANTUM: " + quantum);
    }

    /**
     * Registra uma mensagem de erro no log
     *
     * @param errorMessage Mensagem de erro a ser registrada.
     */
    public void logError(String errorMessage) {
        log("[ERRO] " + errorMessage);
    }

    /**
     * Fecha o arquivo de log, liberando os recursos de IO.
     */
    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar o arquivo de log: " + e.getMessage());
        }
    }

}
