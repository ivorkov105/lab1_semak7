package HeadersList;

import java.util.ArrayList;
import java.util.List;

public final class HeadersListFactory {

    private HeadersListFactory() {}

    private static <T extends Comparable<T>> HeadersList<T> copy(HeadersList<T> original) {
        HeadersList<T> newInstance = new HeadersList<T>();

        for (HeadersList.SerializableNode<T> nodeData : original) {
            List<T> associatedListCopy = new ArrayList<>(nodeData.associatedList());
            newInstance.add(nodeData.header(), associatedListCopy);
        }

        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> add(HeadersList<T> source, T header, List<T> list) {
        HeadersList<T> newInstance = copy(source);
        newInstance.add(header, list);
        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> add(HeadersList<T> source, int index, T header, List<T> list) {
        HeadersList<T> newInstance = copy(source);
        newInstance.add(index, header, list);
        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> addSorted(HeadersList<T> source, T header, List<T> list) {
        HeadersList<T> newInstance = copy(source);
        newInstance.addSorted(header, list);
        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> remove(HeadersList<T> source, int index) {
        HeadersList<T> newInstance = copy(source);
        newInstance.remove(index);
        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> sort(HeadersList<T> source) {
        HeadersList<T> newInstance = copy(source);
        newInstance.sort();
        return newInstance;
    }

    public static <T extends Comparable<T>> HeadersList<T> balance(HeadersList<T> source) {
        HeadersList<T> newInstance = copy(source);
        newInstance.balance();
        return newInstance;
    }
}