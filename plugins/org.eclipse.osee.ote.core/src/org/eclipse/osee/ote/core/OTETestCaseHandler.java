package org.eclipse.osee.ote.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;

class OTETestCaseHandler implements AnnotationHandler<OTETestCase> {

   private TestScript testScript;
   private List<TestCase> testCases;

   public OTETestCaseHandler(TestScript testScript, List<TestCase> testCases) {
      this.testScript = testScript;
      this.testCases = testCases;
   }

   @Override
   public void process(OTETestCase annotation, Object object, Field field) throws Exception {
      
   }

   @Override
   public void process(Annotation annotation, Object object) {
      
   }

   @Override
   public void process(Annotation annotation, Object object, Method method) {
      testCases.add(new TestCaseAnnotated(testScript, method));
   }
   
   private static class TestCaseAnnotated extends TestCase {

      private Method method;

      public TestCaseAnnotated(TestScript testScript, Method method) {
         super(testScript, true, false);
         this.method = method;
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
      
   }

}
