import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TODO

public class GA {
	
	Random r = new Random();
	private int genSize;
	private Network[] generation;
	private int index = 0;
	private int xSize;
	private int ySize;
	private int genNo = 0;
	double threshold = 3.0;

	List<Species> speciesList = new ArrayList<Species>();
	
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
	LocalDateTime now;

	
	public GA(int size, int layers, int inputs, int outputs){
		genSize = size;
		xSize = inputs;
		ySize = outputs;
		generation = new Network[genSize];
		for (int i = 0; i<genSize; i++){
			generation[i] = new Network(xSize, ySize);
			addToSpecies(generation[i]);
		}
		for(int j = 0; j<speciesList.size(); j++) {
			speciesList.get(j).chooseRepresentative();
		}
		now = LocalDateTime.now();
		new File(dtf.format(now)).mkdirs();
		
		
	}

	
	public int getGenome(){
		return index;
	}
	
	public int getGeneration(){
		return genNo;
	}
	
	
	public Network getNext(){
		index++;
		if (index < genSize){
			return generation[index];
		}
		//moves to next generation
		else {
			index = 0;
						
			repopulate();
			genNo++;
			return generation[index];
		}
	}
	
	public void mutate(){
		Random r = new Random();
		for (int i = 0; i<genSize; i++){
			double ran = r.nextDouble();
			
			generation[i].mutate(0.01);
			
			
			
			ran = r.nextDouble();
			if(ran > 0.9) {
				generation[i].mutateAddConnection();
			}
			
			ran = r.nextDouble();
			if(ran > 0.95) {
				generation[i].mutateAddNode();
			}
		}
	}
	
	public void repopulate(){
		
		sort(generation);
		
		double total = 0;
		for(int i = 0; i<genSize; i++){
			total += generation[i].getFitness();
		}
		double av = total/genSize;
		av = (double)Math.round(av * 100d) / 100d;
		double max = generation[genSize-1].getFitness();
		max = (double)Math.round(max * 100d) / 100d;
		double min = generation[0].getFitness();
		min = (double)Math.round(min * 100d) / 100d;
		double median = generation[genSize/2].getFitness();
		median = (double)Math.round(median * 100d) / 100d;
		
		
		String fileName = dtf.format(now) + "/" + "data.txt";

		//save data
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			
			out.write("Generation " +genNo + "	Av: "+Double.toString(av) + " 	 Median " + median + " 	Max: "+ max+ " 	Min: " + min);
			out.newLine();
			out.close();
		}
		catch(IOException e){
			System.out.println("OOPS");
		}
				
		saveNetwork(genSize-1);
		Network[] newGen = new Network[genSize];
		Network[] elite = new Network[genSize/10];
		
		for(int i = 0; i<elite.length; i++) {
			elite[i] = generation[genSize-1-i];
		}
		
		
		//choose representatives
		for(int j = 0; j<speciesList.size(); j++) {
			speciesList.get(j).chooseRepresentative();
		}
		
		//crossover reproduction
		
		for (int i = 0; i<genSize; i++){
			
			int par1 = genSize-1-(i%(int)(genSize/2)); 
			int par2;
			
			
			double inSpecies = r.nextDouble();
			
			//choose second parent from in same species
			if(inSpecies <0.99) {
				par2 = (r.nextInt(generation[par1].getSpecies().size()));
				//create new network and add to new Generation
				newGen[i] = crossover(generation[par1], generation[par1].getSpecies().getlist().get(par2));
				//put into species
				//try parents species first
				if(generation[par1].getSpecies().compare(newGen[i]) < threshold) {
					//add to species
					generation[par1].getSpecies().addToNext(newGen[i]);
				}
				else {
					addToSpeciesNext(newGen[i]);
				}
			}
			
			//choose second parent from out of species
			else {
				par2 = (r.nextInt(generation.length));
				newGen[i] = crossover(generation[par1], generation[par2]);
				//put into species
				//try parents species first
				if(generation[par1].getSpecies().compare(newGen[i]) < threshold) {
					//add to species
					generation[par1].getSpecies().addToNext(newGen[i]);
				}
				else if(generation[par2].getSpecies().compare(newGen[i]) < threshold) {
					//add to species
					generation[par2].getSpecies().addToNext(newGen[i]);
				}
				else {
					addToSpeciesNext(newGen[i]);
				}
			}
		}
		
		
		//asexual reproduction
		/*
		for(int i = 0; i<genSize; i++) {
			int par = genSize-1-i%(int)(genSize/3);
			newGen[i] = copy(generation[par]);
			//put into species
			//try parents species first
			if(generation[par].getSpecies().compare(newGen[i]) < 3.0) {
				//add to species
				generation[par].getSpecies().addToNext(newGen[i]);
			}
			else {
				addToSpeciesNext(newGen[i]);
			}
			
			
		}
		*/
		
		
		
