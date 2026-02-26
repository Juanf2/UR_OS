/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class RoundRobin extends Scheduler{

    int q;
    int cont;
    boolean multiqueue;
    
    RoundRobin(OS os){
        super(os);
        q = 5;
        cont=0;
    }
    
    RoundRobin(OS os, int q){
        this(os);
        this.q = q;
    }

    RoundRobin(OS os, int q, boolean multiqueue){
        this(os);
        this.q = q;
        this.multiqueue = multiqueue;
    }
    

    
    void resetCounter(){
        cont=0;
    }
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if (cpuEmpty && !processes.isEmpty()) {// Case 1 → Empty CPU → load next process
            Process next = processes.poll();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, next);
            resetCounter();  // reset quantum
            return;
        }
        if (!cpuEmpty) {// Case 2 → CPU ocupied → count time
            cont++;
            
            if (cont >= q) {// Quantum expired
                Process current = os.getProcessInCPU();
                
                if (current != null) {
                    current.increaseContextSwitches();
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);// PREEMPT → CPU → ReadyQueue
                    addContextSwitch();
                }
                resetCounter();
            }
        }
    }
    
    
    @Override
    public void newProcess(boolean cpuEmpty) {
        if (cpuEmpty) {
            getNext(true);
        }
    }
    
    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        if (cpuEmpty) {
            getNext(true);
        }
    }
    
}
