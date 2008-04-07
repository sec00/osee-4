/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author David Diepenbrock
 */
public class Xml {
   private static final String[] XML_CHARS = new String[] {"[&]", "[<]", "[>]", "[\"]"};
   private static final String[] XML_ESCAPES = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"};
   private static final String LINEFEED = "&#10;";
   private static final String CARRIAGE_RETURN = "&#13;";
   private static final Pattern squareBracket = Pattern.compile("\\]");

   public final static XPathFactory myXPathFactory = XPathFactory.newInstance();
   public final static XPath myXPath = myXPathFactory.newXPath();
   public final static String wordLeader1 =
         "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<?mso-application progid='Word.Document'?>";
   public final static String wordLeader2 =
         "<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml='http://schemas.microsoft.com/aml/2001/core' xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0='http://www.w3.org/2001/XMLSchema' xmlns:ns1='http://eclipse.org/artifact.xsd' xmlns:st1='urn:schemas-microsoft-com:office:smarttags' w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>";
   public final static String wordLeader = wordLeader1.concat(wordLeader2);
   public final static String wordBody = "<w:body></w:body>";
   public final static String wordTrailer = "</w:wordDocument> ";
   public final SimpleNamespaceContext mySimpleNamespaceContext = new SimpleNamespaceContext();

   /**
    * TODO Optimize algorithm
    * 
    * @param text
    * @return Returns a string with entity reference characters unescaped.
    */
   public static StringBuilder unescape(String text) {
      StringBuilder strB = new StringBuilder();
      int startIndex, endIndex;
      char chr;

      for (int index = 0; index < text.length(); index++) {
         chr = text.charAt(index);

         if (chr == '&') {
            startIndex = index;
            endIndex = text.indexOf(';', startIndex) + 1;

            String entityReference = text.substring(startIndex, endIndex);

            if (entityReference.equals("&amp;"))
               strB.append('&');
            else if (entityReference.equals("&lt;"))
               strB.append('<');
            else if (entityReference.equals("&gt;"))
               strB.append('>');
            else if (entityReference.equals("&nbsp;"))
               strB.append(' ');
            else if (entityReference.equals("&quot;"))
               strB.append('"');
            else
               throw new IllegalArgumentException("unknown entity reference: " + text.substring(startIndex, endIndex));

            index = endIndex - 1;
         } else {
            strB.append(chr);
         }
      }
      return strB;
   }

   /**
    * TODO Optimize algorithm
    * 
    * @param text
    * @return Returns a string with entity reference characters escaped.
    */
   public static CharSequence escape(CharSequence text) {
      String textString = text.toString();
      for (int x = 0; x < XML_CHARS.length; x++) {
         textString = textString.replaceAll(XML_CHARS[x], XML_ESCAPES[x]);
      }

      return textString;
   }

   public static void writeAsCdata(Appendable appendable, String string) throws IOException {
      if (string.indexOf('<') == -1 && string.indexOf('&') == -1 && string.indexOf("]]>") == -1) {
         writeData(appendable, string);
      } else {
         if (string.indexOf(']') == -1) {
            writeCdata(appendable, string);
         } else {
            //  work around bug in excel xml parsing that thinks a single ] closes CDATA
            String[] tokens = squareBracket.split(string);
            for (int i = 0; i < tokens.length; i++) {
               writeCdata(appendable, tokens[i]);
               if (i != tokens.length - 1) { // the last token would not have been followed by ]
                  appendable.append(']');
               }
            }
         }
      }
   }

   private static void writeCdata(Appendable appendable, String content) throws IOException {
      appendable.append("<![CDATA[");
      appendable.append(content);
      appendable.append("]]>");
   }

   private static void writeData(Appendable appendable, String string) throws IOException {
      for (int index = 0; index < string.length(); index++) {
         char value = string.charAt(index);
         if (value == '\r') {
            appendable.append(CARRIAGE_RETURN);
         } else if (value == '\n') {
            appendable.append(LINEFEED);
         } else {
            appendable.append(value);
         }
      }
   }

