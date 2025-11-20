package HeadersList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

//ъуъ
public class HeadersList<T extends Comparable<T>> implements Iterable<HeadersList.SerializableNode<T>> {

    //башку в узел завязал(пара заголовок + список, надо для линковки)
    private static class HeaderNode<T> {
        T header;
        HeaderNode<T> next;
        List<T> associatedList;

        HeaderNode(T header, List<T> list) {
            this.header = header;
            this.associatedList = list;
            this.next = null;
        }
    }

    public record SerializableNode<T>(T header, List<T> associatedList) {}

    private HeaderNode<T> root;
    private int size;

    public HeadersList() {}

    @Override
    public Iterator<SerializableNode<T>> iterator() {
        return new HeadersListIterator();
    }

    private class HeadersListIterator implements Iterator<SerializableNode<T>> {
        private HeaderNode<T> current = root;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public SerializableNode<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            SerializableNode<T> data = new SerializableNode<>(current.header, current.associatedList);
            current = current.next;
            return data;
        }
    }

    //в конец добавляем списачек и загаловочек
    public void add(T header, List<T> list) {
        if (root == null) {
            root = new HeaderNode<>(header, list);
        } else {
            HeaderNode<T> currNode = root;
            while (currNode.next != null) {
                currNode = currNode.next;
            }
            currNode.next = new HeaderNode<>(header, list);
        }
        size++;
    }

    //тотальнейшая вставка списка, его башки ослиной по индексу
    public void add(int index, T header, List<T> list) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("ты чё твориш...");
        HeaderNode<T> newNode = new HeaderNode<>(header, list);
        if (index == 0) {
            newNode.next = root;
            root = newNode;
        } else {
            HeaderNode<T> currNode = root;
            for (int i = 0; i < index - 1; i++) {
                currNode = currNode.next;
            }
            newNode.next = currNode.next;
            currNode.next = newNode;
        }
        size++;
    }

    //сортиров очка + балансиров очка
    public void sort() {
        if (size >= 2) {
            HeaderNode<T> last = root;
            while (last.next != null) {
                last = last.next;
            }
            quickSort(root, last);
        }
    }

    private void quickSort(HeaderNode<T> start, HeaderNode<T> end) {
        if (start == null || end == null || start == end || start == end.next) {
            return;
        }

        HeaderNode<T> pivot = partition(start, end);

        if (pivot != start) {
            HeaderNode<T> prevPivot = start;
            while (prevPivot.next != pivot) {
                prevPivot = prevPivot.next;
            }
            quickSort(start, prevPivot);
        }

        if (pivot != end && pivot.next != null) {
            quickSort(pivot.next, end);
        }
    }

    private HeaderNode<T> partition(HeaderNode<T> start, HeaderNode<T> end) {
        T pivotValue = end.header;
        HeaderNode<T> i = start;

        HeaderNode<T> j = start;
        while (j != end) {
            if (j.header.compareTo(pivotValue) < 0) {
                swapNodes(i, j);
                i = i.next;
            }
            j = j.next;
        }

        swapNodes(i, end);
        return i;
    }

    private void swapNodes(HeaderNode<T> node1, HeaderNode<T> node2) {
        if (node1 != node2) {
            T tempHeader =  node1.header;
            List<T> tempList = node1.associatedList;

            node1.header = node2.header;
            node1.associatedList = node2.associatedList;

            node2.header = tempHeader;
            node2.associatedList = tempList;
        }
    }

    public void balance() {
        if (root == null || root.next == null) {
            return;
        }

        List<T> allItems = new ArrayList<>();
        HeaderNode<T> current = root;
        while (current != null) {
            allItems.addAll(current.associatedList);
            current = current.next;
        }

        current = root;
        while (current != null) {
            current.associatedList.clear();
            current = current.next;
        }

        int totalItems = allItems.size();
        int numLists = this.size;
        int baseSize = totalItems / numLists;
        int remainder = totalItems % numLists;

        int currentItemIndex = 0;
        int nodeIndex = 0;
        current = root;

        while (current != null) {
            int sublistSize = baseSize + (nodeIndex < remainder ? 1 : 0);

            for (int j = 0; j < sublistSize; j++) {
                if (currentItemIndex < totalItems) {
                    current.associatedList.add(allItems.get(currentItemIndex++));
                }
            }

            nodeIndex++;
            current = current.next;
        }
    }

    //сортир... Нажмите для продолжения...
    public void addSorted(T header, List<T> list) {
        HeaderNode<T> newNode = new HeaderNode<>(header, list);

        if (root == null) {
            root = newNode;
            size++;
            return;
        }

        int comparison = header.compareTo(root.header);
        if (comparison < 0) {
            newNode.next = root;
            root = newNode;
            size++;
            return;
        }

        if (comparison == 0) {
            root.associatedList.addAll(list);
            return;
        }

        HeaderNode<T> current = root;

        while (current.next != null) {
            comparison = header.compareTo(current.next.header);

            if (comparison == 0) {
                current.next.associatedList.addAll(list);
                return;
            }

            if (comparison < 0) {
                newNode.next = current.next;
                current.next = newNode;
                size++;
                return;
            }
            current = current.next;
        }
        current.next = newNode;
        size++;
    }

    //бошку открути
    public T getHeader(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("ты чё делаешь? мужик, успокойся");
        }
        HeaderNode<T> curr = root;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.header;
    }

    //вы списков продаете?
    //нет тока показываю
    //красивое...
    public List<T> getList(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("ты чё делаешь? мужик, успокойся");
        }
        HeaderNode<T> curr = root;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.associatedList;    }

    //списачик палучаим дада
    public List<T> getListByHeader(T header) {
        HeaderNode<T> curr = root;
        while (curr != null) {
            if (curr.header.equals(header)) {
                return curr.associatedList;
            }
            curr = curr.next;
        }
        return null;
    }

    //минус элемент
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("да харош уже нет такого индекса");
        }

        if (index == 0) {
            root = root.next;
            size--;
            return;
        }
        HeaderNode<T> prev = root;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        HeaderNode<T> removingNode = prev.next;
        prev.next = removingNode.next;
        size--;
    }

    //да это на морозе уменьшился чесно
    public int size() {
        return size;
    }

    //вешаем итератор
    public void forEachHeader(Consumer<T> action) {
        HeaderNode<T> curr = root;
        while (curr != null) {
            action.accept(curr.header);
            curr = curr.next;
        }
    }

    //парсуем, я сказала парсуем!
    public void saveToFile(File file) throws IOException {
        List<SerializableNode<T>> dataToSave = new ArrayList<>();

        HeaderNode<T> current = root;
        while (current != null) {
            dataToSave.add(new SerializableNode<>(current.header, current.associatedList));
            current = current.next;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(dataToSave, writer);
        }
    }

    public static <T extends Comparable<T>> HeadersList<T> loadFromFile(File file, Type type) throws IOException {
        Gson gson = new Gson();
        HeadersList<T> resultList = new HeadersList<T>();
        try (FileReader reader = new FileReader(file)) {
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            List<SerializableNode<T>> loadedData = gson.fromJson(reader, listType);

            if (loadedData != null) {
                for (SerializableNode<T> nodeData : loadedData) {
                    resultList.add(nodeData.header(), nodeData.associatedList());
                }
            }
        }
        return resultList;
    }

    @Override
    public String toString() {
        if (root == null) {
            return "HeadersList{[]}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("HeadersList{[");
        HeaderNode<T> current = root;
        while (current != null) {
            sb.append(current.header.toString())
                    .append(": ")
                    .append(current.associatedList.toString());
            if (current.next != null) {
                sb.append("], [");
            }
            current = current.next;
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HeadersList<?> other = (HeadersList<?>) obj;

        if (this.size != other.size) {
            return false;
        }

        HeaderNode<?> currentThis = this.root;
        HeaderNode<?> currentOther = other.root;

        while (currentThis != null) {
            if (!Objects.equals(currentThis.header, currentOther.header) ||
                    !Objects.equals(currentThis.associatedList, currentOther.associatedList)) {
                return false;
            }
            currentThis = currentThis.next;
            currentOther = currentOther.next;
        }
        return true;
    }
    @Override
    public int hashCode() {
        int result = 1;
        HeaderNode<T> current = root;

        while (current != null) {
            int headerHash = Objects.hashCode(current.header);
            int listHash = Objects.hashCode(current.associatedList);

            result = 31 * result + headerHash;
            result = 31 * result + listHash;

            current = current.next;
        }
        return result;
    }
}