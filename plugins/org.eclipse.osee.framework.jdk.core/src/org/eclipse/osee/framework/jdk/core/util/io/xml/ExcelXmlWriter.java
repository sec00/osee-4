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
package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @see ExcelXmlWriterTest
 * @author Ryan D. Brooks
 * @author Karol M. Wilk
 */
public final class ExcelXmlWriter extends AbstractSheetWriter {
   public static enum STYLE {
      BOLD,
      ITALICS,
      ERROR,
      CENTERED,
      WRAPPED
   };

   public static final String WrappedStyle = "OseeWraped";

   public static final Pattern stylePattern = Pattern.compile("<Style.*</Style>\\s*", Pattern.DOTALL);

   public static final String defaultEmptyStringXmlRep = "&#248;";
   public static final String defaultEmptyString = "\u00F8";
   public static final String blobMessage = "data stored in EmbeddedClob since longer than 32767 chars";
   public static final int DEFAULT_FONT_SIZE = 11;

   public static final String XML_HEADER = //
      "<?xml version=\"1.0\"?>\n" + //
         "<?mso-application progid=\"Excel.Sheet\"?>\n" + //
         "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
         " xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" + //
         " xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n" + //
         " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
         " xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n";

   public static final String DEFAULT_OSEE_STYLES = //
      "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + //
         " <Alignment ss:Vertical=\"Bottom\"/>\n" + //
         " <Borders/>\n" + //
         " <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"%d\" ss:Color=\"#000000\"/>\n" + //
         " <Interior/>\n" + //
         " <NumberFormat/>\n" + //
         " <Protection/>\n" + //
         "</Style>\n" + //
         "<Style ss:ID=\"OseeDate\"><NumberFormat ss:Format=\"Short Date\"/></Style>\n" + //
         "<Style ss:ID=\"OseeBoldStyle\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeItalicStyle\"><Font x:Family=\"Swiss\" ss:Italic=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeErrorStyle\"><Font x:Family=\"Swiss\" ss:Color=\"#FF0000\" ss:Bold=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeCentered\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/></Style>\n" + //
         "<Style ss:ID=\"OseeWraped\"><Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/></Style>";

   private Appendable out;
   private boolean inSheet;
   private final String emptyStringRepresentation;
   private final String style;
   private final int defaultFontSize;
   private int previouslyWrittenCellIndex;

   private boolean applyStyle = false;
   private final Map<Integer, String> mStyleMap;
   private final Map<Integer, Integer> mColSpanMap = new HashMap<>();

   private String[] rowBuffer;
   private int numColumns = -1;

   private double rowHeight;

   private int richTextCell = -1;

   private int numSheetsWritten = 0;
   private int activeSheetNum = -1;

   public ExcelXmlWriter() {
      this(null, defaultEmptyStringXmlRep);
   }

   public ExcelXmlWriter(File file) {
      this(toAppendable(file), null, defaultEmptyStringXmlRep);
   }

