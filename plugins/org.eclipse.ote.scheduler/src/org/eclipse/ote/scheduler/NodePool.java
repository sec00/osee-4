package org.eclipse.ote.scheduler;

import java.util.ArrayDeque;
import java.util.concurrent.locks.ReentrantLock;

public class NodePool<T extends Comparable<T>> {

   private ReentrantLock poolLock;
   private ArrayDeque<Node<T>> pool;
   private int nodeCount;
   
   public NodePool(){
      poolLock = new ReentrantLock();
      pool = new ArrayDeque<>();
   }
   
   public Node<T> getNode() {
      poolLock.lock();
      Node<T> newNode;
      try{
         if(!pool.isEmpty()){
            newNode = pool.pop();
            newNode.deleted = false;
            newNode.nextNode = null;
            newNode.previousNode = null;
            newNode.object = null;
         } else {
            nodeCount++;
            System.out.printf("NEW NODE %d *****************************************************************\n", nodeCount);
            newNode = new Node<T>();
         }
      } finally {
         poolLock.unlock();
      }
      return newNode;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void pushNode(Node node) {
      poolLock.lock();
      try{
         pool.push(node);
      } finally {
         poolLock.unlock();
      }
   }

}
