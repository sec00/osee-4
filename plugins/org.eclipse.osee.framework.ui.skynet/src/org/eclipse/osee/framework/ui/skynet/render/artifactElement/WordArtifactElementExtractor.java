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

package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkParser;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jeff C. Phillips
 */
public class WordArtifactElementExtractor  implements IElementExtractor{
	   private static final OseeLinkBuilder LINK_BUILDER = new OseeLinkBuilder();
	   private Element oleDataElement;
	   private final Document document;
	   int numberOfStartTags;
	   int numberOfEndTags;

	   
	   private enum ParseState {LOOKING_FOR_START, LOOKING_FOR_END};
	   private enum Side {left, right};
	   
	   public WordArtifactElementExtractor(Document document){
	      super();
	      this.document = document;
	      this.numberOfEndTags = 0;
	      this.numberOfStartTags = 0;
	   }

	   public Element getOleDataElement() {
	      return oleDataElement;
	   }

	   public Collection<Element> extractElements() throws DOMException, ParserConfigurationException, SAXException, IOException, OseeCoreException{
	      final Collection<Element> artifactElements = new LinkedList<Element>();
	      Element rootElement = document.getDocumentElement();
	      oleDataElement = null;
	      numberOfStartTags = 0;
	      numberOfEndTags = 0;

	      NodeList nodeList = rootElement.getElementsByTagName("*");
	      Element newArtifactElement = null;
	      ParseState parseState = ParseState.LOOKING_FOR_START;
	      
	      for (int i = 0; i < nodeList.getLength(); i++) {
	         Element element = (Element) nodeList.item(i);
	         if (isArtifactEditTag(element)) {
	            if(parseState == ParseState.LOOKING_FOR_START){
	               numberOfStartTags++;
	               parseState = ParseState.LOOKING_FOR_END;
	               newArtifactElement = document.createElement("WordAttribute.WORD_TEMPLATE_CONTENT");
	               populateNewArtifactElementFromHlink(newArtifactElement,getHlinkDescendant(element));
	               artifactElements.add(newArtifactElement);
	               
	               Node clonedElement = cloneWithoutArtifactEditTag(element, Side.right);
	               if (elementHasGrandChildren(clonedElement)) {
	                  newArtifactElement.appendChild(clonedElement);
	               }
	            }else if(parseState == ParseState.LOOKING_FOR_END){
	               numberOfEndTags++;
	               parseState = ParseState.LOOKING_FOR_START;
	               
	               Node clonedElement = cloneWithoutArtifactEditTag(element, Side.left);
	               if (elementHasGrandChildren(clonedElement)) {
	                  newArtifactElement.appendChild(clonedElement);
	               }
	            }
	         }else if(parseState == ParseState.LOOKING_FOR_END && properLevelChild(element)) {
	            newArtifactElement.appendChild(element.cloneNode(true));
	         }
	      }
	      
	      validateEditTags();
	      return artifactElements;
	   }

	   /**
	    * @param element
	    * @return
	    */
	   private boolean elementHasGrandChildren(Node element) {
	      return element.getChildNodes().getLength() > 0 && element.getChildNodes().item(0).getChildNodes().getLength() > 0;
	   }

	   private Element getHlinkDescendant(Element element) {
	      NodeList descendants = element.getElementsByTagName("*");
	      for (int i=0; i<descendants.getLength(); i++) {
	         Element descendant = (Element)descendants.item(i);
	         if (isEditLink(descendant)) {
	            return descendant;
	         }
	      }
	      
	      throw new IllegalStateException("We only called this because we found it before, should never get here");
	   }

	   /**
	    * @param element
	    * @param keepSide TODO
	    * @param childNodes
	    * @return
	    */
	   private Node cloneWithoutArtifactEditTag(Element element, Side keepSide) {
	      Collection<Node> removals = new LinkedList<Node>();
	      
	      Element clonedElement = (Element)element.cloneNode(true);
	      boolean beforeEditTag = true;
	      NodeList descendants = clonedElement.getElementsByTagName("*");
	      for (int i=0; i<descendants.getLength(); i++) {
	         Node descendant = descendants.item(i);
	         if (isEditLink(descendant)) {
	            descendant.getParentNode().removeChild(descendant);
	            removals.add(descendant);
	            beforeEditTag = false;
	         } else if (beforeEditTag && keepSide == Side.right || !beforeEditTag && keepSide == Side.left) {
	            removals.add(descendant);
	         }
	      }
	      
	      for(Node remove : removals) {
	         Node parentNode = remove.getParentNode();
	         if (parentNode != null) {
	            parentNode.removeChild(remove);
	         }
	      }
	      return clonedElement;
	   }

	   /**
	    * @param element
	    * @return
	    */
	   private boolean isEditLink(Node element) {
	      String HLINK_ELEMENT_NAME = WordUtil.elementNameFor("hlink");
	      
	      if (element.getNodeName().contains(HLINK_ELEMENT_NAME)) {
	         Node destinationAttribute = element.getAttributes().getNamedItem("w:dest");
	         if (destinationAttribute != null) { 
	            return LINK_BUILDER.isEditArtifactLink(destinationAttribute.getNodeValue());
	         }
	      }
	      
	      return false;
	   }
	   
	   private void validateEditTags() throws OseeCoreException{
	      if(numberOfStartTags == 0 || numberOfEndTags != numberOfStartTags){
	         throw new OseeCoreException("This document is missing start/end edit tags, therefore the document will not be saved. You can re-edit the artifact and the edit tags should reappear.");
	      }
	   }
	   
	   /**
	    * @param newArtifactElement 
	    * @param element
	    * @throws DOMException 
	    * @throws OseeCoreException 
	    */
	   private void populateNewArtifactElementFromHlink(Element newArtifactElement, Element element) throws OseeCoreException, DOMException {
	      OseeLinkParser linkParser = new OseeLinkParser();
	      linkParser.parse(element.getAttribute("w:dest"));
	      newArtifactElement.setAttribute("guid", linkParser.getGuid());
	   }

	   /**
	    * @param element
	    * @return
	    */
	   private boolean isArtifactEditTag(Element element) {
	      if (!properLevelChild(element)) {
	         return false;
	      }
	      
	      NodeList descendants = element.getElementsByTagName("*");
	      for (int i=0; i<descendants.getLength(); i++) {
	         Node descendant = descendants.item(i);
	         if (isEditLink(descendant)) {
	            return true;
	         }
	      }
	      return false ;
	   }

	   /**
	    * @param element
	    * @return
	    */
	   private boolean properLevelChild(Element element) {
	      String grandParentName = element.getParentNode().getParentNode().getNodeName();
	      String parentName = element.getParentNode().getNodeName();
	      String myName = element.getNodeName();
	      
	      boolean nonSubsectionBodyChild = parentName.equals("w:body") && !myName.equals("wx:sub-section");
	      boolean subsectionChild = grandParentName.equals("w:body") && parentName.equals("wx:sub-section");
	      
	      return nonSubsectionBodyChild || subsectionChild;
	   }
	}
