package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

import com.sun.glass.events.KeyEvent;

import team2655.scriptcrafter.engine.CSVController;
import team2655.scriptcrafter.engine.ConfigEditorEngine;
import team2655.scriptcrafter.listener.CommandsListener;
import team2655.scriptcrafter.values.Values;

public class ConfigEditor extends JFrame implements Values {

	public static final long serialVersionUID = 889547632847065343L;
	public boolean removing = false;
	
	public JScrollPane scrollPane;
	
	public JTable table;
	
	public JPanel bottomPanel;
	
	public JButton btnSave;
	public JButton btnDiscard;
	public JButton btnUp;
	public JButton btnDown;
	public JButton btnDelete;
	
	public CommandsListener listener;
	public ConfigEditorEngine engine;
			
	public ConfigEditor(CommandsListener listener){
		
		this.listener = listener;
		
		engine = new ConfigEditorEngine(this);
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		
		scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		setupTable();
		setupValues();
		
		bottomPanel = new JPanel();
		bottomPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Onscreen Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new GridLayout(0, 5, 0, 0));
				
		btnSave = new JButton("Save");
		bottomPanel.add(btnSave);
		
		btnDiscard = new JButton("Discard");
		bottomPanel.add(btnDiscard);
		
		btnUp = new JButton("Up");
		bottomPanel.add(btnUp);
		
		btnDown = new JButton("Down");
		bottomPanel.add(btnDown);
		
		btnDelete = new JButton("Delete");
		bottomPanel.add(btnDelete);
		
		setupButtons();
		
		try{
			
			this.setIconImage(ImageIO.read(new File("./img/icon.png")));
			
		}catch(Exception e){
			
			
			
		}
		
