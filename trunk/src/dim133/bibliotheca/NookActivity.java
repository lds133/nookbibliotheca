package dim133.bibliotheca;

import android.app.Activity;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;


public abstract class NookActivity extends Activity
{
	public static final int NOOK_KEY_PREV_LEFT = 96;
	public static final int NOOK_KEY_PREV_RIGHT = 98;
	public static final int NOOK_KEY_NEXT_LEFT = 95;
	public static final int NOOK_KEY_NEXT_RIGHT = 97;
	public static final int NOOK_KEY_SHIFT_UP = 101;
	public static final int NOOK_KEY_SHIFT_DOWN = 100;
	
	PowerManager.WakeLock _screenlock = null;
	long SSDELAY = 600000;

	static String LOGTAG = "nookActivity";
	protected boolean _firsttime = true;
	private String _title = "noname";
	public final static String UPDATE_TITLE = "com.bravo.intent.UPDATE_TITLE"; 	
	
	
	
	abstract void OnKeyPrevious();
	abstract void OnKeyNext();
	
	
	
	
	protected void InitScreenlock()
	{
        PowerManager power = (PowerManager) getSystemService(POWER_SERVICE);
        _screenlock = power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "nookactivity" + hashCode());          
        _screenlock.setReferenceCounted(false); 
        
        
        _screenlock.acquire();
	}
	
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case NOOK_KEY_SHIFT_UP:
            case NOOK_KEY_PREV_LEFT:
            case NOOK_KEY_PREV_RIGHT:
            	OnKeyPrevious();
                return true;

            case NOOK_KEY_SHIFT_DOWN:
            case NOOK_KEY_NEXT_LEFT:
            case NOOK_KEY_NEXT_RIGHT:
            	OnKeyNext();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
        
    @Override  
    public void onUserInteraction() 
    {   super.onUserInteraction();          
    	if (_screenlock != null) _screenlock.acquire(SSDELAY);          
	}
        
    @Override      
    public void onPause() 
    {   super.onPause();          
    	try 
    	{   if (_screenlock != null)  _screenlock.release();
    	} catch (Exception ex) 
    	{	Log.e(LOGTAG, "exception in onPause - ", ex);
    	}      
    }
    
    @Override      
    public void onResume() 
    {	super.onResume();
       
    	if (_screenlock != null) _screenlock.acquire(SSDELAY);
    	         
    	_firsttime = false;          
    	setAppTitle(_title);
    	     
    }      
    
    //////////////////////////////////////////////////////////////////////
    
    
    
    public void setAppTitle(String title) 
    {	try 
    	{	_title = title;	
    		Intent intent = new Intent(UPDATE_TITLE);
    		String key = "apptitle";
    		intent.putExtra(key, title);
    		sendBroadcast(intent);
    	} catch (Exception ex) 
    	{   Log.e(LOGTAG, "exception on setting title - ", ex);         
    	}      
    }  
    

}
