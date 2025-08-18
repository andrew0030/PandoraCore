public class LinkedDequeTest {
    public static void main(String[] args) {
        LinkedDeque<Object> deque = new LinkedDeque<>();
        deque.add(new Object());
        deque.add("text0");
        deque.add("text1");
        deque.add("text2");
        deque.add("text3");

        System.out.println(deque.removeFirstOccurrence("text2"));
        System.out.println(deque.removeLastOccurrence("text0"));
        System.out.println(deque.removeFirst());
        System.out.println(deque.removeFirst());
        System.out.println(deque.removeLastOccurrence("text3"));

        deque.add(new Object());
        deque.add("text0");
        deque.add("text1");
        deque.add("text2");
        deque.add("text3");

        System.out.println(deque.removeFirstOccurrence("text3"));
    }
}
