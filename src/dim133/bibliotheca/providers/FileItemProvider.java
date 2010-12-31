package dim133.bibliotheca.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;


import dim133.bibliotheca.Bibliotheca;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.*;
import dim133.bibliotheca.items.GoUpItem;
import dim133.bibliotheca.items.ItemComparator;
import dim133.bibliotheca.items.ItemFactory;


public class FileItemProvider extends BaseItemProvider implements IItemProvider 
{

	static final long TIMEOUT = 5 * 1000;//milliseconds
	
	String _path;
	IItemProvider _parent;
	int _itemsperpage;
	
	

	
	public FileItemProvider(String title, String path,IItemProvider parent)
	{
		_path = path;
		_parent = parent;
		_itemsperpage = Bibliotheca.ITEMSMAX;
		_title = title;

		
		
		
	}

	public String getInfo()
	{
		return _path==null ? Util.EMPTYSTR : _path;
	}
	
	
	protected void Fill()
	{

		_issorted = false;
		
		try
		{
			long stop = System.currentTimeMillis()+TIMEOUT;

			
			File  f = new File(_path);
			if (!f.isDirectory()) 
				throw new Exception("Not folder");
			
			if (_title == null) _title = f.getName();

			List<IItem> list = new ArrayList<IItem>();
			if (_parent!=null)
			{	_goupitem = new GoUpItem(_parent.getTitle(), _parent);
				//i.add(_goupitem);
			}
			File[] files = f.listFiles();
			int N = files.length;
			IItem item;
			for(int j=0;j<N;j++)
			{
				item = ItemFactory.FromFile(files[j], this);
				if (item == null) continue;
				list.add(item);
			}

			Boolean istimeout = false;
			for(IItem i:list)
			{
				if (System.currentTimeMillis()>stop) 
				{
					istimeout = true;
					break;
				}
				i.Fill();
			}
			
			if (!istimeout)
			{
				Collections.sort(list, new ItemComparator());
				_issorted = true;
			}
			
			_items = (IItem[])list.toArray(new IItem[list.size()]);
			


		}
		catch(Exception e)
		{
			_title = "Error '"+_path+"'";
			Log.d(Bibliotheca.LOGTAG,"Fill path '"+_path+"' error "+e.getMessage() );
			_items = null;
			_issorted = false;
		}
		
		
	}



	
	




}
