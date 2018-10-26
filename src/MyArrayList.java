
//my version of the ArrayList class
//**IT DIFFERS FROM THE JAVA UTIL VERSION IN THE REMOVE METHOD

//**WHEN AN ELEMENT IS REMOVED FROM THE LIST IN THE JAVA UTIL 

//IN MY VERSION the element we want to "delete" is just swapped with the element at index size-1
//then the size variable is decremented so we know what elements we should access

/*
 * YOU CAN DO THE SAME THING IN THE JAVA UTIL ARRAYLIST BUT IT WOULD TAKE MUCH LONDER
 * 
 * FIRSTLY BECAUSE REMOVING AN ELEMENT IN THE UTIL VERSION MOVES ALL THE 
 * ELEMENTS TO THE RIGHT OF IT TO THE LEFT 1(WHICH WASTES TIME)
 * 
 * SECONDLY, I REUSE THESE LISTS. SO I WOULD HAVE TO ADD THE ELEMENTS I REMOVED BACK (WHICH WASTES TIME)
 * 
 * In MY VERISION I JUST RESET THE size variable 
 */


/*
public class MyArrayList 
{
	//a list of Squares 
	Square[] data;

	//keeping track of how many elements are actually in this List
	int trueSize=0;

	//keeps track of what elements we want to choose from the list
	int size=0;
	public MyArrayList(int length)
	{
		data=new Square[length];
	}

	//swaps the element at index i with the element at index size-1 **SAVES TIME
	public Square remove(int i)
	{
		Square temp=data[i];
		data[i]=data[size-1];
		data[size-1]=temp;
		size--;

		return temp;
	}
	
	//Once this is executed any element in the array can be randomly chosen
	public void resetSize()
	{
		size=trueSize;
	}

	//adds a square to the List
	public void add(Square square) 
	{
		data[size]=square;
		size++;
		if(trueSize<size)
		{
			trueSize=size;
		}
	}

	
	//If value is in the list: the index number is returned
	//else -1 is returned
	public int indexOf(int value)
	{
		int index;
		for(index=0;index<size;index++)
		{
			if(data[index].value==value)
			{
				return index;
			}
		}
		return -1;
	}

	//produces a String representation of the list *** FOR DEBUGGING PURPOSES **
	public String toString()
	{
		String r="[";
		for(int a=0;a<trueSize;a++)
		{
			r+=data[a]+",";
		}
		r=r.substring(0,r.length()-1);
		r+="]";

		return r;
	}

	//return the element in the array at index i
	public int get(int i) 
	{
		return data[i].value;
	}
}

*/
public class MyArrayList<T>
{
	//Square[] data;
	int trueSize=0;
	int size=0;
	 /**
	
		     * The array elements to be stored inside
	
		     * customArrayListElementData.
	
		     */
	private transient Object[] customArrayListElementData;
	public MyArrayList(int length)
	{
		this.customArrayListElementData = new Object[length];
	}
	
	@SuppressWarnings("unchecked")
	public T remove(int index)
	{
		Object temp=customArrayListElementData[index];
		customArrayListElementData[index]=customArrayListElementData[size-1];
		customArrayListElementData[size-1]=temp;
		size--;
		
		return (T) temp;
	}
	public void resetSize()
	{
		size=trueSize;
	}
	public void add(T element) 
	{
		customArrayListElementData[size]=element;
		size++;
		if(trueSize<size)
		{
			trueSize=size;
		}
	}

	public int indexOf(int value )
	{
		int index;
		for(index=0;index<size;index++)
		{
			if((Integer)customArrayListElementData[index]==value)
			{
				return index;
			}
		}
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public String toString()
	{
		String r="[";
		for(int a=0;a<trueSize;a++)
		{
			r+=(T)customArrayListElementData[a]+",";
		}
		r=r.substring(0,r.length()-1);
		r+="]";
		
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i) 
	{
		
		return (T)customArrayListElementData[i];
	}
}