/*
 * Version 1.0.1: Disabled second argument check and multi row select in main table
 * Version 1.0.2: Checking is tolerant of blank command and spelling of 'driver station' was fixed. Save popup on file selector was removed. Rescan for file selector does not discard all changes. If command with none argument types is selected the arguments with type none are cleared
 * Version 1.0.3: Moved deleted and backup locations from autonomous to desktop. Number fields accept negative signs and decimals any time.
 * Version 1.0.4: When a new file is created it is loaded(item event is handeled by createrescanFiles function)
 * Version 2.0.0: Main classes have a gui and listener split, autoRow is not handeled by a thread but by a table model listener config editor changes, can resize rows, 
 * Version 2.0.1: Crafter opens before loading and checking script so script can be seen when check error dialog pops up
 * Version 2.0.2: After a file is deleted the new file is loaded.
 * Version 2.1.0: Updated to be compatible with java 8+
 */


package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

import team2655.scriptcrafter.engine.CSVController;
import team2655.scriptcrafter.engine.ScriptCrafterEngine;
import team2655.scriptcrafter.values.Values;

public class ScriptCrafter extends JFrame implements Values {
	
	public static final long serialVersionUID = 531670650738217800L;
	
	public String lastFileSelected = "";
	
	public boolean scanning = false;
	public boolean removing = false;
	
	public String[] commands = new String[0];
	
	public JScrollPane tableScrollPane;
	
	public JTable table;
	
	public JComboBox<String> fileSelector;
	
	public JPanel topPanel;	
	public JPanel filePanel;
	public JPanel fileButtonsPanel;
	public JPanel bottomPanel;
	
	public JButton btnFileNew;
	public JButton btnEditCommands;
	public JButton btnFileRename;
	public JButton btnFileDelete;
	public JButton btnSave;
	public JButton btnDiscard;
	public JButton btnUp;
	public JButton btnDown;
	public JButton btnDelete;
	
	public ScriptCrafterEngine engine;
	
