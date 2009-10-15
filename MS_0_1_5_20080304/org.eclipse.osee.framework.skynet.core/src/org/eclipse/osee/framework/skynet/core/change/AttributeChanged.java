/*
 * Created on Feb 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import java.io.InputStream;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AttributeChanged extends Change {
   private String sourceValue;
   private InputStream sourceContent;
   private int attrId;
   private int attrTypeId;
   private DynamicAttributeDescriptor dynamicAttributeDescriptor;

   /**
    * @param sourceGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    * @param sourceValue
    * @param sourceContent
    * @param attrId
    * @param attrTypeId
    */
   public AttributeChanged(int artTypeId, String artName, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, TransactionType transactionType, ChangeType changeType, String sourceValue, InputStream sourceContent, int attrId, int attrTypeId) {
      super(artTypeId, artName, sourceGamma, artId, toTransactionId, fromTransactionId, transactionType, changeType);
      this.sourceValue = sourceValue;
      this.sourceContent = sourceContent;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
   }

   /**
    * @return the sourceValue
    */
   public String getSourceValue() {
      return sourceValue;
   }

   /**
    * @return the sourceContent
    */
   public InputStream getSourceContent() {
      return sourceContent;
   }

   /**
    * @return the attrId
    */
   public int getAttrId() {
      return attrId;
   }

   /**
    * @return the attrTypeId
    */
   public int getAttrTypeId() {
      return attrTypeId;
   }

   /**
    * @return the dynamicAttributeDescriptor
    */
   public DynamicAttributeDescriptor getDynamicAttributeDescriptor() throws SQLException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }

   public Image getImage() {
      return ChangeIcons.getImage(getChangeType(),
            TransactionType.convertTransactionTypeToModificationType(getTransactionType()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getSourceDisplayData()
    */
   @Override
   public String getSourceDisplayData() {
      return getSourceValue() != null ? getSourceValue() : "Stream data";
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }
}
