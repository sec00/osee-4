/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.client.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.mylyn.wikitext.help.ui%2Fhelp%2Fdevguide%2FWikiText+
 * Developer+Guide.html
 *
 * @author Ryan D. Brooks
 */
public final class WikiImporter {
   private final BufferedReader wikiReader;
   private static final File root = new File("C:\\UserData\\wiki\\dual");

   public WikiImporter(BufferedReader wikiReader) {
      this.wikiReader = wikiReader;
   }

   public WikiImporter() {
      this(null);
   }

   public static void main(String[] args) throws IOException {
      //      String filename = "Wikipedia-20150818221758.xml";
      String filename = "sw_eng.xml";
      //      String filename = "enwiki-latest-pages-articles.xml";
      BufferedReader wikiReader = new BufferedReader(new FileReader(new File(root, filename)));
      WikiImporter app = new WikiImporter(wikiReader);
      app.paresWholeFile();
      wikiReader.close();
   }

   public void paresWholeFile() throws IOException {

      StringWriter writer = new StringWriter();
      HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
      // builder.setPrependImagePrefix(null);

      MarkupParser parser = new MarkupParser(new MediaWikiLanguage(), builder);
      parser.parse(wikiReader);

      String htmlContent = writer.toString();

      Lib.writeStringToFile(htmlContent, new File(root, "test.html"));
      System.out.println(htmlContent);
   }

   public void simpleParse() throws IOException {
      MarkupParser parser = new MarkupParser(new MediaWikiLanguage());

      String line;
      while ((line = wikiReader.readLine()) != null) {
         System.out.println(line);
         System.out.println("   " + parser.parseToHtml(line));
         //         String htmlContent = writer.toString();
         //         System.out.println();
         //         System.out.println(htmlContent);
         //         System.out.println();
      }
   }

   public static void writeOutSingleFile(String title, String pageText) throws IOException {
      File articleFile = new File(root, title + ".html");
      File folder = articleFile.getParentFile();
      if (!folder.exists()) {
         folder.mkdirs();
      }
      try (Writer writer = new BufferedWriter(new FileWriter(articleFile))) {
         HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
         MarkupParser parser = new MarkupParser(new MediaWikiLanguage(), builder);
         parser.parse(pageText);
      }
   }
}