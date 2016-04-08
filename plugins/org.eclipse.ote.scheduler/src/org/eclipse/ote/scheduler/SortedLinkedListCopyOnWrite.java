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
   private int listCount = 4;
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
            // TODO Auto-generated method stub
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
   
//   LinkedList<T> getList(){
//      poolLock.lock();
//      LinkedList<T> newNode;
//      try{
//         if(!pool.isEmpty()){
//            newNode = pool.pop();
//         } else {
//            listCount++;
//            System.out.printf("NEW LIST %d *****************************************************************\n", listCount);
//            newNode = new LinkedList<>(nodePool);
//         }
//      } finally {
//         poolLock.unlock();
//      }
//      return newNode;
//   }
//   
//   private void pushList(LinkedList<T> node){
//      poolLock.lock();
//      try{
//         pool.push(node);
//      } finally {
//         poolLock.unlock();
//      }
//   }
   
   private synchronized LinkedList<T> cloneList(){
      
      return null;
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
      //do something smart to recoup nodes from it if the data has changed
      
//      if(!data.get().iterator().equals(it)){
////         LinkedList<T> list = it.parent;
////         list.clear();
////         pool.push(list);
//         System.out.println("the iterator has changed so the old array is crap "+ this.hashCode());
//         
//      }
//      else {
//         System.out.println("same iterator... no changes " + this.hashCode());
//      }
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
//      refCount--;
   }

   @Override
   public Object[] toArray() {
//      Object[] objs = new Object[size()];
//      Node<T> node = head;
//      int i = 0;
//      while(node != null && i < objs.length){
//         objs[i] = node.object;
//      }
      return null;//objs;
   }

   @SuppressWarnings({ "unchecked", "hiding" })
   @Override
   public <T> T[] toArray(T[] a) {
//      int size = size();
//      if(a.length != size){
//         a = (T[])Array.newInstance(a.getClass(), size);
//      }
//      Node node = head;
//      int i = 0;
//      while(node != null && i < a.length){
//         a[i] = (T)node.object;
//      }
      return null;//a;
   }

   public synchronized boolean add(T e) {
//      int currentRefcount = refCount;
      LinkedList<T> currentList = data.get();
      LinkedList<T> newList = copy(currentList, pool.get());
      boolean returnVal = newList.addLocal(e);
      data.set(newList);
      currentList.setCurrent(false);
//      if(currentRefcount == refCount){
         
//         currentList.clear();
//         pool.push(currentList);
//      }
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
      data.get().clear();
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

   public void print() {
      LinkedList list = data.get();
      Node node = list.head;
      while(node != null){
         System.out.printf("%s, ", node.object.toString()); 
         node = node.nextNode;
      }
      if(list.head != null){
         System.out.println();
      }
   }
   
  }
