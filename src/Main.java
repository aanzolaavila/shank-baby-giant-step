import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*; 

public class Main {
	
	final static boolean fromStdin = false;
	
	static BigInteger h, g, p;
	final static int bits = 52;
	
	final static SecureRandom rnd = new SecureRandom();
	
	static BigInteger x, y, d;
	
	static BigInteger xx = null;

	public static void main(String[] args) {
		
		if (fromStdin) {
			Scanner s = new Scanner(System.in);
			h = s.nextBigInteger();
			g = s.nextBigInteger();
			p = s.nextBigInteger();
		} else {
			p = BigInteger.probablePrime(bits, rnd);
			g = new BigInteger(bits, rnd);
			g.mod(p.subtract(BigInteger.ONE));
			g.add(BigInteger.ONE);
			g.add(BigInteger.ONE);
			
			xx = new BigInteger(bits, rnd);
			xx = xx.mod(p);
			
			System.err.println(xx.compareTo(p) < 0);
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
		
		System.out.println("time: " + (end - start));
	}
	
	static public BigInteger calc(BigInteger h, BigInteger g, BigInteger p) {
		BigInteger m = sqrt(p);
		assert(m.multiply(m).compareTo(p) >= 0);
		System.out.println("m: " + m);
		
		Map<BigInteger, BigInteger> mem = new HashMap();
		
		/* Baby Step*/
		System.out.println("Baby Step");
		
		BigInteger tmp = BigInteger.ONE;
		for(BigInteger r = BigInteger.ZERO; r.compareTo(m) < 0; r = r.add(BigInteger.ONE)) {
//			mem.put(g.modPow(r, p), r);
			mem.put(tmp, r);
			tmp = tmp.multiply(g).mod(p);
		}
		
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
			if (mem.containsKey(t)) {
				finished = true;
			} else {
				t = t.multiply(gInv);
				t = t.mod(p);
				q = q.add(BigInteger.ONE);
			}
		}
		
//		t = h.multiply(gInv.modPow(q, p)).mod(p);
//		System.out.println("t: " + t);
		
		BigInteger x = m.multiply(q).add(mem.get(t));
		
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
