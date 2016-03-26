package org.eclipse.ote.scheduler;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedLinkedList<T extends Comparable> implements List<T>{

   private ArrayDeque<Node<T>> pool;
   
   private MyIterator it;
   
   private Node<T> head;
   private Node<T> tail;
   
   public SortedLinkedList() {
      pool = new ArrayDeque<>();
      for(int i = 0; i < 8; i++){
         pool.push(new Node<T>());
      }
      it = new MyIterator(this);
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
   public Iterator<T> iterator() {
      it.currentNode = null;
      return it;
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

   @Override
   public boolean add(T e) {
      Node node = head;
      boolean added = false;
      Node<T> newNode;
      if(!pool.isEmpty()){
         newNode = pool.pop();
      } else {
         newNode = new Node<T>();
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
            node.nextNode = null;
            node.previousNode = null;
            node.object = null;                  
            pool.push(node);
            return true;
         }
         node = node.nextNode;
      }
      return false;
   }

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
      
      public Node(){
         previousNode = null;
         nextNode = null;
      }
      
   }
   
   private static class MyIterator<T extends Comparable<T>> implements Iterator<T> {

      Node<T> currentNode;
      private SortedLinkedList<T> parent;
      
      public MyIterator(SortedLinkedList<T> parent) {
         this.parent = parent;
      }
      
      @Override
      public boolean hasNext() {
         if(currentNode == null && parent.head != null){
            return true;
         } else if (currentNode != null && currentNode.nextNode != null){
            return true;
         } else {
            return false;
         }
      }

      @Override
      public T next() {
         if(currentNode == null){
            currentNode = parent.head;
         } else {
            currentNode = currentNode.nextNode;
         }
         return currentNode.object;
      }
      
      @Override
      public void remove(){
         Node toRemove = currentNode;
         currentNode = currentNode.previousNode;
         parent.remove(toRemove.object);
      }
      
   }
   
   public static void main(String[] args){
      SortedLinkedList<Integer> list = new SortedLinkedList<>();
      
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
