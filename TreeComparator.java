import java.util.Comparator;

/**
 * Comparator class utilized by the priority queue.
 * Compares frequencies of two trees
 * 
 * @author Sujay Busam
 * @param <E> the generic data type
 */

public class TreeComparator<E> implements Comparator<CharacterBinaryTree<Character>> {

	public int compare(CharacterBinaryTree<Character> treeOne, 
			CharacterBinaryTree<Character> treeTwo) {
		if (treeOne.getFreq() > treeTwo.getFreq())
			return 1;
		
		else if (treeTwo.getFreq() > treeOne.getFreq())
			return -1;
		
		// Otherwise, both are equal
		else
			return 0;
	}
}