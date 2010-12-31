package dim133.bibliotheca.providers;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import dim133.bibliotheca.Bibliotheca;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.IFileItem;
import dim133.bibliotheca.interfaces.IItem;
import dim133.bibliotheca.interfaces.IItemProvider;
import dim133.bibliotheca.items.GoUpItem;
import dim133.bibliotheca.items.ItemFactory;

public class HistoryItemProvider extends BaseItemProvider implements IItemProvider 
{
	static final String TITLE = "History"; 
	static final int ITEMSMAX = 30;
	static final String LF = "\r\n"; 
	
	
	IItemProvider _parent;
	String _filename;
	Context _context;
	
	public HistoryItemProvider(String filename,IItemProvider parent,Context context)
	{

		_context = context;
		_filename = filename;
		_parent = parent;
		_itemsperpage = Bibliotheca.ITEMSMAX;
		_title = TITLE;	
		_issorted = true;
		
		Fill();
		
		Save();
		
	}

	
	
	

	FileInputStream FileToRead() throws FileNotFoundException
	{
		return _context.openFileInput(_filename);
	}

	void FileDelete()
	{
		_context.deleteFile(_filename);
	}

	FileOutputStream FileToWrite(Boolean isappend) throws FileNotFoundException
	{
		return _context.openFileOutput(_filename, Context.MODE_WORLD_READABLE | (isappend ? Context.MODE_APPEND : 0) );
	}
	

	/*
	//final String SD = "/sdcard/"; 
	final String SD = "/system/media/sdcard/";
	
	FileInputStream FileToRead() throws FileNotFoundException
	{
		return  new FileInputStream(SD+_filename);
	}

	void FileDelete()
	{
		File f = new File(SD+_filename);
		f.delete();
	}

	FileOutputStream FileToWrite(Boolean isappend) throws FileNotFoundException
	{
		return  new FileOutputStream(SD+_filename);
		
	}

	*/

	
	
	
	void Save()
	{
		FileOutputStream fs = null;
		OutputStreamWriter sw = null;
		BufferedWriter bw=null;
		try
		{
			FileDelete();
			
			if ((_items!=null) && (_items.length != 0))
			{
				fs = FileToWrite(false);
				sw = new OutputStreamWriter(fs);
				bw = new BufferedWriter(sw);
				int N = _items.length>ITEMSMAX ? ITEMSMAX : _items.length;
				String line;
				for(int i=N-1;i>=0;i--)
				{
					line = ItemFactory.ToString(_items[i]);
					if (!Util.IsNullOrEmpty(line)) 
						bw.write(line+LF);
				}
				bw.flush();
				if (bw!=null) bw.close();
			}
			
		}
		catch(Exception e)
		{
			Log.d(Bibliotheca.LOGTAG, "Can't update history file -", e);			
		}
	}
	
	
	public void Add(IItem item)
	{
		
		String line =ItemFactory.ToString(item);
		if (Util.IsNullOrEmpty(line)) return;
		
		IItem newitem = ItemFactory.FromString(line);
		
		if (newitem!=null)
		{
			newitem.Fill();
			ReFill(newitem);
		} else
		{
			Log.d(Bibliotheca.LOGTAG,"ItemFactory string serializator fails");
		}
		
		
		
		FileOutputStream fs = null;
		OutputStreamWriter sw = null;
		try
		{	fs = FileToWrite(true);
			sw = new OutputStreamWriter(fs);
			sw.write(line+LF);
			sw.flush();
			if (sw!=null) sw.close();
		}
		catch(Exception e)
		{
			Log.d(Bibliotheca.LOGTAG, "Can't write to history file -", e);			
		}
		
	}
	
	
	
	void Add(List<IItem> list, IItem item)
	{
		if  (item instanceof IFileItem) 
		{
			List<IItem> victims = new ArrayList<IItem>();
			victims.clear();
			
			for(IItem i:list)
				if  (i instanceof IFileItem) 
					if ( ((IFileItem)item).compareTo((IFileItem)i) ) 
						victims.add(i);
			
			if (victims.size()!=0)
				for(IItem i:victims)
					list.remove(i);
		}
			
		list.add(0,item);
		
	}
	
	@Override
	protected void Fill()
	{
		FileInputStream fs = null;
		InputStreamReader sr = null;
		BufferedReader br=null;
		
		List<IItem> i = new ArrayList<IItem>();
		i.clear();
		
		
		if (_parent!=null)
		{	
			_goupitem = new GoUpItem(_parent.getTitle(), _parent);
			//i.add(_goupitem);
		}
		
		try
		{
			fs = FileToRead();
			sr = new InputStreamReader(fs);
			br = new BufferedReader(sr);
			
			String line;
			IItem item;
			while ((line = br.readLine()) != null)
			{
				item = ItemFactory.FromString(line);
				if (item==null) continue;
				Add(i,item);
				item.Fill();
			}
		
			_items = (IItem[])i.toArray(new IItem[i.size()]);			

			
			if (br!=null) br.close();

		}
		catch(Exception e)
		{
			Log.d(Bibliotheca.LOGTAG, "Can't read from history file '"+_filename+"'");			
		}
		
	}
	
	
	public void ReFill(IItem item)
	{
		
		List<IItem> list = new ArrayList<IItem>();
		list.clear();
		
		if (_items!=null)
		{	for(IItem i:_items)
				list.add(i);
		}
		
		Add(list,item);
		
		_items = (IItem[])list.toArray(new IItem[list.size()]);
		
	}


	public String getInfo()
    {
	    return Util.EMPTYSTR;
    }

}
