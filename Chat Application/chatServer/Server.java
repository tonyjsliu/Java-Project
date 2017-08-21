package chatServer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Server extends JFrame {

	static JPanel contentPane = new JPanel();
	static JTextField port = new JTextField();
	static JButton connect = new JButton("CONNECT");
	static JButton disconnect = new JButton("DISCONNECT");
	static JTextArea area = new JTextArea();
	static GroupLayout gl_contentPane = new GroupLayout(contentPane);
	static JScrollPane scrollpaneArea = new JScrollPane(area, 
	        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
	           JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	static List<String> userNames = new ArrayList<String>();
	static List<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	static ServerSocket ss;
	
	// get all of username that is online
	public static String getNameList() {
		StringBuffer sb = new StringBuffer();
		for (String str : userNames) {
			sb.append(":").append(str);
		}
		return sb.substring(1).toString();
	}
	
	// get current time
	public static String getCurrentTime() {
		return "[" + new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime()) + "]: ";
	}
	
	/*
	 * Launch the application.
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		while (true) {	
			if (ss == null || ss.isClosed()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			area.append(getCurrentTime() + "Server is started in port " + port.getText() + ".\n");
			while (!ss.isClosed()) {
				Socket soc = null;
				try {
					soc = ss.accept();
					System.out.println("Connection established");
					area.append(getCurrentTime() + "Connection established\n");
					Handler handler = new Handler(soc);
					handler.start();
				} catch (SocketException e) {
					area.append(getCurrentTime() + "Server is closed.\n");
					break;
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			
		}
	}

	/*
	 * Create the frame.
	 */
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		area.setEditable(false);
		JLabel lblPort = new JLabel("PORT: ");
		disconnect.setEnabled(false);
		port.setColumns(10);
		port.setText("12345");
		connect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				connect.setEnabled(false);
				disconnect.setEnabled(true);
				try {
					ss = new ServerSocket(Integer.parseInt(port.getText()));	
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		disconnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				connect.setEnabled(true);
				disconnect.setEnabled(false);
				try {
					ss.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollpaneArea, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(port, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(connect, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(disconnect, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(port, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
							.addGap(3))
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(disconnect)
							.addComponent(connect)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollpaneArea, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
