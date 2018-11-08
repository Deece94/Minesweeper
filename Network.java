import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network {
	Random r = new Random();
	int xSize, ySize;//size of input nodes
	private double fitness;
	private List<Node> inputs;
	private List<Node> outputs;//holds position of output node
	private List<Node> nodes;
	private List<Connection> connections;
	private Species specie;
	private String name = ""; 
	
	public Network(int x, int y){
		xSize = x;
		ySize = y;
		inputs = new ArrayList<Node>();
		outputs = new ArrayList<Node>();
		nodes = new ArrayList<Node>();
		//initialise input nodes
		for(int i = 0; i<xSize; i++) {
			Node n = new Node(true, false, i+1);
			inputs.add(n);
			nodes.add(n);
		}
		
		//initialise output
		for(int j = 0; j<ySize; j++) {
			Node n = new Node(false, true, x+j+1);
			outputs.add(n);
			nodes.add(n);
		}	
		
		
		//initialise connections list
		connections = new ArrayList<Connection>();
		int c = 0;
		for(int k = 0; k<xSize; k++) {
			for(int l = 0; l<ySize; l++) {
				connections.add(new Connection(inputs.get(k), outputs.get(l), c+1));
				c++;
			}
		}
	}
	
	//requires in[] to be of same length as input nodes
	public double[] run(int in[] ){
		for(int i = 0; i<in.length; i++) {
			inputs.get(i).setValue(in[i]);
		}
		
		double[] out = new double[ySize];
		for(int j = 0; j<ySize; j++) {
			out[j] = outputs.get(j).getValue();
		}
		return out;
	}
	
	//mutates nodes weights
	public void mutate(double mut){
		for(int i = 0; i<connections.size(); i++) {
			double p = r.nextDouble();
			if(p<0.33){
				double addWeight = (r.nextDouble()-0.5)*mut;
				connections.get(i).setWeight(addWeight+connections.get(i).getWeight());
			}
		}
	}
	
	public void mutateAddNode() {
		int c = r.nextInt(connections.size());
		connections.get(c).disable();
		Node newNode = new Node(false, false, Singleton.getNodeID()+xSize+ySize);
		Connection c1 = new Connection(connections.get(c).getFrom(), newNode, Singleton.getID()+xSize*ySize);
		c1.setWeight(connections.get(c).getWeight());
		Connection c2 = new Connection(newNode, connections.get(c).getTo(), Singleton.getID()+xSize*ySize);
		c2.setWeight(1);
		nodes.add(newNode);
		connections.add(c1);
		connections.add(c2);
	}
	
	public void mutateAddConnection() {
		boolean retry = true;
		while(retry) {
			Random r = new Random();
			int r1 = r.nextInt(nodes.size());
			int r2 = r.nextInt(nodes.size());
			boolean canAdd = true;
			//check if connection already exists
			for(int i = 0; i<connections.size(); i++) {
				if((connections.get(i).getFrom() == nodes.get(r1) && connections.get(i).getTo() == nodes.get(r2)) || 
						(connections.get(i).getFrom() == nodes.get(r2) && connections.get(i).getTo() == nodes.get(r1))) {
					retry = false;
					canAdd = false;
				}
			}
			//check they are not the same node
			if(r1 != r2 && canAdd && !nodes.get(r2).isInput() && !nodes.get(r1).isOutput()) {
				Connection c = new Connection(nodes.get(r1), nodes.get(r2), Singleton.getID()+xSize*ySize);
				//adds connection to list
				connections.add(c);
				//check if there is now a cycle
				if(hasCycle()) {
					connections.remove(connections.indexOf(c));
					c.remove();
				}
				return;
			}
			
			
			
			
		}	
	}
	
	//returns [disjoint, excess, weight difference]
	
	public double compare(Network n){
		int disjoint = 0, excess = 0, index1 = 0, index2 = 0;
		double c1 = 1, c2 = 1, c3 = 0.3;
		double weightDifference = 0;
		Connection[] compareConnection = n.getGenome();
		Connection[] thisConnection = connections.toArray(new Connection[connections.size()]);
		boolean cont = true;
		
		while (cont) {
			//if both have finished looking at connection end loop
			if(index1 == thisConnection.length && index2 == compareConnection.length) {
				cont = false;
			}
			else if(index1 == thisConnection.length) {
				excess++;
				index2++;
			}
			else if(index2 == compareConnection.length) {
				excess++;
				index1++;
			}
			//if same check difference in weights
			else if(thisConnection[index1].getInnovationNo() == compareConnection[index2].getInnovationNo()) {
				//weights check
				weightDifference += Math.abs(thisConnection[index1].getWeight() - compareConnection[index2].getWeight());
				index1++;
				index2++;
			}
			else if(thisConnection[index1].getInnovationNo() < compareConnection[index2].getInnovationNo()) {
				disjoint++;
				index1++;
			}
			else if(thisConnection[index1].getInnovationNo() > compareConnection[index2].getInnovationNo()) {
				disjoint++;
				index2++;
			}
		}
		
		double distance = c1*excess +c2*disjoint + c3*weightDifference;
		
		
		
		return distance;
	}
	
	
	public void setFitness(double f){
		fitness = f;
	}
	
	public double getFitness(){
		List<Network> species = specie.getlist();
		double distanceSum = 0;
		//find how close to other networks it is. This decreases clumping
		for(int i = 0; i<species.size(); i++) {
			distanceSum += species.get(i).compare(this)*0.1;
		}
		
		double f = fitness/(distanceSum+1);
		return f;
		
		
	}
	
	public boolean hasCycle() {
		List<Node> visited = new ArrayList<Node>();
		//check all nodes
		for(int i = 0; i<nodes.size(); i++) {
			if(hasCycleUtil(nodes.get(i), visited)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasCycleUtil(Node current, List<Node> visited) {
		
		//check if current node is in visited
		if(visited.contains(current)) {
			return true;
		}
		
		
		
		//create new list of nodes adding current node to it 
		List<Node> newVisited = new ArrayList<Node>();
		for(int i = 0; i<visited.size(); i++) {
			newVisited.add(visited.get(i));
		}
		newVisited.add(current);
		
		//if has no further connections will be skipped over
		for(int i = 0; i<current.getNext().size(); i++) {
			if (hasCycleUtil(current.getNext().get(i).getTo(), newVisited)) {
				//if the next one returns true then send all the way back up recursion TRUE
				return true;
			}
		}
		
		return false;
	}
	
	public int getGenomeSize() {
		return connections.size();
	}
	
	public void setIn(Node[] in) {
		inputs.clear();
		for(int i = 0; i<in.length; i++) {
			inputs.add(in[i]);
		}
	}
	
	public void setOut(Node[] out) {
		outputs.clear();
		for(int i = 0; i<out.length; i++) {
			outputs.add(out[i]);
		}
	}
	
	public void setNodes(Node[] n) {
		nodes.clear();
		for(int i = 0; i<n.length; i++) {
			nodes.add(n[i]);
		}
	}
	
	public void setGenome(Connection[] c) {
		connections.clear();
		for(int i = 0; i<c.length; i++) {
			connections.add(c[i]);
		}
	}
	
	public Connection[] getGenome() {
		return connections.toArray(new Connection[connections.size()]);
	}
	
	public Node[] getNodes() {
		return nodes.toArray(new Node[nodes.size()]);
	}
	
	public void setSpecies(Species s) {
		specie = s;
	}
	
	public Species getSpecies() {
		return specie;
	}
	
	public Network getCopy() {
		Network newNetwork = new Network(xSize, ySize);
		
		
		
		return newNetwork;
	}
	
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
}
