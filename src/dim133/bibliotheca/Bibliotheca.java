package dim133.bibliotheca;





import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

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




public class Bibliotheca extends NookActivity  
{
	public static final String HISTORYFILENAME = "history.txt";
	
	public static final String READING_NOW_URL = "content://com.ereader.android/last";
	public static final String LOGTAG = "Bibliotheca";

	public static final String MYLIBRARY = "bibliotheca";
	
	public static final int ITEMSMAX = 10;
	public static final String TITLE = "Bibliotheca";
	
    public static final String SDFOLDER = "/system/media/sdcard/"+MYLIBRARY;
    public static final String EXTERNAL_SDFOLDER = "/sdcard/"+MYLIBRARY;



    
	ItemView[] _items = new ItemView[ITEMSMAX];
	
	IItemProvider _home=null;
	IItemProvider _root=null;
	HistoryItemProvider _history=null;
	IItem _historyitem=null;
	
	LinearLayout _eink;
	HeaderView _header;
	FooterView _footer;

	Button[] _buttons=null;
	
	
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
    		p.AddItem(_historyitem);
    		p.AddProvider(new FileItemProvider("SDCard",SDFOLDER, p)); 
    		p.AddProvider(new FileItemProvider("Ext SDCard",EXTERNAL_SDFOLDER, p));
    		

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
    
 
    

    	
    
    
    
}