		//promotes next to main list
		
		for(int i = 0; i<speciesList.size(); i++) {
			speciesList.get(i).promote();
		}
		
		
		generation = newGen;
		
		mutate();
		
		for(int i = 0; i < elite.length; i++) {
			generation[genSize-1-i] = copy(elite[i]);
			addToSpecies(generation[genSize-1-i]);
		}
		
		clearEmptySpecies();
		
		//check if any new species are cyclical
		//if so create new random Network
		for(int i = 0; i<genSize; i++) {
			if(generation[i].hasCycle()) {
				generation[i] = new Network(xSize, ySize);
				addToSpecies(generation[i]);
			}
		}
		
		
		
	}
	
	

	private Network crossover(Network p1, Network p2){
		Network newNetwork = new Network(xSize, ySize);
		Connection[] newGenome = new Connection[0];
		boolean cont = true;
		int ind1 = 0, ind2 = 0;

		while(cont) {
			
			//if first but not second index is at the end
			if(ind1==p1.getGenomeSize() && ind2!=p2.getGenomeSize()) {
				Connection[] temp = new Connection[newGenome.length+1];
				for(int i = 0; i<newGenome.length; i++) {
					temp[i] = newGenome[i];
				}
				newGenome = temp;
				newGenome[newGenome.length-1] = p2.getGenome()[ind2];
				ind2++;
			}
			//if the second but not first is at the end
			else if(ind1!=p1.getGenomeSize() && ind2==p2.getGenomeSize()){
				Connection[] temp = new Connection[newGenome.length+1];
				for(int i = 0; i<newGenome.length; i++) {
					temp[i] = newGenome[i];
				}
				newGenome = temp;
				newGenome[newGenome.length-1] = p1.getGenome()[ind1];
				ind1++;
			}
			//if both indexes are at the end
			else if(ind1 == p1.getGenomeSize() && ind2 == p2.getGenomeSize()) {
				cont = false;
			}
			
			//if both indexes are at the end
			else if(p1.getGenome()[ind1].getInnovationNo() == p2.getGenome()[ind2].getInnovationNo()){
				//choose one randomly
				int rand = r.nextInt(2);
				if(rand ==0) {
					Connection[] temp = new Connection[newGenome.length+1];
					for(int i = 0; i<newGenome.length; i++) {
						temp[i] = newGenome[i];
					}
					newGenome = temp;
					newGenome[newGenome.length-1] = p1.getGenome()[ind1];
				}
				else {
					Connection[] temp = new Connection[newGenome.length+1];
					for(int i = 0; i<newGenome.length; i++) {
						temp[i] = newGenome[i];
					}
					newGenome = temp;
					newGenome[newGenome.length-1] = p2.getGenome()[ind2];
				}
				
				ind1++;
				ind2++;
			}
			else if(p1.getGenome()[ind1].getInnovationNo() > p2.getGenome()[ind2].getInnovationNo()){
				Connection[] temp = new Connection[newGenome.length+1];
				for(int i = 0; i<newGenome.length; i++) {
					temp[i] = newGenome[i];
				}
				newGenome = temp;
				newGenome[newGenome.length-1] = p2.getGenome()[ind2];
				ind2++;
			}
			else if(p1.getGenome()[ind1].getInnovationNo() < p2.getGenome()[ind2].getInnovationNo()){
				Connection[] temp = new Connection[newGenome.length+1];
				for(int i = 0; i<newGenome.length; i++) {
					temp[i] = newGenome[i];
				}
				newGenome = temp;
				newGenome[newGenome.length-1] = p1.getGenome()[ind1];
				ind1++;
			}
			else {
				cont  = false;
			}
			
		}
		
		
		//fix genome to have new Nodes with the same innovation numbers
		
		Node[] x = new Node[0];
		Node[] y = new Node[0];
		Node[] all = new Node[0];
		for(int i = 0; i<newGenome.length; i++) {
			Node n1 = new Node(false, false, newGenome[i].getFrom().getInnovationNo());
			Node n2 = new Node(false, false, newGenome[i].getTo().getInnovationNo());
			//check from node

			//check if it is already added
			boolean inAll = false;
			for(int j = 0; j<all.length; j++) {
				if(newGenome[i].getFrom().getInnovationNo() == all[j].getInnovationNo()) {
					inAll = true;
					n1 = all[j];
				}
			}
			if(inAll == false) {
				Node[] temp = new Node[all.length+1];
				for(int j = 0; j<all.length; j++) {
					temp[j] = all[j];
				}
				all = temp;
				all[all.length-1] = n1;
				
				
				if(newGenome[i].getFrom().isInput()) {
					temp = new Node[x.length+1];
					for(int j = 0; j<x.length; j++) {
						temp[j] = x[j];
					}
					x = temp;
					x[x.length-1] = n1;
					n1.setInput(true);
				}
				
				if(newGenome[i].getFrom().isOutput()) {
					temp = new Node[y.length+1];
					for(int j = 0; j<y.length; j++) {
						temp[j] = y[j];
					}
					y = temp;
					y[y.length-1] = n1;
					n1.setOutput(false);
				}
			}
				
				
			//check to node

			//check if it is already added
			inAll = false;
			for(int j = 0; j<all.length; j++) {
				if(newGenome[i].getTo().getInnovationNo() == all[j].getInnovationNo()) {
					inAll = true;
					n2 = all[j];
				}
			}
			if(inAll == false) {
				Node[] temp = new Node[all.length+1];
				for(int j = 0; j<all.length; j++) {
					temp[j] = all[j];
				}
				all = temp;
				all[all.length-1] = n2;
				
				
				if(newGenome[i].getTo().isInput()) {
					temp = new Node[x.length+1];
					for(int j = 0; j<x.length; j++) {
						temp[j] = x[j];
					}
					x = temp;
					x[x.length-1] = n2;
					n2.setInput(true);
				}
				
				if(newGenome[i].getTo().isOutput()) {
					temp = new Node[y.length+1];
					for(int j = 0; j<y.length; j++) {
						temp[j] = y[j];
					}
					y = temp;
					y[y.length-1] = n2;
					n2.setOutput(true);
				}
			}
			Connection old = newGenome[i];
			newGenome[i] = new Connection(n1, n2, newGenome[i].getInnovationNo());
			newGenome[i].setWeight(old.getWeight());
			if(!old.isEnabled()) {
				newGenome[i].disable();
			}
			
		}
		newNetwork.setIn(x);
		newNetwork.setOut(y);
		newNetwork.setNodes(all);
		newNetwork.setGenome(newGenome);
		
		
		
		return newNetwork;
	}
	
	
	private Network copy(Network n) {
		Network newNetwork = new Network(xSize, ySize);
		Connection[] newGenome = n.getGenome();
		
		for(int i = 0; i<newGenome.length; i++) {
			Connection c = newGenome[i];
			Connection newC = new Connection(c.getFrom(), c.getTo(), c.getInnovationNo());
			newC.setWeight(c.getWeight());

			newGenome[i] = newC;
		}
		
		//fix genome to have new Nodes with the same innovation numbers
		
				Node[] x = new Node[0];
				Node[] y = new Node[0];
				Node[] all = new Node[0];
				for(int i = 0; i<newGenome.length; i++) {
					Node n1 = new Node(false, false, newGenome[i].getFrom().getInnovationNo());
					Node n2 = new Node(false, false, newGenome[i].getTo().getInnovationNo());
					//check from node

					//check if it is already added
					boolean inAll = false;
					for(int j = 0; j<all.length; j++) {
						if(newGenome[i].getFrom().getInnovationNo() == all[j].getInnovationNo()) {
							inAll = true;
							n1 = all[j];
						}
					}
					
					if(inAll == false) {
						Node[] temp = new Node[all.length+1];
						for(int j = 0; j<all.length; j++) {
							temp[j] = all[j];
						}
						all = temp;
						all[all.length-1] = n1;
						
						
						if(newGenome[i].getFrom().isInput()) {
							temp = new Node[x.length+1];
							for(int j = 0; j<x.length; j++) {
								temp[j] = x[j];
							}
							x = temp;
							x[x.length-1] = n1;
							n1.setInput(true);
						}
						
						if(newGenome[i].getFrom().isOutput()) {
							temp = new Node[y.length+1];
							for(int j = 0; j<y.length; j++) {
								temp[j] = y[j];
							}
							y = temp;
							y[y.length-1] = n1;
							n1.setOutput(false);
						}
					}
						
						
					//check to node

					//check if it is already added
					inAll = false;
					for(int j = 0; j<all.length; j++) {
						if(newGenome[i].getTo().getInnovationNo() == all[j].getInnovationNo()) {
							inAll = true;
							n2 = all[j];
						}
					}
					if(inAll == false) {
						Node[] temp = new Node[all.length+1];
						for(int j = 0; j<all.length; j++) {
							temp[j] = all[j];
						}
						all = temp;
						all[all.length-1] = n2;
						
						
						if(newGenome[i].getTo().isInput()) {
							temp = new Node[x.length+1];
							for(int j = 0; j<x.length; j++) {
								temp[j] = x[j];
							}
							x = temp;
							x[x.length-1] = n2;
							n2.setInput(true);
						}
						
						if(newGenome[i].getTo().isOutput()) {
							temp = new Node[y.length+1];
							for(int j = 0; j<y.length; j++) {
								temp[j] = y[j];
							}
							y = temp;
							y[y.length-1] = n2;
							n2.setOutput(true);
						}
					}
					Connection old = newGenome[i];
					newGenome[i] = new Connection(n1, n2, newGenome[i].getInnovationNo());
					newGenome[i].setWeight(old.getWeight());
					
				}
				newNetwork.setIn(x);
				newNetwork.setOut(y);
				newNetwork.setNodes(all);
				newNetwork.setGenome(newGenome);
		
		newNetwork.setName(n.getName());
		return newNetwork;
	}
	
	
	//puts it in the first avaliable species
	private void addToSpeciesNext(Network n) {
		for(int i = 0; i<speciesList.size(); i++) {
			if(speciesList.get(i).compare(n) < threshold) {
				//add to species
				speciesList.get(i).addToNext(n);
				return;
			}
		}
		//otherwise create new species
		Species s = new Species();
		s.addToNext(n);
		speciesList.add(s);
	}
	
	private void addToSpecies(Network n) {
		for(int i = 0; i<speciesList.size(); i++) {
			if(speciesList.get(i).compare(n) < threshold) {
				//add to species
				speciesList.get(i).add(n);
				return;
			}
		}
		//otherwise create new species
		Species s = new Species();
		s.add(n);
		speciesList.add(s);
	}
	
	
	
	public void sort(Network arr[])
    {
        int n = arr.length;
 
        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);
 
        // One by one extract an element from heap
        for (int i=n-1; i>=0; i--)
        {
            // Move current root to end
            Network temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
 
            // call max heapify on the reduced heap
            heapify(arr, i, 0);
        }
    }
 
   
    void heapify(Network arr[], int n, int i)
    {
        int largest = i;  // Initialize largest as root
        int l = 2*i + 1;  // left = 2*i + 1
        int r = 2*i + 2;  // right = 2*i + 2
 
        // If left child is larger than root
        if (l < n && arr[l].getFitness() > arr[largest].getFitness())
            largest = l;
 
        // If right child is larger than largest so far
        if (r < n && arr[r].getFitness() > arr[largest].getFitness())
            largest = r;
 
        // If largest is not root
        if (largest != i)
        {
            Network swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
 
            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }
	
    
    public void saveNetwork(int n) {
    		
    	 String file = dtf.format(now) + "/" + "generation_" + genNo + ".txt";
    		try {
    			BufferedWriter out = new BufferedWriter(new FileWriter(file));
    			Connection[] connections = generation[n].getGenome();
    			Node[] nodes = generation[n].getNodes();
    			//save nodes
    			out.write("Nodes:");
    			out.newLine();
    			for(int i = 0; i<nodes.length; i++) {
    				String nodeStr = Integer.toString(nodes[i].getInnovationNo());
    				out.write(nodeStr);
    				out.newLine();
    			}
    			
    			out.write("Connections:");
    			out.newLine();
    			for(int i = 0; i<connections.length; i++) {
    				out.write("{"+connections[i].getFrom().getInnovationNo() + ","
    						+ connections[i].getTo().getInnovationNo() + "," + connections[i].getWeight() + "," + connections[i].isEnabled() + "}");
    				out.newLine();
    				
    				if(!connections[i].isEnabled()) {
    					//pause;
    					System.out.print("");
    				}
    			}
    			
    			
    			
    			
    			out.close();
    		}
    		catch( IOException e) {
    			System.out.println("This should not have happened!");
    		}
    }
    
    public int getGenSize() {
    		return genSize;
    }
    
    
    private void clearEmptySpecies() {
    	for(int i = 0; i<speciesList.size(); i++) {
    		if(speciesList.get(i).size() == 0) {
    			speciesList.remove(i);
    			i--;
    		}
    	}
    }
    
    
	
}