	public ScriptCrafter(){
		
		try{
			
			CSVController.createConfigFile();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		engine = new ScriptCrafterEngine(this);
		
		topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		btnEditCommands = new JButton("Commands");
		topPanel.add(btnEditCommands, BorderLayout.EAST);
		
		filePanel = new JPanel();
		topPanel.add(filePanel, BorderLayout.CENTER);
		filePanel.setLayout(new BorderLayout(0, 0));
		
		fileButtonsPanel = new JPanel();
		filePanel.add(fileButtonsPanel, BorderLayout.EAST);
		fileButtonsPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		btnFileNew = new JButton("New");
		fileButtonsPanel.add(btnFileNew);
		
		btnFileRename = new JButton("Rename");
		fileButtonsPanel.add(btnFileRename);
		
		btnFileDelete = new JButton("Delete");
		fileButtonsPanel.add(btnFileDelete);
		
		fileSelector = new JComboBox<>();
		fileSelector.addPopupMenuListener(engine);
		filePanel.add(fileSelector, BorderLayout.CENTER);
		
		ToolTipManager.sharedInstance().setDismissDelay(999999999); //Hack to "cancel" auto dismiss of tool tips (largest value for int)
		ToolTipManager.sharedInstance().setInitialDelay(10);
		
		table = new JTable() {
			
			public static final long serialVersionUID = 1480182826960427740L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		    	
		    	Component c = super.prepareRenderer(renderer, row, column);
		    	
		        if (c instanceof JComponent) {
		        	
		        	if(column == 0){
		        		
		        		try{
			        		
			        		JComponent jc = (JComponent) c;
			        		jc.setToolTipText("");
		        			
		        		}catch(Exception e){
		        			
		        			
		        			
		        		}
		        		
		        	}else if(column == 1){
		        	   
		        		try{
		        			
		        			ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
		        			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadArguments()));
		        			ArrayList<String> names = new ArrayList<>(Arrays.asList(CSVController.loadArgumentsNames()));
			        	    
			        		String command = ((DefaultTableModel)table.getModel()).getValueAt(row,  0).toString();
			        		String name = names.get(commands.indexOf(command));
			        		String arg = arguments.get(commands.indexOf(command));
			        		
			        		String text = "<html>" + command + ":<br />Argument 1: " + arg + "<br />"  + name + "</html>";
			        		
			        		JComponent jc = (JComponent) c;
			        		jc.setToolTipText(text);
		        			
		        		}catch(Exception e){
		        			
		        			
		        			
		        		}
		        	   
		        	}else if(column == 2){
			        	   
			        		try{
			        			
			        			ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
			        			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadSecondArguments()));
			        			ArrayList<String> names = new ArrayList<>(Arrays.asList(CSVController.loadSecondArgumentsNames()));
				        	    
				        		String command = ((DefaultTableModel)table.getModel()).getValueAt(row,  0).toString();
				        		String name = names.get(commands.indexOf(command));
				        		String arg = arguments.get(commands.indexOf(command));
				        		
				        		String text = "<html>" + "Argument 2: " + arg + "<br />"  + name + "</html>";
				        		
				        		JComponent jc = (JComponent) c;
				        		jc.setToolTipText(text);
			        			
			        		}catch(Exception e){
			        			
			        			
			        			
			        		}
			        	   
			        	}
		        	
		        }
		        
		        return c;
		        
		    }
		    
		};
		
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		
		tableScrollPane = new JScrollPane(table);
		getContentPane().add(tableScrollPane, BorderLayout.CENTER);
		
		JTable rowTable = new RowNumberTable(table);
		tableScrollPane.setRowHeaderView(rowTable);
		tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
		
		rowTable.getTableHeader().setReorderingAllowed(false);
		rowTable.getTableHeader().setResizingAllowed(false);
		rowTable.getInputMap().clear();
		rowTable.setRowSelectionAllowed(false);
		
		bottomPanel = new JPanel();
		bottomPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Onscreen Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new GridLayout(0, 5, 0, 0));
		
		btnSave = new JButton("Save Changes");
		bottomPanel.add(btnSave);
		
		btnDiscard = new JButton("Discard Changes");
		bottomPanel.add(btnDiscard);
		
		btnUp = new JButton("Up");
		bottomPanel.add(btnUp);
		
		btnDown = new JButton("Down");
		bottomPanel.add(btnDown);
		
		btnDelete = new JButton("Delete");
		bottomPanel.add(btnDelete);
		
		setupButtons();
		setupTable();
		setupKeyBindings();
	    setupColumnTypes();
		scanFiles(null);
		
		fileSelector.addItemListener(engine);
		
		try{
			
			this.setIconImage(ImageIO.read(new File("./img/icon.png")));
			
		}catch(Exception e){
			
			
			
		}
		
		this.setTitle("Script Crafter Version: " + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_BUILD + RELEASE_TYPE);
		this.addWindowListener(engine);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		try {
			
			CSVController.loadScript(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel(), this);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
				
		table.getModel().addTableModelListener(engine);
		((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", ""});
		
		try{
			
			CSVController.doBackup();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(new JDialog(), "The automatic backup failed.", "Backup failed.", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			
		}
		
	}
	
	public Integer[] blankRows(){
		
		ArrayList<Integer> rows = new ArrayList<>();
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){							
								
						rows.add(i);
						
					}
					
				}
				
			}
			
		}
		
		return rows.toArray(new Integer[rows.size()]);
		
	}
	
	public void removeBlankRows(){
		
		removing = true;
				
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){
								
						model.removeRow(i);
						
					}
					
				}
				
			}
			
		}
		
		removing = false;
				
	}
	
	public void setupButtons(){
		
		btnEditCommands.addActionListener(engine);
		
		btnFileNew.addActionListener(engine);
		btnFileRename.addActionListener(engine);
		btnFileDelete.addActionListener(engine);
		
		btnSave.addActionListener(engine);
		btnDiscard.addActionListener(engine);
		btnUp.addActionListener(engine);
		btnDown.addActionListener(engine);
		btnDelete.addActionListener(engine);
		
	}
	
	public void setupTable(){
		
		table.setModel(new EditableByArgumentTypeModel());
		table.setSelectionModel(new ForcedListSelectionModel());
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		model.addColumn("Command");
		model.addColumn("Argument 1");
		model.addColumn("Argument 2");
		
		table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(true);
	    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    		
	}
	
	public void setupColumnTypes(){
		
		try {
	    	
			commands = CSVController.loadCommands();
			
		} catch (Exception e){
			
			
			
		}
	    
	    ArrayList<String> cmds = new ArrayList<>(Arrays.asList(commands));
	    cmds.add(0, ""); //Add blank option for combobox
	    
	    commands = cmds.toArray(new String[cmds.size()]);
	    
	    JComboBox<String> comboBox = new JComboBox<>(commands);
	    
	    JFormattedTextField numbersOnly = new JFormattedTextField();
	    numbersOnly.setDocument(new NumbersDocument());
	    
	    TableColumn command = table.getColumnModel().getColumn(0);
	    command.setCellEditor(new DefaultCellEditor(comboBox));
	    
	    TableColumn arg1 = table.getColumnModel().getColumn(1);
	    arg1.setCellEditor(new DefaultCellEditor(numbersOnly));
	    //arg1.setCellRenderer(new ArgumentCellRenderer());
	    
	    TableColumn arg2 = table.getColumnModel().getColumn(2);
	    arg2.setCellEditor(new DefaultCellEditor(numbersOnly));
	    //arg2.setCellRenderer(new ArgumentCellRenderer());
		
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

	public void scanFiles(String toSelect){
		
		scanning = true;
		
		try{
			
			fileSelector.removeAllItems();
			fileSelector.repaint();
			fileSelector.revalidate();
			
			String[] files = CSVController.listScripts();
			
			for(String name : files){
				
				if(name.endsWith(".csv")){
					
					fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv")));
					
				}
				
			}
			
			if(toSelect != null){
				
				for(int i = 0; i < fileSelector.getItemCount(); i++){
					
					if(toSelect.equals(fileSelector.getItemAt(i))){
						
						fileSelector.setSelectedIndex(i);
						
					}
					
				}
				
				lastFileSelected = fileSelector.getSelectedItem().toString();
				
			}
			
		}catch(Exception e){
			
			System.out.println("ERROR HERE!!!");
			e.printStackTrace();
			
		}
		
		scanning = false;
		
	}
			
	public void clearTable(){
			
		try{
				
			CellEditor editor = table.getCellEditor();
				
			if(editor != null){
					
				editor.stopCellEditing();
					
			}
				
			DefaultTableModel model = (DefaultTableModel) table.getModel();
 			   
			for (int i = model.getRowCount() - 1; i >= 0; i--) {
 			  	
				model.removeRow(i);
 			    
 		   	}
				
		}catch(Exception e){
				
				
				
		}
			
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
			
			((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", ""});
			
		}
		
	}
	
	public static void main(String[] args){
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (Throwable e) {
			
			e.printStackTrace();
			
		}
		
		new ScriptCrafter();
		
	}
	
	//Internal Classes
	public class NumbersDocument extends PlainDocument {

		public static final long serialVersionUID = 655037934344680384L;
		
		public String text = "";

		@Override
		public void insertString(int offset, String txt, AttributeSet a) {
			
		    try {
		    	
		        text = getText(0, getLength());
		        text = text.substring(0, offset) + txt + text.substring(offset, text.length());
		        		        
		        try{
		        	
		        	Double.parseDouble(text);
		        	//Is a valid number if exception is not thrown
		        	super.insertString(offset, txt, a);
		        	
		        }catch(Exception e){
		        	
		        	text = getText(0, getLength());
		        	//Not a valid number
		        	if((txt.equals("-") && !text.contains("-")) || (txt.equals(".") && !text.contains("."))){ //These character are only not valid if there is no number
		        		
		        		super.insertString(offset, txt, a);
		        		
		        	}
		        	
		        }
		        
		    } catch (Exception ex) {}

		}
		
	}
	
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
	
	public class ArgumentCellRenderer extends DefaultTableCellRenderer {
		
		public static final long serialVersionUID = 3921019133712008509L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        this.setToolTipText("");
	        return this;
	        
	    }

	}
	
	public class EditableByArgumentTypeModel extends DefaultTableModel {
		
		public static final long serialVersionUID = -3260475730036854273L;

		public EditableByArgumentTypeModel() {
	    	
	        super();
	        
	    }

	    @Override
	    public boolean isCellEditable(int row, int column) {
	    	
	        if(row == -1){
	        	
	        	return false;
	        	
	        }else{
	        	
	        	try{
		        	
		        	if(column == 1){
		        		
		        		ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
	        			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadArguments()));
	        			String command = ((DefaultTableModel)table.getModel()).getValueAt(row,  0).toString();
	        			
	        			String argType = "";
	        			
			        	try{
			        				
			        		argType = arguments.get(commands.indexOf(command));
			        				
			        	}catch(Exception e){
			        		
			        		
			        		
			        	}
			        	
			        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
			        		
			        		return false;
			        		
			        	}
		        		
		        	}else if(column == 2){
		        				        	
		        		ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
	        			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadSecondArguments()));
	        			String command = ((DefaultTableModel)table.getModel()).getValueAt(row,  0).toString();
	        			
			        	String argType = "";
			        			
			        	try{
			        				
			        		argType = arguments.get(commands.indexOf(command));
			        				
			        	}catch(Exception e){
			        		
			        		
			        		
			        	}
			        	
			        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
			        		
			        		return false;
			        		
			        	}
			        	
			        	return true;
		        		
		        	}
		        	
		        	return true;
		        	
		        }catch(Exception e){
		        	
		        	e.printStackTrace();
		        	
		        	return true;
		        	
		        }
	        	
	        }
	        
	    }
	    
	}
	
}
