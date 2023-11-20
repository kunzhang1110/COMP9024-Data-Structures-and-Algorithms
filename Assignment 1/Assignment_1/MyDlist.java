/**
 * Assignment 1 for COMP9024 
 * @author Kun Zhang (z5086704)
 */

import java.util.*;
import java.io.*;

public class MyDlist extends DList{
	
	/** Q1.Default Constructor inherited from DList*/
	public MyDlist (){
		super();
	}
	
	/** Q2. 
	 * Constructor with a String input
	 * @throws FileNotFoundException 
	 */
	public MyDlist (String f) throws FileNotFoundException{
		super();
		if (f == "stdin"){	
			readStdin();	// read String from standard input
		}else {
			readFile(f);	// read String from a file
		}
	}
	
	/** read String from standard input*/
	private void readStdin(){
		String in = null;
		Scanner scanner = new Scanner(System.in);	// create a Scanner
		in = scanner.nextLine();		
		while(!in.isEmpty()){						// scan until next input is null
			DNode inNode = new DNode(in,null,null);
			this.addLast(inNode);
			in = scanner.nextLine();
		}
		scanner.close();
	}
	
	/** read String from a file
	 *	@param fname: the directory of a file
	 */
	private void readFile(String fname) throws FileNotFoundException{  //file may not be in the directory
		File file = new File(fname);			// create a File instance
		Scanner scanner = new Scanner(file);	// create a Scanner instance
		while(scanner.hasNext()){				// scan until next input is null
			String in = scanner.next();
			DNode inNode = new DNode(in,null,null);
			this.addLast(inNode);
		}		
		scanner.close();
	}
	
	/**Q3 static method print elements in a MyDlist by line
	 * @param u: a MyDlist object to be printed
	 */
	public static void printList(MyDlist u){
		if(u.size>0){	// if not an empty list
			DNode node = u.header.next;
			while(node!=u.trailer){
				System.out.println(node.element);	//print line-by-line
				node = node.next;
			}
		}
	}
	
	/**Q4* static method to copy a MyDlist
	 * @param u: a MyDlist object to be copied
	 */
	public static MyDlist cloneList(MyDlist u){
		if(u.size>0){	// if not an empty list
			DNode unode = u.header.next;
			MyDlist clonedDList = new MyDlist();
			while(unode!=u.trailer){	//read the list
				DNode newNode = new DNode(unode.element, null, null);				
				clonedDList.addLast(newNode);	//append nodes to the end of the new list
				unode = unode.next;
			}	
			return clonedDList;
		}
		return null;	//return null if an empty list
	}
		
	/**Q5* static method to return a union of two lists
	 * @param u, v: two MyList objects to be taken union operation
	 * Analysis: the algorithm is based on a nested loop and primitive
	 * operations. The nested loop has a complexity of O(n^2) while the
	 * primitive operations have complexities of O(1). Therefore, the 
	 * complexity of the method is O(n^2).
	 * */
	public static MyDlist union(MyDlist u, MyDlist v){//u, v has distinct elements
		// MyDlist u will be extended by unique values from v 
		DNode unode = u.header.next;	
		DNode vnode = v.header.next;		
		
		int i=0;	//initialize a counter
		int same = 0;	//initialize a flag; when the lists have an identical element,
						//the flag will be set
		while (vnode!=v.trailer){	// for each element in v 
			while (i<u.size){		// scan all elements in u; u will increase in size
				if(unode.element.equals(vnode.element)){ //compare if they are equal
					same = 1;	//if equal, the flag is set
					break;		//no need to scan further, as the element is already in u
				}
				unode = unode.next;	//otherwise, keep scanning
				i++;				//counter increment
			}
			if (same==0){		//if there is no identical value in u for the element in v
				DNode newNode = new DNode(vnode.element,null,null); //append the element in u
				u.addLast(newNode);
			}
			vnode = vnode.next;		//next v node
			unode= u.header.next;	//reset unode to header
			i=0;					//reset counter
			same = 0;				//reset flag
		}
		return u;	//return the union MyDlist
	}

	/**Q6* static method to return an intersection of two lists
	 * @param u, v: two MyList objects to be taken intersection operation
	 * Analysis: Similarly, the algorithm is based on a nested loop and primitive
	 * operations. The nested loop has a complexity of O(n^2) while the
	 * primitive operations have complexities of O(1). Therefore, the 
	 * complexity of the method is also O(n^2).
	 * */
	public static MyDlist intersection(MyDlist u, MyDlist v){//u, v has distinct elements
		MyDlist intMyDlist = new MyDlist();	//list with common elements
		DNode unode = u.header.next;			
		DNode vnode = v.header.next;		

		while (unode!=u.trailer){//for each element in u
			while (vnode!=v.trailer){//scan all element in v
				if(unode.element.equals(vnode.element)){//if elements are equal
					DNode newNode = new DNode(unode.element,null,null);
					intMyDlist.addLast(newNode);//append the elements
				}
				vnode = vnode.next;//next v value
			}
			unode = unode.next;//next u value
			vnode = v.header.next;//reset v node to header
		}
		return intMyDlist;
	}	 
}
	
	
	
	
	
	
	


