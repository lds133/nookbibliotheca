package dim133.bibliotheca.items;



import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItem;





public class HeaderItem extends BaseItem implements IItem
{
	static int _counter = 0;
	

	
	public HeaderItem(String title,String info)
	{
		super();
		
		_title = title;
		_type = ItemType.Header;
		_author = info;
		_isfilled = true;
		
	}




	public void Fill() 
	{

		


		
	}

}
