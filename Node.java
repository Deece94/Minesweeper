import java.util.ArrayList;
import java.util.List;

public class Node {
	private double value;
	private boolean input;
	private boolean output;
	private List<Connection> next;
	private List<Connection> prev;
	private int innovationNo;
	
	public Node(boolean in, boolean out, int i){
		next = new ArrayList<>();
		prev = new ArrayList<>();
		input = in;
		output = out;
		innovationNo = i;
	}
	
	
	public void addNext(Connection c) {
		next.add(c);
	}
	
	public void addPrev(Connection c) {
		prev.add(c);
	}
	
	public void setNext(Connection[] c) {
		next.clear();
		for(int i = 0; i<c.length; i++) {
			next.add(c[i]);
		}
	}
	
	public void setPrev(Connection[] c) {
		prev.clear();
		for(int i = 0; i<c.length; i++) {
			prev.add(c[i]);
		}
	}
	
	public List<Connection> getNext() {
		return next;
	}
	
	public List<Connection> getPrev() {
		return prev;
	}
	
	public boolean isInput() {
		return input;
	}
	
	public boolean isOutput() {
		return output;
	}
	
	public void setValue(double v) {
		value = v;
	}
	
	public double getValue() {
		if(input == true) {
			return value;
		}
		else {
			double sum = 0;
			for (int i = 0; i<prev.size(); i++){
				//weight * prev nodes value
				if(prev.get(i).isEnabled()) {
					sum += prev.get(i).getWeight()*prev.get(i).getFrom().getValue();
				}
			}
			
			//sigmoid
			sum = sig(sum);
			value = sum;
			return value;
		}
	}
	
	
	//changes number to be between -1 and 1
	private double sig(double x){
		double s = 1/(1+Math.pow(Math.E,-x));
		return (s-0.5)*2;
	}
	
	public int getInnovationNo() {
		return innovationNo;
	}
	
	public void setInput(boolean i) {
		input = i;
	}
	
	public void setOutput(boolean o) {
		output = o;
	}
	
	public void remove(Connection c) {
		next.remove(c);
		prev.remove(c);
	}
	
}
