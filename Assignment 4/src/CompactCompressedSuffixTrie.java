/**
 * Assignment 4 for COMP9024 
 * @author Kun Zhang (z5086704)
 * 
 * The CompactCompressedSuffixTrie class construct a compact representation of compressed suffix trie for
 * a DNA sequence. The class provides a public method to find a given pattern in the trie and
 * a static method to find k longest common sequence for two strings
 *
 */

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.*;

public class CompactCompressedSuffixTrie {
	// declare constants and variables
	final static char DOLLAR = '$';	
	final static String NUCLEOTIDE = "[ATGC]";	//Set of nucleotides
	private Node root;
	private String input;

	
	/**Question 1
	 * Constrcut compressed suffix trie from a file
	 * @param fn = file name
	 * Time Complexity Analysis:
	 * The maximum number of all nodes < 2N
	 * Therefore, each traversal of the suffix trie takes O(logN);
	 * There are N suffixes, so we need to traverse N times;
	 * Process elements at each internal nodes will take O(N)
	 * Finally, the total time complexity is O(N^2*log N)
	 */
	public CompactCompressedSuffixTrie(String fname){

		// read and check input
		this.input=readFile(fname);
		if(checkInput()){	// check validity of input
			this.input += DOLLAR;	// append dollar sign to build trie
		}else{
			System.out.println("Invalid Input");
			return;			// return to caller
		}
		
		// build suffix trie
		this.root = new Node();		// create root node
		this.root.setIndex(-1);		// root is set to -1
		String suffix;
		for (int i=input.length()-1; i>=0;i--){	//start with the shortest suffix
			suffix = input.substring(i);		// get suffixes
			if(suffix.length()==0)
				continue;
			build(this.root,suffix,i,0,false);
			
		}

		
	}
	
	
	// method to build suffix trie
	private void build(Node currentNode, String suffix, int index1, int index2, boolean branch){
		// if node has no children
		if(currentNode.getChildren().size()==0){	
			if(branch){		// if need to branch from current node create two children

				// child 1 contains suffix
				Node child1 = new Node();
				child1.setElement(suffix);
				child1.setIndex(index1);
				currentNode.addChild(suffix.charAt(0), child1);
				
				// set child 2 contains concatenated string
				Node child2 = new Node();
				child2.setElement(currentNode.getElement().substring(index2));
				child2.setIndex(currentNode.getIndex());	// inherit parent's index
				for (Node c : currentNode.getChildren().values()) {	//inherit parent's children
					child2.children.put(c.getElement().charAt(0), c);
				}	
				currentNode.getChildren().clear();	// clear all children
				currentNode.getChildren().put(currentNode.getElement().substring(index2).charAt(0),child2);	//link parent to child2
				
				// set parent node
				currentNode.setElement(currentNode.getElement().substring(0,index2));
				currentNode.setIndex(index1);
				
			}else{	// if do not need to branch from currentNode
				if(currentNode==this.root){	// if node is root
					// create a new child
					Node child = new Node();
					child.setElement(suffix);
					child.setIndex(index1);
					currentNode.addChild(suffix.charAt(0), child);
				}
				
				if(currentNode!=this.root){	// if node is not a root
					
					// child 1 contains suffix
					Node child1 = new Node();
					child1.setElement(suffix);
					child1.setIndex(index1);
					currentNode.addChild(suffix.charAt(0), child1);
				
					// child 2 contains concatenated string
					Node child2 = new Node();
					child2.setElement(currentNode.getElement().substring(index2));
					child2.setIndex(currentNode.getIndex());	// inherit parent's index
					currentNode.addChild(currentNode.getElement().substring(index2).charAt(0), child2);
					
					// set parent node
					currentNode.setElement(currentNode.getElement().substring(0,index2));
					currentNode.setIndex(index1);
				}
			}
		}
		
		// if node has children
		if(currentNode.getChildren().size()>0){	
			boolean find_flag = false;	// flag to indicate whether a character is found to match
			// locate the correct branch and find index to split string
			for(Node child:currentNode.getChildren().values()){
				if(!(child.getElement().length() == 0 || suffix.length() == 0)&&child.getElement().charAt(0) == suffix.charAt(0)){	// check if the first character matches
					int splitIndex = -1;
					for (int i = 1; i <= child.getElement().length(); i++){	// find which the first character that does not match
						try{
							if(child.getElement().charAt(i)!=suffix.charAt(i)){	// find first mismatch and split from here
								splitIndex = i;
								break;
							}
						}catch(StringIndexOutOfBoundsException e){	//if string index exceeds length
							splitIndex = i;
						}
					}
				
					// split suffix
					if(splitIndex!=-1)
						suffix = suffix.substring(splitIndex);
					if(suffix.length() == 0)	// if suffix is empty
						continue;
					find_flag = true;	// there is a match
					
					if(child.getChildren().size()!=0 && splitIndex<child.getElement().length()){
						branch = true;	// set branch to create two children
					}
					this.build(child, suffix, index1, splitIndex, branch);	// recurse to futher nodes
					currentNode.setIndex(index1);
					break;	
				}
			}
			
			// if cannot find a matching child, then create a new node
			if(!find_flag&&suffix.length()>0){
				Node child = new Node();	// create a new child to store suffix
				child.setElement(suffix);
				child.setIndex(index1);
				currentNode.addChild(suffix.charAt(0), child);
				currentNode.setIndex(index1);
			}
		}
	}
	
