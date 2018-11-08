
public class Singleton {
	private static int ID = 0;
	private static int nodeID = 0;
	private Singleton(){}
	
	private static Singleton singleObject = null;
	
	public static Singleton getInstance(){
		if (singleObject == null){
			singleObject = new Singleton();
		}
		return singleObject;
	}
	
	public static int getID(){
		ID++;
		return ID;
	}
	
	public static int getNodeID(){
		nodeID++;
		return nodeID;
	}
}
