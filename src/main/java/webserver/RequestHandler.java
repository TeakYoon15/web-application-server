package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	private Object contentLength;

	private Object integer;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
//			BufferedReader line = new BufferedReader(in2);
						
			String line = br.readLine();
			
//			int contentLength = 0;
			
			while(!line.equals("")) {
				log.debug("header : {}", line);
				line = br.readLine();
				if (line.contains("Content-Length"))	 {
					String[] headerTokens = line.split(":");
					
					contentLength = integer.parseInt(headerTokens[1].trim());
					
//				})
//			}
//			String url = getDefaultUrl(tokens);
			
//			If(tokens[1].startsWith("/create")) {
//				
//			}
			
			
			String[] tokens;
			String s = br.readLine();
			tokens = s.split(" ");

			String url = tokens[1];
			
			if(url.startsWith("/create")) {
				
				int index = url.indexOf("?");
				String requestPath = url.substring(0, index);
				String params = url.substring(index+1);
				
				if(requestPath.equals("/create")) {
					String body = IOUtils.readData(line, contentLength);
					Map<String, String> params = HttpRequestUtils.parseQueryString(body); 
					User user = new User(params.get("user Id"), params.get("password"), params.get("name"), params.get("email"));
					DataBase.addUser(user);
					DataOutputStream dos = new DataOutputStream(out);
					response302Header(dos);
					
				}
				
			
			} else {
				if(url.equals("/")) {
					url = "/index.html";
				}
				
				DataOutputStream dos2 = new DataOutputStream(out);
				byte[] body2 = Files.readAllBytes(new File("./webapp" + url).toPath());
				
				response200Header(dos2, body2.length);
				responseBody(dos2, body2);
			}
				}}}
			

			

			
		 catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	


	

//	private Path newFile(String string) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	
	private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 302 OK \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	
	
	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}

