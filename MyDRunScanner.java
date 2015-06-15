import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;


public class MyDRunScanner extends DRunScanner {
	
	boolean showPreTest = true; 
	boolean showEvaluate = true;
	boolean showPostTest = true; 
	boolean verbose = true;
	
	
	public void verbose(boolean b) {
		verbose = b;
	}
	
	public MyDRunScanner(Automata a) {
		super(a);
	}
	
	public boolean preTest() {
		return true;
	}

	public boolean evaluate() {
		int BAD = 0, GOOD = 1;
		
		// test
		BitSet reachables = reachableStates();
		BitSet elementaryCycles[] = findElementaryCycles(reachables);
		BitSet cycles[] = findCycles( elementaryCycles );
		BitSet[][] badAndGoodCycles = splitCycles( cycles );
		boolean isSuccesful = found = badAndGoodCycles[BAD].length == 0;

		// show current deterministic run
		if (verbose) {
			Util.log(Util.NL + "-- RUN " + index + " --");
			for (int q = 0; q < dRun.length; q++)
				Util.log(q + " --> " + dRun[q] + (reachables.get(q)? "" : "**"));
			
			Util.log("Good Cycles (" + badAndGoodCycles[GOOD].length + "): " + cycles2String(badAndGoodCycles[GOOD]));
			Util.log("Bad Cycles (" + badAndGoodCycles[BAD].length + "): " + cycles2String(badAndGoodCycles[BAD]));
		}
		
		return isSuccesful;
	}
	
	public void postTest() {
		if (verbose) {
			Util.log(Util.NL + "-- RESULT --");
			Util.log("Language empty: " + ! found);
			Util.log("Tested Run: " + index + "/" + numDRun);
		}
	}
	
	private BitSet reachableStates() {	// calculate the set of reachable states
		
		int state;
		BitSet reachables = new BitSet( dRun.length );  
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(0);
		
		while ( ! queue.isEmpty() ) {
			
			// pop a state from the queue, and add to the set
			state = queue.get(0);
			queue.remove(0);
			reachables.set(state);
			
			if ( ! reachables.get( succ0(state) ) )
				queue.add( succ0(state) );
				
			if ( ! reachables.get( succ1(state) ) )
				queue.add( succ1(state) );
		}
		
		return reachables;
	}
	
	private BitSet[] findElementaryCycles( BitSet reachables ) {
			
		ArrayList<BitSet> elementaryCycles = new ArrayList<BitSet>();
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(0);
		BitSet bitSet;
		int last, parent, son;
		
		for ( int source = dRun.length - 1; source >= 0; source-- )
			
			if ( reachables.get(source) ) {
				
				// we find all the elementary cycles with "source" as minimum
				bitSet = new BitSet();
				queue.add(source);
				
				while ( ! queue.isEmpty() ) {
					
					last = queue.get( queue.size() - 1 );
					
					if ( ! bitSet.get(last) && source <= last ) {
						// elementary cycle not founded
						bitSet.set(last);
						queue.add( succ0(last) );
						
					} else {
						
						if ( last == source )
							// elementary cycle founded with s as minimum! 
							elementaryCycles.add( (BitSet) bitSet.clone() );
						
						// (queue.length > 1) holds here
						// now we perform the backtracking
						queue.remove( queue.size() - 1 );
						son = last;
						parent = queue.get( queue.size() - 1 );
						
						while ( son == succ1( parent ) && queue.size() > 1 ) {
							queue.remove( queue.size() - 1 );
							bitSet.clear( parent );
							son = parent;
							parent = queue.get( queue.size() - 1 );
						}
						
						if ( son != succ1( parent ))
							// the search isn't ended
							// we insert in the queue the 1-successor of "parent"
							queue.add( succ1( parent ) );
						else 
							// queue.size() == 1
							// we founded all cycles with "source" as minimum
							queue.remove(0);
					}
				}
			}
		
		return elementaryCycles.toArray(new BitSet[elementaryCycles.size()]);
	}
	
	private BitSet[] findCycles( BitSet[] elementaryCycles ) {
		
		HashSet<BitSet> cycles = new HashSet<BitSet>( Arrays.asList(elementaryCycles) );
		HashSet<BitSet> newCycles;
		BitSet c;
		int prevSize;
		
		do {
			prevSize = cycles.size();
			newCycles = new HashSet<BitSet>();
			c = new BitSet();
			
			for (BitSet a: cycles)
				for (BitSet b: cycles)
					if (a.intersects(b)) {
						c = (BitSet) a.clone();
						c.or(b);
						newCycles.add(c);
					}
			
			cycles.addAll(newCycles);
			
		} while (cycles.size() != prevSize);
		
		return cycles.toArray( new BitSet[cycles.size()] );
	}
	
	private BitSet[][] splitCycles( BitSet[] allCycles) {
		
		HashSet<BitSet> badCycles = new HashSet<BitSet>();
		HashSet<BitSet> goodCycles = new HashSet<BitSet>();
		
		for ( BitSet cycle: allCycles )
			for ( int u = 0; u < dRun.length; u++ ) {
				badCycles.add(cycle);
				if ( automata.Omega[u] != null)
					if ( cycle.get(u) && ! cycle.intersects( automata.Omega[u] ) ) {
						badCycles.remove( cycle );
						goodCycles.add(cycle);
						break;
					}
			}
		
		return new BitSet[][] { 
				badCycles.toArray( new BitSet[badCycles.size()] ),
				goodCycles.toArray( new BitSet[goodCycles.size()] ) };
	}
		
	private int succ0( int state ) {
		return dRun[state].x;
	}
		
	private int succ1( int state ) {
		return dRun[state].y;
	}
	
	String cycles2String( BitSet[] sets ){
		String str = "";
		for ( BitSet set: sets )
			str += set + "," + Util.NL;
		return Util.NL + "[" + str.trim().replaceAll("\\,$", "") + "]";
	}
}
