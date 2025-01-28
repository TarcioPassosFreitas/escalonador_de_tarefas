package br.com.escalonadorTarefas.services;

import br.com.escalonadorTarefas.models.ProcessControlBlock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por carregar os processos a partir de arquivos de texto.
 * <p>
 * Cada arquivo contém:
 * - Primeira linha: nome do processo.
 * - Linhas seguintes: instruções (ex: A=10, COM, E/S, SAIDA, etc.).
 */
public class ProcessLoader {

    /**
     * Carrega os processos a partir de uma lista de nomes de arquivos.
     *
     * @param fileNames Lista de nomes de arquivos com programas.
     * @return Lista de Blocos de Controle de Processo carregados.
     */
    public List<ProcessControlBlock> loadProcesses(List<String> fileNames) {
        List<ProcessControlBlock> processes = new ArrayList<>();
        int processId = 1;

        for (String fileName : fileNames) {
            try {
                ClassLoader classLoader = ProcessLoader.class.getClassLoader();
                BufferedReader reader = new BufferedReader(
                        new FileReader(Paths.get(classLoader.getResource("inserts/" + fileName).toURI()).toFile())
                );

                String processName = reader.readLine();
                if (processName == null) {
                    System.err.println("Arquivo " + fileName + " não contém nome de processo.");
                    continue;
                }

                List<String> instructions = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    instructions.add(line.trim());
                }

                ProcessControlBlock pcb = new ProcessControlBlock();
                pcb.setProcessId(processId++);
                pcb.setProcessName(processName);
                pcb.setInstructions(instructions);
                pcb.setProgramCounter(0);

                processes.add(pcb);
            } catch (Exception e) {
                System.err.println("Erro ao carregar o processo do arquivo " + fileName + ": " + e.getMessage());
            }
        }

        return processes;
    }

}

