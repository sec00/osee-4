package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;

class OTETestCaseHandler implements AnnotationHandler<OTETestCase> {

   private TestScript testScript;
   private List<TestCaseAnnotated> testCases;
   private ITestEnvironmentAccessor accessor;
   
   public OTETestCaseHandler(TestScript testScript, List<TestCaseAnnotated> testCases, ITestEnvironmentAccessor accessor) {
      this.testScript = testScript;
      this.testCases = testCases;
      this.accessor = accessor;
   }

   @Override
   public void process(OTETestCase annotation, Object object, Field field) throws Exception {
      
   }

   @Override
   public void process(OTETestCase annotation, Object object) {
      
   }

   @Override
   public void process(OTETestCase annotation, Object object, Method method) {
      testCases.add(new TestCaseAnnotated(testScript, method, accessor, annotation.traceability(), annotation.order()));
   }
   
   public static class TestCaseAnnotated extends TestCase {

      private int order;
      private Method method;
      

      public TestCaseAnnotated(TestScript testScript, Method method, ITestEnvironmentAccessor accessor, String[] traceability, int order) {
         super(testScript, true, false);
         this.method = method;
         setEnv(accessor);
         if(traceability != null){
            for(String trace : traceability){
               addTraceability(trace);
            }
         }
         this.order = order;
      }

      @Override
      public String getName() {
         return method.getName();
      }

      @Override
      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
         try {
            method.setAccessible(true);
            method.invoke(getTestScript());
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            e.printStackTrace();
         } finally {
            method.setAccessible(false);
         }
      }
      
      public int getOrder(){
         return order;
      }
      
   }

}
