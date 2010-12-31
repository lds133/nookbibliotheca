package dim133.bibliotheca.items;




import dim133.bibliotheca.Util;
import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItemProvider;

public class BaseItem 
{
	protected String _title = Util.EMPTYSTR;
	protected String _author =Util.EMPTYSTR;
	protected ItemType _type = ItemType.Unknown; 
	protected String _seriesname =Util.EMPTYSTR;
	protected int _seriesindex =Util.NOTSET;
	protected long _size = 0;
	protected IItemProvider _parent=null;
	protected Boolean _isfilled = false;

	
	
	Boolean _selected = false;	
	protected IItemProvider _children = null;
	
	
	public String getTitle()
	{
		return _title;
	}

	public String getAuthor()
	{
		
		return _author;
	}

	public ItemType getType()
	{
		return _type;
	}

	public String getSeries()
	{
		if (_seriesindex!=Util.NOTSET) 
			return String.format("%s %d",_seriesname,_seriesindex);
		return _seriesname;
	}
	
	public String getSeriesName()
	{
		return _seriesname;
	}
	
	public int getSeriesIndex()
	{
		return _seriesindex;
	}
	

	public long getSize()
	{
		return _size;
	}



	public Boolean getSelected()
	{
		return _selected;
	}

	public void setSelected(Boolean isenabled)
	{
		_selected = isenabled;
	}

	public IItemProvider getChildren()
    {
	    return _children;
    }




	


	
	static final long KILO = 1024;
	static final long MEGA = 1024*1024;
	
	public String getSizeStr()
	{
		if (_size <= 0) 
			return Util.EMPTYSTR;
		
		if (_size < KILO) 
			return String.format("%d",_size);
		
		if (_size < MEGA) 
			return String.format("%dk",_size/KILO);
		
    	return String.format("%dM",_size/MEGA);	
	}

	
	
	
}
