import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {
	
	public static final String NL = System.getProperty("line.separator");
	
	private static final String INT = "( [1-9] \\d* | [0] )"; 	// integer
	private static final String TRANSITION = "\\(" + INT + "\\, ( [a-z] ) \\," + INT + "\\," + INT + "\\)";
	private static final String SET = "( \\[ \\] | \\[ " + INT + "( \\," + INT + ")* \\] )";
	private static final String PAIR = "\\(" + SET + "\\," + SET + "\\)";
	private static final String T_LIST = "( \\[ \\] | \\[ " + TRANSITION + "( \\," + TRANSITION + ")* \\] )";
	private static final String P_LIST = "( \\[ \\] | \\[ " + PAIR + "( \\," + PAIR + ")* \\] )"; 
	
	
	private static final String REGEX = 
		"name := ([a-zA-Z0-9_-]*+) ;" +
		"states :=(" + INT + ");" +
		"letters :=(" + INT + ");" + 
		"transitions :=(" + T_LIST + ");" +
		"pairs :=(" + P_LIST + ");";

	
	public static Automata file2Automata( File file  ) {
		
		String input = "";
		Matcher matcher_automata = null;
		
		try {
			input = ( new Scanner( file, "UTF-8") ).useDelimiter("\\A").next();
			input = input.replaceAll("\\s", ""); // remove spaces
			input = input.replaceAll("\\#.*\\#", ""); // remove comments
			matcher_automata = Pattern.compile( REGEX, Pattern.COMMENTS ).matcher( input );
			
			// parsing error
			if ( ! matcher_automata.matches() ) {
				Util.log("***parsing error in " + file.getAbsolutePath() + "***");
				return null;
			}
			
		} catch (IOException e) {
			// problem with the file
			Util.log("***" + e.getMessage() + "***");
			return null;
		}

		String name = matcher_automata.group(1);
		int states = Integer.parseInt( matcher_automata.group(3) );
		int letters = Integer.parseInt( matcher_automata.group(4) );
		
		Matcher mTrans = Pattern.compile( TRANSITION, Pattern.COMMENTS ).matcher( input );
		Matcher mSet = Pattern.compile( SET, Pattern.COMMENTS ).matcher( input );
		Pattern pInt = Pattern.compile( INT, Pattern.COMMENTS );
		Matcher mInt;
		
		Automata automata = new Automata( name, states, letters );
		
		// parsing transitions
		while ( mTrans.find() )
			automata.addTransition(
					Integer.parseInt( mTrans.group( 1 ) ),
					mTrans.group( 2 ).charAt( 0 ),
					Integer.parseInt( mTrans.group( 3 ) ),
					Integer.parseInt( mTrans.group( 4 ) ) );
		
		int part = 0;
		ArrayList<Integer> list0 = new ArrayList<Integer>();
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		
		// parsing accepting pairs
		while ( mSet.find() ) {
			
			mInt = pInt.matcher( mSet.group() );
			
			while ( mInt.find() )
				if ( part == 0 ) 	list0.add( Integer.parseInt( mInt.group() ) );
				else				list1.add( Integer.parseInt( mInt.group() ) );
			
			part++;
			
			if ( part == 2 ) {
				automata.addPair( list0, list1 );
				list0 = new ArrayList<Integer>();
				list1 = new ArrayList<Integer>();
				part = 0;
			}
		}
			
		return automata;
	}
	
	
	public static void log(String m) {
		System.out.println(m);
	}
	
	public static int fact(int n) {
		int res = 1;
		for (int i = n; i > 0; i--)
			res *= i;
		return res;
	}
	
	public static int binomial(int n, int k) {
		if (0 <= k && k <= n)
			return fact(n) / ( fact(k) * fact(n-k));
		
		return 0;
	}
}
