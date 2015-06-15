import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

public class Automata {
	
	/* Rabin Input Free Automata */
	
	public final String name;
	public final int Q;
	public final int A;
	public final HashSet<Couple>[] Delta;
	public final int q0 = 0;
	public final BitSet[] Omega;
	
	Automata(String name, HashSet<Couple>[] Delta, BitSet[] Omega) {
		this.name = name;
		this.Q = Delta.length;
		this.A = 1;
		this.Delta = Delta;
		this.Omega = Omega;
	}

	Automata( String name, int n_states, int n_letters ) {
		
		this.name = name;
		this.Q = Math.max( n_states, 0 );
		this.A = Math.max( n_letters, 0 );
		this.Delta = new HashSet[n_states];
		this.Omega = new BitSet[n_states];
		
		for ( int i = 0; i < n_states; i++ ) {
			Delta[i] = new HashSet<Couple>();
			Omega[i] = null;
		}
	}
	
	Automata(int n_states, String name) {
		this.Q = n_states;
		this.A = 1;
		
		BitSet[] Omega = new BitSet[Q];
		HashSet<Couple>[] Delta = new HashSet[Q];
		int n, rest, pos;
		Couple c;
		Random gen = new Random();
		
		for (int i = 0; i < Q; i++) {
			
			Delta[i] = new HashSet<Couple>();
			n = 1 + gen.nextInt(Q - 1 + Util.binomial(Q, 2));

			while (Delta[i].size() < n)
				Delta[i].add(new Couple(gen.nextInt(Q), gen.nextInt(Q)));
			
			Omega[i] = new BitSet();
			n = gen.nextInt((int) Math.pow(2, Q));
			pos = 0;
			
			while (n > 0) {
				rest = n % 2;
				n = n / 2;
				if (i == pos && rest == 1 && gen.nextBoolean()) {
					Omega[i] = null;
					n = 0; 
				} else if ((i != pos) && rest == 1 && gen.nextBoolean())
					Omega[i].set(pos);
				pos++;
			}
		}
		
		this.Delta = Delta;
		this.Omega = Omega;
		this.name = name;
	}
	
	void addTransition( int s, char l, int s0, int s1 ) {
		
		boolean test;
		int charNum = (int) l;
		test = s >= 0 && s0 >= 0 && s1 >= 0;
		test &= s < Q && s0 < Q && s1 < Q;
		test &= charNum >= 97 && charNum < 97 + A;
		
		Couple c = new Couple(s0, s1);
		
		if ( test )
			Delta[s].add( c );
		else
			Util.log(String.format("***transition (%d,%c,%d,%d) can't be added***", s, l, s0, s1));
	}
	
	
	void addPair( ArrayList<Integer> L, ArrayList<Integer> U ) {
		
		ArrayList<Integer> LU = new ArrayList<Integer>();
		LU.addAll( L );
		LU.addAll( U );
		
		// the states in L and U must be >= 0 and < Q
		for ( int n: LU )
			if ( n < 0 || n >= Q ) {
				Util.log("***accepting pair (" + listToSetNotation(L) + 
						"," + listToSetNotation(U) + ") can't be added***" );
				return ;	// error
			}
		
		// we add to the automata the accepting pair
		for (int s: U)
			if ( Omega[s] == null )
				Omega[s] = list2BitSet( L );
			else
				Omega[s].and( list2BitSet( L ) );
	}
	
	boolean existsAGoodTransition() {
		for ( HashSet<Couple> set: Delta ) 
			for ( Couple c: set )
				if ( Omega[c.x] != null && Omega[c.y] != null )
					return true;
		return false;
	}
	
	private BitSet list2BitSet( ArrayList<Integer> list ) {
		BitSet bs = new BitSet( Q );
		for ( int n: list )
			bs.set( n );
		return bs;
	}
	
	private String listToSetNotation( ArrayList<Integer> list ) {
		String res = "";
		for ( int n: list)
			res += n + ",";
		return "[" + res.replaceAll("\\,$", "") + "]";
	}
	
	@Override
	public String toString() {
		
		String str = Util.NL + "-- INPUT FREE AUTOMATA --" + Util.NL + "name := IF-" + name + ";" + Util.NL;
		str += "states := " + Q + ";" + Util.NL;
		str += "letters := 1;" + Util.NL;
		str += "transitions := " + Util.NL;
		
		String trans = "";
		// writing transitions
		for ( int i = 0; i < Q; i++ )
			if ( ! Delta[i].isEmpty() ) {
				for ( Couple c: Delta[i] )
					trans += String.format("(%d,a,%d,%d),", i, c.x, c.y);
				trans += Util.NL;
			}
				
		trans = trans.trim().replaceAll("\\,$", "");
		str += "[" + trans + "];" + Util.NL;
		str += "pairs := " + Util.NL;
		
		// writing accepting pairs
		String pairs = "";
		for ( int i = 0; i < Q; i++ )
			if ( Omega[i] != null )
				pairs += "(" + Omega[i] + ",[" + i + "])," + Util.NL;
		
		pairs = pairs.trim().replaceAll("\\,$", "");
		str += "[" + pairs + "];";
		
		str = str.replace('{', '[');
		str = str.replace('}', ']');
		
		return str;
	}
}
