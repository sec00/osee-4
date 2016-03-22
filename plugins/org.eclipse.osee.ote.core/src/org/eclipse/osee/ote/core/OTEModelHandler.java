package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.osee.ote.core.model.IModelManager;
import org.eclipse.osee.ote.core.model.ModelKey;

public class OTEModelHandler extends AbstractAnnotationHandler<OTEModel> {

   private IModelManager modelManager;
   
   public OTEModelHandler(IModelManager modelManager) {
      this.modelManager = modelManager;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public void process(OTEModel annotation, Object object, Field field) throws Exception {
      injectToFields(object, field, modelManager.getModel(new ModelKey(field.getType())));
   }

   @Override
   public void process(Annotation annotation, Object object) {
      
   }

   @Override
   public void process(Annotation annotation, Object object, Method method) {
      
   }

}
