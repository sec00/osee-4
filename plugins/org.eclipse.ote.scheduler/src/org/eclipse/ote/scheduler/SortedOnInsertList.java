package org.eclipse.ote.scheduler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SortedOnInsertList<T extends Comparable<T>> implements Iterable<T> {

   int first = 0;
   int last = 0;
   int count = 0;
   private T[] data;
   List<T> arrayList;
   
   private NoNewIterator it;
   private Class<T> clazz;

   @SuppressWarnings("unchecked")
   public SortedOnInsertList(Class<T> clazz){
      this.clazz = clazz;
      data = (T[]) Array.newInstance(clazz, 8);
      it = new NoNewIterator();
      arrayList = new ArrayList<>();
   }

   public boolean add(T task){
      int index = findClosestGreaterThan(task);
      if(index >= data.length){
         compress();
         index = findClosestGreaterThan(task);
      }
      if(index >= data.length){
         growArray();
      }
      if(data[index] == null){
         data[index]=task;         
      } else if(isSpaceToLeft(index)){//is there a space to the left
         data[index-1]=task;
      } else {
         if(!canShiftRight(index)){
            compress();
            index = findClosestGreaterThan(task);
            if(!canShiftRight(index)){
               growArray(); 
            }
         }
         shiftRight(index);
         data[index] = task;
      }
      return true;
   }
   
   private void compress() {
      for(int i = 0; i < data.length; i++){
         if(data[i] == null){
            for(int j = i+1; j < data.length; j++){
               if(data[j] != null){
                  data[i] = data[j];
                  data[j] = null;
                  break;
               }
            }
         }
      }
   }

   private void shiftRight(int index) {
      int firstNull = index+1;
      for(;firstNull < data.length;firstNull++){
         if(data[firstNull] == null){
            break;
         }
      }
      for(int i = firstNull; i > index; i--){
         data[i] = data[i-1];
      }
      data[index] = null;
   }

   private void growArray() {
      @SuppressWarnings("unchecked")
      T[] newTasks = (T[]) Array.newInstance(clazz, data.length + 8);
      System.arraycopy(data, 0, newTasks, 0, data.length);
      data = newTasks;
   }

   private boolean canShiftRight(int index) {
      for(int i = index+1; i < data.length; i++){
         if(data[i] == null){
            return true;
         }
      }
      return false;
   }

   private boolean isSpaceToLeft(int index) {
      if(index > 0 && data[index-1] == null){
         return true;
      }
      return false;
   }

   private int findClosestGreaterThan(T task){
      int i = 0;
      int lastItem = 0;
      for(; i < data.length; i++){
         if(data[i] != null){
            lastItem = i;
            if(data[i].compareTo(task) > 0){
               return i;
            }
         }
      }
      return lastItem+1;
   }

   public boolean remove(T task){
      for(int i = 0; i < data.length; i++){
         if(data[i] != null){
            if(data[i].equals(task)){
               data[i] = null;
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public Iterator<T> iterator() {
      it.reset();
      return it;
   }
   
   public static void main(String[] args){
      SortedOnInsertList<Integer> list = new SortedOnInsertList<>(Integer.class);
      
      if(list.iterator().hasNext()){
         System.out.println("");
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
   }

   private class NoNewIterator implements Iterator<T> {

      private int current;

      public NoNewIterator() {
         
      }
      
      @Override
      public boolean hasNext() {
         for(int i = current+1; i < SortedOnInsertList.this.data.length; i++){
            if(SortedOnInsertList.this.data[i] != null){
               return true;
            }
         }
         return false;
      }

      public void reset() {
         current = -1;
      }

      @Override
      public T next() {
         for(int i = current+1; i < SortedOnInsertList.this.data.length; i++){
            if(SortedOnInsertList.this.data[i] != null){
               current = i;
               return SortedOnInsertList.this.data[i];
            }
         }
         return null;
      }
      
      @Override
      public void remove(){
         SortedOnInsertList.this.data[current] = null;
      }

   }

   public int size() {
      int size = 0;
      for(int i = 0; i < data.length; i++){
         if(data[i] != null){
            size++;
         }
      }
      return size;
   }

   public boolean isEmpty() {
      for(int i = 0; i < data.length; i++){
         if(data[i] != null){
            return false;
         }
      }
      return true;
   }

   public void addToQueue(T task) {
      arrayList.add(task);
   }
   
   public void flushAddQueue(){
      for(int i = 0; i < arrayList.size(); i++){
         add(arrayList.get(i));
      }
      arrayList.clear();
   }

}
