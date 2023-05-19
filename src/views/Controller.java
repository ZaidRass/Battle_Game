package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import engine.*;
import exceptions.GameActionException;
import model.abilities.AreaOfEffect;
import model.world.*;

public class Controller implements ActionListener{
	
	private Game game;
	private Player player1;
	private Player player2;
	private View view;
	
	public Controller() throws IOException{
		String firstPlayer;
		String secondPlayer;
		
		do{
			firstPlayer = JOptionPane.showInputDialog(view, "Player one name: ");
			if(firstPlayer.equals("")){
				JOptionPane optionPane = new JOptionPane("You can't leave it blank", JOptionPane.ERROR_MESSAGE);    
				JDialog dialog = optionPane.createDialog("ErrorMsg");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
			}
		}while(firstPlayer.equals(""));
		
		player1 = new Player(firstPlayer);
		
		do{
			secondPlayer = JOptionPane.showInputDialog(view, "Player two name: ");
			if(secondPlayer.equals("")){
				JOptionPane optionPane = new JOptionPane("You can't leave it blank", JOptionPane.ERROR_MESSAGE);    
				JDialog dialog = optionPane.createDialog("ErrorMsg");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
			}
		}while(secondPlayer.equals(""));
		
		player2 = new Player(secondPlayer);
		Game.loadAbilities("Abilities.csv");
		Game.loadChampions("Champions.csv");
		view = new View(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		String tmp = e.getActionCommand();
		String[] codes = tmp.split(",");
		
		switch(codes[0]){
			case "stats":
				String[] stats = Arrays.copyOfRange(codes, 1, codes.length);
				view.showStats(stats);
				break;
			case "select":
				try{
					if(codes[1].equals("cancel")){
						view.getStats().dispose();
						view.getAbilities().dispose();
					}
					else{
						String name = codes[2];
						for(int i = 0; i < Game.getAvailableChampions().size(); i++){
							Champion c = Game.getAvailableChampions().get(i);
							if(c.getName().equals(name)){
								if(codes[1].equals("1")){
									if(player1.getTeam().size() == 3){
										throw new Exception();
									}
									player1.getTeam().add(c);
								}
								else{
									if(player2.getTeam().size() == 3){
										throw new Exception();
									}
									player2.getTeam().add(c);
								}
								view.getStats().dispose();
								view.getAbilities().dispose();
								Game.getAvailableChampions().remove(c);
						
								view.getSelections();
								i--;
								break;
							}
						}
					}
				}catch(Exception ex){
					JOptionPane optionPane = new JOptionPane("Can't choose more than 3 champions on your team", JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("ErrorMsg");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
					view.getStats().dispose();
					view.getAbilities().dispose();
				}
			
				if(player1.getTeam().size() == 3 && player2.getTeam().size() == 3){
					String[] team = {"1", player1.getTeam().get(0).getName(), player1.getTeam().get(1).getName(), player1.getTeam().get(2).getName()};
					view.selectLeader(team);
				}
			
				break;
			
			case "Leader":
				if(codes[1].equals("1")){
					for(Champion c:player1.getTeam()){
						if(c.getName().equals(codes[2]))
							player1.setLeader(c);
					}
					String[] team = {"2", player2.getTeam().get(0).getName(), player2.getTeam().get(1).getName(), player2.getTeam().get(2).getName()};
					view.selectLeader(team);
				}
				
				else{
					for(Champion c:player2.getTeam()){
						if(c.getName().equals(codes[2])){
							player2.setLeader(c);
							break;
						}
					}
					game = new Game(player1, player2);
					view.remove(view.getSelection());
					view.populateBoard(game.getBoard());
				}
				break;
				
				
			case "set":
				if(codes[1].equals("effects")){
					view.setShowEffects(true);
					view.setShowStats(false);
				}
				else if(codes[1].equals("stats")){
					view.setShowStats(true);
					view.setShowEffects(false);
				}
				view.populateBoard(this.getGame().getBoard());
				break;
				
			case "remove":
				if(codes[1].equals("effects")){
					view.setShowEffects(false);
				}
				else if(codes[1].equals("stats")){
					view.setShowStats(false);
				}
				view.populateBoard(this.getGame().getBoard());
				break;
				
			case "move":
				if(codes[1].equals("select")){
					view.selectMoveDirection();
				}
				else{
					try{
					view.getMoveDirection().dispose();
					if(codes[1].equals("up"))
						game.move(Direction.UP);
					else if(codes[1].equals("right"))
						game.move(Direction.RIGHT);
					else if(codes[1].equals("left"))
						game.move(Direction.LEFT);
					else if(codes[1].equals("down"))
						game.move(Direction.DOWN);
					
					view.populateBoard(game.getBoard());
					}catch(GameActionException ex){
						JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
						JDialog dialog = optionPane.createDialog("ErrorMsg");
						dialog.setAlwaysOnTop(true);
						dialog.setVisible(true);
					}
				}
				break;
			
			case "attack":
				if(codes[1].equals("select")){
					view.selectAttackDirection();
				}
				else{
					try{
					view.getAttackDirection().dispose();
					if(codes[1].equals("up"))
						game.attack(Direction.UP);
					else if(codes[1].equals("right"))
						game.attack(Direction.RIGHT);
					else if(codes[1].equals("left"))
						game.attack(Direction.LEFT);
					else if(codes[1].equals("down"))
						game.attack(Direction.DOWN);
					
					if(game.checkGameOver() != null)
						view.gameOver(game.checkGameOver());
					else
						view.populateBoard(game.getBoard());
					}catch(GameActionException ex){
						JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
						JDialog dialog = optionPane.createDialog("ErrorMsg");
						dialog.setAlwaysOnTop(true);
						dialog.setVisible(true);
					}
				}
				break;
				
			case "leaderAbility":
				try{
					game.useLeaderAbility();
					if(game.checkGameOver() != null)
						view.gameOver(game.checkGameOver());
					else
						view.populateBoard(game.getBoard());
				}catch(GameActionException ex){
					JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("ErrorMsg");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
				break;
				
			case "endTurn":
				game.endTurn();
				view.populateBoard(game.getBoard());
				break;
			
			case "useAbility":
				try{
					if(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])).getCastArea() == AreaOfEffect.DIRECTIONAL){
						view.selectAbilityDirection(codes[1]);
					}
					else if(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])).getCastArea() == AreaOfEffect.SINGLETARGET){
						view.selectAbilityTarget(codes[1]);
					}
					else{
						game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])));
						view.populateBoard(game.getBoard());
					}
					if(game.checkGameOver() != null)
						view.gameOver(game.checkGameOver());
					else
						view.populateBoard(game.getBoard());
					
				}catch(GameActionException | NumberFormatException | CloneNotSupportedException ex){
					JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("ErrorMsg");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
				break;
				
			case "directAbility":
				try{
					view.getAbilityDirection().dispose();
					if(codes[2].equals("up"))
						game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])), Direction.UP);
					else if(codes[2].equals("right"))
						game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])), Direction.RIGHT);
					else if(codes[2].equals("left"))
						game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])), Direction.LEFT);
					else if(codes[2].equals("down"))
						game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])), Direction.DOWN);
					
					if(game.checkGameOver() != null)
						view.gameOver(game.checkGameOver());
					else
						view.populateBoard(game.getBoard());
				}catch(GameActionException | NumberFormatException | CloneNotSupportedException ex){
					JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("ErrorMsg");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
				break;
				
			case "abilityTarget":
				try{
					view.getAbilityTargets().dispose();
					game.castAbility(game.getCurrentChampion().getAbilities().get(Integer.parseInt(codes[1])), Integer.parseInt(codes[2]), Integer.parseInt(codes[3]));
					if(game.checkGameOver() != null)
						view.gameOver(game.checkGameOver());
					else
						view.populateBoard(game.getBoard());
				}catch(GameActionException | NumberFormatException | CloneNotSupportedException ex){
					JOptionPane optionPane = new JOptionPane(ex.getMessage(), JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("ErrorMsg");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
		}
	}
	
	public Game getGame() {
		return game;
	}

	public View getView() {
		return view;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public static void main(String[] args) throws IOException{
		try {
			new Controller();
		} catch (Exception e) {
			System.out.println("kjahsd");
		}
	}
}
