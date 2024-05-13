import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomThreadSafeList<E> {
    private final List<E> list = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public void add(E element) {
        lock.lock();
        try {
            list.add(element);
        } finally {
            lock.unlock();
        }
    }

    public void remove(E element) {
        lock.lock();
        try {
            list.remove(element);
        } finally {
            lock.unlock();
        }
    }

    public E get(int index) {
        lock.lock();
        try {
            return list.get(index);
        } finally {
            lock.unlock();
        }
    }

    public List<E> subList(int fromIndex, int toIndex) {
        lock.lock();
        try {
            return new ArrayList<>(list.subList(fromIndex, toIndex));
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

    public void removeAll(List<E> elements) {
        lock.lock();
        try {
            list.removeAll(elements);
        } finally {
            lock.unlock();
        }
    }
}