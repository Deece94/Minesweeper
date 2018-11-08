import javax.swing.*;
import java.util.*;

/*TODO: 
 * Add win to game
 */



public class NEATMinesweeper{
	int mines = 10;
	int columns = 9;
	int rows = 9;
	int squareSize = 2;
	Scanner console = new Scanner(System.in);

	JFrame frame = new JFrame();

	GA AI24;
	
	
	public static void main(String[] args){
		
		NEATMinesweeper Mswpr = new NEATMinesweeper();
		Mswpr.run();
	}
	
	public void run(){
		
		int squares = (squareSize*2+1)*(squareSize*2+1);
		
		frame.setResizable(false);
		//exit on close
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
		
		//start();
		
		boolean flag = true;
		System.out.println("Minesweeper NEAT");
		do {
			System.out.println("Enter 1 for play or 2 for AI");
			String response = console.next();
			
			
			
			if(response.equals("2")) {
				AI24 = new GA(200, 2, squares-1, 1);
		
				while(AI24.getGeneration() <10000){
					GAGame(mines, columns, rows);
				}
				flag = false;
			}
			
			
			else if(response.equals("1")){
				flag = false;
				while(true) {
					game(mines,columns,rows);
				}
			}
		}
		while(flag == true);
		
	}
	
	
	public void game(int mines, int columns, int rows){
		
		Board board = new Board(columns, rows, mines);		
		frame.add(board);
		frame.pack();
		
		
		//tiles need to be constantly checked to see what has changed
		do{
			try {
				Thread.sleep(500);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		while (!board.lose() && !board.win());
		
		
		board.gameover();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame.remove(board);
	}
	
	
	
	//The AI learns to play minesweeper
	public void GAGame(int mines, int columns, int rows){
		
		
		
		
		//Tile[] list;
		Network tester = AI24.getNext();
		int games = 10;//the amount of generations that will train on the same minesweeper games
		//create new board
		Board[] board = new Board[5];
		for(int i = 0; i<5; i++) {
			board[i] = new Board(columns, rows, mines);
		}	
		
			
		
		
		

		//train the AI on the current board for a set amount of generatipons
		for( int n = 0; n < games; n++) {
			
				//run through the generation
			for(int k= 0; k< AI24.getGenSize(); k++) {
				System.out.println("Generation: "+AI24.getGeneration()+" Network: "+AI24.getGenome());
				int fit = 0;	
				for(int g = 0; g<5; g++) {
					
					frame.add(board[g]);
					frame.pack();
					
					//plays game of minesweeper
					while (!board[g].lose() && !board[g].win()){
						
						RandomQueue queue = new RandomQueue();
						queue.setClassifier(0-Double.MAX_VALUE);
						RandomQueue flagQueue = new RandomQueue();
						flagQueue.setClassifier(Double.MAX_VALUE);
						
						
						double score;
						
						//run through tiles and rate them
						for(int i = 0; i<rows; i++) {
							for(int j = 0; j<columns; j++) {
								if(!board[g].getTile(i,j).isRevealed()) {
									score = tester.run(board[g].getSurrounding(i, j, squareSize))[0];
									
									//if next is larger score than in list and is not flagged add to new list
									if (score > queue.getClassifier() && !board[g].getTile(i,j).isFlagged()){
										queue.reset();
										queue.setClassifier(score);
										queue.push(board[g].getTile(i,j));
									}
									else if (score == queue.getClassifier() && !board[g].getTile(i,j).isFlagged()){
										queue.push(board[g].getTile(i,j));					
									}
									
									if (score < flagQueue.getClassifier() && !board[g].getTile(i,j).isFlagged()){
										flagQueue.reset();
										flagQueue.setClassifier(score);
										flagQueue.push(board[g].getTile(i,j));
									}
									else if (score == flagQueue.getClassifier() && !board[g].getTile(i,j).isFlagged()){
										flagQueue.push(board[g].getTile(i,j));	
									}
								}
							}
						}
						
						
						//chooses the best scoring tile(or random best scoring if there are multiple)
						if(queue.getClassifier() < (Math.abs(flagQueue.getClassifier()))){
							Tile choose = flagQueue.pop();
							choose.flag();
							if(choose.isMine()){
								fit += 10;
							}
							else {
								fit -= 10;
							}
						}
						else{
							Tile choose = queue.pop();
							choose.lClick();
							if(!choose.isMine()){
								fit++;
							}
						}
						
					}
					if(board[g].win()) {
						fit += 100;
						System.out.println("Congratz");
					}
			
					
					board[g].gameover();
					board[g].reset();
					frame.remove(board[g]);
				}
				tester.setFitness(fit);
				tester = AI24.getNext();

			}

		}
		
	
	}
	
		
	public void start(){
		JPanel startPanel = new JPanel(new SpringLayout());
		startPanel.add(new JLabel("Mines: "));
		startPanel.add(new JButton("Play"));
		frame.add(startPanel);
		frame.pack();
		boolean loop = true;
		while(loop){
			
		}
	}
	
	public void XORTest() {
		int i1, i2;
		
		
		
		
	}
}