   private static Appendable toAppendable(File file) {
      try {
         return new BufferedWriter(new FileWriter(file));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public ExcelXmlWriter(Appendable out) {
      this(out, null, defaultEmptyStringXmlRep);
   }

   /**
    * Calls original constructor with provided style.
    *
    * @param writer output
    * @param style Excel Style XML of form <Styles><Style/><Style/></Styles>
    */
   public ExcelXmlWriter(Appendable out, String style) {
      this(out, style, defaultEmptyStringXmlRep);
   }

   public ExcelXmlWriter(Appendable out, String style, String emptyStringRepresentation) {
      this(out, style, emptyStringRepresentation, DEFAULT_FONT_SIZE);
   }

   public ExcelXmlWriter(Appendable out, String style, String emptyStringRepresentation, int defaultFontSize) {
      mStyleMap = new HashMap<>();
      this.out = out;
      this.emptyStringRepresentation = emptyStringRepresentation;
      this.style = style;
      this.defaultFontSize = defaultFontSize;
      initWorkbook(out);
   }

   private void initWorkbook(Appendable out) {
      if (out != null) {
         this.out = out;
         try {
            out.append(XML_HEADER);

            out.append("<Styles>\n");

            out.append(String.format(DEFAULT_OSEE_STYLES, defaultFontSize));
            if (Strings.isValid(style)) {
               if (stylePattern.matcher(style).matches()) {
                  out.append(style);
               } else {
                  throw new IllegalArgumentException("incomingStyle must match the pattern " + stylePattern);
               }
            }
            out.append("</Styles>\n");
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) throws IOException {
      startSheet(worksheetName, null, ExcelColumn.newEmptyColumns(columnCount));
   }

   @Override
   public void startSheet(String worksheetName, int columnCount, Appendable out) throws IOException {
      startSheet(worksheetName, out, ExcelColumn.newEmptyColumns(columnCount));
   }

   public void startSheet(String worksheetName, ExcelColumn... columns) throws IOException {
      startSheet(worksheetName, null, columns);
   }

   public void startSheet(String worksheetName, Appendable newOut, ExcelColumn... columns) throws IOException {
      initWorkbook(newOut);
      if (inSheet) {
         throw new OseeCoreException("Cannot start a new sheet until the current sheet is closed");
      }
      if (worksheetName.length() > 31) {
         worksheetName = worksheetName.substring(0, 31);
      }
      numColumns = columns.length;
      rowBuffer = new String[numColumns];

      out.append(" <Worksheet ss:Name=\"");
      out.append(worksheetName);
      out.append("\">\n");

      out.append("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"");
      out.append(String.valueOf(numColumns));
      out.append("\">\n");

      for (ExcelColumn column : columns) {
         column.writeColumnDefinition(out);
      }

      if (columns[0].getName() != null) {
         for (ExcelColumn column : columns) {
            writeCell(column.getName());
         }
         endRow();
      }

      inSheet = true;
   }

   @Override
   public void endSheet() throws IOException {
      out.append("  </Table>\n");
      out.append(" </Worksheet>\n");
      inSheet = false;
      numColumns = -1;
      ++numSheetsWritten;
   }

   @Override
   public void endWorkbook() throws IOException {
      try {
         if (inSheet) {
            endSheet();
         }
         if (activeSheetNum >= 0) {
            out.append(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
            out.append("  <ActiveSheet>" + activeSheetNum + "</ActiveSheet>\n");
            out.append(" </ExcelWorkbook>\n");
         }
         out.append("</Workbook>\n");
      } finally {
         if (out instanceof Closeable) {
            Lib.close((Closeable) out);
         }
      }
   }

   @Override
   protected void startRow() throws IOException {
      out.append("   <Row");
      if (rowHeight != 0.0) {
         out.append(String.format(" ss:AutoFitHeight=\"0\" ss:Height=\"%f\"", rowHeight));
      }
      out.append(">\n");

      Arrays.fill(rowBuffer, null);

      previouslyWrittenCellIndex = -1;
   }

   @Override
   public void writeEndRow() throws IOException {
      for (int i = 0; i < numColumns; i++) {
         if (rowBuffer[i] != null && rowBuffer[i].length() > 0) {
            out.append(rowBuffer[i]);
         }
      }
      out.append("   </Row>\n");
   }

   @Override
   public void writeCellText(Object cellData, int cellIndex) throws IOException {
      if (cellIndex >= numColumns) {
         throw new OseeCoreException("ExcelWriter out of bounds: %d, index %d", numColumns, cellIndex);
      } else if (cellData == null) {
         rowBuffer[cellIndex] = null;
      } else {
         StringBuilder sb = new StringBuilder();

         sb.append("    <Cell");

         //Cell styles
         if (cellData instanceof Date) {
            sb.append(" ss:StyleID=\"OseeDate\"");
         } else if (applyStyle) {
            applyStyleToCell(sb, cellIndex);
         }

         if (previouslyWrittenCellIndex + 1 != cellIndex) { // use explicit index if at least one cell was skipped
            sb.append(" ss:Index=\"" + (cellIndex + 1) + "\"");
         }
         previouslyWrittenCellIndex = cellIndex;

         if (cellData instanceof String) {
            String cellDataStr = (String) cellData;
            if (!cellDataStr.equals("") && cellDataStr.charAt(0) == '=') {
               String value = cellDataStr.replaceAll("\"", "&quot;");
               sb.append(" ss:Formula=\"" + value + "\">");
            } else {
               boolean isRichText = richTextCell == cellIndex;
               if (isRichText) {
                  sb.append("><ss:Data ss:Type=\"String\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
               } else {
                  sb.append("><Data ss:Type=\"String\">");
               }
               if (cellDataStr.equals("")) {
                  sb.append(emptyStringRepresentation);
               } else {
                  if (cellDataStr.length() > 32767) {
                     sb.append(blobMessage);
                  } else if (isRichText) {
                     Xml.writeData(sb, cellDataStr);
                  } else {
                     Xml.writeWhileHandlingCdata(sb, cellDataStr);
                  }
               }
               if (isRichText) {
                  richTextCell = -1;
                  sb.append("</ss:Data>");
               } else {
                  sb.append("</Data>");
               }
               if (cellDataStr.length() > 32767) {
                  sb.append("<EmbeddedClob>");
                  Xml.writeWhileHandlingCdata(sb, cellDataStr);
                  sb.append("</EmbeddedClob>");
               }
            }
         } else if (cellData instanceof Number) {
            Number cellDataNum = (Number) cellData;
            sb.append("><Data ss:Type=\"Number\">");
            Xml.writeWhileHandlingCdata(sb, cellDataNum.toString());
            sb.append("</Data>");
         } else if (cellData instanceof Date) {
            Date cellDataDate = (Date) cellData;
            sb.append("><Data ss:Type=\"DateTime\">");
            String dateString = DateUtil.get(cellDataDate, "yyyy-MM-dd") + "T00:00:00.000";
            Xml.writeWhileHandlingCdata(sb, dateString);
            sb.append("</Data>");
         } else {
            sb.append("><Data ss:Type=\"String\">");
            Xml.writeWhileHandlingCdata(sb, cellData.toString());
            sb.append("</Data>");
         }
         sb.append("</Cell>\n");
         rowBuffer[cellIndex] = sb.toString();
      }
   }

   /**
    * Needs to be called before write* operations are called.
    */
   public void setCellStyle(ExcelXmlWriter.STYLE style, int cellIndex) {
      applyStyle = true;
      switch (style) {
         case BOLD:
            mStyleMap.put(cellIndex, "OseeBoldStyle");
            break;
         case ITALICS:
            mStyleMap.put(cellIndex, "OseeItalicStyle");
            break;
         case ERROR:
            mStyleMap.put(cellIndex, "OseeErrorStyle");
            break;
         case CENTERED:
            mStyleMap.put(cellIndex, "OseeCentered");
            break;
         case WRAPPED:
            mStyleMap.put(cellIndex, WrappedStyle);
            break;
      }
   }

   public void setRichTextCell(int cellIndex) {
      richTextCell = cellIndex;
   }

   public void setCellStyle(String style, int cellIndex) {
      applyStyle = true;
      mStyleMap.put(cellIndex, style);
   }

   public void setCellColSpanWidth(int cellIndex, int colWidth) {
      applyStyle = true;
      mColSpanMap.put(cellIndex, colWidth);
   }

   public void setRowHeight(double rowHeight) {
      this.rowHeight = rowHeight;
   }

   private void applyStyleToCell(StringBuilder sb, int cellIndex) {
      String applyThisStyle = mStyleMap.remove(cellIndex);
      if (applyThisStyle != null) {
         sb.append(" ss:StyleID=\"" + applyThisStyle + "\"");
      }

      Integer colSpanWidth = mColSpanMap.remove(cellIndex);
      if (colSpanWidth != null) {
         sb.append(" ss:MergeAcross=\"" + colSpanWidth + "\"");
      }
      applyStyle = mStyleMap.size() > 0 || mColSpanMap.size() > 0;
   }

   /*
    * @param sheetNum - the sheet number uses 0 based counting, i.e. 0 is the first sheet.
    */
   @Override
   public void setActiveSheet(int sheetNum) {
      if (sheetNum >= 0 && sheetNum < numSheetsWritten) {
         activeSheetNum = sheetNum;
      } else if (sheetNum < 0) {
         throw new OseeArgumentException("Cannot set active sheet less than zero");
      } else {
         throw new OseeArgumentException("Cannot set active sheet higher than the number of sheets written");
      }
   }

}