
public class Couple {
	
	public final int x;
	public final int y;
	
	Couple(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		Couple c = (Couple) obj;
		return (x == c.x && y == c.y) || (x == c.y && y == c.x);
	}
	
	@Override
	public int hashCode() {
		return 0;
	};
	
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
