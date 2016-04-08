package org.eclipse.ote.scheduler;

class LinkedList<T extends Comparable<T>> {
      
      Node<T> head;
      Node<T> tail;
      private ObjectPool<Node<T>> pool;
      private ObjectPool<MyInnerIterator<T>> itpool;
      private volatile boolean current;
      
      public LinkedList(ObjectPool<Node<T>> nodePool, ObjectPool<MyInnerIterator<T>> itpool2){
         this.pool = nodePool;
         this.itpool = itpool2;
      }
      
      public void clear(){
         Node<T> node = head;
         while(node != null){
            Node<T> giveBack = node;
            node = node.nextNode;
            pool.push(giveBack);
         }
         head = null;
      }
      
      public boolean addLocal(T e) {
         Node<T> node = head;
         boolean added = false;
         Node<T> newNode = pool.get();
         newNode.object = e;
         if(head == null){
            head = newNode;
            tail = newNode;
         } else {
            while(node != null){
               if(e.compareTo(node.object) < 0){
                  Node<T> before = node.previousNode;
                  Node<T> after = node;
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
      
      public boolean removeLocal(Object o) {
         Node<T> node = head;
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
               node.object = null;  
               pool.push(node);
               return true;
            }
            node = node.nextNode;
         }
         return false;
      }
      
      public synchronized MyInnerIterator<T> iterator() {
         MyInnerIterator<T> it = itpool.get();
         it.init(this);
         return it;
      }
      
      

      public int size() {
         int size = 0;
         Node<T> node = head;
         while(node != null){
            System.out.printf("%s, ", node.object.toString()); 
            node = node.nextNode;
            size++;
         }
         return size;
      }

      public boolean isEmpty() {
         return head == null;
      }

      public boolean contains(Object o) {
         Node<T> node = head;
         while(node != null){
            if(o.equals(node.object)){
               return true;
            }
            node = node.nextNode;
         }
         return false;
      }

      public void print() {
         Node<T> node = head;
         while(node != null){
            System.out.printf("%s, ", node.object.toString()); 
            node = node.nextNode;
         }
         System.out.println();
      }

      public void setCurrent(boolean current) {
         this.current = current;
      }
      
      public boolean getCurrent(){
         return this.current;
      }
      
   }