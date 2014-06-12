/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.vif.core.util;

import java.sql.SQLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.hip.kernel.bom.HomeManager;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.core.bom.impl.RatingsCalculate;
import org.hip.vif.core.bom.impl.RatingsCalculateHome;
import org.hip.vif.core.bom.impl.RatingsCountCorrectness;
import org.hip.vif.core.bom.impl.RatingsCountCorrectnessHome;
import org.hip.vif.core.bom.impl.RatingsCountEfficiency;
import org.hip.vif.core.bom.impl.RatingsCountEfficiencyHome;
import org.hip.vif.core.bom.impl.RatingsCountEtiquette;
import org.hip.vif.core.bom.impl.RatingsCountEtiquetteHome;
import org.w3c.dom.Document;

/**
 * Helper class acting as parameter object holding the <code>QueryResult</code>s relevant for the ratings.
 * 
 * @author Luthiger
 * Created: 06.10.2011
 */
public class RatingsHelper {
	private static final String TEMPL_TOTAL = "%s (%s)";
	private static final String TEMPL_MEAN = "%s%%";
	private static final String XPATH_EXPR_CORRECNTESS = "/Root/RatingsCountCorrectness/propertySet[Correctness=%s]/Count";
	private static final String XPATH_EXPR_EFFICIENCY = "/Root/RatingsCountEfficiency/propertySet[Efficiency=%s]/Count";
	private static final String XPATH_EXPR_ETIQUETTE = "/Root/RatingsCountEtiquette/propertySet[Etiquette=%s]/Count";
	private static final String XPATH_EXPR_SUM = "/Root/RatingsCalculate/propertySet/Sum%s";
	private static final String XPATH_EXPR_TOTAL = "/Root/RatingsCalculate/propertySet/Count%s";
	private static final String XPATH_EXPR_MEAN = "/Root/RatingsCalculate/propertySet/Mean%s";
	
	private String correctnessA;
	private String correctnessB;
	private String correctnessC;
	private String efficiencyA;
	private String efficiencyB;
	private String efficiencyC;
	private String etiquetteA;
	private String etiquetteB;
	private String etiquetteC;
	private String sum1;
	private String sum2;
	private String sum3;
	private String total1;
	private String total2;
	private String total3;
	private String mean1;
	private String mean2;
	private String mean3;

	/**
	 * Constructor
	 * 
	 * @param inActorID Long the actor's id whose ratings should be retrieved
	 * @throws VException
	 * @throws SQLException
	 * @throws XPathExpressionException 
	 */
	public RatingsHelper(Long inActorID) throws VException, SQLException, XPathExpressionException {
		Document lXml = createXML(inActorID);
		XPath lXPath = XPathFactory.newInstance().newXPath();
		correctnessA = evaluate(lXml, lXPath, XPATH_EXPR_CORRECNTESS, "1"); 
		correctnessB = evaluate(lXml, lXPath, XPATH_EXPR_CORRECNTESS, "0"); 
		correctnessC = evaluate(lXml, lXPath, XPATH_EXPR_CORRECNTESS, "-1"); 
		efficiencyA = evaluate(lXml, lXPath, XPATH_EXPR_EFFICIENCY, "1"); 
		efficiencyB = evaluate(lXml, lXPath, XPATH_EXPR_EFFICIENCY, "0"); 
		efficiencyC = evaluate(lXml, lXPath, XPATH_EXPR_EFFICIENCY, "-1"); 
		etiquetteA = evaluate(lXml, lXPath, XPATH_EXPR_ETIQUETTE, "1"); 
		etiquetteB = evaluate(lXml, lXPath, XPATH_EXPR_ETIQUETTE, "0"); 
		etiquetteC = evaluate(lXml, lXPath, XPATH_EXPR_ETIQUETTE, "-1");
		sum1 = evaluate(lXml, lXPath, XPATH_EXPR_SUM, "1");
		sum2 = evaluate(lXml, lXPath, XPATH_EXPR_SUM, "2");
		sum3 = evaluate(lXml, lXPath, XPATH_EXPR_SUM, "3");
		total1 = evaluate(lXml, lXPath, XPATH_EXPR_TOTAL, "1");
		total2 = evaluate(lXml, lXPath, XPATH_EXPR_TOTAL, "2");
		total3 = evaluate(lXml, lXPath, XPATH_EXPR_TOTAL, "3");
		mean1 = evaluate(lXml, lXPath, XPATH_EXPR_MEAN, "1");
		mean2 = evaluate(lXml, lXPath, XPATH_EXPR_MEAN, "2");
		mean3 = evaluate(lXml, lXPath, XPATH_EXPR_MEAN, "3");
	}
	
	private String evaluate(Document inXML, XPath inPath, String inPathTemplate, String inValue) throws XPathExpressionException {
		String out = (String) inPath.evaluate(String.format(inPathTemplate, inValue), inXML, XPathConstants.STRING);
		return out.length() == 0 ? "0" : out;
	}
	
	private Document createXML(Long inActorID) throws VException, SQLException {
		HomeManager lHomeManager = VSys.homeManager;
		
		StringBuilder out = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root>");
		out.append(getSerialized(((RatingsCalculateHome)lHomeManager.getHome(RatingsCalculate.HOME_CLASS_NAME)).getRatingsOf(inActorID)));
		out.append(getSerialized(((RatingsCountCorrectnessHome)lHomeManager.getHome(RatingsCountCorrectness.HOME_CLASS_NAME)).getRatingsOf(inActorID)));
		out.append(getSerialized(((RatingsCountEfficiencyHome)lHomeManager.getHome(RatingsCountEfficiency.HOME_CLASS_NAME)).getRatingsOf(inActorID)));
		out.append(getSerialized(((RatingsCountEtiquetteHome)lHomeManager.getHome(RatingsCountEtiquette.HOME_CLASS_NAME)).getRatingsOf(inActorID)));
		out.append("</Root>");
		return new XMLRepresentation(new String(out)).reveal();
	}
	
	private StringBuilder getSerialized(QueryResult inQuery) throws VException, SQLException {
		StringBuilder outXML = new StringBuilder();
		while (inQuery.hasMoreElements()) {
			outXML.append(inQuery.nextAsXMLString());
		}
		return outXML;
	}
	
	public String getCorrectnessA() {
		return correctnessA;
	}
	
	public String getCorrectnessB() {
		return correctnessB;
	}
	
	public String getCorrectnessC() {
		return correctnessC;
	}
	
	public String getEfficiencyA() {
		return efficiencyA;
	}
	
	public String getEfficiencyB() {
		return efficiencyB;
	}
	
	public String getEfficiencyC() {
		return efficiencyC;
	}
	
	public String getEtiquetteA() {
		return etiquetteA;
	}
	
	public String getEtiquetteB() {
		return etiquetteB;
	}
	
	public String getEtiquetteC() {
		return etiquetteC;
	}
	
	public String getTotal1() {
		return String.format(TEMPL_TOTAL, sum1, total1);
	}
	
	public String getTotal2() {
		return String.format(TEMPL_TOTAL, sum2, total2);
	}
	
	public String getTotal3() {
		return String.format(TEMPL_TOTAL, sum3, total3);
	}
	
	public String getMean1() {
		return String.format(TEMPL_MEAN, mean1);
	}
	
	public String getMean2() {
		return String.format(TEMPL_MEAN, mean2);
	}
	
	public String getMean3() {
		return String.format(TEMPL_MEAN, mean3);
	}

}
