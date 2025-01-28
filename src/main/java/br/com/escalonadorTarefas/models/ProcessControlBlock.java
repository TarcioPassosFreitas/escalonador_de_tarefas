package br.com.escalonadorTarefas.models;


import br.com.escalonadorTarefas.enums.ProcessState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa o Bloco de Controle de Processo (BCP).
 * Usando Lombok para getters, setters e construtores
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessControlBlock {
    private int processId;
    private String processName;
    private ProcessState state;
    private int programCounter;
    private int registerA;
    private int registerB;
    private int registerC;
    private int registerD;
    private List<String> instructions;
    private int waitTime;
    private int interruptionsCount = 0; //TODO
}


