import java.awt.*;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;


public class Board extends JPanel{


	private static final long serialVersionUID = 365996970767090210L;
	private Tile[][] board;
	private int mines, rows, columns;
	private JPanel head = new JPanel();
	private JPanel body = new JPanel();
	
	Board(int c, int r, int m){
		mines = m;
		rows = r;
		columns = c;
		
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		head.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		head.add(new JLabel("Mines: " + mines));
		this.add(head);
		
		
		body.setLayout(new GridLayout(rows, columns));
		this.add(body);
		
		board = new Tile[rows][columns];
		initialiseBoard(mines);
		
	}
	
	private void initialiseBoard(int mines){
		Random r = new Random();
		
		
		//initialise whole board first with blank tiles
		for(int i = 0; i<rows; i++){
			for(int j = 0; j<columns; j++){
				//initialise Tile button
				Tile T = new Tile();
				T.setSize(15, 15);
				body.add(T);
				
				board[i][j] = T;
			}
		}
		
		//sets pointers between tiles
		for (int i = 0; i<rows;i++){
			for (int j = 0; j<columns; j++){
				
				//set the tiles neighbour tiles
				//left
				if (i != 0){
					board[i][j].setLeft(board[i-1][j]);
				}
				//right
				if (i < rows - 1){
					board[i][j].setRight(board[i+1][j]);
				}
				//up
				if (j != 0){
					board[i][j].setUp(board[i][j-1]);
				}
				//down
				if (j < columns - 1){
					board[i][j].setDown(board[i][j+1]);
				}
			}
		}
		
		//adds mines into random positions
		for(int i = 0; i<mines; i++){
					
			//chooses random column and row for mine
			int r1 = r.nextInt(rows);
			int r2 = r.nextInt(columns);
			
			//if there is not already a mine in place add one
			if (!board[r1][r2].isMine()){
				board[r1][r2].setMine();
			}
			
			//otherwise repeat
			else i--;
		}
	}
	
	public void reset() {
		for(int i = 0; i<rows; i++){
			for(int j = 0; j<columns; j++){
				board[i][j].reset();
			}
		}
	}
	
	//Tile is chosen to be activated
	public void activateTile(int c, int r){
		if (board[c][r].isMine()){
			//player chose a mine
		}
		
		else{
			//player chose an empty square
			board[c][r].reveal();
		}
	}
	
	//same as previous but input is Tile
	public void activateTile(Tile t){
		if (t.isMine()){
			//player chose a mine
		}
		
		else{
			//player chose an empty square
			t.reveal();
		}
	}
	
	public Tile getTile(int r, int c) {
		return board[r][c];
	}
	
	//returns the inputs of the surrounding tiles in a radius of radius
	public int[] getSurrounding(int c, int r, int radius) {
		int[] surrounding = new int[(radius*2+1)*(radius*2+1)-1];
		int index = 0;
		for(int i = r-radius; i<=r+radius; i++) {
			for(int j = c-radius; j <=c+radius; j++) {
				if(i==r && j==c){
					//skip over tile being looked at
					index--;
				}
				else if(i<0 || i>rows || j<0 || j>columns) {
					surrounding[index] = -2;
				}
				else {
					surrounding[index] = evaluateTile(board[r][c]);
				}
				index++;
			}
		}
		return surrounding;
	}
	
	private int evaluateTile(Tile t) {

		if(t.isRevealed()){//hs been revealed and gives minecount
			 return t.getValue();
		}
		else if (t.isFlagged()){//has been flagged
			return -10;
		}
		else{//is unrevealed
			return -1;
		}
	}
	
	
	
	public Tile[] getBoardInputs() {
		int index = 0;
		Tile[] list = new Tile[rows*columns];
		for (int i = 0; i<rows; i++ ) {
			for (int j = 0; j< columns; j++) {
				list[index] = board[i][j];
				index++;
			}
		}
		return list;
	}


	public boolean lose(){
		int flag = 0, unrevealed = 0;
		for (int i = 0; i<rows; i++){
			for (int j = 0; j<columns; j++){
				if(board[i][j].isFlagged()){
					flag++;
				}
				if(!board[i][j].isRevealed()) {
					unrevealed++;
				}
				
				
				if(flag >= mines*2){
					return true;
				}
				if (board[i][j].isMine() && board[i][j].isRevealed()){
					return true;
				}
			}
		}
		if(flag == unrevealed) {
			return true;
		}
		return false;
	}
	
	public boolean win(){
		int revealed = 0;
		for (int i = 0; i<rows; i++){
			for (int j = 0; j<columns; j++){
				if (board[i][j].isRevealed()){
					revealed++;
				}
			}
		}
		if (revealed == rows*columns-mines){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void gameover(){
		//invalidates all tiles
		for (int i = 0; i<rows; i++){
			for (int j = 0; j<columns; j++){
				if (board[i][j].isMine()){
					board[i][j].setIcon(new ImageIcon("Images/Mine.png"));
				}
				board[i][j].disableButton();
			}
		}
	}
}

	