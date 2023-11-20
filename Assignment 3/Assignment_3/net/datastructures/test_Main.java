package net.datastructures;

import java.io.FileNotFoundException;

public class test_Main {
	
	public static void main(String args[]) throws FileNotFoundException{
		// output shoud be:feasibleschedule1 feasibleschedule3 feasibleschedule6
		  
		   /** There is a feasible schedule on 2 cores */   
	    	TaskScheduler.scheduler("samplefile0.txt", "feasibleschedule0", 2);
		
		   /** There is a feasible schedule on 4 cores */   
		    TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule1", 4);
		   
		    /** There is no feasible schedule on 3 cores */
		    TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule2", 3);

			/** There is a feasible scheduler on 5 cores */ 
		    TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule3", 5);
		   
		   /** There is no feasible schedule on 4 cores */
		    TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule4", 4);

		   /** There is no feasible scheduler on 2 cores */ 
		    TaskScheduler.scheduler("samplefile3.txt", "feasibleschedule5", 2);
			
		    /** There is a feasible scheduler on 2 cores */ 
		    TaskScheduler.scheduler("samplefile4.txt", "feasibleschedule6", 2);
		
	}

}
