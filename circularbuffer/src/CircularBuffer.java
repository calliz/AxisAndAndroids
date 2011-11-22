

public class CircularBuffer {

	private Object[] buffer;	
	private final int MAXSIZE;	
	private int nextToPut = 0;	
	private int nextToGet = 0;
	private int nAvailable = 0;

	public CircularBuffer(int MAXSIZE) {		
		this.MAXSIZE = MAXSIZE;
		buffer = new Object[MAXSIZE];
	}

	public synchronized void put(Object x) {
		try {
			while (nAvailable == MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Put got interrupted");
			e.printStackTrace();
		}
		buffer[nextToPut] = x;		
		if (++nextToPut == MAXSIZE) nextToPut = 0;
		++nAvailable;
		notifyAll();
	}

	public synchronized Object get() {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		notifyAll();
		return buffer[nextToGet]; // Return copy ?
	}

	public synchronized Object first() {
		if (nAvailable == 0) return -1;
		return buffer[nextToGet]; // Return copy ?
	}

	public synchronized Object sneakpeek(int i) {
		if (i >= nAvailable) {
			System.err.println("Out of bounds.");			
		} 
		return buffer[(nextToGet+i) % MAXSIZE]; // Return copy ?
	}

	public synchronized void printBuffer() {
		System.out.println("** Print Buffer");
		for (int l = 0; l < nAvailable; ++l) {
			System.out.printf("%6d - %s\n", l, buffer[(nextToGet + l) % MAXSIZE].toString());
		}	
		System.out.println("");
	}	
	
	public static void main(String[] args) {
		int BUFFMAX = 10;
		CircularBuffer fb = new CircularBuffer(BUFFMAX);
		byte[] raw_img = { 0,120,32,33,2,12,-23,32,-14,3 };
				
		for (int i = 0; i < BUFFMAX; ++i) {
			byte[] x = new byte[raw_img.length];
			System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
			x[0] = x[i] = (byte) i;			
			fb.put(new Frame(x, x.length));
		}

		fb.printBuffer();
		
		Frame first = (Frame) fb.first();
		System.out.println("First: " + first.toString());
		
		for (int i = 0; i < BUFFMAX; ++i) {
			Frame y = (Frame) fb.sneakpeek(i);
			System.out.println("Index: " + i + " - " + y.toString());
		}
		
		Frame z;
		z = (Frame) fb.get();
		assert(z.x[0] == 0);
		
		z = (Frame) fb.get();
		assert(z.x[0] == 1);

		byte[] x = new byte[raw_img.length];
		System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
		x[0] = (byte) 10;			
		fb.put(new Frame(x, x.length));

		z = (Frame) fb.get();
		assert(z.x[0] == 2);

		fb.printBuffer();
		
		for (int i = 3; i < BUFFMAX; ++i) {
			z = (Frame) fb.get();
			assert(z.x[0] == i);
			assert(z.x[i] == i);			
		}
		
		fb.printBuffer();

		z = (Frame) fb.get();
		assert(z.x[0] == 10);
		
		fb.printBuffer();	
	}
}