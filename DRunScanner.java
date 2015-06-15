import java.util.HashSet;

public abstract class DRunScanner {

	/* Find deterministic run */

	final Automata automata;
	boolean found;
	Couple[] dRun;
	long index;
	long numDRun;
	

	DRunScanner(Automata a) {
		this.automata = a;
		this.dRun = new Couple[a.Q];
		
		int _numDRun = 1;
		for (HashSet<Couple> transitions: a.Delta)
			_numDRun *= transitions.size();
		this.numDRun = _numDRun;
	}

	public void scanDRuns() {
		if (preTest()) {
			found = find(0);
			postTest();
		}
	}

	private boolean find(int q) {
		if (q == automata.Q) {
			index++;
			return evaluate();
		}
		else
			for (Couple c : automata.Delta[q]) {
				dRun[q] = c;
				if (find(++q))
					return true;
				q--;
			}
		return false;
	}
	
	public abstract boolean preTest();
	public abstract boolean evaluate();
	public abstract void postTest();
}
