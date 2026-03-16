/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;
 
import java.util.ArrayList;
import java.util.Arrays;
 

public class PriorityQueue extends Scheduler {
 
    int currentScheduler;
    private ArrayList<Scheduler> schedulers;
 
    private int quantumCont;    
    private int currentQuantum; 
 
    PriorityQueue(OS os) {
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
        quantumCont = 0;
        currentQuantum = 0;
    }
 
    PriorityQueue(OS os, Scheduler... s) {
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if (s.length > 0)
            currentScheduler = 0;
    }
 
   
    @Override
    public void addProcess(Process p) {
        int priority = p.getPriority();
        if (priority < 0) priority = 0;
        if (priority >= schedulers.size()) priority = schedulers.size() - 1;
 
        
        if (p.getState() == ProcessState.NEW) {
            newProcess(os.isCPUEmpty());
        } else if (p.getState() == ProcessState.IO) {
            IOReturningProcess(os.isCPUEmpty());
        }
 
        
        p.setState(ProcessState.READY);
        schedulers.get(priority).processes.add(p);
 
        defineCurrentScheduler();
    }
 
    
    void defineCurrentScheduler() {
        for (int i = 0; i < schedulers.size(); i++) {
            if (!schedulers.get(i).isEmpty()) {
                currentScheduler = i;
                return;
            }
        }
        currentScheduler = -1;
    }
 

    private void loadNext() {
        defineCurrentScheduler();
        if (currentScheduler == -1) return;
 
        Process next = schedulers.get(currentScheduler).processes.poll();
        if (next != null) {
            // Read the quantum from the sub-scheduler before loading
            currentQuantum = ((RoundRobin) schedulers.get(currentScheduler)).q;
            quantumCont = 0;
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, next);
        }
    }
 
    @Override
    public void getNext(boolean cpuEmpty) {
        if (schedulers.isEmpty() || currentScheduler == -1) return;
 
        if (cpuEmpty) {
            // CPU is free — load the highest-priority waiting process
            loadNext();
 
        } else {
            // CPU is busy
            Process running = os.getProcessInCPU();
            if (running == null) return;
 
            int runningPriority = running.getPriority();
 
            
            int bestWaiting = -1;
            for (int i = 0; i < schedulers.size(); i++) {
                if (!schedulers.get(i).isEmpty()) {
                    bestWaiting = i;
                    break;
                }
            }
 
            if (bestWaiting != -1 && bestWaiting < runningPriority) {
                running.increaseContextSwitches();
                addContextSwitch();
                quantumCont = 0;
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                loadNext();
 
            } else {
                quantumCont++;
 
                if (quantumCont >= currentQuantum) {
                    running.increaseContextSwitches();
                    addContextSwitch();
                    quantumCont = 0;
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                    loadNext();
                }
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
