/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common.utilities;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XMLDateUtility {
	
	protected final static int HOURS_FACTOR = 60 * 60;
	protected final static int DAYS_FACTOR = HOURS_FACTOR * 24;
	protected final static int WEEKS_FACTOR = DAYS_FACTOR * 7;
// following two factors depend on specific calendar values, so are approximate, but probably better for rules...	
	protected final static double YEARS_FACTOR = DAYS_FACTOR * 365.25;
	protected final static double MONTHS_FACTOR = YEARS_FACTOR / 12.0;

    public static XMLGregorianCalendar long2XMLGregorian(long dateAsLong) {
    	DatatypeFactory dataTypeFactory;
    	try {
    		dataTypeFactory = DatatypeFactory.newInstance();
    	} catch (DatatypeConfigurationException e) {
    		throw new RuntimeException(e);
    	}
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTimeInMillis(dateAsLong);
    	return dataTypeFactory.newXMLGregorianCalendar(gc);
    	}

    public static XMLGregorianCalendar date2XMLGregorian(Date date) {
    	return long2XMLGregorian(date.getTime());
    	} 

    public static GregorianCalendar xmlGregorian2Gregorian(XMLGregorianCalendar xmlGC) {
    	return xmlGC.toGregorianCalendar();
    }
    
    public static XMLGregorianCalendar gregorian2XMLGregorian(GregorianCalendar gc) {
    	return long2XMLGregorian( gc.getTime().getTime() );
    }

    public static Date xmlGregorian2Date(XMLGregorianCalendar xmlGC) {
    	return xmlGC.toGregorianCalendar().getTime();
    }
    
    public static Date gregorian2Date(GregorianCalendar gc) {
    	return gc.getTime();
    }
    
    public static int calculateAgeInHoursInt( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (int)(age / HOURS_FACTOR);
    }
    
    public static int calculateAgeInDaysInt( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (int)(age / DAYS_FACTOR);
    }
    
    public static int calculateAgeInWeeksInt( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (int)(age / WEEKS_FACTOR);
    }
    
    public static int calculateAgeInMonthsInt( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (int)(age / MONTHS_FACTOR);
    }
    
    public static int calculateAgeInYearsInt( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (int)(age / YEARS_FACTOR);
    }
    
    public static double calculateAgeInHoursDouble( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (double)(age / HOURS_FACTOR);
    }
    
    public static double calculateAgeInDaysDouble( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (double)(age / DAYS_FACTOR);
    }
    
    public static double calculateAgeInWeeksDouble( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (double)(age / WEEKS_FACTOR);
    }
    
    public static double calculateAgeInMonthsDouble( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (double)(age / MONTHS_FACTOR);
    }
    
    public static double calculateAgeInYearsDouble( Date birthTime, Date evalTime ) {
    	long age = evalTime.getTime() - birthTime.getTime();   	
    	return (double)(age / YEARS_FACTOR);
    }
    
}
