package org.cdsframework.cds.supportingdata;

public interface SupportingData {

	/**
	 * Returns true if the supporting data is empty; false if it is not.
	 * @return true if the supporting data instance is empty; false if it is not.
	 */
	public boolean isEmpty();
	
	/**
	 * It is suggested that this method be invoked assure that the state of the supporting data is complete and "consistent", before the data is used in rules. An example of
	 * incomplete or inconsistent supporting data that could be provided: vaccine supporting data would be considered inconsistent/incomplete if a combination vaccine is defined 
	 * in the supporting data with vaccine componenets, but the supporting data for one or more of the vaccine components have not themselves been defined. This method will return
	 * false in this case. Supporting data could also be marked inconsistent in other situations, such as if a problem was encountered when reading the supporting data or if the 
	 * data in some other way does not pass validation.  (e.g. - data such as time periods is in an improper format; specified vaccine group is not previously defined in a  
	 * code system, a series specifies a season that has not previously been defined, etc.)  
	 * @return true if the supporting data is complete and consistent; false if it is not.
	 */
	public boolean isSupportingDataConsistent();
	
}
