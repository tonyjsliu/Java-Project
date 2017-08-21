package chatServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Handler extends Thread{
	
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String name;
	PrintWriter pw;
	static FileWriter fw;
	static BufferedWriter bw;
	
	public Handler(Socket socket){
		this.socket = socket;
		try {
			fw = new FileWriter("Server_logs.txt", true);
			bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// users' login name status
	public void updateLoginStatus() {
		String nameList = Server.getNameList();
		for (PrintWriter writer : Server.printWriters) {
			writer.println("STATUS" + nameList);
		}
	}
	
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			
			// signup and login features 
			while (true) {
				String[] strs = in.readLine().split(":");
				SQLControl i = new SQLControl();
				i.openConnection();
				if (strs.length == 3) {
					if (i.insert(strs)) {
						out.println("SIGNACCEPT");
					}
					else {
						out.println("SIGNERROR");
					}
				}
				else {
					if (!i.queryData(strs)) {
						out.println("LOGERROR");
					}
					else if (Server.userNames.contains(strs[0])) {
						out.println("NAMEINUSE");
					}
					else {
						name = strs[0];
						out.println("LOGACCEPT" + name);
						break;
					}
				}
			}
			
			// update login status to everyone
			Server.userNames.add(name);
			Server.printWriters.add(out);
			Server.area.append(Server.getCurrentTime() + name + " is online.\n");
			pw.println(Server.getCurrentTime() + name + " is online.");
			for (PrintWriter writer : Server.printWriters) {
				writer.println("LOGGING" + name + ":online");
			}
			
			updateLoginStatus();
			
			// implement chatting for multi-user
			while (true) {
				String message = in.readLine();
				if (message == null) {
					
					Server.printWriters.remove(out);
					Server.userNames.remove(name);
					Server.area.append(Server.getCurrentTime() + name + " is offline.\n");
					pw.println(Server.getCurrentTime() + name + " is offline.");
					for (PrintWriter writer : Server.printWriters) {
						writer.println("LOGGING" + name + ":offline");
					}
					
					updateLoginStatus();
					
					return;
				}
				if (message.equals("")) {
					continue;
				}
				Server.area.append(Server.getCurrentTime() + name + ":" + message + "\n");
				pw.println(Server.getCurrentTime() + name + ":" + message);
				for (PrintWriter writer : Server.printWriters) {
					writer.println(name + ":" + message );
				}
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
