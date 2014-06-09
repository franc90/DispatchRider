package dtp.optimization;

/**
 * Queue node consisting of Object and integer weight. Used in PriorityWeightQueue.
 * 
 * @author Szymon Borgosz
 */
public class QueueNode {

    private QueueNode next;
    private Object element;
    private int weight;

    /**
     * Constructs new node with given values. Next is set to null.
     * 
     * @param element
     * @param weight
     */
    public QueueNode(Object element, int weight) {
        super();
        this.element = element;
        this.weight = weight;
        this.next = null;
    }

    /**
     * @return Element of type Object
     */
    public Object getElement() {
        return element;
    }

    /**
     * Sets element
     * 
     * @param element
     *        - Object that will be part of node
     */
    public void setElement(Object element) {
        this.element = element;
    }

    /**
     * @return Reference to the next element in queue
     */
    public QueueNode getNext() {
        return next;
    }

    /**
     * Sets reference to the next element in queue
     * 
     * @param next
     */
    public void setNext(QueueNode next) {
        this.next = next;
    }

    /**
     * @return Weight value of contained Object
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets weight value of contained Object
     * 
     * @param weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
