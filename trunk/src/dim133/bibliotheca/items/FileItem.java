package dim133.bibliotheca.items;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.IFileItem;

public class FileItem extends BaseItem
{
	protected File _file=null;
    protected Map<String, String> _data = new HashMap<String, String>();	
    protected static final String SEPARATOR =" , ";
	protected String _path = null;
	protected String _mimetype=null;
    Date _date=null;
    
	
	public Date getDate()
	{
		return _date;
	}
    
    public void setDate()
    {
    	_date=new Date();
    }
	
	public String getMimeType()
	{
		return _mimetype;
	}    
    
	public String getPath()
	{
		return _path;
	}	
	
	public FileItem(String path)
	{
		super();
		_path= path;
	}
	
	public FileItem()
	{
		super();
		_path= null;
		_file=null;
	}	

	
	public void FromPath(String path,Date date)
	{
		_path = path;
		_date = date;
		Prepare();
	}
	
	
	public Boolean compareTo(IFileItem item)
	{
		if (item==null) return false;
		return getPath().compareTo(item.getPath())==0;
		
	}
	
	public Boolean Prepare()
	{
		if (_file==null)
		{
			if ( Util.IsNullOrEmpty(_path) ) return false;
			_file = new File(_path);
		}
		
		_size = _file.length();
		_path = _file.getAbsolutePath();
		
		return (_file!=null);
		
	}

	static SimpleDateFormat _dateformat = new SimpleDateFormat("dd.MM.yy HH:mm"); 
	
	public String getSizeStr()
	{
		return _date==null ? super.getSizeStr() : _dateformat.format(_date);
	}
	
}
