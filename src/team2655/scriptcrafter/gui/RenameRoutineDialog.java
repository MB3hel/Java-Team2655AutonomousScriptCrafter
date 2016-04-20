package team2655.scriptcrafter.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import team2655.scriptcrafter.engine.CSVController;

public class RenameRoutineDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -2397395850025454800L;
	
	private JTextField fileName;
	private JButton cancelButton;
	private JButton saveButton;
	private JPanel southPanel;
	private JPanel northPanel;
	private JPanel mainPanel;
		
	private ScriptCrafter manager;
	private String originalName;
	
	public RenameRoutineDialog(ScriptCrafter manager, String originalName) {
		
		this.manager = manager;
		this.originalName = originalName;
		
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		northPanel = new JPanel();
		northPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "New File Name", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		mainPanel.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BorderLayout(0, 0));
		
		fileName = new JTextField();
		fileName.setText(originalName);
		northPanel.add(fileName);
		fileName.setColumns(10);
		
		southPanel = new JPanel();
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(1, 2, 0, 0));
		
		saveButton = new JButton("Save");
		southPanel.add(saveButton);
		
		cancelButton = new JButton("Cancel");
		southPanel.add(cancelButton);
		
		cancelButton.addActionListener(this);
		saveButton.addActionListener(this);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setTitle("New Routine");
		this.setVisible(true);
		fileName.requestFocus();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
		
		if(src == cancelButton){
			
			this.dispose();
			
		}else if(src == saveButton){
			try{
				
				boolean rtn = CSVController.renameFile(originalName, fileName.getText());
				
				if(rtn){
					
					manager.rescanFiles(fileName.getText());
					this.dispose();
					
				}
			
			}catch(Exception ex){
				
				
				
			}
			
		}
		
	}
	
}
