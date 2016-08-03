# FRC Team 2655 Autonomous Script Crafter

The Autonomous Script Crafter is a tool to allow anyone to build scripts for FRC robots in autonomous mode.

## Installation and Use:

### Prebuilt Installer/JAR(Recommended)

The easiest way to install the script crafter is to head over to the [releases page](https://github.com/MB3hel/Java-Team2655AutonomousScriptCrafter/releases) and download the latest installer(exe).

For linux or MAC OS X download the jar file. NOTE:You will need java installed.

### Build from source (Not Recommended):

*   Download the latest [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and install it and add javac to the path.
*   Clone or download the repository and open a command prompt or terminal window in the src directory where it weas cloned.
*   Type the following commands

        javac ./*/*/*.java
        java ./team2655/scriptcrafter/gui/ScriptCrafter

*   (OPTIONAL)To Package a jar file:

        jar -cfem ./ScriptCrafter.jar ./META-INF/MANIFEST.MF ./*/*/*