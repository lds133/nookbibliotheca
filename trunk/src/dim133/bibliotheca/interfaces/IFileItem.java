package dim133.bibliotheca.interfaces;

import java.util.Date;

public interface IFileItem extends IItem
{
	String getPath();
	String getMimeType();
	Boolean compareTo(IFileItem item);
	Date getDate();
	void setDate();
	
	void FromPath(String path,Date date);
	
}
