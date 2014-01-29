import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import javax.swing.JFileChooser;

/**
 * CS10: PS-4
 * @author Sujay Busam
 *
 *
 * Huffman encoder main class
 * Encodes / compresses and decodes / decompresses text file according
 * to Huffman algorithm
 */

public class HuffmanEncoder {
	// File Names
	private static String input_original = null;
	private static String input_compressed = null;
	private static String input_decompressed = null;

	/**
	 * Generates a frequency table map of characters in the given text
	 * document and their associated frequencies
	 * 
	 * @param input the BufferedReader input object
	 * @return a map of characters and their associated frequencies
	 */
	public static Map<Character, Integer> generateFreqMap(BufferedReader input) throws IOException{
		// Map that is returned
		Map<Character, Integer> freqMap = new HashMap<Character, Integer>();

		try {
			// Get first character (Unicode)
			int currentInt = input.read();

			// Run through all characters
			while (currentInt != -1) {
				// Cast the Unicode encoding as a character
				char currentChar = (char)currentInt;

				// If map already contains character, increment frequency count
				if (freqMap.containsKey(currentChar)) {
					int incrementedCount = freqMap.get(currentChar) + 1;
					freqMap.put(currentChar, incrementedCount);
				}

				// Otherwise, add character to map
				else {
					freqMap.put(currentChar, 1);
				}

				// Get next character
				currentInt = input.read();
			}
		}

		catch (IOException ex){
			System.out.println(ex + " frequency table exception occured");
		}

		finally {
			input.close();
		}

		// Return the map of character, frequency pairs
		return freqMap;
	}


	/**
	 * Method to construct the priority queue heap containing characters from
	 * the map. Priority related to frequency 
	 * 
	 * @param freqMap the frequency map of the text file
	 * @return a priority queue of the characters implemented as a heap
	 */
	public static PriorityQueue<CharacterBinaryTree<Character>> constructHeap(Map<Character, Integer> freqMap) {
		// Create the queue
		PriorityQueue<CharacterBinaryTree<Character>> charHeap = 
			new PriorityQueue<CharacterBinaryTree<Character>>(1, new TreeComparator<Character>());

		// Run through characters
		for (char character: freqMap.keySet()) {
			int currentFreq = freqMap.get(character);

			// Create singleton tree with current character and frequency value, and add to queue
			CharacterBinaryTree<Character> myTree = new CharacterBinaryTree<Character>(character, currentFreq);
			charHeap.add(myTree);
		}

		// Return the priority queue heap
		return charHeap;
	}

	/**
	 * Helper method that performs one iteration of the tree creation
	 * 
	 * @param charHeap the priority queue heap
	 * @return charHeap the modified priority queue heap
	 */
	public static PriorityQueue<CharacterBinaryTree<Character>> constructOneTree(
			PriorityQueue<CharacterBinaryTree<Character>> charHeap) {

		// Remove two lowest frequency trees from queue and declare new tree
		CharacterBinaryTree<Character> treeT1 = charHeap.poll();
		CharacterBinaryTree<Character> treeT2 = charHeap.poll();
		CharacterBinaryTree<Character> newTree;

		// Sum the two frequencies
		int mySum = treeT1.getFreq() + treeT2.getFreq();

		// Create new tree and add it to priority queue
		newTree = new CharacterBinaryTree<Character>(null, mySum, treeT1, treeT2);
		charHeap.add(newTree);

		// return the priority queue
		return charHeap;
	}

	/**
	 * Construct entire tree using the helper method
	 * @param charHeap the priority queue heap
	 * @return newCharHeap.peek() the final tree
	 */
	public static CharacterBinaryTree<Character> constructEntireTree(
			PriorityQueue<CharacterBinaryTree<Character>> charHeap) {

		// Declare the queue holding the final tree to be returned
		PriorityQueue<CharacterBinaryTree<Character>> newCharHeap = null;

		// Keep building up the tree until the priority queue is just one tree
		try {
			while (charHeap.size() != 1) 
				newCharHeap = constructOneTree(charHeap);
		}

		catch (NullPointerException ex) {
			System.out.println(ex + " tree construction exception occurred.");
		}

		if (newCharHeap != null) {
			return newCharHeap.peek();
		}

		else
			return null;
	}


