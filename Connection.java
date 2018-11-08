import java.util.Random;

public class Connection {
	private int innovationNo;
	private Node from;
	private Node to;
	private boolean enabled;
	private double weight;
	private Random r = new Random();
	
	
	public Connection(Node f, Node t, int i) {
		from = f;
		from.addNext(this);
		to = t;
		to.addPrev(this);
		innovationNo = i;
		enabled = true;
		weight = r.nextDouble()*2-1;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public Node getFrom() {
		return from;
	}
	
	public Node getTo() {
		return to;
	}
	
	public int getInnovationNo() {
		return innovationNo;
	}

	public void disable() {
		enabled = false;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void flip() {
		to.remove(this);
		from.remove(this);
		
		
		Node temp = to;
		to = from;
		from = temp;
		
		to.addPrev(this);
		from.addNext(this);
	}

	public void setWeight(double w) {
		weight = w;
	}
	
	public void remove() {
		from.getNext().remove(from.getNext().indexOf(this));
		to.getPrev().remove(to.getPrev().indexOf(this));
	}
}