   public static String treatNonUTF8Characters(String contentString) {
      String resultString = contentString;
      String[][] nonUTF8CharactersOfInterest = { {"�", "-"}, {"�", "'"}, {"�", "'"}, {"�", "\""}, {"�", "\""}};//Wider than usual dash , smaller than usual bullet
      for (int i = 0; i < nonUTF8CharactersOfInterest.length; i++) {
         String[] splitsOfNonUTF8 = resultString.split(nonUTF8CharactersOfInterest[i][0]);//Wider than usual dash or bullet
         if (splitsOfNonUTF8.length > 1) {
            StringBuffer myStringBuffer = new StringBuffer();
            for (int j = 0; j < splitsOfNonUTF8.length; j++) {
               myStringBuffer.append(splitsOfNonUTF8[j]);
               if (splitsOfNonUTF8[j].length() > 0 && j < splitsOfNonUTF8.length - 1) {
                  myStringBuffer.append(nonUTF8CharactersOfInterest[i][1]);
               }
            }
            resultString = myStringBuffer.toString();
         }
      }
      String[] splits = resultString.split("[^\\p{Space}\\p{Graph}]");
      int stringPosition = 0;
      if (splits.length > 1) {
         StringBuffer myStringBuffer = new StringBuffer();
         for (int i = 0; i < splits.length; i++) {
            stringPosition = stringPosition + splits[i].length();
            myStringBuffer.append(splits[i]);
            stringPosition = stringPosition + 1;
            if (splits[i].length() > 0 && i < splits.length - 1) {
               myStringBuffer.append("-");
            }
         }
         resultString = myStringBuffer.toString();
      }

      return resultString;
   }

