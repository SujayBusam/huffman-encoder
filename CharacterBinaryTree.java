import java.util.HashMap;
import java.util.Map;

/**
 * Class for binary tree that stores character and frequency
 * 
 * @author Sujay Busam
 *
 * @param <Character> data type
 */


public class CharacterBinaryTree<Character> extends BinaryTree<Character> {
	// Frequency value
	private int dataFreq;
	
	/**
	 * Constructor to initialize character and frequency
	 * Constructs leaf node. Left and right are null
	 * 
	 * @param data
	 * @param frequency
	 */
	public CharacterBinaryTree(Character data, int frequency) {
		super(data);
		this.dataFreq = frequency;
	}
	
	/**
	 * Constructs inner node
	 * @param data
	 * @param left the left tree
	 * @param right the right tree
	 */
	public CharacterBinaryTree(Character data, int frequency, 
			CharacterBinaryTree<Character> left, CharacterBinaryTree<Character> right) {
		super (data, left, right);
		this.dataFreq = frequency;
	}

	
	/**
	 * Set the character frequency
	 * @param frequency
	 */
	public void setFreq(int frequency) {
		this.dataFreq = frequency;
	}
	
	
	/**
	 * Get the character frequency
	 * @return int frequency
	 */
	public int getFreq() {
		return this.dataFreq;
	}
	
	
	/**
	 * Creates the map that pairs characters to their code
	 * 
	 * @return the map of characters and Huffman codes
	 */
	public Map<Character, String> mapBinary() {
		
		// Check for empty tree
		if (this == null || this.getLeft() == null && this.getRight() == null) {
			return null;
		}
		
		// Otherwise, construct entire map from traversal of code tree
		else {
			Map<Character, String> binaryMap = new HashMap<Character, String>();
			this.traverseCodeTree(binaryMap, "");
			return binaryMap;
		}
	}
	
	
	/**
	 * Helper method to traverse tree
	 * 
	 * @param binaryMap Map that will hold characters and Huffman codes
	 * @param currentBinary String of binary code
	 */
	public void traverseCodeTree(Map<Character, String> binaryMap, String currentBinary) {
		// Base case
		if (this.isLeaf()) {
			Character currentChar = this.getValue();
			binaryMap.put(currentChar, currentBinary);
		}
		
		// Recursive cases. Not at leaf yet. Continue traversing
		
		if (this.hasLeft()) {
			// Add a 0 to code
			((CharacterBinaryTree<Character>) this.getLeft()).traverseCodeTree(binaryMap, currentBinary + "0");
		}
		
		if (this.hasRight()) {
			// Add a 1 to code
			((CharacterBinaryTree<Character>) this.getRight()).traverseCodeTree(binaryMap, currentBinary + "1");
		}
	}
}
