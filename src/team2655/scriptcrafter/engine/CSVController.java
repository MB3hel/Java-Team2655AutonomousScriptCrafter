package team2655.scriptcrafter.engine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import team2655.scriptcrafter.values.Values;

public class CSVController implements Values{
	
	private static File routinesDir = new File(System.getProperty("user.home") + "/Desktop/Autonomous/");
	private static File userBackupDir = new File(System.getProperty("user.home") + "/Autonomous-BAK/");
	private static File systemBackupDir = new File(System.getenv("PUBLIC") + "/Autonomous-BAK/");
	private static File backupFile = new File(System.getProperty("user.home") + "/Desktop/Backup/");
	private static File deleteBackupsDir = new File(System.getProperty("user.home") + "/Desktop/Deleted/");
	
	private static File[] routineDirs = {routinesDir}; //Routine non-backup locations
	private static File[] routineDirsBackups = {userBackupDir, systemBackupDir, backupFile}; //Backup locations
	
	public static void doBackup() throws IOException, FileNotFoundException{
		
		File[] files = routinesDir.listFiles();
		
		for(File file : files){
			
			for(File dir : routineDirsBackups){
				
				try {
					
					BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
					BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(dir.getAbsolutePath() + "/" + file.getName())));
					
					while(reader.available() > 0){
						
						writer.write(reader.read());
						
					}
					
					reader.close();
					writer.close();
					
				} catch (Exception e) {
					

					
				}

			}
			
		}
		
	}
	
	public static void createScript(String name) throws IOException{
				
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + name + ".csv");
			
			script.getParentFile().mkdirs();
			script.createNewFile();
			
		}
		
		try{
			
			doBackup();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(new JDialog(), "The automatic backup failed.", "Backup failed.", JOptionPane.WARNING_MESSAGE);
			
		}
		
	}
	
	public static void saveFileQuiet(String name, DefaultTableModel model) throws IOException{
		
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + name + ".csv");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(script));
			
			for(int r = 0; r < model.getRowCount(); r++){
				
				for(int c = 0; c < model.getColumnCount(); c++){
					
					Object cellValue = model.getValueAt(r, c);
					
					if(cellValue == null || cellValue.toString().trim().equals("")){
						
						cellValue = " ";
						
					}
					
					writer.write((String)cellValue);
					
					if(c + 1 != model.getColumnCount()){
						
						writer.write(",");
						
					}
					
				}
				
				writer.newLine();
				
			}
			
			writer.close();
			
		}
		
		try{
			
			doBackup();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(new JDialog(), "The automatic backup failed.", "Backup failed.", JOptionPane.WARNING_MESSAGE);
			
		}
		
	}
	
	public static void saveFile(String name, DefaultTableModel model) throws IOException{
		
		saveFileQuiet(name, model);
		checkScript(name, loadCommands(), loadArguments(), loadSecondArguments());
		
	}
	
	public static void deleteFile(String name) throws FileNotFoundException, IOException{
		
		File script = new File(routinesDir.getAbsolutePath() + "/" + name + ".csv");
		File backup = new File(deleteBackupsDir.getAbsolutePath() + "/" + name + ".csv");
		backup.getParentFile().mkdirs();
		
		if(backup.exists()){
			
			backup.delete();
			
		}
		
		backup.createNewFile();
		
		FileInputStream in = new FileInputStream(script);
		FileOutputStream out = new FileOutputStream(backup);
		
		while(in.available() > 0){
			
			out.write(in.read());
			
		}
		
		in.close();
		out.close();
		
		script.delete();
		
	}
	
	public static boolean renameFile(String currentName, String newName){
		
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + currentName + ".csv");
			File newScript = new File(dir.getAbsolutePath() + "/" + newName + ".csv");
			
			if(newScript.exists()){
				
				JOptionPane.showMessageDialog(new JDialog(), "That file already exists. Please choose a different name.", "File Exists!", JOptionPane.WARNING_MESSAGE);
				return false;
				
			}else{
				
				script.renameTo(newScript);
				
			}
			
		}
		
		try{
			
			doBackup();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(new JDialog(), "The automatic backup failed.", "Backup failed.", JOptionPane.WARNING_MESSAGE);
			
		}
		
		return true;
		
	}
	
	public static void loadScript(String name, DefaultTableModel model) throws FileNotFoundException, IOException{

		for(int r = 0; r < model.getRowCount();r++){
			
			model.removeRow(r);
			
		}
		
		File script = new File(routinesDir.getAbsolutePath() + "/" + name + ".csv");
		
		BufferedReader in = new BufferedReader(new FileReader(script));
		
		ArrayList<String> lines = new ArrayList<>();
		
		while(in.ready()){
			
			lines.add(in.readLine());
			
		}
		
		in.close();
		
		if(lines.size() > 0){
			
			for(String line : lines){
				
				String[] columns = line.split(",");
				
				model.addRow(columns);
				
			}
			
			if(model.getValueAt(0, 0).equals("")){
				
				if(model.getValueAt(0, 1).equals("")){
					
					if(model.getValueAt(0, 2).equals("")){
						
						model.removeRow(0);
						
					}
					
				}
				
			}
			
		}
		
		checkScript(name, loadCommands(), loadArguments(), loadSecondArguments());
		
	}
	
	public static String[] listScripts(){
				
		return routinesDir.list();
		
	}
	
	public static void checkScript(String scriptName, String[] commands, String[] arguments, String[] secArgs) throws IOException{
		
		CSVCheckEngine checkEngine = new CSVCheckEngine(commands, arguments, secArgs);
		String message = checkEngine.checkFile(new File(routinesDir.getAbsolutePath()+ "/" + scriptName + ".csv"));
		
		if(!message.trim().equals("")){
			
			JOptionPane.showMessageDialog(new JDialog(), message, "File Error!", JOptionPane.WARNING_MESSAGE);
			
		}
		
	}
	
	public static void saveConfigFile(String[] cmds, String[] args, String[] argNames, String[] secArgs, String[] secArgNames) throws IOException{
		
		for(File dir : routineDirs){
			
			File config = new File(dir.getAbsolutePath() + "/scriptcrafter.config");
			
			if(!config.exists()){
				
				config.mkdirs();
				config.createNewFile();
				
			}
			
			BufferedWriter out = new BufferedWriter(new FileWriter(config));
			
			//out.write("NUMBER," + arguments);
			
			for(int i = 0; i < cmds.length; i++){
				
				if(cmds[i].equals("")){
					
					cmds[i] = " ";
					
				}
				
				if(args[i].equals("")){
					
					args[i] = " ";
					
				}
				
				if(argNames[i].equals("")){
					
					argNames[i] = " ";
					
				}
				
				if(secArgs[i].equals("")){
					
					secArgs[i] = " ";
					
				}
				
				if(secArgNames[i].equals("")){
					
					secArgNames[i] = " ";
					
				}
				
				String row = cmds[i] + "," + args[i] + "," + argNames[i] + "," + secArgs[i] + "," + secArgNames[i];
				
				out.write(row);
				out.newLine();
				
			}
			
			out.close();
			
		}
		
	}
	
	public static void createConfigFile() throws IOException{
		
		for(File dir : routineDirs){
			
			File config = new File(dir.getAbsolutePath() + "/scriptcrafter.config");
			
			if(!config.exists()){
				
				config.getParentFile().mkdirs();
				config.createNewFile();
				
			}
			
		}
		
	}
	
	public static String[] loadCommands() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
		
		ArrayList<String> cmds = new ArrayList<>();
		
		//in.readLine(); //READ THE NUMBER LINE
		
		while(in.ready()){
			
			String line = in.readLine();
			cmds.add((line.split(","))[0]);
			
		}
		
		in.close();
		
		return cmds.toArray(new String[cmds.size()]);
		
	}
	
	public static String[] loadArguments() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
		
		ArrayList<String> args = new ArrayList<>();
		
		//in.readLine(); //READ THE NUMBER LINE
				
		while(in.ready()){
			
			String line = in.readLine();
			args.add((line.split(","))[1]);
			
		}
		
		in.close();
		
		return args.toArray(new String[args.size()]);
		
	}
	
	public static String[] loadSecondArguments() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
		
		ArrayList<String> args = new ArrayList<>();
		
		//in.readLine(); //READ THE NUMBER LINE
		
		while(in.ready()){
			
			String line = in.readLine();
			args.add((line.split(","))[3]);
			
		}
		
		in.close();
		
		return args.toArray(new String[args.size()]);
		
	}
	
	public static String[] loadArgumentsNames() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
		
		ArrayList<String> args = new ArrayList<>();
		
		//in.readLine(); //READ THE NUMBER LINE
		
		while(in.ready()){
			
			String line = in.readLine();
			args.add((line.split(","))[2]);
			
		}
		
		in.close();
		
		return args.toArray(new String[args.size()]);
		
	}
	
	public static String[] loadSecondArgumentsNames() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
		
		ArrayList<String> args = new ArrayList<>();
		
		//in.readLine(); //READ THE NUMBER LINE
		
		while(in.ready()){
			
			String line = in.readLine();
			args.add((line.split(","))[4]);
			
		}
		
		in.close();
		
		return args.toArray(new String[args.size()]);
		
	}
	
	/*public static int loadNumberOfArguments() throws IOException, FileNotFoundException{
		
		File config = new File(routinesDir.getAbsolutePath() + "/scriptcrafter.config");
		
		BufferedReader in = new BufferedReader(new FileReader(config));
				
		String line = in.readLine(); //READ THE NUMBER LINE
		
		in.close();
		
		return Integer.parseInt(line.split(",")[1]);
		
	}*/

}
