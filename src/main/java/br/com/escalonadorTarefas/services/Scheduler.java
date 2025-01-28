package br.com.escalonadorTarefas.services;

import br.com.escalonadorTarefas.enums.ProcessState;
import br.com.escalonadorTarefas.models.ProcessControlBlock;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Classe responsável pelo escalonamento dos processos utilizando o algoritmo Round Robin.
 * <p>
 * Mantém:
 * - Uma fila de processos prontos (readyQueue).
 * - Uma fila de processos bloqueados (blockedQueue).
 * - Uma lista de processos terminados (terminatedProcesses) para controle e estatísticas.
 * - Um contador de trocas de contexto (contextSwitchCount).
 */
public class Scheduler {

    private final Queue<ProcessControlBlock> readyQueue;
    private final Queue<ProcessControlBlock> blockedQueue;
    /**
     * -- GETTER --
     *
     */
    @Getter
    private final List<ProcessControlBlock> terminatedProcesses;
    private final int quantum;
    private final Logger logger;

    /**
     * -- GETTER --
     *
     */
    @Getter
    private int contextSwitchCount;

    /**
     * Para estatísticas de “média de instruções por quantum”:
     */
    @Getter
    private int totalQuantumExecutions = 0; //todo
    @Getter
    private int totalInstructionsInQuantums = 0; //todo

    /**
     * Construtor da classe Scheduler.
     *
     * @param quantum Valor do quantum de tempo
     * @param logger  Instância do Logger para registro das atividades.
     */
    public Scheduler(int quantum, Logger logger) {
        this.readyQueue = new LinkedList<>();
        this.blockedQueue = new LinkedList<>();
        this.terminatedProcesses = new LinkedList<>();
        this.quantum = quantum;
        this.logger = logger;
        this.contextSwitchCount = 0;
    }

    /**
     * Adiciona um processo à fila de prontos e registra no log.
     *
     * @param pcb Bloco de Controle do Processo a ser adicionado.
     */
    public void addProcess(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
        logger.logProcessLoaded(pcb.getProcessName());
    }

    /**
     * Executa o escalonador, processando os processos na fila de prontos e
     * tratando a fila de bloqueados conforme o algoritmo Round Robin.
     */
    public void execute() {
        while (!readyQueue.isEmpty() || !blockedQueue.isEmpty()) {
            ProcessControlBlock currentProcess = readyQueue.poll();

            if (currentProcess != null) {
                contextSwitchCount++;
                executeProcess(currentProcess);
                decrementBlockedProcessesWaitTime();
            } else {
                decrementBlockedProcessesWaitTime();
            }
        }
    }

    /**
     * Executa um processo por até 'quantum' instruções ou até que seja
     * bloqueado (E/S) ou termine (SAIDA).
     *
     * @param pcb Bloco de Controle do Processo a ser executado.
     */
    private void executeProcess(ProcessControlBlock pcb) {
        pcb.setState(ProcessState.RUNNING);
        logger.logProcessExecution(pcb.getProcessName());

        int instructionsExecuted = 0;
        boolean blockedOrTerminatedEarly = false; //todo

        while (instructionsExecuted < quantum && pcb.getProgramCounter() < pcb.getInstructions().size()) {
            String instruction = pcb.getInstructions().get(pcb.getProgramCounter());
            boolean executedNormally = executeInstruction(pcb, instruction);

            if (executedNormally) {
                instructionsExecuted++;
            } else {
                pcb.setInterruptionsCount(pcb.getInterruptionsCount() + 1);

                totalQuantumExecutions++;
                totalInstructionsInQuantums += instructionsExecuted;

                pcb.setState(ProcessState.BLOCKED);
                pcb.setWaitTime(2 * quantum);
                blockedQueue.add(pcb);

                logger.logProcessInterruption(pcb.getProcessName(), instructionsExecuted);
                logger.logProcessIOStart(pcb.getProcessName());

                blockedOrTerminatedEarly = true;
                break;
            }
        }

        if (!blockedOrTerminatedEarly) {
            totalQuantumExecutions++;
            totalInstructionsInQuantums += instructionsExecuted;

            if (pcb.getProgramCounter() >= pcb.getInstructions().size()) {
                pcb.setInterruptionsCount(pcb.getInterruptionsCount() + 1);
                pcb.setState(ProcessState.TERMINATED);
                logger.logProcessTermination(pcb);
                terminatedProcesses.add(pcb);

            } else {
                pcb.setInterruptionsCount(pcb.getInterruptionsCount() + 1);
                pcb.setState(ProcessState.READY);

                logger.logProcessInterruption(pcb.getProcessName(), instructionsExecuted);

                readyQueue.add(pcb);
            }
        }
    }

    /**
     * Executa a instrução atual do processo, atualiza o program counter e
     * retorna se a instrução foi executada sem entrar em E/S.
     *
     * @param pcb         Bloco de Controle do Processo que está sendo executado.
     * @param instruction A instrução a ser executada.
     * @return true se a instrução não causou E/S; false se foi uma instrução de E/S.
     * @throws IllegalArgumentException se a instrução for desconhecida.
     */
    private boolean executeInstruction(ProcessControlBlock pcb, String instruction) {
        if (instruction.startsWith("A=")) {
            int value = Integer.parseInt(instruction.substring(2));
            pcb.setRegisterA(value);

        } else if (instruction.startsWith("B=")) {
            int value = Integer.parseInt(instruction.substring(2));
            pcb.setRegisterB(value);

        } else if (instruction.startsWith("C=")) {
            int value = Integer.parseInt(instruction.substring(2));
            pcb.setRegisterC(value);

        } else if (instruction.startsWith("D=")) {
            int value = Integer.parseInt(instruction.substring(2));
            pcb.setRegisterD(value);

        } else if (instruction.equals("E/S")) {
            pcb.setProgramCounter(pcb.getProgramCounter() + 1);
            return false;

        } else if (instruction.equals("COM")) {

        } else if (instruction.equals("SAIDA")) {
            pcb.setProgramCounter(pcb.getInstructions().size());
            return true;

        } else {
            throw new IllegalArgumentException("Instrução desconhecida: " + instruction);
        }

        pcb.setProgramCounter(pcb.getProgramCounter() + 1);
        return true;
    }

    /**
     * Decrementa o tempo de espera dos processos bloqueados e move-os para
     * a fila de prontos se o tempo de espera expirar (chegar a 0 ou menos).
     */
    private void decrementBlockedProcessesWaitTime() {
        int blockedProcessesCount = blockedQueue.size();
        for (int i = 0; i < blockedProcessesCount; i++) {
            ProcessControlBlock pcb = blockedQueue.poll();
            if (pcb != null) {
                pcb.setWaitTime(pcb.getWaitTime() - 1);
                if (pcb.getWaitTime() <= 0) {
                    pcb.setState(ProcessState.READY);
                    readyQueue.add(pcb);
                } else {
                    blockedQueue.add(pcb);
                }
            }
        }
    }
}
