package team2655.scriptcrafter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
import team2655.scriptcrafter.listener.CommandsListener;
import team2655.scriptcrafter.values.Values;

public class ConfigEditor extends JDialog implements WindowListener, Values, ActionListener {

	private static final long serialVersionUID = 889547632847065343L;
	private JScrollPane scrollPane;
	private JTable table;
	private ScriptCrafter scriptCrafter;
	private JPanel bottomPanel;
	private JButton btnSave;
	private JButton btnDiscard;
	private JButton btnUp;
	private JButton btnDown;
	private JButton btnDelete;
		
	Thread autoRowThread;
	
	public ConfigEditor(ScriptCrafter scriptCrafter){
		
		this.scriptCrafter = scriptCrafter;
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		
		scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		/*number = new JFormattedTextField(NumberFormat.getNumberInstance());
		number.setColumns(1);
		numberPanel.add(number);*/
		
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
		
		setupButtons();
		
		autoRowThread.start();
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setTitle("Configure Commands");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.requestFocus();
		
	}
	
	private void setupButtons(){
		
		btnSave.addActionListener(this);
		btnDiscard.addActionListener(this);
		btnUp.addActionListener(this);
		btnDown.addActionListener(this);
		btnDelete.addActionListener(this);
		
	}
	
	private void setupTable(){
		
		table.setModel(new EditabeByArgumentTypeModel());
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		model.addColumn("Command");
		model.addColumn("Argument 1 Type");
		model.addColumn("Argument 1 Name");
		model.addColumn("Argument 2 Type");
		model.addColumn("Argument 2 Name");
		
		table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(false);
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
	
	private void setupValues(){
		
		try{
			
			//number.setText(String.valueOf(CSVController.loadNumberOfArguments()));
			
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
			
			model.addRow(new String[]{"", "", "", "", ""});
						
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}
	
	private String[] getCommands(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 0);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	private String[] getArguments(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 1);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	private String[] getArgumentNames(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 2);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	private String[] getSecondArguments(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 3);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
	}
	
	private String[] getSecondArgumentNames(){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		ArrayList<String> data = new ArrayList<>();
		
		for(int i = 0; i < model.getRowCount(); i++){
			
			String value = (String)model.getValueAt(i, 4);
			
			data.add(value);
			
		}
		
		return data.toArray(new String[data.size()]);
		
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

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent arg0) {
		
		JDialog dialog = new JDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		
		int rtn = JOptionPane.showConfirmDialog(dialog, "Save before exit?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
		dialog.dispose();
		if(rtn == JOptionPane.YES_OPTION){
			
			autoRowThread.suspend();
			removeBlankRows();
			
			try {
				
				CellEditor editor = table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveConfigFile(getCommands(), getArguments(), getArgumentNames(), getSecondArguments(), getSecondArgumentNames());
				
				((CommandsListener)scriptCrafter).commandsChanged();
				this.dispose();
				
			} catch (Exception er) {
				
				int returned = JOptionPane.showConfirmDialog(new JDialog(), "The file was not saved (is the river station open?). Exit anyways (will discard changes)?", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				if(returned == JOptionPane.YES_OPTION){
					
					this.dispose();
					
				}else{
					
					autoRowThread.resume();				
				}
				
			}
			
			autoRowThread.resume();	
			
		}else if(rtn == JOptionPane.NO_OPTION){
			
			this.dispose();
			
		}
		
	}
	
	/*autoRowThread.stop();
		
		removeBlankRows();
		
		try{
			
			CellEditor editor = table.getCellEditor();
			
			if(editor != null){
				
				editor.stopCellEditing();
				
			}
			
			CSVController.saveConfigFile(getCommands(), getArguments(), getArgumentNames(), getSecondArguments(), getSecondArgumentNames());
			
			((CommandsListener)scriptCrafter).commandsChanged();
			
		}catch(Exception e){
			
			
			
		}
		
		this.dispose();*/

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
	
	public class EditabeByArgumentTypeModel extends DefaultTableModel {
		
		private static final long serialVersionUID = -3260475730036854273L;

		private EditabeByArgumentTypeModel() {
	    	
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

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
		
		if(src == btnSave){
			
			autoRowThread.suspend();
			removeBlankRows();
			
			try {
				
				CellEditor editor = table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveConfigFile(getCommands(), getArguments(), getArgumentNames(), getSecondArguments(), getSecondArgumentNames());
				
				((CommandsListener)scriptCrafter).commandsChanged();
				
			} catch (Exception er) {
				
				JOptionPane.showMessageDialog(new JDialog(), "The file was not saved (is the river station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				
			}
			
			autoRowThread.resume();
			
		}else if(src == btnDiscard){
			
			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
			
			int rtn = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to discard ALL changes since the last save?", "Discard?", JOptionPane.YES_NO_CANCEL_OPTION);
			dialog.dispose();
			if(rtn == JOptionPane.YES_OPTION){
				
				try {
					
					DefaultTableModel model = (DefaultTableModel) table.getModel();
     			   
     			   	for (int i = model.getRowCount() - 1; i >= 0; i--) {
     			   	
     			   		model.removeRow(i);
     			    
     			   	}
     			   	
     			   	setupValues();
     			   	
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
	
}
