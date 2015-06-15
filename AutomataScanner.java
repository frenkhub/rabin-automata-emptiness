import java.util.BitSet;
import java.util.HashSet;

/*
 * NB:
 * 1 state = 2 automata
 * 2 state = 784 automata
 * 3 state = 1393796574908163946327092926109044013269056 automata
 */

public abstract class AutomataScanner {
	
	/* This class generates all the Input Free Automata with Q states, with only 1 initial states,
	 * with at least one exit transition for each state, and with Q accepting pairs. Each pair has
	 * a distinct UNLIMITED set, containing just one state: for this reason the accepting pairs
	 * are described with an array of size Q, and the index of the array represent the UNLIMITED
	 * set.
	 * For each generated automata the evaluation method is called.*/

	boolean stop;
	final int Q;
	final HashSet<Couple>[] Delta;
	final BitSet[] Omega;
	long index;
	Automata automata;
	
	// PRE: n_states > 0
	public AutomataScanner(int n_states) {
		this.Q = n_states;
		this.Delta = new HashSet[Q];
		this.Omega = new BitSet[Q];
		for (int q = 0; q < Q; q++) {
			Delta[q] = new HashSet<Couple>();
			Omega[q] = new BitSet();
		}
	}
	
	public void scanRandomAutomatas(long n) {
		index = 0;
		stop = false;
		preScan();
		
		if (n > 0)
			for (int i = 0; i < n; i++) {
				automata = new Automata(Q, "automata" + ++index);
				evaluate();
			}
		else
			while (!stop) {
				automata = new Automata(Q, "automata" + ++index);
				evaluate();
			}
		
		postScan();
	}
	
	public void scanAutomatas() {
		this.index = 0;
		preScan();
		powerDelta(0, new Couple(0,0));
		postScan();
	}
	
	private void powerDelta(int q, Couple c) {
		
		if (c == null) {
			
			if (! Delta[q].isEmpty())
				
				if (q < Q-1)
					powerDelta(q + 1, new Couple(0,0));
				else
					powerOmega(0, 0);
					
		} else {
			// compute the next couple
			Couple next = null;
			if (c.x == c.y && c.y == Q-1)
				next = null;
			else if (c.x == c.y)
				next = new Couple(0, c.y + 1);
			else if (c.y > c.x)
				next = new Couple(c.x + 1, c.y);
			
			Delta[q].add(c);
			powerDelta(q, next);
			Delta[q].remove(c);
			powerDelta(q, next);
		}
	}
	
	private void powerOmega(int q, int x) {
		
		if (x == -1) {
			
			if (q < Q-1)
				powerOmega(q + 1, 0);
			else {
				automata = new Automata("automata" + ++index, Delta, Omega);
				evaluate();
			}
			
		} else {
			int next = (x == Q-1) ? -1 : x+1;
			Omega[q].set(x);
			powerOmega(q, next);
			Omega[q].clear(x);
			powerOmega(q, next);
		}
	}
	
	public abstract void preScan();
	public abstract boolean evaluate();
	public abstract void postScan();
}
