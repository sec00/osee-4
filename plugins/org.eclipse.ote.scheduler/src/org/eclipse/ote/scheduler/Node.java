package org.eclipse.ote.scheduler;
class Node<T> {
      
      T object;
      Node<T> previousNode;
      Node<T> nextNode;
      
      volatile boolean deleted = false; 
      
      public Node(){
         previousNode = null;
         nextNode = null;
      }
      
   }