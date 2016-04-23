/*
 * Version 1.0.1: Disabled second argument check and multi row select in main table
 * Version 1.0.2: Checking is tolerant of blank command and spelling of 'driver station' was fixed. Save popup on file selector was removed. Rescan for file selector does not discard all changes. If command with none argument types is selected the arguments with type none are cleared
 * Version 1.0.3: Moved deleted and backup locations from autonomous to desktop. Number fields accept negative signs and decimals any time.
 */

package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

import com.sun.glass.events.KeyEvent;

import team2655.scriptcrafter.engine.CSVController;
import team2655.scriptcrafter.listener.CommandsListener;
import team2655.scriptcrafter.values.Values;

public class ScriptCrafter extends JFrame implements ActionListener, WindowListener, Values, CommandsListener, ItemListener, PopupMenuListener {
	
	private static final long serialVersionUID = 531670650738217800L;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JPanel topPanel;
	private JButton btnEditCommands;
	private String[] commands = new String[0];
	private String lastFileSelected = "";  //Stores the name of the previously selected file if the refresh button is pressed
	private final static String fileNameRegex = "^[a-zA-Z0-9_-]+$";
	
	Thread autoRowThread;
	private JPanel filePanel;
	private JPanel fileButtonsPanel;
	private JButton btnFileNew;
	private JComboBox<String> fileSelector;
	private JButton btnFileRename;
	private JButton btnFileDelete;
	private JPanel bottomPanel;
	private JButton btnSave;
	private JButton btnDiscard;
	private JButton btnUp;
	private JButton btnDown;
	private JButton btnDelete;
	
	private boolean scanning = false;
	
