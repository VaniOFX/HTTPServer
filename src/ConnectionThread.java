import java.net.Socket;

public class ConnectionThread extends Thread {
	
	private Socket socket;

	public ConnectionThread(Socket connection) {
		socket = connection;
	}
	public void run() {
		
	}
}
