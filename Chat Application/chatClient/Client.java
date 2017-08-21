package chatClient;

import java.awt.FlowLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	
	static JFrame chatWindow = new JFrame("Chat Application");
	static JTextArea chatArea = new JTextArea(22, 30);
	static JTextArea nameList = new JTextArea(22, 10);
	static JTextField textField = new JTextField(40);
	static JLabel blankLabel = new JLabel("           ");
	static JButton sendButton = new JButton("Send");
	static BufferedReader in;
	static PrintWriter out;
	static JLabel nameLabel = new JLabel("You are not logged in.");
	static String clientName;
	
	static final String EMAIL_PATTERN = 
		    "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	// constructor
	public Client() {
		chatWindow.setResizable(false);
		chatWindow.setLayout(new FlowLayout());
		
		chatWindow.add(nameLabel);
		chatWindow.add(new JScrollPane(chatArea));
		chatWindow.add(new JScrollPane(nameList));
		chatWindow.add(blankLabel);
		chatWindow.add(textField);
		chatWindow.add(sendButton);
		
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatWindow.setSize(500, 500);
		chatWindow.setVisible(true);
		
		chatArea.setEditable(false);
		textField.setEditable(false);
		nameList.setEditable(false);;
		
		sendButton.addActionListener(new Listener());
		textField.addActionListener(new Listener());
	}
	
	// check whether it is a valid IP address
	private boolean validIP(String IP){
        int begin=0,count=0;
        for(int i=0;i<IP.length();i++){
            char ch=IP.charAt(i);
            if(ch>='0'&&ch<='9'){
                if(ch=='0'&&i==begin&&i<IP.length()-1&&IP.charAt(i+1)!='.')
                    return false;
            }
            else if(ch=='.'){
                count++;
                if(i>begin&&i-begin<5&&Integer.parseInt(IP.substring(begin,i))<=255)
                    begin=i+1;
                else
                    return false;
            }
            else
                return false;
            if(i==IP.length()-1)
                if(ch=='.'||Integer.parseInt(IP.substring(begin,i+1))>255)
                    return false;
        }
        if(count!=3)
            return false;
        return true;
    }
	
	// get the IP address from input
	private String[] getIpAddress() {
		String ip = "127.0.0.1", port = "12345";
		
		while (true) {
			ip = JOptionPane.showInputDialog(chatWindow, "Entry IP Address:", "IP Address Required!", JOptionPane.PLAIN_MESSAGE);
			if (ip.equals("localhost") || validIP(ip)) {
				break;
			}
			JOptionPane.showMessageDialog(chatWindow, "Invalid IP Address!");
		}
		
		while (true) {
			port = JOptionPane.showInputDialog(chatWindow, "Entry Port Number:", "Port Number Required!", JOptionPane.PLAIN_MESSAGE);
			if (port.matches("^[0-9]*$") && Integer.parseInt(port) < 65536) {
				break;
			}
			JOptionPane.showMessageDialog(chatWindow, "Invalid Port Number!");
		}
		
		return new String[]{ip, port};
	}
	
	// connect server and return valid socket
	private Socket connectServer() {
		Socket soc = null;
		while (soc == null) {
			String[] ipAddress = getIpAddress();
			try {
				soc = new Socket(ipAddress[0], Integer.parseInt(ipAddress[1]));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			if (soc == null) {
				JOptionPane.showMessageDialog(chatWindow, "Can't connect server, please enter again!"); 
			}
		}
		return soc;
	}
	
	// implement signup function
	private void signup() {
		while (true) {
			JTextField uN = new JTextField(15);
			JPasswordField pW = new JPasswordField(15);
			JPasswordField pW2 = new JPasswordField(15);
			JTextField Email = new JTextField(15);
			Object[] signup = {"Username: ", uN, "Password: ", pW, "Re-password: ", pW2, "Email: ", Email};
			int res2 = JOptionPane.showConfirmDialog(chatWindow, signup, "Registration",  JOptionPane.OK_CANCEL_OPTION);
			
			String un = uN.getText(), pw = String.valueOf(pW.getPassword()), pw2 = String.valueOf(pW2.getPassword()), email = Email.getText(); 
			if (res2 == JOptionPane.OK_OPTION) {
				if (un.length() == 0) {
					JOptionPane.showMessageDialog(chatWindow, "Username can not be empty!");
				}
				else if (pw.length() == 0 ) {
					JOptionPane.showMessageDialog(chatWindow, "Password can not be empty!");
				}
				else if (email.length() == 0) {
					JOptionPane.showMessageDialog(chatWindow, "Email can not be empty!");
				}
				else if (!pw.equals(pw2)) {
					JOptionPane.showMessageDialog(chatWindow, "Password is not consistent!");
				}
				else if (!email.matches(EMAIL_PATTERN)) {
					JOptionPane.showMessageDialog(chatWindow, "Invalid email!");
				}
				else {
					out.println(un + ":" + pw + ":" + email);
					String str = "";
					try {
						str = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (str.equals("SIGNACCEPT")) {
						JOptionPane.showMessageDialog(chatWindow, "Register successfully!");
						break;
					}
					else {
						JOptionPane.showMessageDialog(chatWindow, "Account already exists, please register with another email!");
					}
				}
			}
		}
	}
	
	// implement chat function
	private void chat() {
		while (true) {
			String str = "";
			try {
				str = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (str.startsWith("LOGGING")) {	
				String[] strs = str.substring(7).split(":");
				chatArea.append("               --------  " + strs[0] + " is " + strs[1] + "  --------\n");
			}
			else if (str.startsWith("STATUS")) {
				String[] strs = str.substring(6).split(":");
				nameList.setText("");
				for (String nameStr : strs) {
					nameList.append(nameStr + "\n");
				}
			}
			else {
				int index = str.indexOf(":");
				if (clientName.equals(str.substring(0, index)))
					chatArea.append("You: " + str.substring(index + 1) + '\n');
				else
					chatArea.append(str + '\n');
			}
			
		}
	}
	
	// main function of client
	public void startChat(){
		Socket soc = connectServer();
		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JTextField username = new JTextField(10);
		JPasswordField password = new JPasswordField(10);
		Object[] message = {"Username: ", username, "Password: ", password};
		String[] options = new String[]{"Login", "Signup", "Cancel"};
		
		while (true) {
			int res = JOptionPane.showOptionDialog(chatWindow, message, "Name Required!", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (res == 0) {
				out.println(username.getText() + ":" + String.valueOf(password.getPassword()));
				String str = "";
				try {
					str = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (str.startsWith("LOGACCEPT")) {
					clientName = str.substring(9);
					textField.setEditable(true);
					nameLabel.setText("You are logged in as: " + clientName);
					break;
				}
				else if (str.equals("NAMEINUSE")) {
					JOptionPane.showMessageDialog(chatWindow, "This account is in use, please use another account!");
				}
				else {
					JOptionPane.showMessageDialog(chatWindow, "Wrong username or password, please enter again!");
				}
			}
			else if (res == 1) {
				signup();
			}
			else {
				return;
			}
		}
		
		chat();
	}
	
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.startChat();
	}
}