	/**
	 * Compress input file by writing out the characters as Huffman codes
	 * 
	 * @param binaryMap the map that associates character with Huffman code
	 * @throws IOException
	 */
	public static void compress(Map<Character, String> binaryMap) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(input_original));
		BufferedBitWriter bitOutput = new BufferedBitWriter(input_compressed);

		try {
			// Unicode character
			int currentInt = input.read();

			// Run through all characters of file
			while (currentInt != -1) {
				char character = (char)currentInt;

				// Get the corresponding Huffman code (binary string)
				String huffmanString = binaryMap.get(character);

				// Run through digits of Huffman code
				for(int i = 0; i < huffmanString.length(); i++) {
					int bit = Character.digit(huffmanString.charAt(i), 10);
					// Write bit to file
					bitOutput.writeBit(bit);
				}
				currentInt = input.read();
			}
		}

		catch (IOException ex) {
			System.out.println(ex + " compression exception occurred.");
		}

		finally {
			input.close();
			bitOutput.close();
		}
	}


	/** 
	 * Decompress file by decoding bits
	 * 
	 * @param codeTree tree used to decode bits
	 * @param bitInput input
	 * @param output output
	 */
	public static void decompress(CharacterBinaryTree<Character> codeTree,
			BufferedBitReader bitInput, BufferedWriter output) throws IOException {

		try {
			int currentBit = bitInput.readBit();

			// Iterate over bits and decode
			while (currentBit != -1) {
				decode(codeTree, currentBit, bitInput, output);
				currentBit = bitInput.readBit();
			}
		}

		catch (IOException ex) {
			System.out.println(ex + " decompression exception occurred.");
		}

		finally {
			bitInput.close();
			output.close();
		}
	}


	/**
	 * Helper method to decode the bits
	 * 
	 * @param codeTree tree used to decode bits
	 * @param currentBit current bit in file
	 * @param bitInput input
	 * @param output output
	 */
	public static void decode(CharacterBinaryTree<Character> codeTree, int currentBit,
			BufferedBitReader bitInput, BufferedWriter output) throws IOException{

		try {

			// Recurse on left subtree
			if (currentBit == 0) {

				// Base case - node is a leaf
				if (codeTree.getLeft().isLeaf()) {
					output.write(codeTree.getLeft().getValue());
					return;
				}

				else {
					int nextBit = bitInput.readBit();
					decode((CharacterBinaryTree<Character>) codeTree.getLeft(), nextBit, bitInput, output);
				}
			}

			// Recurse on right subtree
			if (currentBit == 1) {

				// Base case - node is a leaf
				if (codeTree.getRight().isLeaf()) {
					output.write(codeTree.getRight().getValue());
					return;
				}

				else {
					int nextBit = bitInput.readBit();
					decode((CharacterBinaryTree<Character>) codeTree.getRight(), nextBit, bitInput, output);
				}
			}
		}

		catch (IOException ex) {
			System.out.println(ex + " decode exception occurred.");
		}

	}

	/**
	 * Puts up a fileChooser and gets path name for file to be opened.
	 * Returns an empty string if the user clicks "cancel".
	 * @return path name of the file chosen	
	 * @author Professor Gevorg Grigoryan
	 */
	public static String getFilePath() {
		//Create a file chooser
		JFileChooser fc = new JFileChooser();

		int returnVal = fc.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)  {
			File file = fc.getSelectedFile();
			String pathName = file.getAbsolutePath();
			return pathName;
		}
		else
			return "";
	}


	public static void main(String[] args) throws IOException {
		// Set filenames
		input_original = getFilePath();
		input_compressed = input_original.substring(0, 
				input_original.length() - 4) + "_compressed.txt";
		input_decompressed = input_original.substring(0, 
				input_original.length() - 4) + "_decompressed.txt";

		// Create BufferedReader input
		BufferedReader input =  new BufferedReader(new FileReader(input_original));

		// Generate frequency map and construct the priority queue heap
		PriorityQueue<CharacterBinaryTree<Character>> myHeap = constructHeap(generateFreqMap(input));

		// Construct the tree out of the heap
		CharacterBinaryTree<Character> codeTree = constructEntireTree(myHeap);

		// Create a corresponding character map of the code tree
		Map<Character, String> codeMap;
		if (codeTree != null) {
			codeMap = codeTree.mapBinary();
		}
		else {
			codeMap = null;
		}

		// Compress
		compress(codeMap);

		// Decompress and decode
		BufferedBitReader bitInput = new BufferedBitReader(input_compressed);
		BufferedWriter output = new BufferedWriter(new FileWriter(input_decompressed));
		decompress(codeTree, bitInput, output);


	}
}