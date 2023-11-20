import java.io.IOException;

public class TrieTest {
	
	public TrieTest(){
		
	}
	
	 public static void main(String args[]) throws Exception{
	        
		 /** Construct a compact compressed suffix trie named trie1
		  */       
		 CompactCompressedSuffixTrie trie1 = new CompactCompressedSuffixTrie("file_1.txt");
//		         
//		 System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));
//
//		 System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));
//		         
//		 System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));
		         
		 CompactCompressedSuffixTrie.kLongestSubstrings("file_4.txt", "file_5.txt", "file_test.txt", 3);
	}

}
