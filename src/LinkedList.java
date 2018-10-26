
/*
 * A List of Nodes which will be maintained in sorted ascending order so 
 * that the board with the lowest heuristic value will be at the front
 */
public class LinkedList 
{
	//beginning of the list
	Node front=null;

	//the greatest heuristic value in the list
	int greatestH;
	

	//locates the proper place in the list and then adds the newNode into the list
	//so that it will always be sorted
	public void add(Node newNode)
	{
		if(front==null )
		{	
			newNode.next=front;
			front=newNode;
			greatestH=front.f;
			return;
		}
		if(newNode.f<front.f)
		{	
			newNode.next=front;
			front=newNode;
			return;
		}

		Node prev=front;

		for(Node current=front.next;current!=null;current=current.next)
		{
			if(newNode.f<current.f)
			{
				prev.next=newNode;
				newNode.next=current;


				return;
			}
			prev=prev.next;
		}

		prev.next=newNode;

		greatestH=newNode.f;
	}

	
	

	//produces a String representation of the list /* FOR DEBUGGING PURPOSES */
	public String toString()
	{
		if(front==null)
			return "";
		String r="";
		for(Node n=front;n!=null;n=n.next)
		{
			r+=n.toString()+"->";	
		}
		return r.substring(0, r.length()-2);
	}

}