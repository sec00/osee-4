package org.eclipse.ote.scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectPool<T> {

   private ReentrantLock poolLock;
   private ArrayDeque<T> pool;
   private ArrayList<T> inuse;
   private int count;
   private Create<T> create;
   private Initialize<T> initilize;
   private boolean debug = false;
   
   public ObjectPool(Create<T> create, Initialize<T> initilize){
      poolLock = new ReentrantLock();
      inuse = new ArrayList<>();
      pool = new ArrayDeque<>();
      this.create = create;
      this.initilize = initilize;
   }
   
   public T get() {
      poolLock.lock();
      T item;
      try{
         if(!pool.isEmpty()){
            item = pool.pop();
            initilize.initialize(item);
            inuse.add(item);
            if(debug){
               System.out.printf("%s,%d, get, %d, %d, %d\n", item.getClass().getSimpleName(), this.hashCode(), count, inuse.size(), pool.size());
            }
         } else {
            count++;
            item = create.create();
            inuse.add(item);
            if(debug){
               System.out.printf("%s,%d, create, %d, %d, %d\n", item.getClass().getSimpleName(), this.hashCode(), count, inuse.size(), pool.size());
            }
         }
      } finally {
         poolLock.unlock();
      }
      return item;
   }

   public void push(T item) {
      poolLock.lock();
      try{
         inuse.remove(item);
         pool.push(item);
         if(debug){
            System.out.printf("%s,%d, push, %d, %d, %d\n", item.getClass().getSimpleName(), this.hashCode(), count, inuse.size(), pool.size());
         }
      } finally {
         poolLock.unlock();
      }
   }
   
   public void fillInUseItems(ArrayList<T> objects){
      poolLock.lock();
      try{
         for(int i = 0; i < inuse.size(); i++){
            objects.add(inuse.get(i));
         }
      } finally {
         poolLock.unlock();
      }
   }
   
   public void flush(){
      pool.clear();
      inuse.clear();
   }

}
