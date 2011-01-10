package dim133.bibliotheca;






import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import dim133.bibliotheca.R;
import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.*;
import dim133.bibliotheca.items.HeaderItem;
import dim133.bibliotheca.items.HistoryItem;
import dim133.bibliotheca.providers.*;
import dim133.bibliotheca.views.*;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Properties;




public class Bibliotheca extends NookActivity  
{
	public static final String HISTORYFILENAME = "history.txt";
	
	public static final String READING_NOW_URL = "content://com.ereader.android/last";
	public static final String LOGTAG = "Bibliotheca";

	public static final String MYLIBRARY = "bibliotheca";
	
	public static final int ITEMSMAX = 10;
	public static final String TITLE = "Bibliotheca";
	
    public static final String SDFOLDER = "/system/media/sdcard/"+MYLIBRARY;
    public static final String EXTERNAL_SDFOLDER = "/sdcard";
    
    public static final String SETTINGSFILE = "/system/media/sdcard/.settings/bibliotheca.settings";
    //public static final String SETTINGSFILE = "/system/media/sdcard/bibliotheca/bset.txt";

    public static final String P_PATH = "path";
    public static final String P_NAME = "name";
    public static final String P_ISHISTORY = "showhistory";
    public static final int MAXPATHLINES = 10;
    
    public static final String YES = "yes";
    public static final String NO = "no";
    
	ItemView[] _items = new ItemView[ITEMSMAX];
	
	IItemProvider _home=null;
	IItemProvider _root=null;
	HistoryItemProvider _history=null;
	IItem _historyitem=null;
	
	LinearLayout _eink;
	HeaderView _header;
	FooterView _footer;

	Button[] _buttons=null;
	Properties _settings=null;
	
	
	int _page;
		
    private OnClickListener _itemclickhandler = new OnClickListener() 
    {   public void onClick(View v) 
    	{
    		if ((v.getTag()==null) || (((ItemView)v.getTag()).getItem()==null) ) return;
    		IItem item = (IItem)( ((ItemView)v.getTag()).getItem() );

    		
    		if (item.getChildren()!=null)
    		{   Expand(item,false);
    		} else
    		{	if (item instanceof IFileItem)
    				Run((IFileItem)item);
    		}
    	}
    };	
    

    private OnClickListener _gouphandler = new OnClickListener() 
    {   public void onClick(View v) 
    	{
    		Expand(_root.getGoUpItem(),false);
    	}
    };	    

    private OnClickListener _gohomehandler = new OnClickListener() 
    {   public void onClick(View v) 
    	{	SetInitialProvider();
    	    Refresh();
    	}
    };	    

    private OnClickListener _historyhandler = new OnClickListener() 
    {   public void onClick(View v) 
    	{	Expand(_historyitem,true);
    	    Refresh();
    	}
    };    

     
    

    private OnClickListener _clickexit = new OnClickListener() 
    {   public void onClick(View v) 
    	{
    		finish();  
    		throw new Error("Dummy exception to force stop the app."); 
    	}
    };	    
    
    
    void Expand(IItem item,Boolean isgotopage0)
    {
    	if (item==null) return;
    	
 
		_root.setLastPage(_page);
		_root = item.getChildren();
		_page =  isgotopage0 ? 0 : ( (item.getType() == ItemType.GoUp)? _root.getLastPage() : 0 ); 
		Refresh();

    	
    }
    
    
    
    void SetInitialProvider()
    {
    	if (_home==null)
    	{
    		MultiItemProvider p = new MultiItemProvider("Home", null);
        	_history = new HistoryItemProvider(HISTORYFILENAME,p,this);
        	_historyitem = new HistoryItem(_history);
        	
        	if (GetSettingsStr(P_ISHISTORY).compareToIgnoreCase(YES)==0)
        		p.AddItem(_historyitem);
    		
        	String name,path;
    		for (int n=1;n<MAXPATHLINES;n++)
    		{
    			path = _settings.getProperty(P_PATH+Util.ToDec(n));
    			if (Util.IsNullOrEmpty( path)) continue;
    			
    			name = _settings.getProperty(P_NAME+Util.ToDec(n));
    			if (Util.IsNullOrEmpty(name)) name = "Path "+Util.ToDec(n);
    			p.AddProvider(new FileItemProvider(name,path, p)); 
    		}    		

    		_home=p;
    	}

    	_root = _home;
        _page=0;    	
    }
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        super.InitScreenlock();

        
        
        String version = Util.EMPTYSTR;
        
        try
        {
        	PackageManager manager = this.getPackageManager();
        	PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
        	version = info.versionName + "." + info.versionCode;
        }
        catch(Exception e)
        {}
                
        
        
        
        
        
        _eink = (LinearLayout) findViewById(R.id.einklayout);
        
        _header = new HeaderView(this,Util.NOTSET);
        _eink.addView(_header);
        
        
        
        for(int i=0;i<ITEMSMAX;i++)
        {	_items[i] = new ItemView(this,i);
        	_eink.addView(_items[i]);
        }
        
        _footer = new FooterView(this);
        _eink.addView(_footer);
        
        LoadSettings();
        
