package dim133.bibliotheca.views;

import dim133.bibliotheca.interfaces.IItem;
import android.content.Context;
import android.graphics.Canvas;

public class HeaderView extends ItemView
{

	final int NOTSORTED = 3;
	
	public HeaderView(Context context,int index) 
	{
		super(context, index);
		
	}
	
	public void setItem(IItem i)
	{
		super.setItem(i);
		
		
		
	}
	
	
    protected void onDraw(Canvas canvas) 
    {
    	DrawText(canvas);
    	
    	if ( (_item!=null) && (_item.getChildren()!=null) && (!_item.getChildren().isItemsSorted()) )
    		canvas.drawBitmap(_pictures[NOTSORTED],XPOS1,(HEIGHT-PICSIZE)/2,_psepline);
    }
	
}
