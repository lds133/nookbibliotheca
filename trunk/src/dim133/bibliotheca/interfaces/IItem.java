package dim133.bibliotheca.interfaces;


import dim133.bibliotheca.enums.ItemType;



public interface IItem
{
	String getTitle();
	String getAuthor();
	ItemType getType();
	String getSeries();
	String getSeriesName();
	int getSeriesIndex();
	long getSize();
	String getSizeStr();

	
	Boolean getSelected();
	void setSelected(Boolean isenabled);

	void Fill();

	IItemProvider getChildren();
	
	
	
	
	
}

