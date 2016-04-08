package org.eclipse.ote.scheduler;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SortedLinkedListCopyOnWriteTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void test() {
      SortedLinkedListCopyOnWrite<Integer> list = new SortedLinkedListCopyOnWrite<>();
      list.add(24);
      list.add(2);
      list.add(3);
      list.add(56);
      list.add(563);
      list.add(5634);
      list.add(56345);
      Assert.assertEquals(7, list.size());
      Assert.assertTrue(list.contains(56));
      Assert.assertFalse(list.contains(156));
      Iterator<Integer> it = list.iterator();
      
      /**
       * test that sorting is happening
       */
      Assert.assertEquals(new Integer(2), (Integer)it.next());
      Assert.assertEquals(new Integer(3), (Integer)it.next());
      Assert.assertEquals(new Integer(24), (Integer)it.next());
      Assert.assertEquals(new Integer(56), (Integer)it.next());
      Assert.assertEquals(new Integer(563), (Integer)it.next());
      Assert.assertEquals(new Integer(5634), (Integer)it.next());
      Assert.assertEquals(new Integer(56345), (Integer)it.next());
      
      /**
       * test removes
       */
      //remove head
      Assert.assertTrue(list.remove(new Integer(2))); 
      it = list.iterator();
      Assert.assertEquals(6, list.size());
      Assert.assertEquals(new Integer(3), (Integer)it.next());
      Assert.assertEquals(new Integer(24), (Integer)it.next());
      Assert.assertEquals(new Integer(56), (Integer)it.next());
      Assert.assertEquals(new Integer(563), (Integer)it.next());
      Assert.assertEquals(new Integer(5634), (Integer)it.next());
      Assert.assertEquals(new Integer(56345), (Integer)it.next());
      
      //remove something that doesn't exist
      Assert.assertFalse(list.remove(new Integer(5))); 
      it = list.iterator();
      Assert.assertEquals(6, list.size());
      Assert.assertEquals(new Integer(3), (Integer)it.next());
      Assert.assertEquals(new Integer(24), (Integer)it.next());
      Assert.assertEquals(new Integer(56), (Integer)it.next());
      Assert.assertEquals(new Integer(563), (Integer)it.next());
      Assert.assertEquals(new Integer(5634), (Integer)it.next());
      Assert.assertEquals(new Integer(56345), (Integer)it.next());
      
      //remove tail
      Assert.assertTrue(list.remove(new Integer(56345))); 
      it = list.iterator();
      Assert.assertEquals(5, list.size());
      Assert.assertEquals(new Integer(3), (Integer)it.next());
      Assert.assertEquals(new Integer(24), (Integer)it.next());
      Assert.assertEquals(new Integer(56), (Integer)it.next());
      Assert.assertEquals(new Integer(563), (Integer)it.next());
      Assert.assertEquals(new Integer(5634), (Integer)it.next());
      
      //remove middle
      Assert.assertTrue(list.remove(new Integer(56))); 
      it = list.iterator();
      Assert.assertEquals(4, list.size());
      Assert.assertEquals(new Integer(3), (Integer)it.next());
      Assert.assertEquals(new Integer(24), (Integer)it.next());
      Assert.assertEquals(new Integer(563), (Integer)it.next());
      Assert.assertEquals(new Integer(5634), (Integer)it.next());
      
      
      Assert.assertFalse(list.isEmpty());
      list.clear();
      Assert.assertTrue(list.isEmpty());
      
      
      /**
       * test removing the last item
       */
      Assert.assertTrue(list.add(24));
      Assert.assertTrue(list.remove(new Integer(24)));
      it = list.iterator();
      Assert.assertFalse(it.hasNext());
      
   }

}