	public ScriptCrafter(){
		
		try{
			
			CSVController.createConfigFile();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
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
		fileSelector.addPopupMenuListener(this);
		filePanel.add(fileSelector, BorderLayout.CENTER);
		
		ToolTipManager.sharedInstance().setDismissDelay(999999999); //Hack to "cancel" auto dismiss of tool tips (largest value for int)
		
		table = new JTable() {
			
			private static final long serialVersionUID = 1480182826960427740L;

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
			        		
			        		String text = "<html>" + "Argument 1: " + arg + "<br />"  + name + "</html>";
			        		
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
		
		autoRowThread = new Thread(){
			
			@Override
			public void run(){
				
				while(true){
					
					try{
						
						Integer[] rows = blankRows();
						int blank = rows.length;
						
						if(blank > 1){
							
							for(Integer i : rows){
								
								((DefaultTableModel)table.getModel()).removeRow(i);
								
							}
							
						}else if(blank < 1){
							
							((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", ""});
							
						}
						
					}catch(Exception e){
						
						
						
					}
					
				}
				
			}
			
		};
		
		setupButtons();
		setupTable();
		scanFiles();
		
		fileSelector.addItemListener(this);
		
		try {
			
			CSVController.loadScript(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		autoRowThread.start();
		
		try{
			
			this.setIconImage(ImageIO.read(new File("./img/icon.png")));
			
		}catch(Exception e){
			
			
			
		}
		
		this.setTitle("Script Crafter Version: " + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_BUILD);
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		try{
			
			CSVController.doBackup();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(new JDialog(), "The automatic backup failed.", "Backup failed.", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			
		}
		
	}
	
	private Integer[] blankRows(){
		
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
	
	private void removeBlankRows(){
				
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
				
	}
	
	private void setupButtons(){
		
		btnEditCommands.addActionListener(this);
		
		btnFileNew.addActionListener(this);
		btnFileRename.addActionListener(this);
		btnFileDelete.addActionListener(this);
		
		btnSave.addActionListener(this);
		btnDiscard.addActionListener(this);
		btnUp.addActionListener(this);
		btnDown.addActionListener(this);
		btnDelete.addActionListener(this);
		
	}
	
	private void setupTable(){
		
		table.setModel(new EditableByArgumentTypeModel());
		table.setSelectionModel(new ForcedListSelectionModel());
		table.getModel().addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent e) {
				
				if(e.getColumn() == 0){
					
					try{
						
						ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
	        			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadArguments()));
	        			String command = ((DefaultTableModel)table.getModel()).getValueAt(e.getFirstRow(),  0).toString();
	        			ArrayList<String> secArguments = new ArrayList<>(Arrays.asList(CSVController.loadSecondArguments()));
	        			
	        			String argType = "";
	        			String secArgType = "";
	        			
			        	try{
			        				
			        		argType = arguments.get(commands.indexOf(command));
			        		secArgType = secArguments.get(commands.indexOf(command));		
			        		
			        	}catch(Exception er){
			        		
			        		
			        		
			        	}
			        	
			        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
			        		
			        		((DefaultTableModel)table.getModel()).setValueAt("", e.getFirstRow(), 1);
			        		
			        	}
			        	
			        	if(secArgType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
			        		
			        		((DefaultTableModel)table.getModel()).setValueAt("", e.getFirstRow(), 2);
			        		
			        	}
						
					}catch(Exception er){
						
						
						
					}
					
				}
				
			}
			
		});
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		model.addColumn("Command");
		model.addColumn("Argument 1");
		model.addColumn("Argument 2");
		
		table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(false);
	    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    
	    setupKeyBindings();
	    
	    setupColumnTypes();
	    		
	}
	
	private void setupColumnTypes(){
		
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
	private void setupKeyBindings(){
		
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
			
			new ConfigEditor(this);
			
		}else if(src == btnFileNew){
			
			try{
				
				Object returned = JOptionPane.showInputDialog(new JDialog(), "New file name: ", "Create New File ", JOptionPane.QUESTION_MESSAGE);
				
				if(returned != null){
					
					if(returned.toString().matches(fileNameRegex)){
						
						CSVController.createScript(returned.toString());
													
						rescanFiles(returned.toString());
						
					}else{
						
						JOptionPane.showMessageDialog(new JDialog(), "Invalid file name. File names can only contain letters, numbers and dashes or underscores.", "Invalid Name!", JOptionPane.WARNING_MESSAGE);
						
					}
					
				}
				
			}catch(Exception er){
				
				
				
			}
			
		}else if(src == btnFileRename){
			
			//new RenameRoutineDialog(this, fileSelector.getSelectedItem().toString());
			
			Object returned = JOptionPane.showInputDialog(new JDialog(), "Rename to: ", "Rename '" + fileSelector.getSelectedItem().toString() + "'", JOptionPane.QUESTION_MESSAGE, null, null, fileSelector.getSelectedItem().toString());
			
			if(returned != null){
				
				if(returned.toString().matches(fileNameRegex)){
					
					boolean done = CSVController.renameFile(fileSelector.getSelectedItem().toString(), returned.toString());
					
					if(done){
						
						rescanFiles(returned.toString());
						
					}
					
				}else{
					
					JOptionPane.showMessageDialog(new JDialog(), "Invalid file name. File names can only contain letters, numbers and dashes or underscores.", "Invalid Name!", JOptionPane.WARNING_MESSAGE);
					
				}
				
			}
			
		}else if(src == btnFileDelete){
			
			int returned = JOptionPane.showConfirmDialog(new JDialog(), "Are you sure you want to delete '" + fileSelector.getSelectedItem().toString() + "'?", "Delete?", JOptionPane.YES_NO_OPTION);
			if(returned == JOptionPane.YES_OPTION){
				
				try {
					
					CSVController.deleteFile(fileSelector.getSelectedItem().toString());
					deleteRescanFiles();
					
				} catch (Exception er) {er.printStackTrace();}
				
				
			}
			
		}else if(src == btnSave){
			
			try{
				
				CellEditor editor = table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveFile(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel());
				
			}catch(Exception er){
				
				JOptionPane.showMessageDialog(new JDialog(), "Save failed (is the driver station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
				
			}
			
		}else if(src == btnDiscard){
						
			int rtn = JOptionPane.showConfirmDialog(new JDialog(), "Are you sure you want to discard ALL changes since the last save?", "Discard?", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(rtn == JOptionPane.YES_OPTION){
				
				try {
					
					DefaultTableModel model = (DefaultTableModel) table.getModel();
     			   
     			   	for (int i = model.getRowCount() - 1; i >= 0; i--) {
     			   	
     			   		model.removeRow(i);
     			    
     			   	}
     			   	
     			   	CSVController.loadScript(fileSelector.getSelectedItem().toString(), model);
					
				} catch (Exception er) {}
				
			}
			
		}else if(src == btnUp){
			
			moveRowUp();
			
		}else if(src == btnDown){
			
			moveRowDown();
			
		}else if(src == btnDelete){
			
			deleteRow();
			
		}
		
	}
	
	private void moveRowUp(){
		
		int row = table.getSelectedRow();
		
		if(row > 0){
			
			((DefaultTableModel)table.getModel()).moveRow(row, row, row - 1);
			
			table.getSelectionModel().setSelectionInterval(row - 1,  row - 1);
			
		}
		
	}
	
	private void moveRowDown(){
		
		int row = table.getSelectedRow();
		
		if(row < table.getRowCount() - 1){
			
			((DefaultTableModel)table.getModel()).moveRow(row, row, row + 1);
			
			table.getSelectionModel().setSelectionInterval(row + 1,  row + 1);
			
		}
		
	}
	
	private void deleteRow(){
		
		int row = table.getSelectedRow();
		
		if(row >= 0){
								
			((DefaultTableModel)table.getModel()).removeRow(row);
			
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent e) {
		
		int rtn = JOptionPane.showConfirmDialog(new JDialog(), "Save before exit?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(rtn == JOptionPane.YES_OPTION){
			
			autoRowThread.suspend();
			removeBlankRows();
			
			try {
				
				CellEditor editor = table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveFile(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel());
				System.exit(0);
				
			} catch (Exception er) {
				
				int returned = JOptionPane.showConfirmDialog(new JDialog(), "The file was not saved (is the driver station open?). Exit anyways (will discard changes)?", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				if(returned == JOptionPane.YES_OPTION){
					
					System.exit(0);
					
				}else{
					
					autoRowThread.resume();				
				}
				
			}
			
			autoRowThread.resume();	
			
		}else if(rtn == JOptionPane.NO_OPTION){
			
			System.exit(0);
			
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
	
	public class NumbersDocument extends PlainDocument {

		private static final long serialVersionUID = 655037934344680384L;
		
		private String text = "";

		@Override
		public void insertString(int offset, String txt, AttributeSet a) {
			
		    try {
		    	
		        text = getText(0, getLength());
		        text = text.substring(0, offset) + txt + text.substring(offset, text.length());
		        
		        System.out.println(text);
		        
		        try{
		        	
		        	Double.parseDouble(text);
		        	//Is a valid number if exception is not thrown
		        	super.insertString(offset, txt, a);
		        	
		        }catch(Exception e){
		        	
		        	//Not a valid number
		        	if(txt.equals("-") || txt.equals(".")){ //These character are only not valid if there is no number
		        		
		        		super.insertString(offset, txt, a);
		        		
		        	}
		        	
		        }
		        
		    } catch (Exception ex) {}

		}
		
	}
	
	//Allow only one row to be selected at a time
	public class ForcedListSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -7073835059132006928L;

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
		
		private static final long serialVersionUID = 3921019133712008509L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        this.setToolTipText("");
	        return this;
	        
	    }

	}
	
	public class EditableByArgumentTypeModel extends DefaultTableModel {
		
		private static final long serialVersionUID = -3260475730036854273L;

		private EditableByArgumentTypeModel() {
	    	
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

	@Override
	public void commandsChanged() {
		
		setupColumnTypes();
		
	}
	
	//Create a list of routine files
	private void scanFiles(){			
			scanning = true;
			String[] fileNames = CSVController.listScripts(); //list file names
			
			for(String name : fileNames){
				
				if(name.endsWith(".csv")){
					
					fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add name without .csv
					
				}
				
			}
			
			scanning = false;
			
		}
		
		//scan files by refresh button (need to clear combobox and table)
		public void rescanFiles(){
			
			scanning = true;
			
			String currentlySelected = (String) fileSelector.getSelectedItem(); //Get currently selected name
			
			String[] fileNames = CSVController.listScripts(); //List file Names
			
			//Clear Drop-down Items
			fileSelector.removeAllItems();
			fileSelector.repaint();
			fileSelector.revalidate();
			
			for(String name : fileNames){
				
				if(name.endsWith(".csv")){
					
					fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add each csv file without .csv
					
				}
				
			}
			
			//Reselect currentlySelected if it exists
			for(int i = 0; i < fileSelector.getItemCount(); i++){
				
				if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
					
					fileSelector.setSelectedIndex(i);
					
				}
				
			}
			
			//Create file and select if it does not exist
			if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
				
				try{
					
					CSVController.createScript(currentlySelected);
					rescanFiles(currentlySelected);
					
				}catch(Exception e){}
				
			}
			
			scanning = false;
			
		}
		
		//scan files after a delete
		public void deleteRescanFiles(){
			
			scanning = true;
				
				String currentlySelected = (String) fileSelector.getSelectedItem(); //Get currently selected name
				
				String[] fileNames = CSVController.listScripts(); //List file Names
				
				//Clear Drop-down Items
				fileSelector.removeAllItems();
				fileSelector.repaint();
				fileSelector.revalidate();
				
				for(String name : fileNames){
					
					if(name.endsWith(".csv")){
						
						fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add each csv file without .csv
						
					}
					
				}
				
				if(fileSelector.getItemCount() == 0){
					
					try {
						
						CSVController.createScript("DUMMY");
						
					} catch (Exception e) {}
					
					rescanFiles();
					
				}
				
				//Clear table if it doesn't exist anymore
				if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
					
				   //Clear the table
	 			   DefaultTableModel model = (DefaultTableModel) table.getModel();
	 			   
	 			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
	 			   	
	 				   model.removeRow(i);
	 			    
	 			   }
	 			   
	 			   try{
	 				   
	 				   CSVController.loadScript(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel());
	 				   
	 			   }catch(Exception e){
	 				   
	 				   
	 				   
	 			   }
					
				}
				
				scanning = false;
				
			}
		
		
		public void rescanFiles(String toSelect){
			
			scanning = true;
			
			String currentlySelected = toSelect; //Which file to select
			
			System.out.println("'" + toSelect + "'");
						
			String[] fileNames = CSVController.listScripts(); //List all file names
			
			//Clear drop-down
			fileSelector.removeAllItems();
			fileSelector.repaint();
			fileSelector.revalidate();
			
			for(String name : fileNames){
				
				if(name.endsWith(".csv")){
					
					fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add all csv files without .csv
					
				}
				
			}
			
			//Select the file
			for(int i = 0; i < fileSelector.getItemCount(); i++){
				
				if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
					
					fileSelector.setSelectedIndex(i);
					
				}
				
			}
			
			//Create file and select if it does not exist
			if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
						
				try{
							
					CSVController.createScript(currentlySelected);
					rescanFiles();
							
				}catch(Exception e){
						
						
						
				}
						
			}
			
			scanning = false;
			
		}
		
		@Override
	    public void itemStateChanged(ItemEvent e) {
			
			Object src = e.getSource();
			
	       if(src == fileSelector && !scanning){ //If file selector
	    	   
	    	   if (e.getStateChange() == ItemEvent.SELECTED) { //if something selected
	        	   
	    		   String newItem = (String) fileSelector.getSelectedItem();
	    		   
	    		   if(newItem != null){
	    			   
	    			   if(!newItem.equals(lastFileSelected)){ //if it is not the same
	        			   
	        			   //Clear the table
	        			   DefaultTableModel model = (DefaultTableModel) table.getModel();
	        			   
	        			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
	        			   	
	        				   model.removeRow(i);
	        			    
	        			   }
	        			   
	        			   //load the data
	        			try {

							CSVController.loadScript(fileSelector.getSelectedItem().toString(), (DefaultTableModel) table.getModel());
							((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", ""});
							
						} catch (Exception e1) {}
	        			   
	        		   }
	    			   
	    		   }else{
	    			   	    				
	    				try{
	    									
	    					CSVController.createScript("DUMMY");
	    					
	    				}catch(Exception ex){
	    					
	    					
	    				}
	    			   
	    		   }
	    	          
	           }
	    	   
	       }
	       
	    }

		@Override
		public void popupMenuCanceled(PopupMenuEvent arg0) {}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}

		@SuppressWarnings("deprecation")
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			
			rescanFiles(fileSelector.getSelectedItem().toString());
			
			//int rtn = JOptionPane.showConfirmDialog(new JDialog(), "Save before continue?", "Save?", JOptionPane.YES_NO_OPTION);
			int rtn = JOptionPane.YES_OPTION;
			if(rtn == JOptionPane.YES_OPTION){
				
				autoRowThread.suspend();
				removeBlankRows();
				
				try {
					
					CellEditor editor = table.getCellEditor();
					
					if(editor != null){
						
						editor.stopCellEditing();
						
					}
					
					CSVController.saveFileQuiet(fileSelector.getSelectedItem().toString(), (DefaultTableModel)table.getModel());
					
				} catch (Exception er) {
					
					JOptionPane.showMessageDialog(new JDialog(), "The file was not saved (is the driver station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
					er.printStackTrace();
					
					
				}
				
				autoRowThread.resume();	
				
			}else if(rtn == JOptionPane.NO_OPTION){
				
				
			
			}
			
		}
	
}
