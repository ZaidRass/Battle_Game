package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ImageIcon;

import model.abilities.*;
import model.effects.Effect;
import model.world.*;
import engine.*;

public class View extends JFrame{
	private ActionListener l;
	private JPanel main;
	private JFrame stats;
	private JFrame abilities;
	private JFrame moveDirection;
	private JFrame attackDirection;
	private JFrame abilityDirection;
	private JFrame abilityTargets;
	private JPanel selections;
	private boolean showStats;
	private boolean showEffects;
	
	public View(ActionListener l) throws IOException{
		this.l = l;
		
		setTitle("Marvel");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1960, 1080));
		setVisible(true);
		main = new JPanel();
		add(main);
		selections = new JPanel(new GridLayout(3, 5));
		add(selections);
		validate();
		showStats = false;
		showEffects = false;
		
		
		getSelections();
	}


	public JFrame getStats() {
		return stats;
	}

	public JFrame getAbilities() {
		return abilities;
	}
	
	public JPanel getSelection(){
		return selections;
	}
	
	public JFrame getMoveDirection() {
		return moveDirection;
	}

	public JFrame getAttackDirection() {
		return attackDirection;
	}

	public JFrame getAbilityDirection() {
		return abilityDirection;
	}

	public JFrame getAbilityTargets() {
		return abilityTargets;
	}

	public void setShowStats(boolean showStats) {
		this.showStats = showStats;
	}


	public void setShowEffects(boolean showEffects) {
		this.showEffects = showEffects;
	}

	public void populateBoard(Object[][] board){
		this.remove(this.main);
		main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));
		
		JPanel gameInfo = new JPanel();
		gameInfo.setLayout(new BoxLayout(gameInfo, BoxLayout.Y_AXIS));
		
		JPanel grid = new JPanel(new GridLayout(5, 5));
		grid.setSize(new Dimension(1400, 800));
		grid.setMaximumSize(new Dimension(1400, 800));
		
		for(int i = 4; i >= 0; i--){
			Object[] os = board[i];
			for(Object o:os){
				String stats = "<html>";
				
				if(o == null){
					grid.add(new JButton());
					continue;
				}
				
				if(o instanceof Cover){
					JButton cover = new JButton("" +((Cover) o).getCurrentHP());
					ImageIcon ii = new ImageIcon("Cover.png");
					cover.setIcon(ii);
					grid.add(cover);
					continue;
				}
				if(o instanceof Champion){
					if(!(this.showEffects || this.showStats)){
						if(((Controller)l).getGame().getFirstPlayer().getLeader().equals(o) || ((Controller)l).getGame().getSecondPlayer().getLeader().equals(o)){
							stats += "Leader<br>";
						}
					
						stats +=  ((Champion) o).getName();
						if(o instanceof Hero)
							stats += "<br>Type: Hero";

						else if(o instanceof Villain)
							stats += "<br>Type: Villain";
				
						else if(o instanceof AntiHero)
							stats += "<br>Type: AntiHero";
						}
					if(this.showStats)
						stats += "<br>HP: " +((Champion) o).getCurrentHP()+ "<br>Mana: " +((Champion) o).getMana()+ "<br>Speed: " 
								+((Champion) o).getSpeed()+ "<br>AP: " +((Champion) o).getMaxActionPointsPerTurn()+ "<br>Attack: " 
								+((Champion) o).getAttackDamage()+ "<br>Range: " +((Champion) o).getAttackRange();
					
					if(this.showEffects){
						for(Effect e:((Champion)o).getAppliedEffects()){
							stats += "<br>" + e;
						}
					}
					
					JButton champion = new JButton(stats);
					ImageIcon ii = new ImageIcon(((Champion) o).getName() + ".png");
					champion.setIcon(ii);
					grid.add(champion);
					continue;
				}
			}
		}
		grid.setVisible(true);
		
		JPanel firstPlayer = new JPanel(new FlowLayout());
		firstPlayer.add(new JLabel("<html>"+((Controller)l).getGame().getFirstPlayer().getName() + "<br>leader ability used: " +((Controller)l).getGame().isFirstLeaderAbilityUsed()+ "</html>"));
		firstPlayer.setMaximumSize(new Dimension(200, 100));
		firstPlayer.setBackground(Color.ORANGE);
		firstPlayer.setVisible(true);
		
		JPanel secondPlayer = new JPanel(new FlowLayout());
		secondPlayer.add(new JLabel("<html>"+((Controller)l).getGame().getSecondPlayer().getName() + "<br>leader ability used: " +((Controller)l).getGame().isSecondLeaderAbilityUsed()+ "</html>"));
		secondPlayer.setMaximumSize(new Dimension(200, 100));
		secondPlayer.setBackground(Color.CYAN);
		secondPlayer.setVisible(true);
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setMaximumSize(new Dimension(200, 900));
		JPanel turnOrder = new JPanel(new FlowLayout());
		String turns = "<html>Turn Order: <br>";
		
		for(int i = ((Controller)l).getGame().getTurnOrder().size() - 1; i >= 0; i--){
			turns += ((Champion)((Controller)l).getGame().getTurnOrder().getElements()[i]).getName()+ "<br>";
		}
		
		turns += "</html>";
		JLabel label = new JLabel(turns);
		label.setFont(new Font("", Font.PLAIN, 20));
		
		turnOrder.add(label);
		turnOrder.setMaximumSize(new Dimension(420, 500));
		turnOrder.setVisible(true);
		
		JButton effects = new JButton();
		JButton stats = new JButton();
		
		if(!showEffects){
			effects= new JButton("Show Effects");
			effects.setPreferredSize(new Dimension(200, 100));
			effects.addActionListener(l);
			effects.setActionCommand("set,effects");
		}else{
			effects= new JButton("remove Effects");
			effects.setPreferredSize(new Dimension(200, 100));
			effects.addActionListener(l);
			effects.setActionCommand("remove,effects");
		}
		
		if(!showStats){
			stats = new JButton("Show Stats");
			stats.setPreferredSize(new Dimension(200, 100));
			stats.addActionListener(l);
			stats.setActionCommand("set,stats");
		}else{
			stats= new JButton("remove stats");
			stats.setPreferredSize(new Dimension(200, 100));
			stats.addActionListener(l);
			stats.setActionCommand("remove,stats");
		}
		
		left.add(turnOrder);
		left.add(effects);
		left.add(stats);

		JPanel actions = new JPanel();
		actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
		
		JPanel championInformation = new JPanel(new FlowLayout());
		Champion c = ((Controller)l).getGame().getCurrentChampion();
		String championInfo = "<html>Current Champion<br>Name: " +c.getName();
		
		if(c instanceof Hero)
			championInfo += "<br>Type: Hero";
		else if(c instanceof Villain)
			championInfo += "<br>Type: Villain";
		else
			championInfo += "<br>Type: AntiHero";
		
		championInfo += "<br>HP: " +c.getCurrentHP()+ "<br>Mana: " +c.getMana()+ "<br>AP: " +c.getCurrentActionPoints()+ "<br>Attack Damage: " +c.getAttackDamage()+ "<br>Attack Range: " +c.getAttackRange()+ "</html>";
		championInformation.add(new JLabel(championInfo));
		championInformation.setMaximumSize(new Dimension(420, 150));

		championInformation.setVisible(true);
		
		JPanel abilityInformation = new JPanel(new GridLayout(1, 3));
		abilityInformation.setMaximumSize(new Dimension(420, 250));
		String abilityInfo;
		for(int i = 0; i < c.getAbilities().size(); i++){
			Ability a = c.getAbilities().get(i);
			abilityInfo = "<html>Ability " +(i+1)+ "<br>Name: " +a.getName();
			
			if(a instanceof CrowdControlAbility){
				abilityInfo += "<br>Type: Crowd Control<br>AOE: " +a.getCastArea()+ "<br>Range: " +a.getCastRange()+ "<br>Mana Cost: " +a.getManaCost()+ 
								"<br>Actions: " +a.getRequiredActionPoints()+ "<br>Current Cooldown: " +a.getCurrentCooldown()+ "<br>Base CoolDown: " +a.getBaseCooldown()+
								"<br>Effect: " +((CrowdControlAbility) a).getEffect()+ "<br></html>";
			}
			else if(a instanceof DamagingAbility){
				abilityInfo += "<br>Type: Damaging<br>AOE: " +a.getCastArea()+ "<br>Range: " +a.getCastRange()+ "<br>Mana Cost" +a.getManaCost()+ 
						"<br>Actions: " +a.getRequiredActionPoints()+ "<br>Current Cooldown: " +a.getCurrentCooldown()+ "<br>Base CoolDown: " +a.getBaseCooldown()+
						"<br>Damage: " +((DamagingAbility) a).getDamageAmount()+ "<br></html>";
			}
			else{
				abilityInfo += "<br>Type: Healing<br>AOE: " +a.getCastArea()+ "<br>Range: " +a.getCastRange()+ "<br>Mana Cost" +a.getManaCost()+ 
						"<br>Actions: " +a.getRequiredActionPoints()+ "<br>Current Cooldown: " +a.getCurrentCooldown()+ "<br>Base CoolDown: " +a.getBaseCooldown()+
						"<br>Healing: " +((HealingAbility) a).getHealAmount()+ "<br></html>";
			}
			abilityInformation.add(new JLabel(abilityInfo));
		}
		abilityInformation.setVisible(true);
		
		JPanel abilityButtons = new JPanel(new GridLayout(1, 3));
		abilityButtons.setMaximumSize(new Dimension(400, 100));
		for(int i = 0; i < c.getAbilities().size(); i++){	
			JButton useAbility = new JButton("Use Ability " + (i+1));
			useAbility.setPreferredSize(new Dimension(100, 100));
			useAbility.addActionListener(l);
			useAbility.setActionCommand("useAbility," + i);
			abilityButtons.add(useAbility);
		}		
		abilityButtons.setVisible(true);
		
		JPanel effectInformation = new JPanel(new GridLayout(1, 3));
		effectInformation.setMaximumSize(new Dimension(420, 100));
		String effectInfo = "";
		for(int i = 0; i < c.getAppliedEffects().size(); i++){
			Effect e = c.getAppliedEffects().get(i);
			effectInfo = "<html>Name: " +e.getName()+ "<br>Duration: " +e.getDuration()+ "</html>";
			effectInformation.add(new JLabel(effectInfo));
		}
		effectInformation.setVisible(true);
		
		JPanel buttons = new JPanel(new GridLayout(2, 2));
		buttons.setMaximumSize(new Dimension(400, 400));
		JButton move = new JButton("Move");
		move.addActionListener(l);
		move.setActionCommand("move,select");
		buttons.add(move);
		JButton attack = new JButton("Attack");
		attack.addActionListener(l);
		attack.setActionCommand("attack,select");
		buttons.add(attack);
		JButton leaderAbility = new JButton("Use Leader Ability");
		leaderAbility.addActionListener(l);
		leaderAbility.setActionCommand("leaderAbility");
		buttons.add(leaderAbility);
		JButton endTurn = new JButton("End Turn");
		endTurn.addActionListener(l);
		endTurn.setActionCommand("endTurn");
		buttons.add(endTurn);
		buttons.setVisible(true);
		
		main.add(left);
		gameInfo.add(secondPlayer);
		gameInfo.add(grid);
		gameInfo.add(firstPlayer);
		gameInfo.setVisible(true);
		main.add(gameInfo);
		actions.add(championInformation);
		actions.add(abilityInformation);
		actions.add(abilityButtons);
		actions.add(effectInformation);
		actions.add(buttons);
		actions.setVisible(true);
		main.add(actions);
		main.setVisible(true);
		repaint();
		revalidate();
		add(main);
		pack();
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	}
	
	public void showStats(String[] s){
		stats = new JFrame();
		stats.setLocationRelativeTo(null);
		stats.setTitle("stats");
		stats.setSize(500, 500);
		stats.setVisible(true);
		
		JPanel parent = new JPanel();
	    parent.setLayout(new BoxLayout(parent,BoxLayout.LINE_AXIS));
	    parent.setVisible(true);
	    stats.add(parent);
		
		JPanel championStats = new JPanel(new GridLayout(8, 1));;
		championStats.setVisible(true);
		for(int i = 0; i < 8; i++){
			JLabel label = new JLabel(s[i]);
			label.setVisible(true);
			championStats.add(label);
		}
		parent.add(championStats);
		
		JPanel selection = new JPanel(new GridLayout(0, 1));
		selection.setMaximumSize(new Dimension(200, 200));
		selection.setVisible(true);
		
		JButton select1 = new JButton("Select " +((Controller)l).getPlayer1().getName());
		select1.setPreferredSize(new Dimension(200, 100));
		select1.setVisible(true);
		select1.addActionListener(l);
		selection.add(select1);
		
		JButton select2 = new JButton("Select " +((Controller)l).getPlayer2().getName());
		select2.setPreferredSize(new Dimension(200, 100));
		select2.setVisible(true);
		select2.addActionListener(l);
		selection.add(select2);
		
		JButton Cancel = new JButton("Back");
		Cancel.setPreferredSize(new Dimension(200, 100));
		Cancel.setVisible(true);
		Cancel.addActionListener(l);
		selection.add(Cancel);
		
		parent.add(selection, BorderLayout.LINE_END);
		
		String name = s[1];
		name = name.substring(6);
		
		select1.setActionCommand("select,1," +name);
		select2.setActionCommand("select,2," +name);
		Cancel.setActionCommand("select,cancel");
		
		s = Arrays.copyOfRange(s, 8, s.length);
		
		
		abilities = new JFrame();
		abilities.setTitle("abilities");
		abilities.setSize(800, 300);
		abilities.setVisible(true);
		
		JPanel ability = new JPanel();
		ability.setLayout(new BoxLayout(ability, BoxLayout.X_AXIS));
		
		ability.setVisible(true);
		
		String tmp = "<html>";
		for(int i = 0; i < 3; i++){
			int j = i*8;
			tmp += s[j]+ "<br>" + s[j+1]+ "<br>" +s[j+2]+ "<br>" +s[j+3]+ "<br>" +s[j+4]+ "<br>" +s[j+5]+ "<br>" +s[j+6]+ "<br>" +s[j+7]+ "</html>";
			JLabel label = new JLabel(tmp);
			label.setVisible(true);
			ability.add(label);
			tmp = "<html>";
		}
		
		abilities.add(ability);
	}
	
	@SuppressWarnings("static-access")
	public void getSelections(){
		this.remove(this.selections);
		selections = new JPanel(new GridLayout(3, 5));
		for(int i = 0; i < ((Controller)l).getGame().getAvailableChampions().size(); i++){
				JButton button = new JButton();
				ImageIcon ii = new ImageIcon(((Controller)l).getGame().getAvailableChampions().get(i).getName()+ ".png");
			    button.setIcon(ii);
			    button.addActionListener(l);
			    button.setActionCommand("stats,"+ ((Controller)l).getGame().getAvailableChampions().get(i) + 
			    		((Controller)l).getGame().getAvailableChampions().get(i).getAbilities().get(0) + 
			    		((Controller)l).getGame().getAvailableChampions().get(i).getAbilities().get(1) + 
			    		((Controller)l).getGame().getAvailableChampions().get(i).getAbilities().get(2));
			    selections.add(button);
		}
		repaint();
		revalidate();
		add(selections);
		pack();
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	}

	public void selectLeader(String[] team){
		this.remove(this.selections);
		this.setTitle("Player " +team[0]+ " choose your leader");
		selections = new JPanel(new GridLayout(1, 3));
		selections.setMaximumSize(new Dimension(360, 200));
		
		for(int i = 1; i < 4; i++){
			JButton button = new JButton();
			ImageIcon ii = new ImageIcon(team[i] +".png");
			button.setIcon(ii);
			button.addActionListener(l);
			button.setActionCommand("Leader," +team[0]+ "," +team[i]);
			selections.add(button);
		}
		
		repaint();
		revalidate();
		selections.setVisible(true);
		add(selections);
		pack();
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	}

	public void selectMoveDirection(){
		this.moveDirection = new JFrame();
		moveDirection.setSize(new Dimension(200, 200));
		moveDirection.setLocationRelativeTo(null);
		moveDirection.setAlwaysOnTop(true);
		JPanel directions = new JPanel(new BorderLayout());
		
		JButton up = new JButton("UP");
		up.addActionListener(l);
		up.setActionCommand("move,up");
		directions.add(up, BorderLayout.PAGE_START);
		
		JButton right = new JButton("RIGHT");
		right.addActionListener(l);
		right.setActionCommand("move,right");
		directions.add(right, BorderLayout.LINE_END);
		
		JButton left = new JButton("LEFT");
		left.addActionListener(l);
		left.setActionCommand("move,left");
		directions.add(left, BorderLayout.LINE_START);
		
		JButton down = new JButton("DOWN");
		down.addActionListener(l);
		down.setActionCommand("move,down");
		directions.add(down, BorderLayout.PAGE_END);
		
		directions.setVisible(true);
		this.moveDirection.add(directions);
		this.moveDirection.setVisible(true);
		
	}
	
	public void selectAttackDirection(){
		this.attackDirection = new JFrame();
		attackDirection.setSize(new Dimension(200, 200));
		attackDirection.setLocationRelativeTo(null);
		attackDirection.setAlwaysOnTop(true);
		JPanel directions = new JPanel(new BorderLayout());
		
		JButton up = new JButton("UP");
		up.addActionListener(l);
		up.setActionCommand("attack,up");
		directions.add(up, BorderLayout.PAGE_START);
		
		JButton right = new JButton("RIGHT");
		right.addActionListener(l);
		right.setActionCommand("attack,right");
		directions.add(right, BorderLayout.LINE_END);
		
		JButton left = new JButton("LEFT");
		left.addActionListener(l);
		left.setActionCommand("attack,left");
		directions.add(left, BorderLayout.LINE_START);
		
		JButton down = new JButton("DOWN");
		down.addActionListener(l);
		down.setActionCommand("attack,down");
		directions.add(down, BorderLayout.PAGE_END);
		
		directions.setVisible(true);
		this.attackDirection.add(directions);
		this.attackDirection.setVisible(true);
		
	}
	
	public void selectAbilityDirection(String num){
		this.abilityDirection = new JFrame();
		abilityDirection.setSize(new Dimension(200, 200));
		abilityDirection.setLocationRelativeTo(null);
		abilityDirection.setAlwaysOnTop(true);
		JPanel directions = new JPanel(new BorderLayout());
		
		JButton up = new JButton("UP");
		up.addActionListener(l);
		up.setActionCommand("directAbility," +num+ ",up");
		directions.add(up, BorderLayout.PAGE_START);
		
		JButton right = new JButton("RIGHT");
		right.addActionListener(l);
		right.setActionCommand("directAbility," +num+ ",right");
		directions.add(right, BorderLayout.LINE_END);
		
		JButton left = new JButton("LEFT");
		left.addActionListener(l);
		left.setActionCommand("directAbility," +num+ ",left");
		directions.add(left, BorderLayout.LINE_START);
		
		JButton down = new JButton("DOWN");
		down.addActionListener(l);
		down.setActionCommand("directAbility," +num+ ",down");
		directions.add(down, BorderLayout.PAGE_END);
		
		directions.setVisible(true);
		this.abilityDirection.add(directions);
		this.abilityDirection.setVisible(true);
		
	}
	
	public void selectAbilityTarget(String num){
		this.abilityTargets = new JFrame();
		abilityTargets.setSize(500, 500);
		abilityTargets.setLocationRelativeTo(null);
		abilityTargets.setAlwaysOnTop(true);
		JPanel buttons = new JPanel(new GridLayout(5, 5));
		
		for(int i = 4; i >= 0; i--)
			for(int j = 0; j < 5; j++){
				JButton button = new JButton(i+ ", " +j);
				button.addActionListener(l);
				button.setActionCommand("abilityTarget," +num+ "," +i+ "," +j);
				buttons.add(button);
			}
		
		buttons.setVisible(true);
		this.abilityTargets.add(buttons);
		this.abilityTargets.setVisible(true);
	}

	public void gameOver(Player player){
		JOptionPane.showMessageDialog(this, "THE WINNER IS " +player.getName().toUpperCase());
		this.dispose();
	}
}
