package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
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

public class ScriptCrafter extends JFrame implements ActionListener, WindowListener, Values, CommandsListener {
	
	private static final long serialVersionUID = 531670650738217800L;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JPanel topPanel;
	private JButton btnEditCommands;
	private String[] commands = new String[0];
	
	Thread autoRowThread;
	
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
							
							((DefaultTableModel)table.getModel()).addRow(new String[]{"", "", "", "", ""});
							
						}
						
					}catch(Exception e){
						
						
						
					}
					
				}
				
			}
			
		};
		
		autoRowThread.start();
		
		setupButtons();
		setupTable();
		
		this.setTitle("Script Crafter Version: " + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_BUILD);
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	private Integer[] blankRows(){
		
		ArrayList<Integer> rows = new ArrayList<>();
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){
						
						if(model.getValueAt(i, 3).toString().trim().equals("")){
							
							if(model.getValueAt(i, 4).toString().trim().equals("")){
								
								rows.add(i);
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		return rows.toArray(new Integer[rows.size()]);
		
	}
	
	@SuppressWarnings("unused")
	private void removeBlankRows(){
				
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			if(model.getValueAt(i, 0).toString().trim().equals("")){
				
				if(model.getValueAt(i, 1).toString().trim().equals("")){
					
					if(model.getValueAt(i, 2).toString().trim().equals("")){
						
						if(model.getValueAt(i, 3).toString().trim().equals("")){
							
							if(model.getValueAt(i, 4).toString().trim().equals("")){
								
								model.removeRow(i);
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
				
	}
	
	private void setupButtons(){
		
		btnEditCommands.addActionListener(this);
		
	}
	
	private void setupTable(){
		
		table.setModel(new EditabeByArgumentTypeModel());
		
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
	    
	    JTextField lettersOnly = new JTextField();
	    lettersOnly.setDocument(new AlphabetDocument());
	    
	    TableColumn command = table.getColumnModel().getColumn(0);
	    command.setCellEditor(new DefaultCellEditor(comboBox));
	    
	    TableColumn arg1 = table.getColumnModel().getColumn(0);
	    arg1.setCellEditor(new DefaultCellEditor(comboBox));
	    //arg1.setCellRenderer(new ArgumentCellRenderer());
	    
	    TableColumn arg2 = table.getColumnModel().getColumn(0);
	    arg2.setCellEditor(new DefaultCellEditor(comboBox));
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
				
				int row = table.getSelectedRow();
				
				if(row > 0){
					
					((DefaultTableModel)table.getModel()).moveRow(row, row, row - 1);
					
					table.getSelectionModel().setSelectionInterval(row - 1,  row - 1);
					
				}
				
			}
	    	
	    });
	    
	    table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "DOWN");
	    table.getActionMap().put("DOWN", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int row = table.getSelectedRow();
				
				if(row < table.getRowCount() - 1){
					
					((DefaultTableModel)table.getModel()).moveRow(row, row, row + 1);
					
					table.getSelectionModel().setSelectionInterval(row + 1,  row + 1);
					
				}
				
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
				
				int row = table.getSelectedRow();
				
				if(row >= 0){
										
					((DefaultTableModel)table.getModel()).removeRow(row);
					
				}
				
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
	
	public class AlphabetDocument extends PlainDocument {

		private static final long serialVersionUID = 655037934344680384L;
		
		private String text = "";

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
	
	public class EditabeByArgumentTypeModel extends DefaultTableModel {
		
		private static final long serialVersionUID = -3260475730036854273L;

		private EditabeByArgumentTypeModel() {
	    	
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
	
}
