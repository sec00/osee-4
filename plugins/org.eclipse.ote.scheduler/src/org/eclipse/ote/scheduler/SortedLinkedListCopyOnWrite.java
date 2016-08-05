package org.eclipse.ote.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;

public class SortedLinkedListCopyOnWrite<T extends Comparable<T>> implements List<T>{

   private ObjectPool<MyInnerIterator<T>> itpool;
   private ObjectPool<Node<T>> nodePool;
   private ObjectPool<LinkedList<T>> pool;
   
   private ArrayList<LinkedList<T>> inUseItems;
   
   private AtomicReference<LinkedList<T>> data;
   private volatile int refCount;
   private boolean debug = false;

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public SortedLinkedListCopyOnWrite() {
      inUseItems = new ArrayList<>();
      data = new AtomicReference<>();
      nodePool = new ObjectPool( new Create<Node<T>>(){
         @Override
         public Node<T> create() {
            return new Node<>();
         }
      }
      , new Initialize<Node<T>>() {
         @Override
         public void initialize(Node<T> obj) {
            obj.deleted = false;
            obj.nextNode = null;
            obj.previousNode = null;
            obj.object = null;
         }
      });
      itpool = new ObjectPool( new Create<MyInnerIterator<T>>(){
         @Override
         public MyInnerIterator<T> create() {
            return new MyInnerIterator<>();
         }

      }
      , new Initialize<MyInnerIterator<T>>() {

         @Override
         public void initialize(MyInnerIterator<T> obj) {

         }
      });

      pool = new ObjectPool( new Create<LinkedList<T>>() {

         @Override
         public LinkedList<T> create() {
            LinkedList<T> list = new LinkedList(nodePool, itpool);
            list.setCurrent(true);
            list.head = null;
            list.tail = null;
            return list;
         }
      }, new Initialize<LinkedList<T>>() {

         @Override
         public void initialize(LinkedList<T> obj) {
            obj.head = null;
            obj.tail = null; 
            obj.setCurrent(true);
         }
      });
      LinkedList<T> list = pool.get();
      list.setCurrent(true);
      data.set(list);
      if(debug ){
         System.out.printf("nodepool, itpool, linkedlistpool\n%d, %d, %d\n", nodePool.hashCode(), itpool.hashCode(), pool.hashCode());
      }
   }
   
   public void dispose(){
      data.get().clear();
      itpool.flush();
      nodePool.flush();
      pool.flush();
      inUseItems.clear();
   }
   
   @Override
   public int size() {
      return data.get().size();
   }
   
   @Override
   public boolean isEmpty() {
      return data.get().isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return data.get().contains(o);
   }

   @Override
   public synchronized MyInnerIterator<T> iterator() {
      refCount++;
      return data.get().iterator();
   }
   
   public synchronized void doneWithIterator(MyInnerIterator<T> it){
      inUseItems.clear();
      pool.fillInUseItems(inUseItems);
      for(int i = 0; i < inUseItems.size(); i++){
         LinkedList<T> item = inUseItems.get(i);
         if(!item.getCurrent()){
            item.clear();
            pool.push(item);
         }
      }
      itpool.push(it);
   }

   @Override
   public Object[] toArray() {
      return null;
   }

   @SuppressWarnings("hiding")
   @Override
   public <T> T[] toArray(T[] a) {
      return null;
   }

   public synchronized boolean add(T e) {
      LinkedList<T> currentList = data.get();
      LinkedList<T> newList = copy(currentList, pool.get());
      boolean returnVal = newList.addLocal(e);
      data.set(newList);
      currentList.setCurrent(false);
      return returnVal;
   }
   
   private LinkedList<T> copy(LinkedList<T> currentList, LinkedList<T> list) {
      list.clear();
      Node<T> node = currentList.head;
      while(node != null){
        list.addLocal(node.object);
        node = node.nextNode;
      }
      return list;
   }

   @Override
   public synchronized boolean remove(Object o) {
      LinkedList<T> currentList = data.get();
      LinkedList<T> newList = copy(currentList, pool.get());
      boolean returnVal = newList.removeLocal(o);
      data.set(newList);
      newList.setCurrent(true);
      currentList.setCurrent(false);
      return returnVal;
   }
   
  

   @Override
   public boolean containsAll(Collection<?> c) {
      return false;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      return false;
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      return false;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return false;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return false;
   }

   @Override
   public void clear() {
      data.get().clear();
   }

   @Override
   public T get(int index) {
      return null;
   }

   @Override
   public T set(int index, T element) {
      return null;
   }

   @Override
   public void add(int index, T element) {
   }

   @Override
   public T remove(int index) {
      return null;
   }

   @Override
   public int indexOf(Object o) {
      return 0;
   }

   @Override
   public int lastIndexOf(Object o) {
      return 0;
   }

   @Override
   public ListIterator<T> listIterator() {
      return null;
   }

   @Override
   public ListIterator<T> listIterator(int index) {
      return null;
   }

   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      return null;
   }

   public void print() {
      LinkedList<T> list = data.get();
      Node<T> node = list.head;
      while(node != null){
         System.out.printf("%s, ", node.object.toString()); 
         node = node.nextNode;
      }
      if(list.head != null){
         System.out.println();
      }
   }
   
  }
