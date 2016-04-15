package team2655.scriptcrafter.engine;

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

import javax.swing.table.DefaultTableModel;

public class CSVController {
	
	private static File routinesDir = new File(System.getProperty("user.home") + "/Desktop/Autonomous/");
	private static File userBackupDir = new File(System.getProperty("user.home") + "/Autonomous-BAK/");
	private static File systemBackupDir = new File(System.getenv("PUBLIC") + "/Autonomous-BAK/");
	private static File deleteBackupsDir = new File(System.getProperty("user.home") + "/Desktop/Autonomous/Deleted/");
	
	private static File[] routineDirs = {routinesDir, userBackupDir, systemBackupDir};
	
	public static void createScript(String name) throws IOException{
				
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + name + ".csv");
			
			script.mkdirs();
			script.createNewFile();
			
		}
		
	}
	
	public static void saveFile(String name, DefaultTableModel model) throws IOException{
				
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + name + ".csv");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(script));
			
			for(int r = 0; r < model.getRowCount(); r++){
				
				for(int c = 0; c < model.getRowCount(); r++){
					
					Object cellValue = model.getValueAt(r, c);
					
					if(cellValue == null || cellValue.toString().trim().equals("")){
						
						cellValue = " ";
						
					}
					
					writer.write((String)cellValue);
					
					if(c != model.getColumnCount()){
						
						writer.write(",");
						
					}
					
					writer.close();
					
				}
				
				writer.newLine();
				
			}
			
		}
		
	}
	
	public static void deleteFile(String name) throws FileNotFoundException, IOException{
		
		File script = new File(routinesDir.getAbsolutePath() + "/" + name + ".csv");
		File backup = new File(deleteBackupsDir.getAbsolutePath() + "/" + name + ".csv");
		
		FileInputStream in = new FileInputStream(script);
		FileOutputStream out = new FileOutputStream(backup);
		
		while(in.available() > 0){
			
			out.write(in.read());
			
		}
		
		in.close();
		out.close();
		
	}
	
	public static void renameFile(String currentName, String newName){
				
		for(File dir : routineDirs){
			
			File script = new File(dir.getAbsolutePath() + "/" + currentName + ".csv");
			File newScript = new File(dir.getAbsolutePath() + "/" + newName + ".csv");
			
			script.renameTo(newScript);
			
		}
		
	}
	
	public static void loadScript(String name, DefaultTableModel model) throws FileNotFoundException, IOException{
				
		File script = new File(routinesDir.getAbsolutePath() + "/" + name + ".csv");
		
		BufferedReader in = new BufferedReader(new FileReader(script));
		
		ArrayList<String> lines = new ArrayList<>();
		
		while(in.ready()){
			
			lines.add(in.readLine());
			
		}
		
		in.close();
		
		for(String line : lines){
			
			String[] columns = line.split(",");
			
			model.addRow(columns);
			
		}
		
	}
	
	public String[] listScripts(){
				
		return routinesDir.list();
		
	}
	
	public static void checkScript(String scriptName, String[] commands, String[] arguments) throws IOException{
		
		CSVCheckEngine checkEngine = new CSVCheckEngine(commands, arguments);
		checkEngine.checkFile(new File(routinesDir.getAbsolutePath()+ "/" + scriptName + ".csv"));
		
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
