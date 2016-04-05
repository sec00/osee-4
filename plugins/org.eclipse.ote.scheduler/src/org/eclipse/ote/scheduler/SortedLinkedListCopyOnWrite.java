package org.eclipse.ote.scheduler;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class SortedLinkedListCopyOnWrite<T extends Comparable> implements List<T>{

   private List<T> added = new Vector<>();
   private List<T> deleted = new Vector<>();
   
   private ReentrantLock poolLock = new ReentrantLock();
   private ArrayDeque<Node<T>> pool;
   
   
   private ReentrantLock itLock = new ReentrantLock();
   private ArrayDeque<MyIterator> itpool;
   
   private Node<T> head;
   private Node<T> tail;
   
   public SortedLinkedListCopyOnWrite() {
      pool = new ArrayDeque<>();
      for(int i = 0; i < 8; i++){
         pool.push(new Node<T>());
      }
      itpool = new ArrayDeque<>();
      for(int i = 0; i < 8; i++){
         itpool.push(new MyIterator(this));
      }
   }
   
   
   
   @Override
   public int size() {
      Node<T> node = head;
      int size = 0;
      while(node != null){
         size++;
         node = node.nextNode;
      }
      return size;
   }

   @Override
   public boolean isEmpty() {
      return head == null;
   }

   @Override
   public boolean contains(Object o) {
      Node<T> node = head;
      while(node != null){
         if (o.equals(node)){
            return true;
         } else {
            node = node.nextNode;
         }
      }
      return false;
   }

   @Override
   public synchronized Iterator<T> iterator() {
      compact();
      
      
      
      itLock.lock();
      MyIterator it;
      try{
         if(!itpool.isEmpty()){
            it = itpool.pop();
         } else {
            it = new MyIterator(this);
         }
      } finally {
         itLock.unlock();
      }
      it.currentNode = null;
      it.currentHead = head;
      return it;
   }
   
   public void doneWithIterator(Iterator<T> it){
      itLock.lock();
      try{
         if(it instanceof MyIterator){
            itpool.push((MyIterator)it);
         }
      } finally {
         itLock.unlock();
      }
   }

   @Override
   public Object[] toArray() {
      Object[] objs = new Object[size()];
      Node<T> node = head;
      int i = 0;
      while(node != null && i < objs.length){
         objs[i] = node.object;
      }
      return objs;
   }

   @SuppressWarnings({ "unchecked", "hiding" })
   @Override
   public <T> T[] toArray(T[] a) {
      int size = size();
      if(a.length != size){
         a = (T[])Array.newInstance(a.getClass(), size);
      }
      Node node = head;
      int i = 0;
      while(node != null && i < a.length){
         a[i] = (T)node.object;
      }
      return a;
   }

   public boolean add(T e) {
      return added.add(e);
   }
   
   public boolean addLocal(T e) {
      Node node = head;
      boolean added = false;
      Node<T> newNode;
      poolLock.lock();
      try{
         if(!pool.isEmpty()){
            newNode = pool.pop();
            newNode.deleted = false;
            newNode.nextNode = null;
            newNode.previousNode = null;
            newNode.object = null;
         } else {
            newNode = new Node<T>();
         }
      } finally {
         poolLock.unlock();
      }
      newNode.object = e;
      if(head == null){
         head = newNode;
         tail = newNode;
      } else {
         while(node != null){
            if(e.compareTo(node.object) < 0){
               Node before = node.previousNode;
               Node after = node;
               newNode.previousNode = before;
               newNode.nextNode = node;
               if(before != null){
                  before.nextNode = newNode;
               } else {
                  head = newNode;                     
               }
               after.previousNode = newNode;
               added = true;
               break;
            }
            node = node.nextNode;
         }
         if(!added){
            newNode.previousNode = tail;
            tail.nextNode = newNode;
            tail = newNode;               
         }
      }
      return true;
   }
   
   @Override
   public boolean remove(Object o) {
      
      Node node = head;
      while(node != null){
         if(o.equals(node.object)){
            node.deleted = true;
//            if(node == head){
//                if(node.nextNode != null){
//                   node.nextNode.previousNode = null;
//                   head = node.nextNode;
//                } else {
//                   head = null;
//                   tail = null;
//                }
//            } else if(node == tail){
//               node.previousNode.nextNode = null;
//               tail = node.previousNode;
//            } else {
//               node.previousNode.nextNode = node.nextNode;
//               node.nextNode.previousNode = node.previousNode;
//            }
//            node.nextNode = null;
//            node.previousNode = null;
//            node.object = null;   
//            poolLock.lock();
//            try{
//               pool.push(node);
//            } finally {
//               poolLock.unlock();
//            }
            return true;
         }
         node = node.nextNode;
      }
      return false;
   }
   
   public void compact(){
    Node node = head;
    while(node != null){
       if(node.deleted){
          if(node == head){
              if(node.nextNode != null){
                 node.nextNode.previousNode = null;
                 head = node.nextNode;
              } else {
                 head = null;
                 tail = null;
              }
          } else if(node == tail){
             node.previousNode.nextNode = null;
             tail = node.previousNode;
          } else {
             node.previousNode.nextNode = node.nextNode;
             node.nextNode.previousNode = node.previousNode;
          }
//          node.nextNode = null;
//          node.previousNode = null;
//          node.object = null;   
          node.object = null;
          poolLock.lock();
          try{
             pool.push(node);
          } finally {
             poolLock.unlock();
          }
          node = node.nextNode;
       } else {
          node = node.nextNode;
       }
    }
   }

//   @Override
//   public boolean remove(Object o) {
//      Node node = head;
//      while(node != null){
//         if(o.equals(node.object)){
//            if(node == head){
//                if(node.nextNode != null){
//                   node.nextNode.previousNode = null;
//                   head = node.nextNode;
//                } else {
//                   head = null;
//                   tail = null;
//                }
//            } else if(node == tail){
//               node.previousNode.nextNode = null;
//               tail = node.previousNode;
//            } else {
//               node.previousNode.nextNode = node.nextNode;
//               node.nextNode.previousNode = node.previousNode;
//            }
//            node.nextNode = null;
//            node.previousNode = null;
//            node.object = null;   
//            poolLock.lock();
//            try{
//               pool.push(node);
//            } finally {
//               poolLock.unlock();
//            }
//            return true;
//         }
//         node = node.nextNode;
//      }
//      return false;
//   }

   @Override
   public boolean containsAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void clear() {
      // TODO Auto-generated method stub
      
   }

   @Override
   public T get(int index) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public T set(int index, T element) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void add(int index, T element) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public T remove(int index) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public int indexOf(Object o) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public int lastIndexOf(Object o) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public ListIterator<T> listIterator() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListIterator<T> listIterator(int index) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      // TODO Auto-generated method stub
      return null;
   }
   
   private static class Node<T> {
      
      T object;
      Node<T> previousNode;
      Node<T> nextNode;
      
      T safeobject;
      Node<T> safepreviousNode;
      Node<T> safenextNode;
      
      volatile boolean deleted = false; 
      
      public Node(){
         previousNode = null;
         nextNode = null;
      }
      
   }
   
   private static class MyIterator<T extends Comparable<T>> implements Iterator<T> {

      Node<T> currentNode;
      Node<T> currentHead;
      private SortedLinkedListCopyOnWrite<T> parent;
      
      public MyIterator(SortedLinkedListCopyOnWrite<T> parent) {
         this.parent = parent;
      }
      
      public void init(Node<T> head){
         currentHead = head;
      }
      
      @Override
      public boolean hasNext() {
         Node nextCheck = advanceNode(currentNode);
         return (nextCheck != null);
      }

      @Override
      public T next() {
         currentNode = advanceNode(currentNode);
         if(currentNode != null){
            return currentNode.object;
         } else {
            return null;
         }
      }
      
      public Node advanceNode(Node node) {
         if(node == null){
            node = currentHead;
         } else {
            node = node.nextNode;
         }
         if(node != null && node.deleted){
            advanceNode(node);
         }
         return node;
      }
      
//      public T peekNext() {
//         if(currentNode == null){
//            return currentHead.object;
//         } else {
//            currentNode = currentNode.nextNode;
//         }
//         return currentNode.object;
//      }
      
      @Override
      public void remove(){
//         Node toRemove = currentNode;
//         currentNode = currentNode.previousNode;
//         if(currentNode == null){
            currentHead = null;
//         }
         parent.remove(currentNode.object);
      }
      
   }
   
   public static void main(String[] args){
      SortedLinkedListCopyOnWrite<Integer> list = new SortedLinkedListCopyOnWrite<>();
      
      if(list.iterator().hasNext()){
         System.out.println("no");
      }
      
      list.add(1);
      list.print();
      list.add(30);
      list.print();
      list.add(35);
      list.print();

      list.add(3);
      list.print();

      list.add(10);
      list.print();

      list.add(23);
      list.print();

      list.add(350);
      list.print();

      list.add(200);
      list.print();

      list.add(31);
      list.print();

      list.add(30);
      list.print();

   }

   private void print() {
      for(T t:this){
         System.out.printf("%s, ", t.toString()); 
      }
      
      System.out.println();
      Node node = tail;
      while(node != null){
         System.out.printf("%s, ", node.object.toString()); 
         node = node.previousNode;
      }
      System.out.println();
      
   }
   
  }
