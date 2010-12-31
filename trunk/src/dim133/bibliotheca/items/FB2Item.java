package dim133.bibliotheca.items;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import dim133.bibliotheca.Bibliotheca;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.*;


public class FB2Item extends FileItem implements IFileItem 
{
	Boolean _iszip;
	
	
	static final String NAME = "name";
	static final String TAGINFO = "title-info";
	static final String TAGTITLE = "book-title";
	static final String TAGAUTHOR = "author";
	static final String TAGTRANSLATOR = "translator";
	static final String TAGSERIES = "sequence";
	static final String TAGSERIESNAME = "name";
	static final String TAGSERIESNUM = "number";

	static final String SERIES = "seriesname";
	static final String SERIES_INDEX = "seriesnumber";
	static final String MIME = "application/fb2";
	
	

	
	public FB2Item()
	{
		super();
		_mimetype = MIME;
	}
	
	public void FromPath(String path,Date date)
	{
		super.FromPath(path, date);
		
		_iszip = (_path.toLowerCase().endsWith(".zip"));
		_type = _iszip ? ItemType.FB2ZIP : ItemType.FB2; 
		
	}
	
	
	
	public FB2Item(File file,Boolean iszip)
	{
		super(file==null ? null : file.getAbsolutePath());
		
		_iszip = iszip;
		_type = iszip ? ItemType.FB2ZIP : ItemType.FB2; 
		_file = file;
		_mimetype = MIME;
	}	
	
	private void FillSeries(XmlPullParser parser) 
	{
		int N = parser.getAttributeCount();
		for(int i = 0;i<N;i++)
		{
			if  (TAGSERIESNAME.equalsIgnoreCase(parser.getAttributeName(i)))
			{	_data.put(SERIES,parser.getAttributeValue(i));
				continue;
			}
			if  (TAGSERIESNUM.equalsIgnoreCase(parser.getAttributeName(i)))
			{	_data.put(SERIES_INDEX,parser.getAttributeValue(i));
				continue;
			}
		}
		
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
			
			InputStream input;
			if (_iszip)
			{
				zip = new ZipFile(_file);
				ZipEntry entry=zip.entries().nextElement();
				if (entry == null) 
					throw new Exception("No files in arcive found");
				input = zip.getInputStream(entry);
			} else
			{	input= new FileInputStream(_file);
			}
				
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(input, null);
			
			Boolean done=false;
			Boolean infofound=false;
			Boolean findname=false;
			Boolean suspend = false;
			String text=Util.EMPTYSTR;

			
			
			while (!done) 
				switch (parser.next())
				{	case XmlPullParser.END_DOCUMENT:
						done=true;
						break;
					case XmlPullParser.END_TAG:
						if (TAGINFO.equalsIgnoreCase(parser.getName()))
						{	done=true;
						} else
						{	
							if ( findname &&
								( (TAGAUTHOR.equalsIgnoreCase(parser.getName())) ||
								  (TAGTRANSLATOR.equalsIgnoreCase(parser.getName())) ) )
							{
									findname = false;
									suspend = false;
							}
							if ((infofound)&& (!findname))
							{	
								String name = parser.getName().toLowerCase();
								if (_data.containsKey(name))
								{	String val = _data.get(name);
									_data.remove(name);
									text = val+SEPARATOR + text;
								} 
								_data.put(name,text);
							}
						}
						break;
					case XmlPullParser.START_TAG:
						if (!infofound)
						{	if (TAGINFO.equalsIgnoreCase(parser.getName()))
								infofound=true;
						} else
						{	if (!findname)
							{
								text=Util.EMPTYSTR;
								if (TAGSERIES.equalsIgnoreCase(parser.getName()))
									FillSeries(parser);
								if ( (TAGAUTHOR.equalsIgnoreCase(parser.getName())) ||
									 (TAGTRANSLATOR.equalsIgnoreCase(parser.getName())) )
								{
									findname = true;
									suspend = true;
								}
							} else
							{
								suspend= ! ( (parser.getName().toLowerCase().contains(NAME)) );  
							}
						}						
						break;
					case  XmlPullParser.TEXT:
						if ((infofound)&(!suspend))
						{	String s = parser.getText().trim();
							if (s.length()!=0) 
								text+=(text.length()!=0 ? " " : "")+s;
						}
						break;
				}				
			input.close();
			if (_iszip) zip.close();
			
			_title 		= _data.containsKey(TAGTITLE) 	? _data.get(TAGTITLE) 	:	Util.EMPTYSTR;
			
			if (Util.IsNullOrEmpty(_title.trim()))
				throw new Exception("Title not found");			
			
			_author 	= _data.containsKey(TAGAUTHOR) 	? _data.get(TAGAUTHOR) 	: 	Util.EMPTYSTR;
			_seriesname	= _data.containsKey(SERIES) 	? _data.get(SERIES) 	: 	Util.EMPTYSTR;
			if (_data.containsKey(SERIES_INDEX)) 
				_seriesindex = Util.FromDec(  _data.get(SERIES_INDEX));
			
			
			
			
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
