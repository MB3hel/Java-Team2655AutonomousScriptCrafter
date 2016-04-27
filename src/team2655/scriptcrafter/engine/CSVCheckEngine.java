package team2655.scriptcrafter.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVCheckEngine {
	
	public final static String ARGUMENT_TYPE_INTEGER = "Numeric";
	public final static String ARGUMENT_TYPE_NONE = "None";
	
	String[] commands;
	String[] argumentTypes;
	String[] secArgTypes;
	
	int commandIndex = 0;
	
	public CSVCheckEngine(String[] commands, String[] argumentTypes, String[] secArgTypes){
		
		this.commands = commands;
		this.argumentTypes = argumentTypes;
		this.secArgTypes = secArgTypes;
				
	}
	
	@SuppressWarnings("unused")
	public String checkFile(File file) throws IOException{
		
		String dataString = "";
		
		ArrayList<String> lines = loadFromCSV(file);
		
		//Separate the lines by ','s
		for(int row = 0; row < lines.size(); row++){
			
			String[] columns = lines.get(row).split(","); //Get csv columns
			
			boolean commandCorrect = checkCommand(columns[0]); //Check command MUST BE FIRST
			boolean argumentCorrect = checkArgument(columns[1]); //Check argument
			boolean secondArg = checkSecondArgument(columns[2]); //Check 2nd arg
			
			if(!commandCorrect)
				dataString += "Line " + String.valueOf(row + 1) + ": Command Invalid!\n"; //Add error message
				
			if(!argumentCorrect)
				dataString += "Line " + String.valueOf(row + 1) + ": First Argument Invalid\n"; //Add error message
			/*if(!secondArg)
				dataString += "Line " + String.valueOf(row + 1) + ": Second Argument Invalid\n"; //Add error message*/
			
		}
		
		return dataString;
		
	}
	
	private ArrayList<String> loadFromCSV(File file){
		
		try{
			
			File inFile = file;
			
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			
			ArrayList<String> lines = new ArrayList<String>();
			
			//Read file by lines into ArrayList
			while(reader.ready()){
				
				lines.add(reader.readLine());
				
			}
			
			reader.close();
					
			return lines;
			
		}catch(Exception e){
			
			return null;
			
		}
		
	}
	
	//Check if command is in list of valid commands
	private boolean checkCommand(String command){
		
		boolean isCommand = false;
		
		for(int i = 0; i < commands.length; i++){
			
			if(command.equals(commands[i]) || (command.trim().equals(""))){
				
				isCommand = true;
				commandIndex = i;
				
			}
			
		}
		
		return isCommand;
		
	}
	
	private boolean checkArgument(String argument){
		
		boolean isArgument = false;
		
		String shouldBe = argumentTypes[commandIndex];
		
		
		if((shouldBe.equals(ARGUMENT_TYPE_NONE) && (argument.equals("") || argument.equals(" "))) || commands[commandIndex].trim().equals("")){
			
			isArgument = true;
		
		}
			
		if(shouldBe.equals(ARGUMENT_TYPE_INTEGER)){

			try{
				
				Double.parseDouble(argument);
				
				isArgument = true;
				
			}catch(NumberFormatException e){
				
				
				
			}
			
		}
		
		return isArgument;
		
	}
	
	private boolean checkSecondArgument(String argument){
		
		boolean isArgument = false;
		
		String shouldBe = secArgTypes[commandIndex];
		
		if((shouldBe.equals(ARGUMENT_TYPE_NONE) && (argument.equals("") || argument.equals(" "))) || commands[commandIndex].trim().equals("")){
			
			isArgument = true;
		
		}
			
		if(shouldBe.equals(ARGUMENT_TYPE_INTEGER)){
			
			try{
				
				Double.parseDouble(argument);
				
				isArgument = true;
				
			}catch(NumberFormatException e){
				
				
				
			}
			
		}
		
		return isArgument;
		
	}
	
}
