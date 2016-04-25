package team2655.scriptcrafter.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.CellEditor;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import team2655.scriptcrafter.gui.ConfigEditor;
import team2655.scriptcrafter.values.Values;

public class ConfigEditorEngine implements ActionListener, TableModelListener, WindowListener, Values {
	
	ConfigEditor editor;
	
	public ConfigEditorEngine(ConfigEditor editor){
		
		this.editor = editor;
		
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
		JDialog dialog = new JDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setAlwaysOnTop(true);
		
		int rtn = JOptionPane.showConfirmDialog(dialog, "Save before exit?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
		if(rtn == JOptionPane.YES_OPTION){
			
			editor.removeBlankRows();
			
			try {
				
				CellEditor cEditor = editor.table.getCellEditor();
				
				if(cEditor != null){
					
					cEditor.stopCellEditing();
					
				}
				
				CSVController.saveConfigFile(editor.getCommands(), editor.getArguments(), editor.getArgumentNames(), editor.getSecondArguments(), editor.getSecondArgumentNames());
				
				editor.listener.commandsChanged();
				editor.dispose();
				
			} catch (Exception er) {
				
				JDialog dialog2 = new JDialog();
				dialog2.setLocationRelativeTo(null);
				dialog2.setAlwaysOnTop(true);
				
				int returned = JOptionPane.showConfirmDialog(dialog2, "The file was not saved (is the driver station open?). Exit anyways (will discard changes)?", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				if(returned == JOptionPane.YES_OPTION){
					
					editor.dispose();
					
				}
				
			}
						
		}else if(rtn == JOptionPane.NO_OPTION){
			
			editor.dispose();
			
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
		
		if(src == editor.btnSave){
			
			editor.removeBlankRows();
			
			try {
				
				CellEditor cEditor = editor.table.getCellEditor();
				
				if(cEditor != null){
					
					cEditor.stopCellEditing();
					
				}
				
				CSVController.saveConfigFile(editor.getCommands(), editor.getArguments(), editor.getArgumentNames(), editor.getSecondArguments(), editor.getSecondArgumentNames());
				
				editor.listener.commandsChanged();
				
			} catch (Exception er) {
				
				JDialog dialog = new JDialog();
				dialog.setLocationRelativeTo(null);
				dialog.setAlwaysOnTop(true);
				
				JOptionPane.showMessageDialog(dialog, "The file was not saved (is the driver station open?).", "Save Error!", JOptionPane.ERROR_MESSAGE);
				er.printStackTrace();
				
			}
						
		}else if(src == editor.btnDiscard){
			
			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
			
			int rtn = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to discard ALL changes since the last save?", "Discard?", JOptionPane.YES_NO_CANCEL_OPTION);
			dialog.dispose();
			if(rtn == JOptionPane.YES_OPTION){
				
				try {
					
					DefaultTableModel model = (DefaultTableModel) editor.table.getModel();
     			   
     			   	for (int i = model.getRowCount() - 1; i >= 0; i--) {
     			   	
     			   		model.removeRow(i);
     			    
     			   	}
     			   	
     			   editor.setupValues();
     			   	
				} catch (Exception er) {}
				
			}
			
		}else if(src == editor.btnUp){
			
			editor.moveRowUp();
			
		}else if(src == editor.btnDown){
			
			editor.moveRowDown();
			
		}else if(src == editor.btnDelete){
			
			editor.deleteRow();
			
		}
		
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		
		if(!editor.removing){
			
			editor.blankRowCorrection();
			
		}
		
	}
	
}
