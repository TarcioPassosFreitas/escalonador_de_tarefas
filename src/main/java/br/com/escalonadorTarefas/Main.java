package br.com.escalonadorTarefas;

import br.com.escalonadorTarefas.models.ProcessControlBlock;
import br.com.escalonadorTarefas.services.Logger;
import br.com.escalonadorTarefas.services.ProcessLoader;
import br.com.escalonadorTarefas.services.Scheduler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe principal que inicializa o sistema de escalonamento Round Robin.
 *
 * <p>Passo a passo resumido:
 * 1) Lê o quantum de "quantum.txt".
 * 2) Inicializa o Logger (gera log com base no quantum e um timestamp).
 * 3) Carrega os processos a partir de arquivos texto (prog_01.txt, prog_02.txt, etc.).
 * 4) Instancia o Scheduler e adiciona os processos.
 * 5) Executa o escalonador (round-robin).
 * 6) Calcula e registra estatísticas finais no log.
 * 7) Fecha o Logger.
 */
public class Main {

    public static void main(String[] args) {
        int quantum = loadQuantum("quantum.txt");
        Logger logger = new Logger(quantum);

        ClassLoader classLoader = Main.class.getClassLoader();
        File insertsDir;
        try {
            insertsDir = new File(classLoader.getResource("inserts").toURI());
        } catch (Exception e) {
            System.err.println("Erro ao localizar o diretório 'inserts': " + e.getMessage());
            return;
        }

        File[] txtFiles = insertsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado em " + insertsDir.getAbsolutePath());
            return;
        }

        Arrays.sort(txtFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

        List<String> processFiles = new ArrayList<>();
        for (File f : txtFiles) {
            processFiles.add(f.getName()); // Ex: "prog_01.txt"
        }

        ProcessLoader loader = new ProcessLoader();
        List<ProcessControlBlock> processes = loader.loadProcesses(processFiles);

        Scheduler scheduler = new Scheduler(quantum, logger);
        for (ProcessControlBlock pcb : processes) {
            scheduler.addProcess(pcb);
        }

        scheduler.execute();

        calculateAndLogStatistics(scheduler, processes, logger, quantum);

        logger.close();
    }


    /**
     * Lê o valor do quantum a partir de um arquivo texto
     * Se ocorrer erro, o programa é encerrado
     *
     * @param fileName Nome do arquivo que contém o valor do quantum (em inteiro).
     * @return Valor do quantum.
     */
    private static int loadQuantum(String fileName) {
        try {
            // Use o ClassLoader para localizar o arquivo em src/main/resources
            ClassLoader classLoader = Main.class.getClassLoader();
            String content = new String(Files.readAllBytes(Paths.get(classLoader.getResource("quantum/" + fileName).toURI())));
            return Integer.parseInt(content.trim());
        } catch (IOException | NumberFormatException | NullPointerException | URISyntaxException e) {
            System.err.println("Erro ao ler o arquivo " + fileName + ": " + e.getMessage());
            System.exit(1);
            return -1;
        }
    }


    /**
     * Calcula e registra as estatísticas finais do escalonador.
     *
     * @param scheduler  Instância do Scheduler para obter informações de trocas de contexto e processos terminados.
     * @param processes  Lista de processos
     * @param logger     Logger para registrar as informações.
     * @param quantum    Valor do quantum utilizado.
     */
    private static void calculateAndLogStatistics(Scheduler scheduler,
                                                  List<ProcessControlBlock> processes,
                                                  Logger logger,
                                                  int quantum) {
        int sumInterruptions = 0;
        for (ProcessControlBlock pcb : processes) {
            sumInterruptions += pcb.getInterruptionsCount();
        }
        double averageSwitches = (double) sumInterruptions / processes.size();

        double averageInstructions = 0.0;
        if (scheduler.getTotalQuantumExecutions() > 0) {
            averageInstructions = (double) scheduler.getTotalInstructionsInQuantums()
                    / scheduler.getTotalQuantumExecutions();
        }

        logger.logFinalStatistics(averageSwitches, averageInstructions, quantum);
    }
}