        SetInitialProvider();
        _page=0;

        
        _buttons = new Button[]
	    {
			(Button)findViewById(R.id.Button0),
			(Button)findViewById(R.id.Button1),
			(Button)findViewById(R.id.Button2),
			(Button)findViewById(R.id.Button3),
			(Button)findViewById(R.id.Button4),
			(Button)findViewById(R.id.Button5),
			(Button)findViewById(R.id.Button6),
			(Button)findViewById(R.id.Button7),
			(Button)findViewById(R.id.Button8),
			(Button)findViewById(R.id.Button9)        		
	    };
  
	    for(int i=0;i<_buttons.length;i++)
	    {	_buttons[i].setTag(_items[i]);
  	  	  	_buttons[i].setOnClickListener(_itemclickhandler);
	    }
	  
	    findViewById(R.id.ButtonGoUp).setOnClickListener(_gouphandler); 
	    findViewById(R.id.ButtonGoHome).setOnClickListener(_gohomehandler);
	    findViewById(R.id.ButtonHistory).setOnClickListener(_historyhandler);
	
	    findViewById(R.id.ButtonExit).setOnClickListener(_clickexit); 
	  
	    setAppTitle(TITLE+" "+version);        
        
        
        
        Fill();       
        
        



    }


    public void Fill()
    {
    	
    	
    	_footer.Set(_page,  _root.getPagesCount()+1);
    	_header.setItem(new HeaderItem(_root.getTitle(), _root.getInfo()) );
    	
    	int i=0;
        for(IItem item: _root.getItems(_page))
        {
        	_buttons[i].setVisibility(View.VISIBLE);
        	item.Fill();
        	_items[i].setItem(item);
        	i++;
        	
        }
        
     	for(int j=i;j<ITEMSMAX;j++)
     	{
     		_buttons[j].setVisibility(View.INVISIBLE);
     		_items[j].setItem(null);
     	}
     	
    }
    
    
	void Run(IFileItem item)
	{
		if (item.getPath()==null) return;
		if (item.getMimeType()==null) return;

		String path = "file://" + item.getPath();
        
        Intent intent = new Intent("com.bravo.intent.action.VIEW");

        intent.setDataAndType(Uri.parse(path), item.getMimeType());
        updateReadingNow(intent);
        _history.Add(item);
        //file.updateLastAccessDate();
        

        
        try 
        {   
        	startActivity(intent);
        } catch (ActivityNotFoundException ex) 
        {
        	Log.e(LOGTAG, "Exception while trying to execute - ", ex);
        }

		
	}    
    
	/**
     * Update reading now.
     *
     * @param intent the intent
     */
    protected void updateReadingNow(Intent intent) 
    {
        try 
        {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream aout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(aout);
            dout.writeUTF(intent.getAction());
            dout.writeUTF(intent.getDataString());
            String tmp = intent.getDataString();
            int idx = tmp.indexOf('?');
            if (idx != -1) tmp = tmp.substring(0, idx);
            File f = new File(tmp.substring(6));
            f.setLastModified(System.currentTimeMillis());
            dout.writeUTF(intent.getType());
            dout.writeByte(1);
            dout.writeUTF("BN/Bravo_PATH");
            dout.writeUTF(tmp);
            byte[] data = aout.toByteArray();
            dout.close();
            values.put("data", data);
            getContentResolver().insert(Uri.parse(READING_NOW_URL), values);
        } catch (Exception ex) 
        {
            Log.e(LOGTAG, "Exception while updating reading now data - ", ex);
        }
    }

   
    
	@Override
    void OnKeyPrevious()
    {
		if (_page==0) return;
		_page--;
		Refresh();
	    
    }


	@Override
    void OnKeyNext()
    {
		if (_page >= _root.getPagesCount()) return;
		_page++;
		Refresh();
	    
    }

	void Refresh()
	{
		Fill();
		_eink.invalidate();
	}
    
 
	
	
	
	
	void LoadSettings()
	{
		File f = new File(SETTINGSFILE); 
	
		if (!f.exists())
		{	CreateSettingsFile(f);
		} else
		{	
	    	try
	    	{
	    		_settings = new Properties();
	    		_settings.load(new FileInputStream (f));
	    		if(!_settings.containsKey(P_PATH+Util.ToDec(1))) 
	    			_settings=null;

	    	}
	    	catch(Exception ex)
	    	{
	    		_settings = null;
	    		Log.e(LOGTAG, "Exception while loading settings - ", ex);
	    	}
	    	if (_settings==null) CreateSettingsFile(f);
		}		
		
	}
	
	
    String GetSettingsStr(String key)
    {
    	if (_settings==null) LoadSettings();
    	String result = _settings.getProperty(key); 
    	return result==null ? Util.EMPTYSTR : result;
    }
    
    

    void CreateSettingsFile(File f)
    {
    	_settings = new Properties();
    	_settings.setProperty(P_ISHISTORY,YES);
    	_settings.setProperty(P_PATH+Util.ToDec(1), SDFOLDER);
    	_settings.setProperty(P_NAME+Util.ToDec(1), "SDCard");
    	_settings.setProperty(P_PATH+Util.ToDec(2),EXTERNAL_SDFOLDER);
    	_settings.setProperty(P_NAME+Util.ToDec(2), "Ext SDCard");    	
    	
    	try
    	{
    		_settings.store(new FileOutputStream(f), null);
    	}
    	catch(Exception ex)
    	{
    		Log.e(LOGTAG, "Exception while saving settings - ", ex);
    	}
    	
    }
    
    
    
}