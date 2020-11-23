import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
//import javax.net.ssl.SSLSession;


public class ClientHandler extends Thread{
	SSLSocket client;
	String[] message;

	private static final String password = "firleinv";
	
	public ClientHandler(SSLSocket client) {
		this.client = client;
	}
	
	public void run() {
		try {
			this.client.setEnabledCipherSuites(this.client.getSupportedCipherSuites());

			// Start handshake
			this.client.startHandshake();
                 
			// Get session after the connection is established
			//SSLSession sslSession = this.client.getSession();


			//Read Input
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String mes = "";
			String line = "";
			while(!(line = br.readLine()).equals("")) {
				mes += (line + "\n");
			}
			//System.out.println(mes);
			message = mes.split("\n"); 
			
			//Handle Message
			if(message[0].startsWith("GET /")) {
				String path = message[0].substring(5,message[0].substring(5).indexOf(" ")+5);
				if(path.equals("") || path.equals("index.html")) {
					sendFile("./src/www/authorization.html");
				}
				else if(path.equals("authorization.js")) {
					sendFile("./src/www/authorization.js");
				}
				else if(path.equals("script.js")) {
					sendFile("./src/www/script.js");
				}
				else if(path.equals("save.js")) {
					sendFile("./src/www/save.js");
				}
				else if(path.equals("download.js")) {
					sendFile("./src/www/download.js");
				}
				else if(path.equals("style.css")) {
					sendFile("./src/www/style.css");
				}
				else if(path.equals("logo.gif")) {
					sendFile("./src/www/logo.gif");
				}
				else if(path.equals("favicon.ico")) {
					sendFile("./src/www/favicon.png");
				}
				else if(path.equals("fail.mp3")) {
					sendFile("./src/www/fail.mp3");
				}
				
			}
			else if(message[0].startsWith("POST /")){
				String path = message[0].substring(6,message[0].substring(6).indexOf(" ")+6);
				if(path.equals("DATA")) {
					String body = "";
					while(!(line = br.readLine()).equals("")) {
						body += line + "\n";
					}
					String toSend = Loader.getData(body);
					sendData(toSend);
					
					String filename = Server.reqdir + File.separator + java.time.LocalDate.now().toString()+ ".txt";
					File newFile = new File(filename);
					newFile.createNewFile();
					try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardOpenOption.APPEND)) {
						writer.write(toSend);
						writer.close();
					} catch (IOException ioe) {
						System.err.format("IOException: %s%n", ioe);
					}
				}
				else if (path.equals("Password")){
					String body = "";
					body = br.readLine();
					if(checkAuthentication(body)){
						sendFile("./src/www/index.html");
					}
					else{
						sendBadReq();
					}
				}
			}
			
			else{
				sendBadReq();
			}
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendBadReq(){
		String res = "HTTP/1.1 400 Bad Request\n\n";
		byte[] header = res.getBytes();
		try{
			OutputStream os = client.getOutputStream();
			os.write(header,0,header.length);
			os.flush();
			if (os != null) os.close();
		}
		catch(Exception e ){e.printStackTrace();}
	}

	private boolean checkAuthentication(String psw){
		if(psw.equals(password)){
			return true;
		}
		return false;
	}

	public void sendFile(String path) throws IOException{
		File file = new File(path);
		if(!file.exists()) {
			System.out.println("No Valid Path " + path + " " + file.getCanonicalPath());
			return;
		}

		String response = "";
		response += "HTTP/1.1 200 OK\r\n";
		response += "Connection: close\r\n";
		response += "Content-Length: " + file.length() + "\r\n";
		response += "Content-Type: "+ getContentType(path) + "\r\n";
		
		response += "\r\n";
		

		byte[] header = response.getBytes();
		
   	 	OutputStream os = null;
		
		byte[] mybytearray = Files.readAllBytes(Paths.get(path));
		os = client.getOutputStream();
		os.write(header,0,header.length);
		os.write(mybytearray,0,mybytearray.length);
		os.flush();
		if (os != null) os.close();
	}

	
	public void sendData(String body) throws IOException{
		OutputStream os = client.getOutputStream();
		String response = "";
		response += "HTTP/1.1 200 OK\r\n";
		response += "Connection: close\r\n";
		response += "Content-Type: text/html; charset=UTF-8\r\n";
		response += "Content-Length: " + body.length() + "\r\n";
		response += "\r\n";
		byte[] header = response.getBytes(StandardCharsets.UTF_8);
		byte[] bodyarr = body.getBytes(StandardCharsets.UTF_8);
		os.write(header);
		os.write(bodyarr);
		os.close();
	}
	
	
	private String getContentType(String path) {
		String ending = path.substring(path.lastIndexOf('.')+1);
		
		if(ending.equals("css")) {
			return "text/css";
		}
		
		if(ending.equals("js")) {
			return "text/javascript";
		}
		
		if(ending.equals("gif")) {
			return "image/gif";
		}
		if(ending.equals("jpg")) {
			return "image/jpeg";
		}
		if(ending.equals("ico")) {
			return "image/ico";
		}
		if(ending.equals("html")) {
			return "text/html";
		}
		if(ending.equals("png")) {
			return "image/png";
		}
		if(ending.equals("mp3")){
			return "audio/mpeg";
		}
		return "text/plain";
		
	}

}


