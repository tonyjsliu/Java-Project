package chatClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listener implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Client.out.println(Client.textField.getText());
		Client.textField.setText("");
	}

}
