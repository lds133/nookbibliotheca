package dim133.bibliotheca.items;



import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.*;


public class ItemFactory 
{
	static final String STR_SEPARATOR = "|";
	static final String REX_SEPARATOR = "\\|";
	static SimpleDateFormat _dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 

	public static IItem FromFile(File file,IItemProvider parent)
	{
		
		if (file.isDirectory())
			return new FolderItem(file.getAbsolutePath(), parent);
		
		String ext = file.getName().toLowerCase();
        if (ext.endsWith("epub"))
        	return new EPUBItem(file);
        
        if (ext.endsWith("fb2"))
        	return new FB2Item(file,false);
		
        if (ext.endsWith("fb2.zip"))
        	return new FB2Item(file,true);

		return null;
		
	}
	
	public static String ToString(IItem item)
	{
		if (!(item instanceof IFileItem)) 
			return Util.EMPTYSTR;
		
		String t = IFileItem.class.getName();
		String c = item.getClass().getName();
		String p = ((IFileItem)item).getPath();
		String d = _dateformat.format( ((IFileItem)item).getDate()==null ?  (new Date()) : ((IFileItem)item).getDate() ) ;
		
		return t+STR_SEPARATOR+c+STR_SEPARATOR+p+STR_SEPARATOR+d;
	}
	
	public static IItem FromString(String text)
	{

		String[] param =  text.split(REX_SEPARATOR);
		
		if (param.length==0) return null;
		
		if (param[0].compareTo(IFileItem.class.getName())!=0) return null;
		if (param.length<4) return null;
		

		Date  d=null;
		try
        {   d = _dateformat.parse(param[3]);
        } catch (ParseException e)
        {   d =null;
        }		
		
        Class<?> c;
        IFileItem item=null;
        try
        {
	        c = Class.forName(param[1]);
	        Object i = c.newInstance();
	        if (!(i instanceof IFileItem))
	        	throw new Exception("Wrong class");
	        item = (IFileItem)i;
	        
	        item.FromPath(param[2], d);
	        
        } catch (Exception e)
        {
        	item=null;
        }
        

		
		return item;
	}
	
	
	
}
