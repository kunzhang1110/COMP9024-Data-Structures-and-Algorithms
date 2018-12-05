/**
 * Assignment 3 for COMP9024 
 * @author Kun Zhang (z5086704)
 * 
 * The TaskScheduler class provides a static method scheduler that uses EDF 
 * to schedule tasks of a given task set. The output is written into a txt file.
 *
 */

package net.datastructures;

import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class TaskScheduler {
	
	/**package-private method
	 * 
	 * @param file1: input: task specification
	 * @param file2: output: schedules
	 * @param m: numbers of cores
	 * @throws FileNotFoundException 
	 * 
	 * Time Complexity Summary
	 * Time complexity for all three blocks are of O(n*log n) (see time complexity analysis for each block)
	 * Therefore, the total complexity for scheduler method is O(n*log n).
	 */
	static void scheduler(String file1, String file2, int numOfCore) throws FileNotFoundException {
		
		// create fields
		HeapPriorityQueue<Integer, Task> releaseQueue = new HeapPriorityQueue<Integer, Task>();
		HeapPriorityQueue<Integer, Task> readyQueue = new HeapPriorityQueue<Integer, Task>();
		// regex pattern for task name
		final Pattern REGEX_TASKNAME = Pattern.compile("\\A\\D+\\d+");	//characters + digits
		
		
		/** File 1 Read, Check and Put into releaseQueue ------------------------------------------
		 *  Time Complexity: priority queue insertion is O(log n); other operations in loop are of O(1);
		 *  Insert n tasks in to priority queue will take O(n*log n); other operations in loop are of O(n);
		 *  The total time complexity for this block is O(n*log n)
		 * */
		try{
			File fileOne = new File(file1);
			Scanner s = new Scanner(fileOne);
			
			// check if the next 4 tokens are in format; if in format, then set to Task object
			while(s.hasNext()){				// worst-case run n times
				Task task = new Task();		// create a new Task ojbect
				
				// check if Task Name is valid: characters + digits	-------------------------------
				String tempTaskName = s.next();		// 1st token is regarded as taskName X even if unvalid
				if(REGEX_TASKNAME.matcher(tempTaskName).matches()){
					task.setTaskName(tempTaskName);
				}else{
					printFormatError(tempTaskName,s);
				}
				
				// check if Execution Time is a natural number ------------------------------------		
				if(s.hasNextInt()){	
					int exctime = s.nextInt();
					if (exctime>0){
						task.setExecutionTime(exctime);	
					}
					else{
						printFormatError(task.getTaskName(),s);
					}
				}else{
					printFormatError(task.getTaskName(),s);
				}

				// check if Release Time is an integer --------------------------------------------	
				if(s.hasNextInt()){		
						task.setReleaseTime(s.nextInt());	
				}else{
					printFormatError(task.getTaskName(),s);
				}

				
				// check if Deadline is a natural number ------------------------------------------
				if(s.hasNextInt()){		
					int ddl = s.nextInt();
					if (ddl>0){
						task.setDeadline(ddl);	
					}
					else{
						printFormatError(task.getTaskName(),s);
					}
				}else{
					printFormatError(task.getTaskName(),s);
				}
				
				// if all 4 tokens are valid, insert into releaseQueue
				// TIME COMPLEXITY for priority queue insertion is O(log n)
				releaseQueue.insert(task.getReleaseTime(), task);
			}
			s.close();
		}catch (FileNotFoundException e){		// print error message
			System.out.print("file1 does not exist");
			return;
		}
		// System.out.print(releaseQueue.toString());		// DEBUG: print out releaseQueue
		/** File 1 Read, Check and Put into releaseQueue ends -----------------------------------*/

		
		/** EDF -----------------------------------------------------------------------------------
		 *  EDF works as follows:
		 *  At any time t, among all the ready tasks, find a task with the smallest deadline, and schedule
		 *  it on an idle core. Ties are broken arbitrarily.
		 *  
		 *  In implementation, tasks are removed from releasedQueue are insert into readyQueue, then
		 *  removed from readyQueue and insert into runningQueue and schedulingQueue.
		 *  
		 *  Time Complexity: 
		 *  In total the Priority Queue operations will only run n times (as analyzed below) 
		 *  due to the fact that each tasks can only be inserted or removed once and only once.
		 *  These operations in total will be of O(n*log n)
		 *  The primary operation inside for-loop bracket will run c*n time, where c is the average 
		 *  execution time (constant): O(c*n)=O(n)<O(n*log n).
		 *  The total time complexity for this block is O(n*log n)
		 * */

		int clock_time = 0;		// clock time of the embedded system
		HeapPriorityQueue<Integer, Task> scheduleQueue = new HeapPriorityQueue<Integer, Task>();	// EDF schedule
		HeapPriorityQueue<Integer, Task> runningQueue = new HeapPriorityQueue<Integer, Task>();		// Running tasks
		boolean core_busy[] = new boolean[numOfCore];												// intialize buzy cores to false
		
		// at each clock time

		
		for (clock_time = 0; !releaseQueue.isEmpty()||!runningQueue.isEmpty(); clock_time++){		
																									
			// at clock time tc, put all ready to run tasks onto ready queue
			// Time Complexity:
			// In total, the releaseQueue has to removeMin() all n tasks and insert them into ready queue.
			// Because there is no insertion back into releaseQueue, we can say  releaseQueue.removeMin() and
			// readyQueue.insert() will run n times. Therefore, O(n*log n)
			while (!releaseQueue.isEmpty() && releaseQueue.min().getKey() == clock_time){	// if there is a task has been released
				Task t = releaseQueue.removeMin().getValue();								// get the task out of releaseQueue; O(log n)
				readyQueue.insert(t.getDeadline(), t);										// put the task in readay queue;  O(log n)	
			}
			
			// at clock time tc, check if there is running task
			// Time Complexity:
			// In worst case scenario, every task has to be run once and only once and get removed from runningQueue
			// Because for every task there is no insertion back into runningQueue after being removed, 
			// we can say runningQueue.removeMin() and will run n times. Therefore, O(n*log n)	
			while(runningQueue.isEmpty()==false												// while there is a running
					&&runningQueue.min().getKey()==clock_time){								// AND the running task ends at clock time
				Task finishedTask = runningQueue.removeMin().getValue();					// remove it from runningQueue
				core_busy[finishedTask.getCoreNumber()] = false;							// set the core to idle
			}
	
			// at clock time tc, schedule readyTask on scheduleQueue and Running Queue
			// Time Complexity:
			// In worst case scenario, every task has to be inserted once and only once
			// into runningQueue into runningQueue and scheduleQueue after being removed. 
			// We can say scheduleQueue.insert() and runningQueue.insert() will each run n times. Therefore, O(n*log n)				
			
			while (!readyQueue.isEmpty() && runningQueue.size() < numOfCore){	//while there is a ready task and an idle core
				if (readyQueue.min().getKey() <= clock_time){					// if smallest deadline is earlier than clocktime
					System.out.println("No feasible schedule exists.");			// no feasible solution
					return;
				}
				Task runningTask = readyQueue.removeMin().getValue();						// remove task with smallest deadline; O(logn)
				if (clock_time+runningTask.getExecutionTime()>runningTask.getDeadline()){	// if end time is greater than deadline
					System.out.println("No feasible schedule exists.");						// no feasible solution
					return;
				}	
			
				int c = 0;	// core_number
				for(c = 0;c<core_busy.length;c++){	// find idle core number; O(1) as core number is constant
					if(core_busy[c]==false)		// break when find the first idle core; 
						break;
				}
				runningTask.setCoreNumber(c);		// set the Task with CoreNumber
				core_busy[c]=true;					// set the core with CoreNumber to be buzy
				scheduleQueue.insert(clock_time, runningTask);										//  O(logn)
				runningQueue.insert(clock_time+runningTask.getExecutionTime(), runningTask);		// store all running task with end time; O(logn)
			}
		} // end for loop
		/** EDF ends ---------------------------------------------------------------------------------------*/	

		
		/** File 2 Write -----------------------------------------------------------------------------------
		 *  Time Complexity: priority queue removeMin() is O(log n); other operations in loop are of O(1);
		 *  Remove n tasks from priority queue will take O(n*log n); other operations in loop are of O(n);
		 *  The total time complexity for this block is O(n*log n)
		 * */
		
		try{
			File fileTwo = new File(file2+".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileTwo));	// use Bufferwriter for many small writes
																				// create new file if not exist
			while(!scheduleQueue.isEmpty()){									
				Entry<Integer, Task> printTask = scheduleQueue.removeMin();		// get Entry from scheduleQueue
				bw.write(printTask.getValue().getTaskName()+" Core"+printTask.getValue().getCoreNumber()+" "+printTask.getKey()+" ");
			}	// print "taskName CoreX start_Time"
			bw.close();
		}catch (IOException e){
			e.printStackTrace();	// print call stack 
			return;
		}
		/** File 2 Write ends ---------------------------------------------------------------------------------*/
		
	}		

	// static utilities methods
	/** print out error message if any format in file1 is invald*/
	public static void printFormatError(String tskname, Scanner s){
		System.out.println("Input error when reading the attribute of the task "+tskname);
		s.close();
		return;		// program terminates
	}
}
// end of TaskScheduler Task ----------------

// additional class Task --------------
class Task{
	private String taskName; // RE:\\A\\D+\\d+\\z
	private Integer executionTime;	// 1~9
	private Integer  releaseTime;	// 0~9
	private Integer deadline; 		// 1~9
	
	private Integer coreNumber = null;
	
	// constructors
	public Task(){}
	public Task(String tskname, Integer exctime, Integer rlstime, Integer ddl){
		taskName = tskname;
		executionTime = exctime;
		releaseTime = rlstime;
		deadline = ddl;
	}
	// access methods: get fields
	protected String getTaskName(){return taskName;}	
	protected Integer getExecutionTime(){return executionTime;}		
	protected Integer getReleaseTime(){return releaseTime;}		
	protected Integer getDeadline(){return deadline;}	
	protected Integer getCoreNumber(){return coreNumber;}
	
	// update methods: set fields
	protected void  setTaskName(String tskname){taskName = tskname;}	
	protected void  setExecutionTime(Integer exctime){executionTime = exctime;}		
	protected void  setReleaseTime(Integer rlstime){releaseTime = rlstime;}		
	protected void  setDeadline(Integer ddl){deadline = ddl;}	
	protected void  setCoreNumber(Integer cnb){coreNumber = cnb;}	
}
// end of Task class -------------