   public static final Element appendNewElementWithText(Node parentElementName, String newElementTagName, String newText) {
      Element newElement = null;
      try {
         newElement = parentElementName.getOwnerDocument().createElement(newElementTagName);
         parentElementName.appendChild(newElement);
         if (newText != null) {
            Node newTextNode = parentElementName.getOwnerDocument().createTextNode(newText);
            newElement.appendChild(newTextNode);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
   }

   public static final Element appendNewElementWithTextCData(Node parentElementName, String newElementTagName, String newText) {
      Element newElement = null;
      try {
         newElement = parentElementName.getOwnerDocument().createElement(newElementTagName);
         parentElementName.appendChild(newElement);
         if (newText != null) {
            Node newTextNode = parentElementName.getOwnerDocument().createCDATASection(newText);
            newElement.appendChild(newTextNode);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
   }

   public static final Element appendNewElementWithTextAndAttributes(Node parentElementName, String newElementTagName, String newText, String[][] attributes) {
      Element newElement = null;
      try {
         newElement = parentElementName.getOwnerDocument().createElement(newElementTagName);
         parentElementName.appendChild(newElement);
         if (newText != null) {
            Node newTextNode = parentElementName.getOwnerDocument().createTextNode(newText);
            newElement.appendChild(newTextNode);
         }
         if (attributes != null) {
            for (String[] attribute : attributes) {
               newElement.setAttribute(attribute[0], attribute[1]);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return newElement;
   }

   public static final Element[] appendNewElementsWithText(Node parentElementName, String newElementsTagName, String[] textInstances) {
      Element[] newElements = new Element[textInstances.length];
      try {
         if (textInstances != null) {
            for (int i = 0; i < textInstances.length; i++) {
               newElements[i] = parentElementName.getOwnerDocument().createElement(newElementsTagName);
               parentElementName.appendChild(newElements[i]);
               Node newTextNode = parentElementName.getOwnerDocument().createTextNode(textInstances[i]);
               newElements[i].appendChild(newTextNode);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return newElements;
   }

   public static final Element appendNewElementWithTextAndOneAttribute(Node parentElementName, String newElementTagName, String newText, String attributeName, String attributeValue) {
      Element newElement = null;
      try {
         newElement = appendNewElementWithText(parentElementName, newElementTagName, newText);
         newElement.setAttribute(attributeName, attributeValue);
      } catch (Exception e) {
         e.printStackTrace();
      }
      ;
      return newElement;
   }

   public final static String restartNumberingWhenPreparingToEditWithWord(InputStream myInputStream) throws XPathExpressionException, ParserConfigurationException, TransformerException, SAXException, IOException {
      SimpleNamespaceContext mySimpleNamespaceContext = new SimpleNamespaceContext();
      addNamespacesForWordMarkupLanguage(myXPath, mySimpleNamespaceContext);
      Document myDocument = Jaxp.readXmlDocumentNamespaceAware(myInputStream);
      Element myDocumentElement = myDocument.getDocumentElement();
      Node[] myListss = selectNodeList(myDocumentElement, "descendant::w:lists");
      Node[] myLists = selectNodeList(myDocumentElement, "descendant::w:lists/w:list");
      if (myLists.length > 0 && myListss.length > 0) {
         Node[] mywilfo = selectNodeList(myLists[myLists.length - 1], "@w:ilfo");
         if (mywilfo.length > 0) {
            String myLastUsedListInitializeListFormat = mywilfo[0].getNodeValue();//
            int myNextILFO = Integer.parseInt(myLastUsedListInitializeListFormat);
            Node[] myListDefinition =
                  selectNodeList(myDocumentElement,
                        "descendant::w:listDef[child::w:lvl[1]/w:pStyle/@w:val = 'listlvl1'][1]");//<w:pStyle w:val="listlvl1"/>
            if (myListDefinition.length > 0) {
               String mylistDefaultID = selectNodeList(myListDefinition[0], "@w:listDefId")[0].getNodeValue();
               Node[] myWord_Formatted_Contents = selectNodeList(myDocument, "descendant::ns1:Word_Formatted_Content");
               for (int i = 0; i < myWord_Formatted_Contents.length; i++) {
                  Node[] myPStyles =
                        selectNodeList(myWord_Formatted_Contents[i],
                              "descendant::w:pPr[child::w:pStyle[@w:val = 'listlvl1']]");
                  for (int j = 0; j < Math.min(1, myPStyles.length); j++) {
                     Node[] myListProperties = selectNodeList(myPStyles[j], "child::w:listPr");
                     if (myListProperties.length > 0) {
                        myNextILFO++;
                        Element newWList =
                              appendNewElementWithTextAndOneAttribute(myListss[0], "w:list", null, "w:ilfo",
                                    "" + myNextILFO);
                        appendNewElementWithTextAndOneAttribute(newWList, "w:ilst", null, "w:val", mylistDefaultID);
                        Element new_lvlOverride =
                              appendNewElementWithTextAndOneAttribute(newWList, "w:lvlOverride", null, "w:ilvl", "0");
                        appendNewElementWithTextAndOneAttribute(new_lvlOverride, "w:startOverride", null, "w:val", "1");
                        appendNewElementWithTextAndOneAttribute(myListProperties[0], "w:ilvl", null, "w:val", "0");
                        appendNewElementWithTextAndOneAttribute(myListProperties[0], "w:ilfo", null, "w:val",
                              "" + myNextILFO);
                     }
                  }
               }
            }
         }
      }
      String myString = Jaxp.xmlToString(myDocument, Jaxp.getPrettyFormat(myDocument));
      return myString;
   }

   public static Document readWordFormattedContent(String myInputString) throws IOException, ParserConfigurationException, SAXException {
      Document myDocument = null;
      myDocument = Jaxp.readXmlDocumentNamespaceAware(wordLeader.concat(myInputString).concat(wordTrailer));
      return myDocument;
   }

   public static byte[] getFormattedContent(Element formattedItemElement) {
      ByteArrayOutputStream data = new ByteArrayOutputStream();
      OutputFormat format = Jaxp.getCompactFormat(formattedItemElement.getOwnerDocument());
      format.setOmitDocumentType(true);
      format.setOmitXMLDeclaration(true);
      XMLSerializer serializer = new XMLSerializer(data, format);

      try {
         for (Element e : Jaxp.getChildDirects(formattedItemElement))
            serializer.serialize(e);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }

      return data.toByteArray();
   }

   public static final Object addNamespacesForWordMarkupLanguage(XPath myXPath, SimpleNamespaceContext mySimpleNamespaceContext) {
      try {
         if (myXPath.getNamespaceContext() == null) {
            mySimpleNamespaceContext.addNamespace("w", "http://schemas.microsoft.com/office/word/2003/wordml");
            mySimpleNamespaceContext.addNamespace("wx", "http://schemas.microsoft.com/office/word/2003/auxHint");
            mySimpleNamespaceContext.addNamespace("o", "urn:schemas-microsoft-com:office:office");
            mySimpleNamespaceContext.addNamespace("v", "urn:schemas-microsoft-com:vml");
            mySimpleNamespaceContext.addNamespace("aml", "http://schemas.microsoft.com/aml/2001/core");
            mySimpleNamespaceContext.addNamespace("dt", "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882");
            mySimpleNamespaceContext.addNamespace("ns0", "http://www.w3.org/2001/XMLSchema");
            mySimpleNamespaceContext.addNamespace("ns1", "http://eclipse.org/artifact.xsd");
            mySimpleNamespaceContext.addNamespace("ns2", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("sl", "http://schemas.microsoft.com/schemaLibrary/2003/core");
            mySimpleNamespaceContext.addNamespace("st0", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st1", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st2", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st3", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("st4", "urn:schemas-microsoft-com:office:smarttags");
            mySimpleNamespaceContext.addNamespace("w10", "urn:schemas-microsoft-com:office:word");
            mySimpleNamespaceContext.addNamespace("wsp", "http://schemas.microsoft.com/office/word/2003/wordml/sp2");
            mySimpleNamespaceContext.addNamespace("foo", "http://apache.org/foo");
            mySimpleNamespaceContext.addNamespace("bar", "http://apache.org/bar");
            myXPath.setNamespaceContext(mySimpleNamespaceContext);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public static final Node[] selectNodeList(Node startingNode, String xPathExpression) throws XPathExpressionException {
      Object publisherNodeSet = null;
      publisherNodeSet = myXPath.evaluate(xPathExpression, startingNode, XPathConstants.NODESET);
      DTMNodeList myNodeList = (DTMNodeList) publisherNodeSet;
      Node[] resultNodes = new Node[myNodeList.getLength()];
      for (int i = 0; i < resultNodes.length; i++) {
         resultNodes[i] = myNodeList.item(i);
      }
      return resultNodes;
   }

   public static final String selectNodesText(Node startingNode, String xPathExpression) throws XPathExpressionException {
      String resultString = "";
      Node[] selectedNodes = selectNodeList(startingNode, xPathExpression);
      if (selectedNodes.length == 1) {
         Node[] selectedTextNodes = selectNodeList(selectedNodes[0], "child::text()");
         for (Node node : selectedTextNodes) {
            resultString = resultString.concat(node.getNodeValue().trim());
         }

      }
      return resultString;
   }

   public static final Element makeTable(Element parentDivElement, String caption, String[][] columnDescriptors) {

      //      appendNewElementWithTextAndAttributes(parentDivElement, "a", null, new String[][] {{"name", caption}});
      Element newTableElement =
            appendNewElementWithTextAndAttributes(parentDivElement, "table", null, new String[][] { {"border", "1"},
                  {"cellpadding", "3"}, {"cellspacing", "0"}, {"width", "95%"}});
      //      appendNewElementWithText(newTableElement, "caption", "");
      appendNewElementWithText(newTableElement, "caption", caption);
      Element columnGroupElement =
            appendNewElementWithTextAndAttributes(newTableElement, "colgroup", null, new String[][] {{"align", "left"}});
      String[] columnNames = new String[columnDescriptors.length];
      for (int i = 0; i < columnDescriptors.length; i++) {
         columnNames[i] = columnDescriptors[i][0];
         appendNewElementWithTextAndAttributes(columnGroupElement, "col", null, new String[][] {{"width",
               columnDescriptors[i][1]}});//width,33
      }
      Element headingTableRowElement = appendNewElementWithText(newTableElement, "tr", null);
      appendNewElementsWithText(headingTableRowElement, "th", columnNames);
      return newTableElement;
   }

   public static final Element[] makeDivElementAndTableElement(Element parentDivElement, String caption, String[][] columnDescriptors) {
      Element[] divAndTableElements =
            new Element[] {parentDivElement, parentDivElement.getOwnerDocument().createElement("div"), null};
      divAndTableElements[2] = makeTable(divAndTableElements[1], caption, columnDescriptors);
      return divAndTableElements;
   }

   public static final Element[] makeTableRow(Element[] devAndTableElements, String[] cellContents) {
      Element nextRow = Xml.appendNewElementWithText(devAndTableElements[2], "tr", null);
      appendNewElementsWithText(nextRow, "td", cellContents);
      //      devAndTableElements[0].appendChild(devAndTableElements[1]);
      return devAndTableElements;
   }

   public static final Element[] makeTableRow(Element[] devAndTableElements, String[][] cellContentsAndStyle) {
      Element nextRow = Xml.appendNewElementWithText(devAndTableElements[2], "tr", null);
      for (String[] cellContentsAndStyleArray : cellContentsAndStyle) {
         if (cellContentsAndStyleArray.length > 1) {
            appendNewElementWithTextAndOneAttribute(nextRow, "td", cellContentsAndStyleArray[0],
                  cellContentsAndStyleArray[1], cellContentsAndStyleArray[2]);
         } else {
            appendNewElementsWithText(nextRow, "td", new String[] {cellContentsAndStyleArray[0]});
         }

      }
      //      devAndTableElements[0].appendChild(devAndTableElements[1]);
      return devAndTableElements;
   }

}
