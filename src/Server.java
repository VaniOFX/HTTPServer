import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private int portNumber = 8080;

	public Server(String[] args) {
		if(args.length == 1) {
			try{
				portNumber = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("Wrong usage");
			System.exit(1);
		}
	}

	public void start(){
		try(ServerSocket serverSocket = new ServerSocket(portNumber)){
			while(true) {
				System.out.println("Listening for connections\n");
				new ConnectionThread(serverSocket.accept()).start();
			}
		}
		catch(IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

}