	/**Question 2 
	 * Method for finding the first occurrence of a pattern s in the DNA sequence
	 * @param s: string pattern to be found
	 * Time Complexity Analysis:
	 * Looping through all S characters in pattern requires O(S)
	 * However, in the worst case where all initial path are selected incorrectly,
	 * the number of search will be of O(N)
	 * Therefore, the total time complexity is O(N)=O(S)
	 */
	public int findString(String s){
		// initialize local fields
		int nodeIndex = 0;
		int returnIndex = -1;
		Node currentNode = this.root;
		boolean hasPath = false;
		char[] sArray = s.toCharArray();	// convert pattern to array
		
		// traverse the trie; O(N)
		for(int i =0;i<s.length();i++){
			if(hasPath){	// if there is a path
				try{
					if(sArray[i]==currentNode.getElement().charAt(nodeIndex)){ 	// if there is a match
						nodeIndex++;
						continue;
					}else{
						return -1;	// no match
					}
				}catch(StringIndexOutOfBoundsException e){	// if exceeds string index
					hasPath = false;
					i--;	// move back to parent and try other path
				}
			}else{
				try{
					currentNode=currentNode.getChildren().get(sArray[i]);	// find a path
					if(currentNode.getElement().length()>1){	// if there is a path
						nodeIndex = 1;
						hasPath = true;
					}
					returnIndex = currentNode.getIndex();	// the starting index of a matching sequence
				}catch(StringIndexOutOfBoundsException e){
					return -1; // no match
				}
			}
		}
		return returnIndex;
	}
	
