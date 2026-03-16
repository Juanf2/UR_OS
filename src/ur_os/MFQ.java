/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author prestamour
 */
public class MFQ extends Scheduler{

    int currentScheduler;
    
    private ArrayList<Scheduler> schedulers;
    //This may be a suggestion... you may use the current sschedulers to create the Multilevel Feedback Queue, or you may go with a more tradicional way
    //based on implementing all the queues in this class... it is your choice. Change all you need in this class.
    
    MFQ(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
    }
    
    MFQ(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if(s.length > 0)
            currentScheduler = 0;
    }
        
    @Override
    public void addProcess(Process p){

        System.out.println(p.getState());
        System.out.println("p sch: " + p.currentScheduler);
        System.out.println("current sch: " + currentScheduler);

        if (p.getState() == ProcessState.NEW){

            System.out.println("in new");
            System.out.println("Pid: " + p.getPid());

            p.currentScheduler = 0;
            schedulers.get(0).addProcess(p);
            this.currentScheduler = 0;
        }

        else if (p.getState() == ProcessState.CPU){

            System.out.println("in CPU");
            System.out.println("Pid: " + p.getPid());

            if (p.currentScheduler < schedulers.size() - 1){
                schedulers.get(p.currentScheduler + 1).addProcess(p);
                p.currentScheduler++;
            }
        }

        else if (p.getState() == ProcessState.IO){

            System.out.println("in IO");
            System.out.println("Pid: " + p.getPid());

            p.currentScheduler = 0;

            if (!(p.currentScheduler > this.currentScheduler)) {
                this.currentScheduler = p.currentScheduler;
            }

            schedulers.get(p.currentScheduler).addProcess(p);
        }

        defineCurrentScheduler();
    }
    
    void defineCurrentScheduler() {
        
        for (int i = 0; i < schedulers.size(); i++) {

            Scheduler s = schedulers.get(i);

            if (!s.isEmpty()) {  
                currentScheduler = i;
                return;
            }
        }

        currentScheduler = -1;
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        
        defineCurrentScheduler();

        if (currentScheduler == -1)
            return;

        Scheduler s = schedulers.get(currentScheduler);

        s.getNext(cpuEmpty);
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) { //Non-preemtive in this event

        Process p = os.getProcessInCPU();

        p.currentScheduler = 0;

        schedulers.get(0).addProcess(p);

        currentScheduler = 0;
    }
    
    @Override
    public void IOReturningProcess(boolean cpuEmpty) { //Non-preemtive in this event
    
        Process p = os.getProcessInCPU();

        p.currentScheduler = 0;

        schedulers.get(0).addProcess(p);

        currentScheduler = 0;
    }
}
