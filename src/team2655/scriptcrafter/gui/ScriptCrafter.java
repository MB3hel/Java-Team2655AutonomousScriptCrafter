package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import team2655.scriptcrafter.engine.CSVController;

public class ScriptCrafter extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 531670650738217800L;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JPanel topPanel;
	private JButton btnEditCommands;
		
	public ScriptCrafter(){
		
		try{
			
			CSVController.createConfigFile();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		btnEditCommands = new JButton("Configure");
		topPanel.add(btnEditCommands, BorderLayout.EAST);
		
		//TODO CREATE COMMANDS
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(500, 500));
		
		tableScrollPane = new JScrollPane(table);
		getContentPane().add(tableScrollPane, BorderLayout.CENTER);
		
		JTable rowTable = new RowNumberTable(table);
		tableScrollPane.setRowHeaderView(rowTable);
		tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
		
		setupButtons();
		
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	private void setupButtons(){
		
		btnEditCommands.addActionListener(this);
		
	}
	
	public static void main(String[] args){
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (Throwable e) {
			
			e.printStackTrace();
			
		}
		
		new ScriptCrafter();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		
		Object src = e.getSource();
		
		if(src == btnEditCommands){
			
			new ConfigEditor();
			
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
		System.exit(0);
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
	
}
