/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Os Tx Comment Criteria</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl.OsTxCommentCriteriaImpl#getClause <em>Clause</em>}
 * </li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OsTxCommentCriteriaImpl extends OsTxCriteriaImpl implements OsTxCommentCriteria {
   /**
    * The cached value of the '{@link #getClause() <em>Clause</em>}' containment reference. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @see #getClause()
    * @generated
    * @ordered
    */
   protected OsTxCommentClause clause;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OsTxCommentCriteriaImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CRITERIA;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OsTxCommentClause getClause() {
      return clause;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public NotificationChain basicSetClause(OsTxCommentClause newClause, NotificationChain msgs) {
      OsTxCommentClause oldClause = clause;
      clause = newClause;
      if (eNotificationRequired()) {
         ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE, oldClause, newClause);
         if (msgs == null) {
            msgs = notification;
         } else {
            msgs.add(notification);
         }
      }
      return msgs;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void setClause(OsTxCommentClause newClause) {
      if (newClause != clause) {
         NotificationChain msgs = null;
         if (clause != null) {
            msgs = ((InternalEObject) clause).eInverseRemove(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE, null, msgs);
         }
         if (newClause != null) {
            msgs = ((InternalEObject) newClause).eInverseAdd(this,
               EOPPOSITE_FEATURE_BASE - OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE, null, msgs);
         }
         msgs = basicSetClause(newClause, msgs);
         if (msgs != null) {
            msgs.dispatch();
         }
      } else if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET,
            OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE, newClause, newClause));
      }
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE:
            return basicSetClause(null, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE:
            return getClause();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE:
            setClause((OsTxCommentClause) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eUnset(int featureID) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE:
            setClause((OsTxCommentClause) null);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID) {
      switch (featureID) {
         case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA__CLAUSE:
            return clause != null;
      }
      return super.eIsSet(featureID);
   }

} //OsTxCommentCriteriaImpl
