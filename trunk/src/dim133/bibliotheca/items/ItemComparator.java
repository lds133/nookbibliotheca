package dim133.bibliotheca.items;

import java.util.Comparator;

import dim133.bibliotheca.Util;
import dim133.bibliotheca.interfaces.IItem;

public class ItemComparator implements  Comparator<Object>
{

	public int compare(Object o1, Object o2) 
	{
		IItem i1 = (IItem) o1;
		IItem i2 = (IItem) o2;
		
		if (i1.getType().getWeight() != i2.getType().getWeight())
			return (i1.getType().getWeight() > i2.getType().getWeight()) ? 1 : -1;
			
		int result;

		if ( (!Util.IsNullOrEmpty(i1.getSeriesName())) && (!Util.IsNullOrEmpty(i2.getSeriesName())) )
		{
			result =  i1.getSeriesName().compareToIgnoreCase(i2.getSeriesName());
			if (result!=0) return result;
	
			if (i1.getSeriesIndex() != i2.getSeriesIndex())
				return (i1.getSeriesIndex() > i2.getSeriesIndex()) ? 1 : -1;
		}		
		
		result =  i1.getAuthor().compareToIgnoreCase(i2.getAuthor());
		if (result!=0) return result;		
		
		result =  i1.getTitle().compareToIgnoreCase(i2.getTitle());
		if (result!=0) return result;
		
		

		return 0;
	}

}
