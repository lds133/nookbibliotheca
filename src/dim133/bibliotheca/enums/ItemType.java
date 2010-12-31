package dim133.bibliotheca.enums;

import dim133.bibliotheca.Util;

public enum ItemType
{
	// see FMItemView.LoadPictures
	
	Unknown(3,100),
	FB2(1,10),
	EPUB(0,10),
	GoUp(6,2),
	History(5,1),
	Folder(2,3),
	FB2ZIP(7,10),
	Broken(3,20),
	Header(Util.NOTSET,0);
	
	private int _index,_weight;
	ItemType(int index,int weight) 
	{	this._index = index;
		this._weight = weight;
	}
	public int	getIndex() 
	{   return _index;      
	} 	
	public int	getWeight() 
	{   return _weight;      
	} 	
	
	
	
}
