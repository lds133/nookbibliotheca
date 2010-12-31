package dim133.bibliotheca.items;


import java.io.File;

import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItem;
import dim133.bibliotheca.interfaces.IItemProvider;
import dim133.bibliotheca.providers.FileItemProvider;

public class FolderItem extends BaseItem implements IItem 
{

	String _path;
	
	public FolderItem(String path,IItemProvider parent)
	{
		super();
		
		
		_parent = parent;
		
		try
		{
			File  f = new File(path);
			if (!f.isDirectory()) 
				throw new Exception("Not folder");
			_title = f.getName();
		    _type = ItemType.Folder;
			_children =  new FileItemProvider(_title,path, parent);

		}
		catch(Exception e)
		{
			_title = path;
			_children = null;
			_type = ItemType.Broken;
			_author = e.getMessage();
		}
		
		
		
	}

	
	public void Fill() 
	{
		if (_isfilled) return;
		
		_isfilled = true;

		
	}
	
	

}
