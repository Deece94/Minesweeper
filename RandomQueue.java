import java.util.Random;

public class RandomQueue {

	int size;
	QNode head;
	Random r = new Random();
	double classifier;
	
	RandomQueue(){
		size = 0;
		classifier = 0;
	}
	
	public void setClassifier(double c){
		classifier = c;
	}
	
	public void push(Tile newData){
		QNode temp = new QNode();
		temp.setData(newData);
		if (head == null){
			head = temp;
			head.setNext(head);
			head.setPrev(head);
			size++;
		}
		else{
			temp.setNext(head);
			temp.setPrev(head.getPrev());
			head.getPrev().setNext(temp);
			head.setPrev(temp);
			size++;
		}
		
	}
	
	public Tile pop(){
		QNode temp = head;
		if (size == 1){
			head = null;
			size = 0;
			return temp.getData();
		}
		else{
			
			int rnd = r.nextInt(size);
			QNode index = head;
			for (int i = 0; i<rnd; i++){
				index = index.getNext();
			}
			return index.getData();
		}
	
	}
	
	public void reset(){
		head = null;
		size = 0;
	}
	
	public double getClassifier(){
		return classifier;
	}
	
	
	public class QNode {
		private QNode next;
		private QNode prev;
		private Tile data;
		
		QNode(){
			
		}
		
		public void setNext(QNode node){
			next = node;
		}
		
		public void setPrev(QNode node){
			prev = node;
		}
		
		public void setData(Tile d){
			data = d;
		}
		
		public QNode getNext(){
			return next;
		}
		
		public QNode getPrev(){
			return prev;
		}
		
		public Tile getData(){
			return data;
		}
	}
}
