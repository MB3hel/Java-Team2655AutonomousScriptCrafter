package team2655.scriptcrafter.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.CellEditor;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import team2655.scriptcrafter.gui.ConfigEditor;
import team2655.scriptcrafter.gui.ScriptCrafter;
import team2655.scriptcrafter.listener.CommandsListener;
import team2655.scriptcrafter.values.Values;

public class ScriptCrafterEngine implements ActionListener, WindowListener, Values, CommandsListener, ItemListener, PopupMenuListener, TableModelListener {
	
	ScriptCrafter crafter;
	
	public ScriptCrafterEngine(ScriptCrafter crafter){
		
		this.crafter = crafter;
		
	}
	
	//Handles Button Presses
	@Override
	public void actionPerformed(ActionEvent e){
		
		Object src = e.getSource();
		
		if(src == crafter.btnEditCommands){
			
			new ConfigEditor(this);
			
		}else if(src == crafter.btnFileNew){
			
			try{
				
				Object returned = JOptionPane.showInputDialog(new JDialog(), "New file name: ", "Create New File ", JOptionPane.QUESTION_MESSAGE);
				
				if(returned != null){
					
					if(returned.toString().matches(FILE_NAME_PATTERN)){
						
						CSVController.createScript(returned.toString());
						
						try{
							
							CSVController.saveFileQuiet(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel)crafter.table.getModel());
						
						}catch(Exception error){
							
							System.out.println(error.getClass().getName() + ": " + error.getMessage());
							
						}
						
						crafter.clearTable();
						crafter.scanFiles(returned.toString());
						CSVController.loadScript(returned.toString(), (DefaultTableModel)crafter.table.getModel(), crafter);
						
					}else{
						
						JOptionPane.showMessageDialog(new JDialog(), "Invalid file name. File names can only contain letters, numbers and dashes or underscores.", "Invalid Name!", JOptionPane.WARNING_MESSAGE);
						
					}
					
				}
				
			}catch(Exception er){
				
				System.out.println("ERROR AT CREATE.....:::::\n\n\n");
				er.printStackTrace();
				System.out.println("END.....:::::\n\n\n\n\n");
				
			}
			
		}else if(src == crafter.btnFileRename){
						
			Object returned = JOptionPane.showInputDialog(new JDialog(), "Rename to: ", "Rename '" + crafter.fileSelector.getSelectedItem().toString() + "'", JOptionPane.QUESTION_MESSAGE, null, null, crafter.fileSelector.getSelectedItem().toString());
			
			if(returned != null){
				
				if(returned.toString().matches(FILE_NAME_PATTERN)){
					
					boolean done = CSVController.renameFile(crafter.fileSelector.getSelectedItem().toString(), returned.toString());
					
					if(done){
						
						crafter.scanFiles(returned.toString());
						
					}
					
				}else{
					
					JOptionPane.showMessageDialog(new JDialog(), "Invalid file name. File names can only contain letters, numbers and dashes or underscores.", "Invalid Name!", JOptionPane.WARNING_MESSAGE);
					
				}
				
			}
			
		}else if(src == crafter.btnFileDelete){
			
			int returned = JOptionPane.showConfirmDialog(new JDialog(), "Are you sure you want to delete '" + crafter.fileSelector.getSelectedItem().toString() + "'?", "Delete?", JOptionPane.YES_NO_OPTION);
			if(returned == JOptionPane.YES_OPTION){
				
				try {
					
					CSVController.deleteFile(crafter.fileSelector.getSelectedItem().toString());
					crafter.scanFiles(null);
					CSVController.loadScript(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel) crafter.table.getModel(), crafter);
					
				} catch (Exception er) {er.printStackTrace();}
				
				
			}
			
		}else if(src == crafter.btnSave){
			
			try{
				
				CellEditor editor = crafter.table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveFile(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel)crafter.table.getModel());
				
			}catch(Exception er){
				
				JOptionPane.showMessageDialog(new JDialog(), "Save failed (is the driver station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
				
			}
			
		}else if(src == crafter.btnDiscard){
						
			int rtn = JOptionPane.showConfirmDialog(new JDialog(), "Are you sure you want to discard ALL changes since the last save?", "Discard?", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(rtn == JOptionPane.YES_OPTION){
				
				try {
					
					DefaultTableModel model = (DefaultTableModel) crafter.table.getModel();
     			   
     			   	for (int i = model.getRowCount() - 1; i >= 0; i--) {
     			   	
     			   		model.removeRow(i);
     			    
     			   	}
     			   	
     			   	CSVController.loadScript(crafter.fileSelector.getSelectedItem().toString(), model, crafter);
					
				} catch (Exception er) {}
				
			}
			
		}else if(src == crafter.btnUp){
			
			crafter.moveRowUp();
			
		}else if(src == crafter.btnDown){
			
			crafter.moveRowDown();
			
		}else if(src == crafter.btnDelete){
			
			crafter.deleteRow();
			
		}
		
	}
	
	//Handles window events
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		
		int rtn = JOptionPane.showConfirmDialog(new JDialog(), "Save before exit?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(rtn == JOptionPane.YES_OPTION){
			
			crafter.removeBlankRows();
			
			try {
				
				CellEditor editor = crafter.table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveFile(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel)crafter.table.getModel());
				System.exit(0);
				
			} catch (Exception er) {
				
				int returned = JOptionPane.showConfirmDialog(new JDialog(), "The file was not saved (is the driver station open?). Exit anyways (will discard changes)?", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				if(returned == JOptionPane.YES_OPTION){
					
					System.exit(0);
					
				}else{
					
					
					
				}
				
			}
						
		}else if(rtn == JOptionPane.NO_OPTION){
			
			System.exit(0);
			
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
	
	@Override
	public void commandsChanged() {
		
		crafter.setupColumnTypes();
		
	}
	
	//File selector events
	@Override
    public void itemStateChanged(ItemEvent e) {
		
		Object src = e.getSource();
		
		if(src == crafter.fileSelector && !crafter.scanning){
			    	   
			if (e.getStateChange() == ItemEvent.SELECTED) {
        	   
				String newItem = (String) crafter.fileSelector.getSelectedItem();
    		   
				if(newItem != null){
    			   
					if(!newItem.equals(crafter.lastFileSelected)){
        			   
						crafter.clearTable();
        			   
						try {
							
							CSVController.loadScript(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel) crafter.table.getModel(), crafter);
							crafter.lastFileSelected = crafter.fileSelector.getSelectedItem().toString();
						
						} catch (Exception er) {}
        			   	        				
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
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		
		if(crafter.fileSelector.getSelectedItem().toString().equals(crafter.lastFileSelected)){
			
			crafter.blankRowCorrection();
			
		} //Otherwise the load will run and take care of it from the itemEvent																									
		
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		
		crafter.scanFiles(crafter.fileSelector.getSelectedItem().toString());
		
		int rtn = JOptionPane.YES_OPTION;
		if(rtn == JOptionPane.YES_OPTION){
			
			crafter.removeBlankRows();
			
			try {
				
				CellEditor editor = crafter.table.getCellEditor();
				
				if(editor != null){
					
					editor.stopCellEditing();
					
				}
				
				CSVController.saveFileQuiet(crafter.fileSelector.getSelectedItem().toString(), (DefaultTableModel)crafter.table.getModel());
				
			} catch (Exception er) {
				
				JOptionPane.showMessageDialog(new JDialog(), "The file was not saved (is the driver station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				
				
			}
							
		}else if(rtn == JOptionPane.NO_OPTION){
			
			
		
		}
		
	}
	
	//Table change events
	@Override
	public void tableChanged(TableModelEvent e) {
		
		if(e.getColumn() == 0){
			
			try{
				
				ArrayList<String> commands = new ArrayList<>(Arrays.asList(CSVController.loadCommands()));
    			ArrayList<String> arguments = new ArrayList<>(Arrays.asList(CSVController.loadArguments()));
    			String command = ((DefaultTableModel)crafter.table.getModel()).getValueAt(e.getFirstRow(),  0).toString();
    			ArrayList<String> secArguments = new ArrayList<>(Arrays.asList(CSVController.loadSecondArguments()));
    			
    			String argType = "";
    			String secArgType = "";
    			
	        	try{
	        				
	        		argType = arguments.get(commands.indexOf(command));
	        		secArgType = secArguments.get(commands.indexOf(command));		
	        		
	        	}catch(Exception er){
	        		
	        		
	        		
	        	}
	        	
	        	if(argType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
	        		
	        		((DefaultTableModel)crafter.table.getModel()).setValueAt("", e.getFirstRow(), 1);
	        		
	        	}
	        	
	        	if(secArgType.equals(ARGUMENT_TYPE_NONE) || argType.trim().equals("")){
	        		
	        		((DefaultTableModel)crafter.table.getModel()).setValueAt("", e.getFirstRow(), 2);
	        		
	        	}
				
			}catch(Exception er){
				
				
				
			}
			
		}
		
		if(!crafter.removing){
			
			crafter.blankRowCorrection();
			
		}
		
	}
	
}
