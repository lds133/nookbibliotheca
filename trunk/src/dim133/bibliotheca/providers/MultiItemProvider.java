package dim133.bibliotheca.providers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dim133.bibliotheca.Bibliotheca;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.IItem;
import dim133.bibliotheca.interfaces.IItemProvider;
import dim133.bibliotheca.items.GoUpItem;
import dim133.bibliotheca.items.ItemComparator;


public class MultiItemProvider extends BaseItemProvider implements IItemProvider 
{

	List<IItemProvider> _providers = new ArrayList<IItemProvider>();
	List<IItem> _newitems = new ArrayList<IItem>();
	IItemProvider _parent;	
	
	
	public String getInfo()
	{
		String result= Util.EMPTYSTR;
		for(IItemProvider p:_providers)
		{
			if (p.getInfo().length()==0) continue;
			result += (result.length()!=0 ? ", " : "") + p.getInfo();
		}
		return result;
	}
	
	
	public MultiItemProvider(String title, IItemProvider parent)
	{
		super();
		
		_providers.clear();
		_parent = parent;
		_itemsperpage = Bibliotheca.ITEMSMAX;
		_title = title;		
		
	}
	
	public void AddProvider(IItemProvider p)
	{
		_providers.add(p);
	}
	
	public void AddItem(IItem p)
	{
		_newitems.add(p);
	}

	
	
	@Override
	protected void Fill() 
	{
		
		_issorted = false;
		
		List<IItem> i = new ArrayList<IItem>();
		if (_parent!=null)
			i.add(new GoUpItem(_parent.getTitle(), _parent));
		

		
		for(IItem item:_newitems)
				i.add(item);
		
		Boolean istimeout = false;
		for(IItemProvider p:_providers)
		{	for(IItem item: p.getItems())
				i.add(item);
			if (!p.isItemsSorted()) istimeout = true;
		}
		
		if (!istimeout)
		{
			Collections.sort(i, new ItemComparator());
			_issorted = true;
		}
        
		_items = (IItem[])i.toArray(new IItem[i.size()]);

	}

}
