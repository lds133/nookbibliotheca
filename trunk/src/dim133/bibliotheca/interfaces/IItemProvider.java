package dim133.bibliotheca.interfaces;


public interface IItemProvider
{
	String getTitle();
	String getInfo();
	
	Iterable<IItem> getItems();
	Iterable<IItem> getItems(int pagenumber);
	int getPagesCount();
	int getCount();
	
	void setLastPage(int page);
	int getLastPage();

	IItem getGoUpItem();
	
	Boolean isItemsSorted();
	
}
