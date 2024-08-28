/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
 
 // Functions as an interface used for selenium testing
 
function drillDownChart(chartElement, drillDownWindowType)
{
	return window.drillDown(chartElement.toString(), drillDownWindowType.toString());
}
	
function getChartURL()
{
	return window.getURL();
}