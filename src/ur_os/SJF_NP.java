/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class SJF_NP extends Scheduler{

    
    SJF_NP(OS os){
        super(os);
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if (cpuEmpty && !processes.isEmpty()) {

            // Search the process with the lowest CPU burst 
            Process shortest = processes.get(0);

            for (Process p : processes) {
                if (p.getRemainingTimeInCurrentBurst() < shortest.getRemainingTimeInCurrentBurst()) {
                    shortest = p;
                }
            }

            // Remove from de waiting line
            processes.remove(shortest);

            // Send to the CPU
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, shortest);
        }
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
}
