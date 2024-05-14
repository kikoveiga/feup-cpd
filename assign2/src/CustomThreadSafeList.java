import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomThreadSafeList<T> implements Iterable<T> {
    private final List<T> list = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public void add(T element) {
        lock.lock();
        try {
            list.add(element);
        } finally {
            lock.unlock();
        }
    }

    public void remove(T element) {
        lock.lock();
        try {
            list.remove(element);
        } finally {
            lock.unlock();
        }
    }

    public void removeAll(List<T> elements) {
        lock.lock();
        try {
            list.removeAll(elements);
        } finally {
            lock.unlock();
        }
    }

    public T get(int index) {
        lock.lock();
        try {
            return list.get(index);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return list.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T element) {
        lock.lock();
        try {
            return list.contains(element);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new CustomThreadSafeIterator();
    }

    private class CustomThreadSafeIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            lock.lock();
            try {
                return currentIndex < list.size();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public T next() {
            lock.lock();
            try {
                return list.get(currentIndex++);
            } finally {
                lock.unlock();
            }
        }
    }
}