	/** Method for finding k longest common substrings of two DNA sequences stored in the text files f1 and f2
	 * @param f1: file 1 to investigate
	 * @param f1: file 2 to investigate
	 * @param f3: file to write result
	 * Time Complexity Analysis:
	 * Dynamic programming approach to find all common sequences takes O(M*N),
	 * read matrix takes O(MN);
	 * split common sequences takes O(N) and
	 * obtain k longest common sequence takes O(k);
	 * Therefore, the total time complexity is O(M*N)
	 */
	public static void kLongestSubstrings(String f1, String f2, String f3, int k){
		
		// initialize local fileds and read from files
		String file1 = readFile(f1);
		String file2 = readFile(f2);
		int file1Size = file1.length();
		int file2Size = file2.length();
		
		// create a matrix for dynamic programming
		int[][] fileCombine = new int[file1Size+1][file2Size+1];
		
		// evaluate matrix from left top: O(M*N)
		for (int i = 0; i <= file1Size; i++) {
			for (int j = 0; j <= file2Size; j++) {
				if(i==0||j==0){
					fileCombine[i][j]=0;
				}
				else if (file1.charAt(i-1) == file2.charAt(j-1))
					fileCombine[i][j] = fileCombine[i - 1][j - 1] + 1;	
				else
					fileCombine[i][j] = Math.max(fileCombine[i - 1][j], fileCombine[i][j - 1]);
			}
		}
		
		// read matrix and covert to indices in file1, from right bottom: O(M*N)
		int ind = fileCombine[file1Size][file2Size];	// size of common characters	
//		char[] LCS = new char[ind];			// all common sequence
		int [] LCSIndices = new int[ind];	//  all common sequence's indices in file1
		int i = file1Size;
		int j = file2Size;
		while (i > 0 && j > 0) {
			if (file1.charAt(i-1) == file2.charAt(j-1)) {	// if character matches
//				LCS[ind-1]=file1.charAt(i-1);
				LCSIndices[ind-1] = i-1;
				i--;
				j--;
				ind--;
			} else if (fileCombine[i - 1][j] >= fileCombine[i][j - 1]) {
				i--;
			} else {
				j--;
			}
		}
		
		// split all common sequence into consecutive sequences: O(N)
		TreeMap<Integer, String> dict = new TreeMap<Integer, String>(Collections.reverseOrder());	// set TreeMap to sort in a descending order
		int initial_index = 0;
		for(i=0;i<LCSIndices.length-1;i++){
			if(LCSIndices[i+1]-LCSIndices[i]!=1){	// if not consecutive
				dict.put(i+1-initial_index, file1.substring(LCSIndices[initial_index],LCSIndices[i]+1));	// record length and consecutive sequence
				initial_index = i+1;
			}
			if(i==LCSIndices.length-2){	// if reach the end
				dict.put(LCSIndices.length-initial_index, file1.substring(LCSIndices[initial_index],LCSIndices[LCSIndices.length-1]+1)); // record length and consecutive sequence
			}
		}
		
		// get first k consecutive sequence: O(K)
		int count = 1;
		String result ="";
		for (Integer len: dict.keySet()){	// get elements from TreeMap
			if(count>k)
				break;
			result += Integer.toString(count)+": "+dict.get(len).toString()+"\n";   
            count++;
		} 
		writeFile(f3,result);  // write to file
	}
	
	// utility method: read from file and store in input
	public static String readFile(String fn){
		String returnString="";
		String line;
		try{ 		// read lines
			BufferedReader br = new BufferedReader(new FileReader(fn));	// use BufferedReader to read line
			while((line = br.readLine()) != null){
				returnString += line;
			}
			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Unable to open file " +  fn);                // error opening file
	    }
		catch(IOException e){ 
			e.printStackTrace();	// error reading file
	    }
		return returnString;
	}
	
	// utility method: write to file
	public static void writeFile(String fn, String output){
		File file = new File(fn);
		try{
			FileWriter fw = new FileWriter(fn);	
			fw.write(output);
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	// utility method: check whether input are valid nucleotides
	public boolean checkInput(){
		Pattern p = Pattern.compile(NUCLEOTIDE+"{"+this.input.length()+"}");	// construct a regex pattern
		Matcher m = p.matcher(this.input);										// to match ATGC
		return m.matches();
	}
}

// Node class
class Node{
	
	private String element;
	private int index;		// starting index
	protected HashMap<Character, Node> children;
	
	public Node(){
		element="";
		children = new HashMap<>();
	}
	
	// accessor methods
	public String getElement(){
		return this.element;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public HashMap<Character,Node> getChildren(){
		return this.children;
	}
	
	// update methods
	public void setElement(String elmt){
		this.element=elmt;
	}
	
	public void setIndex(int idx){
		this.index = idx;
	}
	
	public void addChild(Character c, Node chd){	// use first character as key
		this.children.put(c,chd);
	}	
}
// end of Node class

