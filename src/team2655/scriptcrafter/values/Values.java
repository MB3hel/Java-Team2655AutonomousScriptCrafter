package team2655.scriptcrafter.values;

public interface Values {
	
	public final int VERSION_MAJOR = 2;
	public final int VERSION_MINOR = 1;
	public final int VERSION_BUILD = 1;
	
	public final String RELEASE_TYPE = "";
	public final String ARGUMENT_TYPE_NUMERIC = "Numeric";
	public final String ARGUMENT_TYPE_NONE = "None";
	public final String[] ARGUMENT_TYPES = {" ", ARGUMENT_TYPE_NUMERIC, ARGUMENT_TYPE_NONE};
	public final static String FILE_NAME_PATTERN = "^[a-zA-Z0-9_-]+$";
	
}
