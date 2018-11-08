import java.util.*;

public class Species {
	
	private List<Network> list = new ArrayList<Network>();
	private List<Network> next = new ArrayList<Network>();
	private Network representative;
	
	public Species() {
		
	}
	
	
	public void add(Network n) {
		list.add(n);
		n.setSpecies(this);
		if(representative == null) {
			representative = n;
		}
	}
	
	public void addToNext(Network n) {
		next.add(n);
		n.setSpecies(this);
		if(representative == null) {
			representative = n;
		}
	}
	
	public void promote() {
		
		List<Network> newList = new ArrayList<Network>();
		for(int i = 0; i<next.size(); i++) {
			newList.add(next.get(i));
		}
		list = newList;
		next.clear();
	}
	
	public double compare(Network n) {
		double distance = representative.compare(n);
		return distance;
	}
	
	public int size() {
		return list.size();
	}
	
	public void clear() {
		list = new ArrayList<Network>();
	}
	
	public void chooseRepresentative() {
		Random r = new Random();
		int i = r.nextInt(list.size());
		representative = list.get(i);
	}
	
	public List<Network> getlist(){
		return list;
	}
	
}
