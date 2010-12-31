package dim133.bibliotheca.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class FooterView extends View 
{
	private Paint _ptext,_pline,_pcurrline,_psepline;
	int _page,_pages;
	
	final int WIDTH =  ItemView.WIDTH;
	final int HEIGHT = 20;
	final int RADIUS = 4;
	final int MAXGAP = RADIUS*6; 
	final int DIAM = RADIUS*2;  
	final int MOVETEXTDOWN = 2;
	
	Boolean _istextmode;
	int _gap;
	int _startpos,_ypos;
	String _text;
	
	public FooterView(Context context) 
	{
		super(context);
		
		
        _psepline = new Paint();
        _psepline.setColor(Color.BLACK); 
        _psepline.setStrokeWidth(1); 
        _psepline.setStyle(Style.STROKE);
        _psepline.setAntiAlias(true);		
		
        _ptext = new Paint();
        _ptext.setColor(Color.BLACK); 
        _ptext.setStrokeWidth(1); 
        _ptext.setTextSize(15);
        _ptext.setStyle(Style.FILL);
        _ptext.setAntiAlias(true);
        
        _pline = new Paint(_ptext);
        _pline.setStyle(Style.STROKE);
        
        _pcurrline = new Paint(_pline);
        _pcurrline.setStyle(Style.FILL_AND_STROKE);
        

        
        this.setMinimumWidth(WIDTH);
        this.setMinimumHeight(HEIGHT);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = HEIGHT;
        lp.width = WIDTH;
        this.setLayoutParams(lp);
        
        Set(0,0);
	}
	
	
	
	public void Set(int page,int pages)
	{
		_page = page;
		_pages = pages;
		


		
		if (pages>1)
		{	_gap = WIDTH - _pages*DIAM/(pages-1);
			if (_gap>MAXGAP) _gap = MAXGAP;
			_startpos = WIDTH - (_pages*DIAM + _gap*(_pages-1));
		} else
		{	_gap=0;
			_startpos = WIDTH - DIAM/2;
		}
		
		if ((_pages+1)*(DIAM+_gap) > WIDTH)
		{	_istextmode = true;
			_text = String.format("Page %d (%d)",page+1,pages);
			Rect bounds = new Rect();
			_ptext.getTextBounds(_text, 0,_text.length(), bounds);
			_startpos =(WIDTH - bounds.width())/2;
			_ypos = (HEIGHT - bounds.height())/2 + bounds.height()/2+MOVETEXTDOWN;
			_gap=0;
		} else
		{	_istextmode = false;
			_text = null;
			_ypos = HEIGHT/2;
			if (pages>1)
			{	_gap = WIDTH - _pages*DIAM/(pages-1);
				if (_gap>MAXGAP) _gap = MAXGAP;
				_startpos = (WIDTH - (_pages*DIAM + _gap*(_pages-1)))/2;
			} else
			{	_gap=0;
				_startpos = (WIDTH - DIAM)/2;
			}			
			
		}
		
	}
	
	
    protected void onDraw(Canvas canvas) 
    { 
    	canvas.drawLine(0, 0, WIDTH,0, _psepline);
    	
    	if (_pages == 0) return;
    	
    	if (_istextmode)
    	{	canvas.drawText(_text, _startpos, _ypos,_ptext);
    	} else
    	{
    		int pos = _startpos;
    		for(int page=0;page<_pages;page++)
    		{
    			canvas.drawCircle(pos,_ypos,RADIUS, (page==_page) ? _pcurrline : _pline);
    			pos+=RADIUS+_gap;
    			
    		}
    		
    		
    	}
    	
    }	

}
