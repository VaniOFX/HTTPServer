import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class ConnectionThread extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private boolean gzipEncoding = false;

	public ConnectionThread(Socket connection) throws IOException {
		System.out.println("Thread started with name:"
				+ Thread.currentThread().getName());
		socket = connection;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
		
		while(!(line = in.readLine()).equals("")) {
			request = request + line + "\n";
			System.out.println(request);
		}
		System.out.println("Keep going ahead");
		
		String[] requestLines = request.split("\n");
		
		for(String s :requestLines) {
			if(s.toLowerCase().contains("accept-encoding") && s.toLowerCase().contains("gzip")) {
				System.out.println("gzip encoding accepted");
				gzipEncoding = true;
			}
		}
		
		String headerMethod = requestLines[0].split(" ")[0].toLowerCase();
		String file = requestLines[0].split(" ")[1];
		System.out.println(file);
				
		
		if(headerMethod.equals("get") && checkFile(file.split("/")[1])) {
			handleGetRequest(file);
		}else if(headerMethod.equals("put")) {
			
		}else if(headerMethod.equals("post")) {
			
		}else if(headerMethod.equals("delete")) {
			
		}else {
			sendResponse(constructResponse(null, 404), null);
		}
	}
	
	private void handleGetRequest(String fileName) throws IOException {
		String response = getResponseString(fileName);
		sendResponse(response, fileName);
		
	}

	private String getResponseString(String fileName) {
		
		String[] temp = fileName.split("\\.");
		String fileType = temp[temp.length-1].toLowerCase();
		String response;
		
		if(fileType.equals("html") || fileType.equals("css")) 
			response = constructResponse("text/" + fileType, 200);
		
		else if(fileType.equals("pptx")) 
			response = constructResponse("application/vnd.openxmlformats-officedocument.presentationml.presentation", 200);
		
		else if(fileType.equals("jpg") || fileType.equals("jpeg")) 
			response = constructResponse("image/jpeg", 200);
		
		else if(fileType.equals("pdf")) 
			response = constructResponse("application/pdf", 200);
		
		else 
			response = constructResponse("text/plain", 200);
		
		return response;
	}



	private String constructResponse(String contentType, int responseCode) {
		String response;
		System.out.println("Constructing Response for you");
		switch(responseCode) {
			case 200:
				response =  "HTTP/1.1 200 OK\r\n"+
							"Date: " + getDate() + "\r\n"+
							"Server: localHotst\r\n"+
							"Content-Type: " + contentType + "\r\n"+
							"Connection: Closed\r\n"+
							(gzipEncoding ? "Content-Encoding: gzip\r\n" : "") +
							"\r\n";
				break;
			case 404:
				response =  "HTTP/1.1 404 Not Found\r\n"+
							"Date: " + getDate() + "\r\n"+
							"Server: localHotst\r\n"+
							"\r\n";
				break;
			default:
				response =  "HTTP/1.1 500 Internal Server Error\r\n"+
							"Date: " + getDate() + "\r\n"+
							"Server: localHotst\r\n"+
							"\r\n";
				break;
		}
		return response;
	}


	private void sendResponse(String response, String fileName) throws IOException {
		OutputStream dos = socket.getOutputStream();
		
		byte[] a = response.getBytes();
		dos.write(a);
		dos.flush();

		if(fileName != null) {
			if(gzipEncoding) dos = new java.util.zip.GZIPOutputStream(dos);
			File file = new File(fileName.split("/")[1]);
			byte[] b = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			
			fis.read(b);
			fis.close();
	
//			dos.writeInt((int) file.length());
//			dos.flush();
			
			dos.write(b, 0, b.length);
			dos.flush();
			dos.close();
			
		}
		dos.close();	

	}


	private boolean checkFile(String fileName) {
		File f = new File(fileName);
		return f.exists() && f.isFile();
	}
	
	private static String getDate() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
		String dateString = dateFormat.format(date);
		return dateString;
	}

	
}
