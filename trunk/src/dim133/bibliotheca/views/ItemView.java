package dim133.bibliotheca.views;



import dim133.bibliotheca.R;
import dim133.bibliotheca.Util;
import dim133.bibliotheca.enums.ItemType;
import dim133.bibliotheca.interfaces.IItem;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ItemView extends View
{
	static final int WIDTH = 584;
	static final int HEIGHT = 67;
	protected Paint _ptitle,_ptitlesmall,_pauth,_pseries,_pindex,_psepline,_pboldline;
	int _index;
	IItem _item;
	int[] _positions=POSTAB1;
	
	static Bitmap[] _pictures=null;

	
	static void LoadPictures(Resources res)
	{
		//see FMIType index property
		_pictures = new Bitmap[]
		 {	BitmapFactory.decodeResource( res, R.drawable.book_epub),	//0
			BitmapFactory.decodeResource( res, R.drawable.book_fb2),	//1
			BitmapFactory.decodeResource( res, R.drawable.folder),		//2
			BitmapFactory.decodeResource( res, R.drawable.question),	//3
			BitmapFactory.decodeResource( res, R.drawable.save),		//4
			BitmapFactory.decodeResource( res, R.drawable.scenario),	//5
			BitmapFactory.decodeResource( res, R.drawable.undo),		//6
			BitmapFactory.decodeResource( res, R.drawable.book_fb2zip)	//7
		 };
	}
	
	

    public ItemView(Context context,int index) 
    { 
        super(context); 
        
        if (_pictures==null) LoadPictures(getResources());
        
        _index = index;
 
        Paint p = new Paint();
        p.setColor(Color.BLACK); 
        p.setStrokeWidth(1); 
        p.setStyle(Style.FILL);
        p.setTextSize(30);
        p.setAntiAlias(true);

        _pindex = p;
        _ptitle = p;
        
        _ptitlesmall = new Paint(p);
        _ptitlesmall.setTextSize(20);
        _ptitlesmall.setTypeface(Typeface.DEFAULT_BOLD);

        
        _pseries = new Paint(p);
        _pseries.setTextSize(15);
        
        _pauth = new Paint(p);
        _pauth.setTextSize(20);

        
        _psepline = new Paint();
        _psepline.setColor(Color.BLACK); 
        _psepline.setStrokeWidth(1); 
        _psepline.setStyle(Style.STROKE);
        _psepline.setAntiAlias(true);
        
        _pboldline = new Paint(_psepline);
        _pboldline.setStrokeWidth(3); 
        
        this.setMinimumWidth(WIDTH);
        this.setMinimumHeight(HEIGHT);
         
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = HEIGHT;
        lp.width = WIDTH;
        
       
        this.setLayoutParams(lp);
        
        
    } 
    
	public void setItem(IItem i)
	{
		_item = i;
		
		if (_item==null) return;
		
		if (_item.getType() == ItemType.Header)
			_pauth =_pseries;
		
    	_positions =  POSTAB3;
    	if (_item.getSeries().length()==0) _positions = POSTAB2;
    	if ((_item.getAuthor().length()==0) && (_item.getSeries().length()==0)) _positions = POSTAB1;
		
	}
	    
	public IItem getItem()
	{
		return _item;
	}
	

    final int MARGIN =5;
    final int RECTOFFSET = 10;
    final int XPOS1 = HEIGHT - RECTOFFSET*2 + MARGIN;
    final int PICSIZE = 32;
    final int XPOS2 = XPOS1+PICSIZE+ MARGIN;    
    final int TITLEMOVE = 0;
    final int AUTHMOVE = 1;
  
    final float RECTROUND = 5;
    
    static int[] POSTAB3 = new int[]{28,44,62};
    static int[] POSTAB2 = new int[]{30,0,55};
    static int[] POSTAB1 = new int[]{45,0,0};
	
    

    protected void DrawText(Canvas canvas)
    {
    	Rect tb = new Rect();
    	_ptitle.getTextBounds( _item.getTitle(),0, _item.getTitle().length(),tb);
    	if (tb.width()+XPOS2>WIDTH)
    	{	canvas.drawText( _item.getTitle(),XPOS2,_positions[0]+TITLEMOVE, _ptitlesmall);
    	} else
    	{	canvas.drawText( _item.getTitle(),XPOS2,_positions[0], _ptitle);
    	}
    	
    	String a = _item.getAuthor();
    	if (!Util.IsNullOrEmpty(a))
    	{
    		_pauth.getTextBounds( a,0, a.length(),tb);
    		if (tb.width()+XPOS2>WIDTH)
    		{	canvas.drawText( a,XPOS2,_positions[2]+AUTHMOVE, _pseries);
    		} else
    		{	canvas.drawText( a,XPOS2,_positions[2], _pauth);
    		}
    		
    	}
    }
    
    
    protected void onDraw(Canvas canvas) 
    { 

    	if (_item==null) return;
   	
    	String s = 	String.format("%d", _index);
    	RectF rect = new RectF(0, RECTOFFSET,HEIGHT - RECTOFFSET*2,HEIGHT- RECTOFFSET);
    	canvas.drawRoundRect(rect, RECTROUND, RECTROUND, _pboldline);
    	Rect bounds = new Rect();
    	_pindex.getTextBounds(s,0,s.length(),bounds);
    	canvas.drawText( 	s,
    						(rect.width() - bounds.width())/2 +rect.left,
    						(rect.height() - bounds.height())/2 +rect.top+bounds.height() , 
    						_pindex);
    	canvas.drawBitmap(_pictures[_item.getType().getIndex()],XPOS1,(HEIGHT-PICSIZE)/2,_psepline);
    	canvas.drawLine(0, 0, WIDTH,0, _psepline);
    	
    	String sizestr = _item.getSizeStr(); 
    	if (sizestr.length()!=0)
    	{
        	Rect sb = new Rect();
        	_pseries.getTextBounds( sizestr,0, sizestr.length(),sb);
        	canvas.drawText( sizestr ,WIDTH - sb.width(),HEIGHT-1, _pseries);    		
    		
    	}
    		
    	if (_item.getSeries().length()!=0)
    		canvas.drawText( _item.getSeries(),XPOS2,_positions[1], _pseries);
    	
    	DrawText(canvas);
    	
    } 
}
