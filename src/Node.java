import java.util.ArrayList;

/*each instance of the Node class will have its own unique 
board configuration, a heuristic value which corresponds to that board
and a rows ArrayList which contains every Square on the sudoku board which 
is not given aka every square on the board whose value can be altered 


*FOR EXAMPLE: rows[0] contains all the Squares in the first row of the board that are not given Squares
* 
* say were given a sudoku board [4..1]  where a '.' is a number you must "fill in"
* 								[..34]
* 								[....]
* 								[....]
* 
* 					so rows[0] will not contain the 4 and 1, because those values are "taken"
* 					and rows[1] will not contain the 4 and 3, because those values are "taken"
*/
public class Node 
{
	//List of non-given Squares on the sudoku board
	MyArrayList<Square>[] rows;
	
	//unique board configuration
	int [][] sudokuBoard;
	
	//heuristic value which corresponds to the above sudokuBoard
	int f;
	
	//Pointer to the next Node in the List
	Node next;

	ArrayList<Square> errors;
	public Node(int gridSize)
	{
		sudokuBoard=new int [gridSize][gridSize];	
		next=null;
		errors=new ArrayList<Square>();
	}

	//produces a String representation of the Node /* FOR DEBUGGING PURPOSES */
	public String toString()
	{
		return "[ "+f+" ]  ";
	}


}