package dim133.bibliotheca;

public class Util 
{
	static public final String EMPTYSTR = "";
	static public final int NOTSET = -1;


	public static int FromDec(String s)
	{
		Integer val = Integer.valueOf( s ).intValue();
		return (val!=null) ? val : NOTSET;		
		
	}
	
	public static Boolean IsNullOrEmpty(String s)
	{
		return ((s==null) || (s.length()==0));
	}
	
}
