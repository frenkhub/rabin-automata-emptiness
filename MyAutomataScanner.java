import java.util.Locale;

public class MyAutomataScanner extends AutomataScanner {

	static final int FIND_EMPTY = 0;
	static final int FIND_NOT_EMPTY = 1;
	static final int FIND_ALL = 2;
	private int mode = FIND_ALL;
	private long empty = 0;

	public MyAutomataScanner(int n_states) {
		super(n_states);
		mode = FIND_ALL;
	}
	
	public MyAutomataScanner(int n_states, int mode) {
		super(n_states);
		this.mode = mode;
	}
	
	public void preScan(){
		empty = 0;
	}
	
	@Override
	public boolean evaluate() {
		MyDRunScanner scanner = new MyDRunScanner(super.automata);
		scanner.verbose(false);
		scanner.scanDRuns();
		
		switch (mode) {
		
		case FIND_ALL:
			Util.log(automata.toString());
			
			if (!scanner.found) {
				empty++;
				Util.log("empty: yes");
			} else
				Util.log("empty: no");
			
			break;
			
		case FIND_EMPTY:
			if (!scanner.found) {
				Util.log(automata.toString());
				stop = true;
				return true;
			}
			
			break;
			
		case FIND_NOT_EMPTY:
			if (scanner.found) {
				Util.log(automata.toString());
				stop = true;
				return true;
			}
			
			break;
		}
		
		return false;
	}

	public boolean preEvaluate() {
		empty = 0;
		return true;
	}

	public void postScan() {
		if (mode == FIND_ALL) {
			double perc = (double) (empty * 100) / index;
			Util.log(Util.NL + "-- STATS INPUT FREE AUTOMATA WITH " + super.Q + " STATES --");
			Util.log("Evaluated: " + super.index);
			Util.log(String.format(Locale.ENGLISH, "Empty: %d (%.2f%%)", empty, perc));
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}
