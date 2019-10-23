import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*; 

public class Main {
	
	final static boolean fromStdin = true;
	
	static BigInteger h, g, p;
	final static int bits = 53;
	
	final static SecureRandom rnd = new SecureRandom();
	
	static BigInteger x, y, d;
	
	static BigInteger xx = null;

	public static void main(String[] args) {
		
		if (fromStdin) {
			Scanner s = new Scanner(System.in);
			h = s.nextBigInteger();
			g = s.nextBigInteger();
			p = s.nextBigInteger();
			
			boolean isPrimitive = isPrimitive(g, p);
			System.out.println(g + " is primitive: " + isPrimitive);
			if (!isPrimitive) {
				return;
			}
		} else {
			p = BigInteger.probablePrime(bits, rnd);
//			g = new BigInteger(bits, rnd);
//			g = g.mod(p.subtract(BigInteger.ONE));
//			g = g.add(BigInteger.ONE).add(BigInteger.ONE);
			g = generateRandomPrimitive(p);
			
			xx = new BigInteger(bits, rnd);
			xx = xx.mod(p);
			
			System.err.println("x: " + xx);
			
			h = g.modPow(xx, p);
		}
		
		assert(p.isProbablePrime(Integer.MAX_VALUE));
		
//		System.out.println(extendedEuclid(BigInteger.valueOf(55), BigInteger.valueOf(11)));
		
		System.out.println("h: " + h);
		System.out.println("g: " + g);
		System.out.println("p: " + p);
		
		long start = System.currentTimeMillis();
		BigInteger x1 = calc(h, g, p);
		long end = System.currentTimeMillis();
		System.out.println(x1 + " vs " + xx + " = " + x1.equals(xx) + " " + 
				(xx != null ? g.modPow(xx, p).equals(g.modPow(x1, p)) : false));
		System.out.println("h == g^x mod p = " + h.equals(g.modPow(x1, p)));
		
		System.out.println("time: " + (end - start));
	}
	
	static public BigInteger generateRandomPrimitive(BigInteger p) {
		BigInteger g = null;
		
		while(g == null) {
			g = new BigInteger(bits, rnd);
			g = g.mod(p);
			
			if(! isPrimitive(g, p)) {
				g = null;
			}
		}
		
		
		return g;
	}
	
	static public boolean isPrimitive(BigInteger g, BigInteger p) {
		if (g.compareTo(BigInteger.ZERO) == 0) {
			return false;
		}
		
		// Check that g^2 != 1 mod p
		if (g.modPow(new BigInteger("2"), p).compareTo(BigInteger.ONE) == 0) {
			return false;
		}
		
		// Check that g^((p-1)/2) != 1 mod p
		BigInteger tmp = g.modPow(g, p.subtract(BigInteger.ONE).divide(new BigInteger("2")));
		if (tmp.compareTo(BigInteger.ONE) == 0) {
			return false;
		}
		
		return true;
	}
	
	static public BigInteger calc(BigInteger h, BigInteger g, BigInteger p) {
		BigInteger m = sqrt(p);
		assert(m.multiply(m).compareTo(p) >= 0);
		System.out.println("m: " + m);
		
		Map<Long, Integer> mem = new HashMap(m.intValue()+1);
		
		/* Baby Step*/
		System.out.println("Baby Step");
		
		BigInteger tmp = BigInteger.ONE;
		for(BigInteger r = BigInteger.ZERO; r.compareTo(m) < 0; r = r.add(BigInteger.ONE)) {
			if (r.mod(m.divide(BigInteger.valueOf(10))).equals(BigInteger.ZERO)) {
				System.out.print(r.multiply(BigInteger.valueOf(100)).divide(m) + "%..");
			}
			
//			mem.put(g.modPow(r, p), r);
			mem.put(tmp.longValue(), r.intValue());
			tmp = tmp.multiply(g).mod(p);
		}
		System.out.println("");
		
//		System.out.println("Memorization: " + mem);
		
		extendedEuclid(p, g);
		assert(g.multiply(y).mod(p).equals(BigInteger.ONE));
		BigInteger gInv = y.modPow(m, p);
		
		/* Giant Step */
		System.out.println("Giant Step");
		
		BigInteger q;
		BigInteger t;
		boolean finished = false;
//		do {
//			if (mem.containsKey(t)) {
//				finished = true;
//			} else {
//				t = h.multiply(gInv.modPow(q, p)).mod(p);
//				q = q.add(BigInteger.ONE);
//			}
////			System.out.println("q: " + q);
//		} while(q.compareTo(m) < 0 && !finished);
		
		q = BigInteger.ZERO;
		t = h;
		while(!finished && q.compareTo(m) < 0) {
			if (mem.containsKey(t.longValue())) {
				finished = true;
			} else {
				t = t.multiply(gInv);
				t = t.mod(p);
				q = q.add(BigInteger.ONE);
			}
		}
		
//		t = h.multiply(gInv.modPow(q, p)).mod(p);
//		System.out.println("t: " + t);
		
		BigInteger x = m.multiply(q).add(BigInteger.valueOf(mem.get(t.longValue())));
		
		return x;
	}
	
	static public void extendedEuclid(BigInteger a, BigInteger b) {
//		assert(a.compareTo(b) > 0);
		
		if (b.equals(BigInteger.ZERO)) {
			x = BigInteger.ONE;
			y = BigInteger.ZERO;
			d = a;
			return;
		}
		
		extendedEuclid(b, a.mod(b));
		BigInteger x1 = y;
		BigInteger y1 = x.subtract(a.divide(b).multiply(y));
		x = x1;
		y = y1;
	}
	
	static public BigInteger sqrt(BigInteger n) {
		BigInteger low, high, mid;
		final BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
		low = BigInteger.ZERO;
		high = n;
		mid = BigInteger.ZERO;
		
		boolean finished = false;
		while(low.compareTo(high.subtract(BigInteger.ONE)) < 0 && !finished) {
//			System.out.println(low + " " + mid + " " + high);
			mid = low.add(high).divide(two);
			BigInteger sq = mid.multiply(mid); 
			if (sq.compareTo(n) < 0) {
				low = mid;
			} else if (sq.compareTo(n) > 0) {
				high = mid;
			} else {
				finished = true;
			}
		}
		
		mid = low.add(high).divide(two);
		
		BigInteger sq = mid.multiply(mid);
		if (sq.compareTo(n) < 0) {
			mid = mid.add(BigInteger.ONE);
		}
		
		return mid;
	}

}
