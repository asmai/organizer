package org.musalahuddin.myexpenseorganizer.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

public class Utils {

	 /**
	   * <a href="http://www.ibm.com/developerworks/java/library/j-numberformat/">http://www.ibm.com/developerworks/java/library/j-numberformat/</a>
	   * @param strFloat parsed as float with the number format defined in the locale
	   * @return the float retrieved from the string or null if parse did not succeed
	   */
	  public static BigDecimal validateNumber(DecimalFormat df, String strFloat) {
	    ParsePosition pp;
	    pp = new ParsePosition( 0 );
	    pp.setIndex( 0 );
	    df.setParseBigDecimal(true);
	    BigDecimal n = (BigDecimal) df.parse(strFloat,pp);
	    if( strFloat.length() != pp.getIndex() || 
	        n == null )
	    {
	      return null;
	    } else {
	      return n;
	    }
	  }
}
