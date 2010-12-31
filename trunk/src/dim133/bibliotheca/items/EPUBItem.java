package dim133.bibliotheca.items;

import java.io.File;
import java.io.InputStream;


import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;  

import dim133.bibliotheca.Bibliotheca;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.*;

import org.xmlpull.v1.XmlPullParser;  
import org.xmlpull.v1.XmlPullParserFactory;  

import android.util.Log;




public class EPUBItem extends FileItem implements IFileItem 
{

	
	static final String TAGROOTFILE = "rootfile";
	static final String CONTAINER = "META-INF/container.xml";
	static final String TAGPATH ="full-path";
	
    static final String TITLE = "title";
    static final String CREATOR = "creator";
    static final String CONTRIBUTOR = "contributor";
    static final String PUBLISHER = "publisher";
    static final String DESCRIPTION = "description";
    static final String SUBJECT = "subject";
    static final String IDENTIFIER = "identifier";
    static final String SERIES = "series";
    static final String SERIES_INDEX = "series_index";
    static final String DATE = "date";
    static final String ISBN = "ISBN";  	
    static final String TAGMETADATA = "metadata";
    static final String MIME = "application/epub";
    

    public EPUBItem()
    {
    	super();
    	_type = ItemType.EPUB; 
		_mimetype = MIME;
    }
    



	public EPUBItem(File file)
	{
		super(file==null ? null : file.getAbsolutePath());
		
		_file = file;
		_type = ItemType.EPUB; 
		_path = null;
		_mimetype = MIME;
	}
	
	
	

	
	

	
	
	public void Fill()
	{
		if (_isfilled) return;
		
		ZipFile zip = null;
		_data.clear();
		
		

		try
		{

			if (!Prepare())
				throw new Exception("File not set");
			
			if (!_file.exists())
				throw new Exception("File not found");
			
			zip = new ZipFile(_file);
			ZipEntry container = zip.getEntry(CONTAINER);              
			if (container == null) 
				throw new Exception("No container found");
			
			InputStream cinput = zip.getInputStream(container);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(cinput, null);
			

			String path=null;
			Boolean done=false;
			while (!done) 
				switch (parser.next())
				{	case XmlPullParser.END_DOCUMENT:
						done=true;
						break;
					case XmlPullParser.START_TAG:
						if (TAGROOTFILE.equalsIgnoreCase(parser.getName())) 
						{	path = parser.getAttributeValue(parser.getAttributeNamespace(0), TAGPATH);
						 	done=true;
						}
						break;
				}		
				
			cinput.close();
			
			if (path==null)
				throw new Exception("Root file path not found");
			
			InputStream rinput = zip.getInputStream(zip.getEntry(path));
			parser.setInput(rinput, null);  
			
			done=false;
			Boolean metafound=false;
			String text=Util.EMPTYSTR;
			while (!done) 
				switch (parser.next())
				{	case XmlPullParser.END_DOCUMENT:
						done=true;
						break;
					case XmlPullParser.END_TAG:
						if (TAGMETADATA.equalsIgnoreCase(parser.getName()))
						{	done=true;
						} else
						{	if (metafound) 
							{
								String name = parser.getName().toLowerCase();
								if (_data.containsKey(name))
								{	String val = _data.get(name);
									_data.remove(name);
									text = val+SEPARATOR + text;
								} 
								_data.put(parser.getName().toLowerCase(),text);
							}
						}
						break;
					case XmlPullParser.START_TAG:
						if (!metafound)
						{	if (TAGMETADATA.equalsIgnoreCase(parser.getName()))
								metafound=true;
						} else
						{	text=Util.EMPTYSTR;	
						}						
						break;
					case  XmlPullParser.TEXT:
						if (metafound)
						{	String s = parser.getText().trim();
							if (s.length()!=0) 
								text+= (text.length()!=0 ? " " : "")+s;
						}

						break;
				}				
			rinput.close();
			zip.close();
			
			_title 		= _data.containsKey(TITLE) 		? _data.get(TITLE) 		:	Util.EMPTYSTR;

			if (Util.IsNullOrEmpty(_title.trim()))
				throw new Exception("Title not found");			
			
			_author 	= _data.containsKey(CREATOR) 	? _data.get(CREATOR) 	: 	Util.EMPTYSTR;
			_seriesname	= _data.containsKey(SERIES) 	? _data.get(SERIES) 	: 	Util.EMPTYSTR;
			if (_data.containsKey(SERIES_INDEX)) 
				_seriesindex = Util.FromDec( _data.get(SERIES_INDEX) );
			


			
		}
		catch(Exception e)
		{
			_title = _file.getName();
			_author = e.getMessage();
			_seriesname = _path;
			_seriesindex = Util.NOTSET;
			_type = ItemType.Broken;
			
			Log.d(Bibliotheca.LOGTAG,"Parse error '"+_path+"' "+e.getMessage() );
			
			_isfilled = true;
			
		}

		
		
		
		_data.clear();
		_file =null;
		_isfilled = true;
		
	}
	
	


}
