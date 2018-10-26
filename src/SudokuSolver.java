import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SudokuSolver 
{
	//where program begins executing
	public static void main(String [] args)
	{

		//2D array which will contain the initial given locations/values/squares in the Board
		//(if the location is not given, its value in the array will be 0)
		int [][] givenSquares={
				{1,0,3,0},
				{0,0,0,0},
				{2,0,4,0},
				{0,0,0,0}
				};
		 
		//The dimension of the sudoku board we are working with (in this case 4 by 4)
		int gridSize=givenSquares.length;



		//Generates a list of candidate numbers (from 1 to gridSize) to be placed on the board
		//NOTE: I need!! this list so when choose randomly choose numbers
		//I don't choose a number I have chosen before
		MyArrayList<Integer> takenNumbers=new MyArrayList<Integer>(gridSize);
		for(int a=0;a<gridSize;a++)
		{
			takenNumbers.add(a+1);
		}


		//Create a list of randomized sudoku boards which will be of size 10
		//This list will be in sorted ascending order so 
		//that the board with the lowest heuristic value will be at the front
		int numberOfStates=50;
		LinkedList population =reset(takenNumbers, givenSquares, numberOfStates, gridSize);


		//performs a local beam search algorithm on the sudoku board until the solution is found 
		int [][] solvedBoard=localBeamSearch( population, numberOfStates, givenSquares,takenNumbers, gridSize);


		printBoard(solvedBoard);

		//outputs the solution to a text file
		writeToFile(solvedBoard);
	}



	//a local search algorithm 
	/*
	 * The search begins with k (10 in this case)
	   randomly generated states/  randomly generated sudoku boards
	 * At each step, all the successors of all 
	   k boards are generated (in my implementation each board gets n successors)
	   n being one of the dimensions of the board, 4 in this case 
	   because the board is 4 by 4

	 * If any one of the successors is the desired solution, the algorithm stops 

	 * Otherwise, it selects the 
	   10 best boards from both the current list and successor list
	   and repeats until the solution is found or if the algorithm gets stuck at a local minimum 

	* if the algorithm gets stuck at a local minimum. It starts over. ( generates k (10 in this case)
	   random sudoku boards) and performs local beam search again. This way it will eventually find the solution.

	 *  The aim of my implementation of local beam search was so that  
	 	unfruitful searches/board with lots of duplicate numbers will be abandoned  
	 	and it would focus on the generating successors of sudoku boards that 
	 	are "better"/do not have a lot of duplicates
	 */
	static int [][] localBeamSearch(LinkedList currentSudokuBoards, int numberOfStates, int [][] givenSquares,MyArrayList<Integer> takenNumbers, int gridSize)
	{
		//continues until the solution is found
		int old=currentSudokuBoards.front.f;
		int limit =100;
		int trys=0;
		while(true)
		{
			//gets the greatest heuristic value in the list
			int greatestH=currentSudokuBoards.greatestH;

			LinkedList possibleNewBoards =new LinkedList();
			for(Node n=currentSudokuBoards.front;n!=null;n=n.next)
			{
				
				//printBoard(n.sudokuBoard);
				if(generateSuccessors(n.sudokuBoard, possibleNewBoards,givenSquares,n.rows,takenNumbers, greatestH,gridSize))
				{
					// the solution was found so return it
					return possibleNewBoards.front.sudokuBoard;
				}

			}
			

			//selects the 10 best boards from both the currentSudokuBoards list and the possibleNewBoards list
			currentSudokuBoards=createBestSuccessorList(currentSudokuBoards,possibleNewBoards, numberOfStates);

			//in case we are stuck in a local miniumum. restart
			if(old==currentSudokuBoards.front.f)
			{
				trys++;
				if(trys>=limit)
				{ 
					

					currentSudokuBoards=reset(takenNumbers, givenSquares, numberOfStates, gridSize);
					trys=0;
					old=currentSudokuBoards.front.f;

				}
			}
			else
			{
				trys=0;
				old=currentSudokuBoards.front.f;
			}

		}

	}

	//selects the 10 best boards from both the currentSudokuBoards list and the possibleNewBoards list
	static LinkedList createBestSuccessorList(LinkedList currentSudokuBoards, LinkedList possibleNewBoards, int numberOfStates)
	{
		LinkedList bestSuccessorList = new LinkedList();
		int size=0;

		Node ptr1=currentSudokuBoards.front, ptr2=possibleNewBoards.front;

		//continue until one of these list has reached the end
		while(ptr1!=null && ptr2!=null)
		{
			if(ptr1.f<ptr2.f)
			{
				Node f=ptr1;
				ptr1=ptr1.next;

				f.next=null;
				bestSuccessorList.add(f);

			}
			else
			{
				Node f=ptr2;
				ptr2=ptr2.next;

				f.next=null;
				bestSuccessorList.add(f);
			}
			size++;
			if(size>=numberOfStates)
				break;
		}

		if(size<numberOfStates)
		{
			while(ptr1!=null)
			{

				Node f=ptr1;
				ptr1=ptr1.next;

				f.next=null;
				bestSuccessorList.add(f);


				size++;
				if(size>=numberOfStates)
					break;
			}	

			while( ptr2!=null)
			{

				Node f=ptr2;
				ptr2=ptr2.next;

				f.next=null;
				bestSuccessorList.add(f);

				size++;
				if(size>=numberOfStates)
					break;
			}
		}
		return bestSuccessorList;
	}

	//generates a list of all the Squares on the board which are not the given Squares
	@SuppressWarnings("unchecked")
	static MyArrayList<Square>[] createRows(int[][] sudokuBoard,int [][]givenSquares,int gridSize)
	{
		//Each row in the sudoku 
		MyArrayList<Square>[] rows=new MyArrayList[gridSize];

		for(int r=0;r<gridSize;r++)
		{
			rows[r]=new MyArrayList<Square>(gridSize);
			for(int c=0;c<gridSize;c++)
			{
				//if the Square at Row: r and Column: c is not a Given Square add it to the List 
				if(givenSquares[r][c]==0)
				{
					rows[r].add(new Square(r,c,sudokuBoard[r][c]));
				}
			}
		}

		return rows;
	}

	static LinkedList reset(MyArrayList<Integer> takenNumbers,int [][] givenSquares,int numberOfStates, int gridSize)
	{
		LinkedList currentSudokuBoards=new LinkedList();
		for(int a=0;a<numberOfStates;a++)
		{
			//create a new Node -> NOTE each Node has their own sudoku board
			Node n=new Node(gridSize);

			//fill out n's board with random possible values
			randomizeBoard(n.sudokuBoard, takenNumbers, givenSquares,gridSize);

			//calculates the heuristic value of n's sudoku board 
			Package hPackage=heuristic(n.sudokuBoard, takenNumbers, -1,null,null,gridSize);

			n.f=hPackage.nErrors;

			n.errors=hPackage.errors;

			//a list of the Squares on each row of n's the board which DOES NOT CONTAIN the given Squares
			n.rows=createRows(n.sudokuBoard,givenSquares, gridSize);

			//add n to the list
			currentSudokuBoards.add(n);
		}
		return currentSudokuBoards;
	}

	//randomly generates gridSize number of successors, computes their heuristic value, 
	//and if their heuristic value was low enough, adds them to the possibleNewBoards list
	static boolean generateSuccessors(int[][] sudokuBoard, LinkedList possibleNewBoards, int[][] givenSquares, MyArrayList<Square>[] rows,MyArrayList<Integer> takenNumbers,int oldHeuristic,int gridSize)
	{	
		//Goes through every row in the sudoku board that was provided
		
		for(int r4=0;r4<gridSize;r4++)
		{
			//Gets 2 random distinct Squares in the same row, whose values will be swapped 
			//this new sudoku board might be a successor depending on its heuristic value
			rows[r4].resetSize();

			if(rows[r4].size<2)
				continue;
			
			int index1=(int)(Math.random()*rows[r4].size);
			Square s1=rows[r4].remove(index1);
			int index2=(int)(Math.random()*rows[r4].size);
			Square s2=rows[r4].remove(index2);

			//calculates the Heuristic value of this new board where s1 and s2 are swapped
			Package newHeuristicPackage=heuristic(sudokuBoard,takenNumbers,oldHeuristic,s1,s2,gridSize);

			//if this new Heuristic value is lower than the 
			//we add this new board to the list of possible successors 
			if(newHeuristicPackage.nErrors<=oldHeuristic)
			{
				if(!isDupicate(possibleNewBoards,newHeuristicPackage.errors))
				{


					Node n=new Node(gridSize);
					n.f=newHeuristicPackage.nErrors;

					for(int r=0;r<gridSize;r++)
					{
						for(int c=0;c<gridSize;c++)
						{
							n.sudokuBoard[r][c]=sudokuBoard[r][c];
						}
					}
					n.sudokuBoard[s1.r][s1.c]=s2.value;
					n.sudokuBoard[s2.r][s2.c]=s1.value;
					n.rows=createRows(n.sudokuBoard,givenSquares, gridSize);

					possibleNewBoards.add(n);

					//if we found the solution STOP
					if(newHeuristicPackage.nErrors==0)
						return true;
				}
			}
		}
		return false;
	}


	static boolean isDupicate(LinkedList possibleNewBoards,ArrayList<Square> list) 
	{

		for(Node n=possibleNewBoards.front;n!=null;n=n.next)
		{
			if(equals(n.errors,list))
				return true;
		}
		return false;
	}

	static boolean equals(ArrayList<Square> list1,ArrayList<Square> list2)
	{
		if(list1.size()!=list2.size())
			return false;
		
		for(int a=0;a<list1.size();a++)
		{
			if(list1.get(a).r!=list2.get(a).r || list1.get(a).c!=list2.get(a).c)
			{
				return false;
			}
		}
			
		return true;	
	}


	//fills out the 2D sudokuBoard array with random possible values (given squares are already on the board)
	static void randomizeBoard(int[][] sudokuBoard,MyArrayList<Integer> takenNumbers,int [][] givenSquares,int gridSize)
	{
		//goes through each row on the board
		for(int r=0;r<gridSize;r++)
		{

			//take the given values on row: r on the board temporarily out of the takenNumbers array
			takenNumbers.resetSize();
			for(int a=0;a<gridSize;a++)
			{
				if(givenSquares[r][a]!=0)
				{
					int i=takenNumbers.indexOf(givenSquares[r][a]);
					takenNumbers.remove(i);
				}
			}

			for(int c=0;c<gridSize;c++)
			{
				if(givenSquares[r][c]!=0)
				{
					sudokuBoard[r][c]=givenSquares[r][c];
					continue;
				}

				//gets a random possible number from the range (1 to gridSize)
				int i=(int)(Math.random()*takenNumbers.size);

				//places random value on the board
				sudokuBoard[r][c]=takenNumbers.get(i);

				//So we cant choose the same number again
				takenNumbers.remove(i);


			}
		}
	}

	//returns the numbers of duplicates which occur in every column and every sub grid 
	//aka the inner smaller grids (in this case the 4, 2 by 2 grids)
	//the smaller the return value the "better" the board is
	static Package heuristic(int[][] sudokuBoard, MyArrayList<Integer> takenNumbers,int firstTime,Square s1, Square s2,int gridSize )
	{
		//number of duplicates 
		Package errors=new Package(0);

		//the dimension of the sub grid
		int subGridSize=(int)(Math.sqrt(gridSize));

		//resets the array
		takenNumbers.resetSize();

		//if this a the first Time we have randomly generated a sudoku board 
		// firstTime will be equal to -1 and no Squares on the board will be swapped
		if(firstTime!=-1)
		{
			sudokuBoard[s1.r][s1.c]=s2.value;
			sudokuBoard[s2.r][s2.c]=s1.value;
		}

		//go to every sub-grid on the board
		for(int r1=0;r1<gridSize;r1+=subGridSize)
		{
			for(int c1=0;c1<gridSize;c1+=subGridSize)
			{

				//scan the sub-grid for duplicates
				takenNumbers.resetSize();
				for(int r2=0;r2<subGridSize;r2++)
				{
					for(int c2=0;c2<subGridSize;c2++)
					{
						int i=takenNumbers.indexOf(sudokuBoard[r1+r2][c1+c2]);
						if(i==-1)
						{
							errors.nErrors++;
							errors.errors.add(new Square((r1+r2),(c1+c2),sudokuBoard[r1+r2][c1+c2]));
						}
						else
							takenNumbers.remove(i);
					}
				}
			}
		}

		//scans every column for duplicates
		for(int c1=0;c1<gridSize;c1++)
		{
			takenNumbers.resetSize();
			for(int r1=0;r1<gridSize;r1++)
			{
				int i=takenNumbers.indexOf(sudokuBoard[r1][c1]);
				if(i==-1)
				{
					errors.nErrors++;
					errors.errors.add(new Square((r1),(c1),sudokuBoard[r1][c1]));
				}
				else
					takenNumbers.remove(i);
			}
		}

		//if 2 squares were swapped, switch them back
		if(firstTime!=-1)
		{
			sudokuBoard[s1.r][s1.c]=s1.value;
			sudokuBoard[s2.r][s2.c]=s2.value;
		}
		return errors;
	}

	//prints out the sudokuBoard
	static void printBoard(int [] [] sudokuBoard)
	{
		int gridSize=sudokuBoard.length;

		for(int r=0;r<gridSize;r++)
		{
			String j="";
			for(int c=0;c<gridSize;c++)
			{
				j+=sudokuBoard[r][c]+",";
			}
			j=j.substring(0,j.length()-1);

			System.out.println("["+j+"]");

		}
		System.out.println();
	}

	//outputs the solution to a text file
	static void writeToFile(int [] [] sudokuBoard)
	{
		// The name of the file to open.
		String fileName = "output.txt";

		try {
			// Assume default encoding.
			FileWriter fileWriter =
					new FileWriter(fileName);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =
					new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			int gridSize=sudokuBoard.length;
			for(int r=0;r<gridSize;r++)
			{
				String j="";
				for(int c=0;c<gridSize;c++)
				{
					j+=sudokuBoard[r][c]+",";
				}
				j=j.substring(0,j.length()-1);

				bufferedWriter.write("["+j+"]");
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"
							+ fileName + "'");
		}
	}
}


/*
 * HELPER CLASSES
 */


//class which represent a single square on the sudoku board

/*each instance of the Square class will have its own unique 
row and column number, and a value which represents the value
 on the sudoku board at that particular row and column
 */
class Square 
{
	//row and column number
	int r,  c;

	//the value on the sudoku board
	int value;

	public Square(int row, int column, int v) {
		r=row;
		c=column;
		value=v;
	}

	public String toString()
	{
		return "("+r+","+c+"): {"+value+"}";
	}
}

class Package
{
	public Package(int i) {
		nErrors=i;
	}
	int nErrors;
	ArrayList<Square> errors=new ArrayList<Square>();
}


