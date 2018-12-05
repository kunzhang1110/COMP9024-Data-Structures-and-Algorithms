 public static void main(String[] args) throws Exception{
 
   System.out.println("please type some strings, one string each line and an empty line for the end of input:");
    /** Create the first doubly linked list
    by reading all the strings from the standard input. */
    MyDlist firstList = new MyDlist("stdin");
    
   /** Print all elememts in firstList */
    printList(firstList);
   
   /** Create the second doubly linked list                         
    by reading all the strings from the file myfile that contains some strings. */
  
   /** Replace the argument by the full path name of the text file */  
    MyDlist secondList=new MyDlist("C:/Users/Hui Wu/Documents/NetBeansProjects/MyDlist/myfile.txt");

   /** Print all elememts in secondList */                     
    printList(secondList);

   /** Clone firstList */
    MyDlist thirdList = cloneList(firstList);

   /** Print all elements in thirdList. */
    printList(thirdList);

  /** Clone secondList */
    MyDlist fourthList = cloneList(secondList);

   /** Print all elements in fourthList. */
    printList(fourthList);
    
   /** Compute the union of firstList and secondList */
    MyDlist fifthList = union(firstList, secondList);

   /** Print all elements in thirdList. */ 
    printList(fifthList); 

   /** Compute the intersection of thirdList and fourthList */
    MyDlist sixthList = intersection(thirdList, fourthList);

   /** Print all elements in fourthList. */
    printList(sixthList);
  }