		table.getModel().addTableModelListener(engine);
		((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", "", "", ""});
				
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(engine);
		this.setTitle("Configure Commands");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(false);
		this.requestFocus();
		
	}
	
	public void setupButtons(){
		
		btnSave.addActionListener(engine);
		btnDiscard.addActionListener(engine);
		btnUp.addActionListener(engine);
		btnDown.addActionListener(engine);
		btnDelete.addActionListener(engine);
		
	}
	
	public void setupTable(){
		
		table.setModel(new EditabeByArgumentTypeModel());
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		model.addColumn("Command");
		model.addColumn("Argument 1 Type");
		model.addColumn("Argument 1 Name");
		model.addColumn("Argument 2 Type");
		model.addColumn("Argument 2 Name");
		
		table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(true);
	    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    table.setSelectionModel(new ForcedListSelectionModel());
	    
	    setupKeyBindings();
	    
	    JComboBox<String> comboBoxRow1 = new JComboBox<>(ARGUMENT_TYPES);
	    comboBoxRow1.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED){
					
					if(comboBoxRow1.getSelectedItem().toString().equals(ARGUMENT_TYPE_NONE) || comboBoxRow1.getSelectedItem().toString().trim().equals("")){
						
						((DefaultTableModel)table.getModel()).setValueAt("", table.getSelectedRow(), 2);
						
					}
					
				}
				
			}
	    	
	    });
	    
	    JComboBox<String> comboBoxRow3 = new JComboBox<>(ARGUMENT_TYPES);
	    comboBoxRow3.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED){
					
					if(comboBoxRow3.getSelectedItem().toString().equals(ARGUMENT_TYPE_NONE) || comboBoxRow3.getSelectedItem().toString().trim().equals("")){
						
						((DefaultTableModel)table.getModel()).setValueAt("", table.getSelectedRow(), 4);
						
					}
					
				}
				
			}
	    	
	    });
	    
	    JTextField lettersOnly = new JTextField();
	    lettersOnly.setDocument(new AlphabetDocument());
	    
	    TableColumn commandColumn = table.getColumnModel().getColumn(0);
	    commandColumn.setCellEditor(new DefaultCellEditor(lettersOnly));
	    
	    TableColumn argNameColumn = table.getColumnModel().getColumn(2);
	    argNameColumn.setCellEditor(new DefaultCellEditor(lettersOnly));
	    
	    TableColumn argColumn = table.getColumnModel().getColumn(1);
	    argColumn.setCellEditor(new DefaultCellEditor(comboBoxRow1));
	    
	    TableColumn secondArgColumn = table.getColumnModel().getColumn(3);
	    secondArgColumn.setCellEditor(new DefaultCellEditor(comboBoxRow3));
	    
	    TableColumn secondNameColumn = table.getColumnModel().getColumn(4);
	    secondNameColumn.setCellEditor(new DefaultCellEditor(lettersOnly));
	    		
	}
	
	@SuppressWarnings("serial")
	public void setupKeyBindings(){
		
		table.getInputMap().clear();
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ROW_DOWN");
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "UP");
	    table.getActionMap().put("UP", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				moveRowUp();
				
			}
	    	
	    });
	    
	    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "DOWN");
	    table.getActionMap().put("DOWN", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				moveRowDown();
				
			}
	    	
	    });
	    
	    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "ROW_UP");
	    table.getActionMap().put("ROW_UP", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int row = table.getSelectedRow();
				
				if(row > 0){
										
					table.getSelectionModel().setSelectionInterval(row - 1,  row - 1);
					
				}
				
			}
	    	
	    });
	    
	    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "ROW_DOWN");
	    table.getActionMap().put("ROW_DOWN", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int row = table.getSelectedRow();
				
				if(row < table.getRowCount() - 1){
										
					table.getSelectionModel().setSelectionInterval(row + 1,  row + 1);
					
				}
				
			}
	    	
	    });
	    
	    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
	    table.getActionMap().put("DELETE", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				deleteRow();
				
			}
	    	
	    });
	    
	}
	
	public void setupValues(){
		
		try{
						
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			
			String[] commands = CSVController.loadCommands();
			String[] arguments = CSVController.loadArguments();
			String[] argnames = CSVController.loadArgumentsNames();
			String[] secondArguments = CSVController.loadSecondArguments();
			String[] secondArgumentsNames = CSVController.loadSecondArgumentsNames();
			
			for(int i = 0; i < commands.length; i++){
				
				String[] rowData = {commands[i], arguments[i], argnames[i], secondArguments[i], secondArgumentsNames[i]};
				
				model.addRow(rowData);
				
			}
									
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}
	
	public String[] getCommands(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 0);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	public String[] getArguments(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 1);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	public String[] getArgumentNames(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 2);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	public String[] getSecondArguments(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 3);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	public String[] getSecondArgumentNames(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 4);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	public Integer[] blankRows(){
		
		ArrayList<Integer> rows = new ArrayList<>();
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				/*if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){
						
						if(model.getValueAt(i, 3).toString().trim().equals("")){
							
							if(model.getValueAt(i, 4).toString().trim().equals("")){*/ //blank if no command
								
								rows.add(i);
								
							/*}
							
						}
						
					}
					
				}*/
				
			}
			
		}
		
		return rows.toArray(new Integer[rows.size()]);
		
	}
	
	public void removeBlankRows(){
		
		removing = true;
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				/*if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){
						
						if(model.getValueAt(i, 3).toString().trim().equals("")){
							
							if(model.getValueAt(i, 4).toString().trim().equals("")){*/ //blank if no command
								
								model.removeRow(i);
								
							/*}
							
						}
						
					}
					
				}*/
				
			}
			
		}
		
		removing = false;
				
	}
	
	public void blankRowCorrection(){
		
		Integer[] blank = blankRows();
		
		while(blank.length > 1){
			
			int row = blank[0];
						
			if(row != (table.getModel().getRowCount() - 1)){
				
				((DefaultTableModel)table.getModel()).removeRow(row);
				
			}
			
			blank = blankRows();
			
		}
		
		blank = blankRows();
		ArrayList<Integer> list = new ArrayList<>(Arrays.asList(blank));
		
		if(!list.contains(table.getRowCount() - 1)){
			
			((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", "", "", ""});
			
		}
		
	}
	
	public class AlphabetDocument extends PlainDocument {

		public static final long serialVersionUID = 655037934344680384L;
		
		public String text = "";

		@Override
		public void insertString(int offset, String txt, AttributeSet a) {
			
		    try {
		    	
		        text = getText(0, getLength());
		        
		        if (!(text + txt).contains(",")) {
		        	
		            super.insertString(offset, txt, a);
		            
		        }
		        
		    } catch (Exception ex) {}

		}
		
	}
	
	//Allow only one row to be selected at a time
	public class ForcedListSelectionModel extends DefaultListSelectionModel {

		public static final long serialVersionUID = -7073835059132006928L;

		public ForcedListSelectionModel () {
		    	
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        
		}

		@Override
		public void clearSelection() {
		    	
			super.clearSelection();
		    	
		}

		@Override
		public void removeSelectionInterval(int index0, int index1) {
		    	
			super.removeSelectionInterval(index0, index1);
		    	
		}

	}
	
	public class EditabeByArgumentTypeModel extends DefaultTableModel {
		
		public static final long serialVersionUID = -3260475730036854273L;

		public EditabeByArgumentTypeModel() {
	    	
	        super();
	        
	    }

	    @Override
	    public boolean isCellEditable(int row, int column) {
	    	
	        try{
	        	
	        	if(column == 2){
	        		
	        		DefaultTableModel model = (DefaultTableModel)table.getModel();
		        	
		        	String argType = model.getValueAt(row, 1).toString();
		        	
		        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
		        		
		        		return false;
		        		
		        	}
	        		
	        	}else if(column == 4){
	        		
	        		DefaultTableModel model = (DefaultTableModel)table.getModel();
		        	
		        	String argType = model.getValueAt(row, 3).toString();
		        	
		        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
		        		
		        		return false;
		        		
		        	}
		        	
		        	return true;
	        		
	        	}
	        	
	        	return true;
	        	
	        }catch(Exception e){
	        	
	        	return true;
	        	
	        }
	        
	    }
	    
	}
	
	public void moveRowUp(){
		
		int row = table.getSelectedRow();
		
		if(row > 0){
			
			((DefaultTableModel)table.getModel()).moveRow(row, row, row - 1);
			
			table.getSelectionModel().setSelectionInterval(row - 1,  row - 1);
			
		}
		
	}
	
	public void moveRowDown(){
		
		int row = table.getSelectedRow();
		
		if(row < table.getRowCount() - 1){
			
			((DefaultTableModel)table.getModel()).moveRow(row, row, row + 1);
			
			table.getSelectionModel().setSelectionInterval(row + 1,  row + 1);
			
		}
		
	}
	
	public void deleteRow(){
		
		int row = table.getSelectedRow();
		
		if(row >= 0){
								
			((DefaultTableModel)table.getModel()).removeRow(row);
			
		}
		
	}
	
}
