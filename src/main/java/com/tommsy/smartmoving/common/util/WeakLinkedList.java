/*
* Smart Moving Reloaded
* Copyright (C) 2018  Tommsy64
*
* Smart Moving Reloaded is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Smart Moving Reloaded is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Smart Moving Reloaded.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tommsy.smartmoving.common.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A List implementation that uses WeakReferences and is backed by a custom linked list implementation. This list tries to gracefully handle elements dropping out of the list due to being
 * unreachable.
 * <p>
 * An Iterator or ListIterator on this list will not fail if an element disappears due to being unreachable and no reachable element will ever be skipped.
 *
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 */
public class WeakLinkedList<E> implements List<E> {
    private final Object LOCK = new Object();
    private final ReferenceQueue<E> queue = new ReferenceQueue<E>();
    private int size = 0;
    private long modcount = 0;
    private WeakListNode head = null;
    private WeakListNode tail = null;

    public WeakLinkedList() {}

    public WeakLinkedList(Collection<? extends E> c) {
        this.addAll(c);
    }

    public void add(final int index, final E element) {
        synchronized (LOCK) {
            final ListIterator<E> itr = this.listIterator(index);
            itr.add(element);
        }
    }

    public boolean add(E o) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();
            this.add(this.size, o);
            return true;
        }
    }

    public boolean addAll(Collection<? extends E> c) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();
            return this.addAll(this.size, c);
        }
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.size() <= 0)
            return false;

        synchronized (LOCK) {
            this.cleanPhantomReferences();
            for (E element : c)
                this.add(index++, element);
            return true;
        }
    }

    public void clear() {
        synchronized (LOCK) {
            for (final ListIterator<?> itr = this.listIterator(); itr.hasNext();) {
                itr.next();
                itr.remove();
            }
        }
    }

    public boolean contains(Object o) {
        return this.indexOf(o) != -1;
    }

    public boolean containsAll(Collection<?> c) {
        synchronized (LOCK) {
            boolean foundAll = true;
            for (final Iterator<?> elementItr = c.iterator(); elementItr.hasNext() && foundAll;)
                foundAll = this.contains(elementItr.next());
            return foundAll;
        }
    }

    public E get(int index) {
        synchronized (LOCK) {
            final ListIterator<E> itr = this.listIterator(index);
            try {
                return itr.next();
            } catch (NoSuchElementException exc) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }
        }
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        synchronized (LOCK) {
            int index = 0;
            for (final ListIterator<E> itr = this.listIterator(); itr.hasNext();) {
                final E value = itr.next();
                if (o == value || (o != null && o.equals(value)))
                    return index;
                index++;
            }
            return -1;
        }
    }

    /**
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        synchronized (LOCK) {
            this.cleanPhantomReferences();
            return this.size == 0;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The Iterator cannot ensure that after calling hasNext() successfully a call to next() will not throw a NoSuchElementException due to element expiration due to weak references. <br>
     * The remove method has been implemented
     */
    public Iterator<E> iterator() {
        return this.listIterator();
    }

    public int lastIndexOf(Object o) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();

            int index = this.size - 1;
            for (final ListIterator<E> itr = this.listIterator(this.size); itr.hasPrevious();) {
                final Object value = itr.previous();
                if (o == value || (o != null && o.equals(value)))
                    return index;

                index--;
            }

            return -1;
        }
    }

    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    public ListIterator<E> listIterator(int index) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();

            if (index < 0)
                throw new IndexOutOfBoundsException("index must be >= 0");
            else if (index > this.size)
                throw new IndexOutOfBoundsException("index must be <= size()");

            return new WeakListIterator(index);
        }
    }

    public E remove(int index) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();

            final ListIterator<E> itr = this.listIterator(index);
            final E value;
            try {
                value = itr.next();
            } catch (NoSuchElementException exc) {
                throw (new IndexOutOfBoundsException("Index: " + index));
            }

            itr.remove();
            return value;
        }
    }

    public boolean remove(Object o) {
        synchronized (LOCK) {
            for (final ListIterator<?> itr = this.listIterator(); itr.hasNext();) {
                final Object value = itr.next();
                if (o == value || (o != null && o.equals(value))) {
                    itr.remove();
                    return true;
                }
            }

            return false;
        }
    }

    public boolean removeAll(Collection<?> c) {
        synchronized (LOCK) {
            boolean changed = false;

            for (final ListIterator<?> itr = this.listIterator(); itr.hasNext();) {
                final Object value = itr.next();
                if (c.contains(value)) {
                    itr.remove();
                    changed = true;
                }
            }

            return changed;
        }
    }

    public boolean retainAll(Collection<?> c) {
        synchronized (LOCK) {
            boolean changed = false;

            for (final ListIterator<?> itr = this.listIterator(); itr.hasNext();) {
                final Object value = itr.next();
                if (!c.contains(value)) {
                    itr.remove();
                    changed = true;
                }
            }

            return changed;
        }
    }

    public E set(int index, E element) {
        synchronized (LOCK) {
            final ListIterator<E> itr = this.listIterator(index);
            try {
                final E oldVal = itr.next();
                itr.set(element);
                return oldVal;
            } catch (NoSuchElementException exc) {
                throw (new IndexOutOfBoundsException("Index: " + index));
            }
        }
    }

    public int size() {
        synchronized (LOCK) {
            this.cleanPhantomReferences();
            return this.size;
        }
    }

    /**
     * Not implemented.
     *
     * @throws UnsupportedOperationException
     */
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList is not yet supported");
    }

    public Object[] toArray() {
        synchronized (LOCK) {
            this.cleanPhantomReferences();
            return this.toArray(new Object[this.size]);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        synchronized (LOCK) {
            this.cleanPhantomReferences();

            if (a.length < this.size) {
                a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size);
            }

            int index = 0;
            for (final ListIterator<E> itr = this.listIterator(); itr.hasNext();) {
                final E value = itr.next();
                a[index] = (T) value;
                index++;
            }

            if (a.length > index)
                a[index] = null;

            return a;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof List))
            return false;
        else {
            final List<?> other = (List<?>) obj;

            if (this.size() != other.size()) {
                return false;
            } else {
                synchronized (LOCK) {
                    final Iterator<?> itr1 = this.iterator();
                    final Iterator<?> itr2 = other.iterator();

                    while (itr1.hasNext() && itr2.hasNext()) {
                        final Object v1 = itr1.next();
                        final Object v2 = itr2.next();

                        if (v1 != v2 && (v1 == null || !v1.equals(v2)))
                            return false;
                    }
                }

                return true;
            }
        }
    }

    public int hashCode() {
        int hashCode = 1;

        synchronized (LOCK) {
            for (final Iterator<?> itr = this.iterator(); itr.hasNext();) {
                final Object obj = itr.next();
                hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
            }
        }

        return hashCode;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("[");
        synchronized (LOCK) {
            final Iterator<?> itr = this.iterator();
            if (itr.hasNext())
                sb.append(itr.next());
            if (itr.hasNext()) {
                do {
                    sb.append(", ");
                    sb.append(itr.next());
                } while (itr.hasNext());
            }
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Checks the ReferenceQueue for nodes whose values are no long valid and cleanly removes them from the list
     */
    @SuppressWarnings("unchecked")
    private void cleanPhantomReferences() {
        synchronized (LOCK) {
            WeakListNode deadNode;
            while ((deadNode = (WeakListNode) queue.poll()) != null)
                this.removeNode(deadNode);
        }
    }

    /**
     * Removes a node from the list
     *
     * @param deadNode
     */
    private void removeNode(WeakListNode deadNode) {
        synchronized (LOCK) {
            if (deadNode.isRemoved())
                throw new IllegalArgumentException("node has already been removed");

            final WeakListNode deadPrev = deadNode.getPrev();
            final WeakListNode deadNext = deadNode.getNext();

            // Removing the only node in the list
            if (deadPrev == null && deadNext == null) {
                this.head = null;
                this.tail = null;
            }
            // Removing the first node in the list
            else if (deadPrev == null) {
                this.head = deadNext;
                deadNext.setPrev(null);
            }
            // Removing the last node in the list
            else if (deadNext == null) {
                this.tail = deadPrev;
                deadPrev.setNext(null);
            }
            // Removing any other node
            else {
                deadPrev.setNext(deadNext);
                deadNext.setPrev(deadPrev);
            }

            // Flag the removed node as removed
            deadNode.setRemoved();

            // Update the lists size
            this.size--;

            // Ensure the list is still valid
            if (this.size < 0)
                throw new IllegalStateException("size is less than zero - '" + this.size + "'");
            if (this.size == 0 && this.head != null)
                throw new IllegalStateException("size is zero but head is not null");
            if (this.size == 0 && this.tail != null)
                throw new IllegalStateException("size is zero but tail is not null");
            if (this.size > 0 && this.head == null)
                throw new IllegalStateException("size is greater than zero but head is null");
            if (this.size > 0 && this.tail == null)
                throw new IllegalStateException("size is greater than zero but tail is null");
        }
    }

    /**
     * Represents a node in the weak linked list.
     */
    private class WeakListNode extends WeakReference<E> {
        private boolean removed = false;
        private WeakListNode prev;
        private WeakListNode next;

        public WeakListNode(E value) {
            super(value, WeakLinkedList.this.queue);
        }

        /**
         * @return the next node.
         */
        public WeakListNode getNext() {
            return this.next;
        }

        /**
         * @return the previous node.
         */
        public WeakListNode getPrev() {
            return this.prev;
        }

        /**
         * @param next The next node to set.
         */
        public void setNext(WeakListNode next) {
            this.next = next;
        }

        /**
         * @param prev The previous node to set.
         */
        public void setPrev(WeakListNode prev) {
            this.prev = prev;
        }

        /**
         * Marks this node as being removed from a list.
         */
        public void setRemoved() {
            this.removed = true;
        }

        /**
         * @return true if this node has been removed from a list.
         */
        public boolean isRemoved() {
            return this.removed;
        }

        public String toString() {
            final StringBuilder sb = new StringBuilder();

            sb.append("[prev=");

            if (this.prev == null)
                sb.append("null");
            else
                sb.append("'").append(this.prev.get()).append("'");

            sb.append(", value='");
            sb.append(this.get());
            sb.append("', next=");

            if (this.next == null)
                sb.append("null");
            else
                sb.append("'").append(this.next.get()).append("'");

            sb.append("]");

            return sb.toString();
        }
    }

    /**
     * Iterator implementation that can deal with weak nodes.
     */
    private class WeakListIterator implements ListIterator<E> {
        private WeakListNode nextNode;
        private WeakListNode prevNode;
        private long expectedModCount;
        private int index;
        private byte lastDirection;

        public WeakListIterator(final int initialIndex) {
            synchronized (LOCK) {
                this.expectedModCount = WeakLinkedList.this.modcount;
                this.lastDirection = 0;

                // Make worst case for initialization O(N/2)
                if (initialIndex <= (size / 2)) {
                    this.prevNode = null;
                    this.nextNode = WeakLinkedList.this.head;
                    this.index = 0;

                    // go head -> tail to find the initial index
                    while (this.nextIndex() < initialIndex) {
                        this.next();
                    }
                } else {
                    this.prevNode = WeakLinkedList.this.tail;
                    this.nextNode = null;
                    this.index = WeakLinkedList.this.size;

                    // go tail -> head to find the initial index
                    while (this.nextIndex() > initialIndex) {
                        this.previous();
                    }
                }
            }
        }

        public boolean hasNext() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();
                return this.nextNode != null;
            }
        }

        public int nextIndex() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();
                return this.index;
            }
        }

        public E next() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();

                if (this.nextNode == null)
                    throw new NoSuchElementException("No elements remain to iterate through");

                // Move the node refs up one
                this.prevNode = this.nextNode;
                this.nextNode = this.nextNode.getNext();

                // Update the list index
                this.index++;

                // Mark the iterator as clean so add/remove/set operations will work
                this.lastDirection = 1;

                // Return the appropriate value
                return this.prevNode.get();
            }
        }

        public boolean hasPrevious() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();
                return this.prevNode != null;
            }
        }

        public int previousIndex() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();
                return this.index - 1;
            }
        }

        public E previous() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();

                if (this.prevNode == null)
                    throw new NoSuchElementException("No elements previous element to iterate through");

                // Move the node refs down one
                this.nextNode = this.prevNode;
                this.prevNode = this.prevNode.getPrev();

                // Update the list index
                this.index--;

                // Mark the iterator as clean so add/remove/set operations will work
                this.lastDirection = -1;

                // Return the appropriate value
                return this.nextNode.get();
            }
        }

        public void add(E o) {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();

                final WeakListNode newNode = new WeakListNode(o);

                // Add first node
                if (WeakLinkedList.this.size == 0) {
                    WeakLinkedList.this.head = newNode;
                    WeakLinkedList.this.tail = newNode;
                }
                // Add to head
                else if (this.index == 0) {
                    newNode.setNext(WeakLinkedList.this.head);
                    WeakLinkedList.this.head.setPrev(newNode);
                    WeakLinkedList.this.head = newNode;
                }
                // Add to tail
                else if (this.index == WeakLinkedList.this.size) {
                    newNode.setPrev(WeakLinkedList.this.tail);
                    WeakLinkedList.this.tail.setNext(newNode);
                    WeakLinkedList.this.tail = newNode;
                }
                // Add otherwise
                else {
                    newNode.setPrev(this.prevNode);
                    newNode.setNext(this.nextNode);
                    newNode.getPrev().setNext(newNode);
                    newNode.getNext().setPrev(newNode);
                }

                // The new node is always set as the previous node
                this.prevNode = newNode;

                // Update all the counters
                WeakLinkedList.this.size++;
                WeakLinkedList.this.modcount++;
                this.index++;
                this.expectedModCount++;
                this.lastDirection = 0;
            }
        }

        public void set(E o) {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();

                if (this.prevNode == null)
                    throw new IllegalStateException("No element to set");
                if (this.lastDirection == 0)
                    throw new IllegalStateException("next or previous must be called first");

                final WeakListNode deadNode = this.prevNode;
                final WeakListNode newNode = new WeakListNode(o);

                // If the replaced node was the head of the list
                if (deadNode == WeakLinkedList.this.head) {
                    WeakLinkedList.this.head = newNode;
                }
                // Otherwise replace refs with node before the one being set
                else {
                    newNode.setPrev(deadNode.getPrev());
                    newNode.getPrev().setNext(newNode);
                }

                // If the replaced node was the tail of the list
                if (deadNode == WeakLinkedList.this.tail) {
                    WeakLinkedList.this.tail = newNode;
                }
                // Otherwise replace refs with node after the one being set
                else {
                    newNode.setNext(deadNode.getNext());
                    newNode.getNext().setPrev(newNode);
                }

                // Update the ListIterator reference
                this.prevNode = newNode;

                // Clean up the dead node(WeakLinkedList.this.removeNode is not used as it does not work with inserting nodes)
                deadNode.setRemoved();

                // Update counters
                this.expectedModCount++;
                WeakLinkedList.this.modcount++;
                this.lastDirection = 0;
            }
        }

        public void remove() {
            synchronized (LOCK) {
                this.checkConcurrentModification();
                this.updateRefs();

                if (this.lastDirection == 0)
                    throw new IllegalStateException("next or previous must be called first");

                if (this.lastDirection == 1) {
                    if (this.prevNode == null)
                        throw new IllegalStateException("No element to remove");

                    // Use the remove node method from the List to ensure clean up
                    WeakLinkedList.this.removeNode(this.prevNode);

                    // Update the prevNode reference
                    this.prevNode = this.prevNode.getPrev();

                    // Update position
                    this.index--;
                } else if (this.lastDirection == -1) {
                    if (this.nextNode == null)
                        throw new IllegalStateException("No element to remove");

                    // Use the remove node method from the List to ensure clean up
                    WeakLinkedList.this.removeNode(this.nextNode);

                    // Update the nextNode reference
                    this.nextNode = this.nextNode.getNext();
                }

                // Update the counters
                this.expectedModCount++;
                WeakLinkedList.this.modcount++;
                this.lastDirection = 0;
            }
        }

        public String toString() {
            final StringBuilder sb = new StringBuilder();

            sb.append("[index='").append(this.index).append("'");

            sb.append(", prev=");
            if (this.prevNode == null)
                sb.append("null");
            else
                sb.append("'").append(this.prevNode).append("'");

            sb.append(", next=");
            if (this.nextNode == null)
                sb.append("null");
            else
                sb.append("'").append(this.nextNode).append("'");

            sb.append("]");

            return sb.toString();
        }

        /**
         * Inspects the previous and next nodes to see if either have been removed from the list because of a removed reference
         */
        private void updateRefs() {
            synchronized (LOCK) {
                WeakLinkedList.this.cleanPhantomReferences();

                // Update nextNode refs
                while (this.nextNode != null && (this.nextNode.isRemoved() || this.nextNode.isEnqueued()))
                    this.nextNode = this.nextNode.getNext();

                // Update prevNode refs
                while (this.prevNode != null && (this.prevNode.isRemoved() || this.prevNode.isEnqueued()))
                    this.prevNode = this.prevNode.getPrev();

                // Update index
                this.index = 0;
                WeakListNode currNode = this.prevNode;
                while (currNode != null) {
                    currNode = currNode.getPrev();
                    this.index++;
                }

                // Ensure the iterator is still valid
                if (this.nextNode != null && this.nextNode.getPrev() != this.prevNode)
                    throw new IllegalStateException("nextNode.prev != prevNode");
                if (this.prevNode != null && this.prevNode.getNext() != this.nextNode)
                    throw new IllegalStateException("prevNode.next != nextNode");
            }
        }

        /**
         * Checks to see if the list has been modified by means other than this Iterator
         */
        private void checkConcurrentModification() {
            if (this.expectedModCount != WeakLinkedList.this.modcount)
                throw new ConcurrentModificationException("The WeakLinkedList was modified outside of this Iterator");
        }
    }
}
