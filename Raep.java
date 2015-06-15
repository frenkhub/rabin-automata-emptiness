import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class Raep {
	
	private static final String HELP = Util.NL + "USAGE" + Util.NL +
			"  1) java -jar <jar file> -path <file>" + Util.NL +
			"  2) java -jar <jar file> -random <number of states> <number of automata>" + Util.NL +
			"  3) java -jar <jar file> -scan <number of states>" + Util.NL +
			"  4) java -jar <jar file> -findEmpty <number of states>" + Util.NL +
			"  5) java -jar <jar file> -find <number of states>" + Util.NL + 
			"  5) java -jar <jar file> " + "-help" + Util.NL + Util.NL + 
			"FILE EXAMPLE:" + Util.NL + 
			"  #this is a comment#" + Util.NL +
			"  name := limited-a-on-every-path;" + Util.NL +
			"  states := 5;" + Util.NL +
			"  letters := 3;" + Util.NL +
			"  transitions := [(0,a,1,1),(0,b,2,2),(0,c,2,2)," + Util.NL +
			"   (1,a,1,1),(1,b,2,2),(1,c,2,2)," + Util.NL +
			"   (2,a,1,1),(2,b,2,2),(2,c,2,2)];" + Util.NL +
			"  pairs := [([0,1],[2])];";
	
	public static void main(String[] args) {
		
		File file;
		
		Automata automata = null;
		MyAutomataScanner scanner = null;
		
		switch (args[0].toLowerCase()) {
			case "-path":
				file = new File( args[1] );
				automata = Util.file2Automata( file );
				MyDRunScanner finder = new MyDRunScanner(automata);
				finder.scanDRuns();
				Util.log(automata.toString());
				break;
			case "-scan":
				scanner = new MyAutomataScanner(Integer.parseInt(args[1]));
				scanner.scanAutomatas();
				break;
			case "-random":
				scanner = new MyAutomataScanner(Integer.parseInt(args[1]));
				scanner.scanRandomAutomatas(Integer.parseInt(args[2]));
				break;
			case "-find":
				scanner = new MyAutomataScanner(Integer.parseInt(args[1]));
				scanner.setMode(MyAutomataScanner.FIND_NOT_EMPTY);
				scanner.scanRandomAutomatas(0);
				break;
			case "-findempty":
				scanner = new MyAutomataScanner(Integer.parseInt(args[1]));
				scanner.setMode(MyAutomataScanner.FIND_EMPTY);
				scanner.scanRandomAutomatas(0);
				break;
			default:
				Util.log(HELP);
				break;
		}
	}

}
