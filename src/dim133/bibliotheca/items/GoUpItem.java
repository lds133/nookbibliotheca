package dim133.bibliotheca.items;



import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItem;
import dim133.bibliotheca.interfaces.IItemProvider;


public class GoUpItem extends BaseItem implements IItem 
{

	
	String _path;
	
	public GoUpItem(String name,IItemProvider parent)
	{
		super();
		
		_parent = parent;
		_title = name;
		_children =  parent ;
		_type = ItemType.GoUp;
		_isfilled = true;
		
	}
	
	public void Fill() 
	{

		
	}
	
	


}
