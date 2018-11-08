import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Tile extends JButton{

	private static final long serialVersionUID = -4923921927921185084L;
	boolean mine;
	int mines;
	private Tile up;
	private Tile down;
	private Tile left;
	private Tile right;
	private boolean flagged;
	private boolean revealed;
	private boolean buttonEnabled = true;
	
	Tile(){
		super();
		addMouseListener(mouseListener);
		mine = false;
		flagged = false;
		revealed = false;
		this.setPreferredSize(new Dimension(30,30));
		this.setContentAreaFilled(true);
		//this.setOpaque(true);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setIcon(new ImageIcon("Images/Unrevealed.png"));
	}
	
	public void flag(){
		if (isFlagged()){
			flagged = false;
			//image icon for blank
			this.setIcon(new ImageIcon("Images/Unrevealed.png"));
		}
		else {
			flagged = true;
			this.setIcon(new ImageIcon("Images/Flag.png"));
		}
		
		
	}
	
	public void reveal(){
		revealed = true;
		int value = getValue();
		this.setEnabled(false);
		if (value > 0){
			if (value == 1){
				this.setIcon(new ImageIcon("Images/1.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 2){
				this.setIcon(new ImageIcon("Images/2.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 3){
				this.setIcon(new ImageIcon("Images/3.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 4){
				this.setIcon(new ImageIcon("Images/4.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 5){
				this.setIcon(new ImageIcon("Images/5.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 6){
				this.setIcon(new ImageIcon("Images/6.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 7){
				this.setIcon(new ImageIcon("Images/7.png"));
				this.setDisabledIcon(getIcon());
			}
			
			else if (value == 8){
				this.setIcon(new ImageIcon("Images/8.png"));
				this.setDisabledIcon(getIcon());
			}
		}
		
		
		else{
			this.setIcon(new ImageIcon("Images/Revealed.png"));
			this.setDisabledIcon(getIcon());
		}

		//Recursively checks all tiles that have not already been revealed if the value of current tile is 0
		
		if(value == 0){
			if(getUp() != null && !getUp().isRevealed() && !getUp().isFlagged()){
				getUp().reveal();
			}
			
			if(getRight() != null && !getRight().isRevealed() && !getRight().isFlagged()){
				getRight().reveal();
			}
			
			if(getDown() != null && !getDown().isRevealed() && !getDown().isFlagged()){
				getDown().reveal();
			}
			
			if(getLeft() != null && !getLeft().isRevealed() && !getLeft().isFlagged()){
				getLeft().reveal();
			}
			
			if(getUp() != null && getRight() != null && !getUp().getRight().isRevealed() && !getUp().getRight().isFlagged()){
				getUp().getRight().reveal();
			}
			
			if(getDown() != null && getRight() != null && !getDown().getRight().isRevealed() && !getDown().getRight().isFlagged()){
				getDown().getRight().reveal();
			}
			
			if(getDown() != null && getLeft() != null && !getDown().getLeft().isRevealed() && !getDown().getLeft().isFlagged()){
				getDown().getLeft().reveal();
			}
			
			if(getUp() != null && getLeft() != null && !getUp().getLeft().isRevealed() && !getUp().getLeft().isFlagged()){
				getUp().getLeft().reveal();
			}
		}
		
	}
		
	//resets mine to unrevealed
	public void reset() {
		flagged = false;
		revealed = false;
		this.setEnabled(true);
		this.setIcon(new ImageIcon("Images/Unrevealed.png"));
		
	}
	
	
	public void setMine(){
		mine = true;
	}
	
	public void setLeft(Tile t){
		left = t;
	}
	
	public void setRight(Tile t){
		right = t;
	}
	
	public void setUp(Tile t){
		up = t;
	}
	
	public void setDown(Tile t){
		down = t;
	}
	
	public int getValue(){
		mines = 0;
		//top
		if (getUp() != null){
			if (getUp().isMine() == true){
				mines++;
			}
		}
		
		//top right **
		if (getUp() != null && getRight() != null){
			if (getUp().getRight().isMine() == true){
				mines++;
			}
		}
		
		//right
		if (getRight() != null){
			if (getRight().isMine() == true){
				mines++;
			}
		}
		
		//down right **
		if (getDown() != null && getRight() != null){
			if (getDown().getRight().isMine() == true){
				mines++;
			}
		}
		//down
		
		if (getDown() != null){
			if (getDown().isMine() == true){
				mines++;
			}
		}
		
		//down left
		if (getDown() != null && getLeft() != null){
			if (getDown().getLeft().isMine() == true){
				mines++;
			}
		}
		
		//left
		if (getLeft() != null){
			if (getLeft().isMine() == true){
				mines++;
			}
		}
		
		//up left
		if (getUp() != null && getLeft() != null){
			if (getUp().getLeft().isMine() == true){
				mines++;
			}
		}
		
		return mines;
	}
	
	
	public boolean isMine(){
		return mine;
	}
	
	public boolean isRevealed(){
		return revealed;
	}
	
	public boolean isFlagged(){
		return flagged;
	}
	
	public Tile getUp(){
		return up;
	}
	
	public Tile getDown(){
		return down;
	}
	
	public Tile getLeft(){
		return left;
	}
	
	public Tile getRight(){
		return right;
	}
	
	public void disableButton(){
		buttonEnabled = false;
	}
	
	
	public int getInput() {

		if(this.isRevealed()){
			 return this.getValue();
		}
		else{
			if (this.isFlagged()){
				return -10;
			}
			else{
				return -1;
			}
		}
	}
	
	public void lClick(){
		//if it is not revealed or flagged it can be clicked
		if (!isRevealed() && !isFlagged()){
			if (isMine()){
				setIcon(new ImageIcon("Images/Mine.png"));
				//redundant hopefully
				revealed = true;
			}
			else
				reveal();
		}
	}

	
	MouseListener mouseListener = new MouseListener(){
		//checks whether right or left click
		@Override
		public void mouseClicked(MouseEvent e) {
		
		}
	
	
	
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (buttonEnabled){
			if (SwingUtilities.isLeftMouseButton(e)){
				lClick();
			}
		
			else if (SwingUtilities.isRightMouseButton(e)){
				if (!isRevealed()){
					flag();
				}
			}		
		}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}
	
	};
}



