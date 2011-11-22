package se.axisandandroids.networking;


public class Protocol {

	public class COMMAND {	
		public final static int IMAGE 		= 10;
		public final static int SYNC_MODE 	= 20;
		public final static int DISP_MODE 	= 30;
		public final static int CONNECTED 	= 40;
		//public final static int END_MSG 	= 50;
		public final static int NOTOK	 	= 130;
	}

	public class SYNC_MODE {		
		public final static int AUTO 		= 0;
		public final static int SYNC 		= 1;
		public final static int ASYNC 		= 2;
	}

	public class DISP_MODE {		
		public final static int AUTO 		= 0;
		public final static int IDLE 		= 1;
		public final static int MOVIE 		= 2;
	}
	
	/*
	public class CONNECTED {
		public final static int TRUE		= 0;
		public final static int FALSE		= 0;
	}
	*/
	
}
