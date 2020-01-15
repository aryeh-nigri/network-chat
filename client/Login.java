import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JTextField txtPort;
	private JLabel lblIpDesc;
	private JLabel lblPortDesc;
	private JLabel lblErrormessage;

	/**
	 * Create the frame.
	 */
	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(50, 47, 193, 22);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(119, 29, 56, 16);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(50, 114, 193, 22);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setBounds(106, 96, 81, 16);
		contentPane.add(lblIpAddress);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(133, 177, 28, 16);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(50, 195, 193, 22);
		contentPane.add(txtPort);
		
		lblIpDesc = new JLabel("(eg. 192.168.0.2)");
		lblIpDesc.setBounds(97, 139, 100, 16);
		contentPane.add(lblIpDesc);
		
		lblPortDesc = new JLabel("(eg. 8192)");
		lblPortDesc.setBounds(117, 218, 60, 16);
		contentPane.add(lblPortDesc);
		
		JLabel lblErrormessage = new JLabel("", SwingConstants.CENTER);
		lblErrormessage.setBounds(50, 241, 193, 42);
		contentPane.add(lblErrormessage);
		
		JButton btnLogin = new JButton("Login");
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				name = name.isEmpty() ? "Guest" : name;
				String address = txtAddress.getText();
				address = address.isEmpty() ? "localhost" : address;
				try {
					int port = Integer.parseInt(txtPort.getText());					
					login(name, address, port);
				} catch (NumberFormatException exception) {
					lblErrormessage.setText("Port must be a number!");
				}
			}
		});
		
		btnLogin.setBounds(98, 286, 97, 25);
		contentPane.add(btnLogin);
		
	}
	
	/**
	 * Login to the chat.
	 */
	private void login(String name, String address, int port) {
		dispose();		
		new ClientWindow(name, address, port);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
