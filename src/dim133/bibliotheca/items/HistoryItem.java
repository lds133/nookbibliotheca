package dim133.bibliotheca.items;

import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItem;
import dim133.bibliotheca.interfaces.IItemProvider;
import dim133.bibliotheca.providers.HistoryItemProvider;

public class HistoryItem extends BaseItem implements IItem
{

	
	public HistoryItem(HistoryItemProvider p)
	{
		super();
		
		_title = p.getTitle();
		_children =  (IItemProvider)p;
		_type = ItemType.History;
		_isfilled = true;
		
	}

	public void Fill()
    {
	    
    }
	
}
