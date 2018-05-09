import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
	

public class ConnectionThread extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;

	public ConnectionThread(Socket connection) throws IOException {
		socket = connection;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void run() {
		try {
			handleRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void handleRequest() throws IOException {
		String request = "";
		String line = " ";
		
		while((line = in.readLine()) != "") {
			request = request + line + "\n";
		}
		
		String headerMethod = request.split("\n")[0].split(" ")[0].toLowerCase();
		String file = request.split("\n")[0].split(" ")[1];
		
		if(headerMethod.equals("get") && checkFile(file)) {
			handleGetRequest(file);			
		}else if(headerMethod.equals("put")) {
			
		}else if(headerMethod.equals("post")) {
			
		}else if(headerMethod.equals("delete")) {
			
		}else {
			
		}
		
	}

	private void handleGetRequest(String fileName) throws IOException {
		String response = constructResponse(getContentType(fileName));
		out.write(response);
		out.write(getFileData(fileName));
		out.flush();
	}

	private String getFileData(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	private String constructResponse(String contentType) {
		return null;
	}

	private String getContentType(String fileName) {
		String[] temp = fileName.split(".");
		String fileType = temp[temp.length-1].toLowerCase();
		String contentType;
		if(fileType.equals("html") || fileType.equals("css")) {
			contentType = "text/" + fileType;
		}else if(fileType.equals("pptx")) {
			contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		}else if(fileType.equals("jpg") || fileType.equals("jpeg")) {
			contentType = "image/jpeg";
		}else if(fileType.equals("pdf")) {
			contentType = "applicationt/pdf";
		}else {
			contentType = "text/plain";
		}
		return contentType;
	}

	private boolean checkFile(String fileName) {
		File f = new File(fileName);
		return f.exists() && f.isFile();
	}
	
	private static String getDate() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String dateString = dateFormat.format(date);
		return dateString;
	}

	
}
