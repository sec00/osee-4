package org.eclipse.ote.scheduler;

import java.util.Iterator;

class MyInnerIterator<T extends Comparable<T>> implements Iterator<T> {

   Node<T> currentNode;
   Node<T> nextNode;
   LinkedList<T> parent;

   public void init(LinkedList<T> parent){
      currentNode = null;
      this.parent = parent;
      this.nextNode = parent.head;
   }

   @Override
   public boolean hasNext() {
      return nextNode != null;
   }

   @Override
   public T next() {
      T toReturn = nextNode.object;
      currentNode = nextNode;
      nextNode = currentNode.nextNode;
      return toReturn;
   }
}