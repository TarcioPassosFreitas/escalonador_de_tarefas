package br.com.escalonadorTarefas.enums;

/**
 * Enumera os possíveis estados de um processo.
 * READY - O processo está pronto para executar, aguardando CPU.
 * RUNNING - O processo está em execução no momento.
 * BLOCKED - O processo está bloqueado, aguardando tempo de E/S se esgotar.
 * TERMINATED - O processo finalizou (SAIDA) e está encerrado.
 */
public enum ProcessState {
    READY,
    RUNNING,
    BLOCKED,
    TERMINATED
}

