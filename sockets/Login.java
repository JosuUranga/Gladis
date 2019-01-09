package sockets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.naming.Context;  
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;



@SuppressWarnings("serial")
public class Login extends JDialog implements ActionListener{
	JTextField usuario;
	JPasswordField password;
	JCheckBox recordar;
	JButton logear;
	Boolean loginCorrecto;
	public Login(JFrame ventana) {
		super(ventana,"Login",true);
		this.setSize(600,600);
		this.setLocation (100,100);
		this.setContentPane(crearPanelGeneral());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		this.setVisible(true);
		loginCorrecto=false;
	}
	private Container crearPanelGeneral() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setBackground(new Color(0,153,255));
		panel.add(crearPanelTitulo());
		panel.add(crearPanelCuadro());	
		
		return panel;
	}
	public Container crearPanelTitulo() {
		JPanel panel=new JPanel();
		JLabel label= new JLabel("GLADIS");
		panel.setBackground(new Color(0,153,255));
		panel.setBorder(BorderFactory.createEmptyBorder(200,20, 20, 20));
		panel.add(label);
		return panel;
	}
	public Container crearPanelCuadro() {
		JPanel panel= new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20,20, 20, 20));
		panel.setBackground(new Color(0,153,255));
		panel.add(crearPanelLogin());
		panel.setOpaque(true);
		return panel;
	}
	public Container crearPanelLogin() {
		JPanel panel=new JPanel(new GridLayout(2,2,10,10));
		panel.setBorder(BorderFactory.createRaisedBevelBorder());
		panel.setPreferredSize(new Dimension(500,200));
		panel.add(crearPanelTextos());
		panel.add(crearPanelBoton());
		panel.setBackground(Color.white);
		return panel;	
	}	
	public Container crearPanelTextos() {
		JPanel panel=new JPanel(new GridLayout(2,2,10,10));
		JLabel label,label2;
		label=new JLabel("Usuario:");
		panel.setBackground(Color.white);
		label.setHorizontalAlignment(JLabel.CENTER);
		label2=new JLabel("Contraseña:");
		label2.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(usuario=new JTextField());
		panel.add(label2);
		panel.add(password=new JPasswordField());
		return panel;
	}
	public Container crearPanelBoton() {
		JPanel panel=new JPanel();
		recordar=new JCheckBox("Recordar");
		panel.setBackground(Color.white);
		recordar.setBackground(Color.white);
		panel.add(recordar);
		logear=new JButton("Entrar");
		logear.setActionCommand("logear");
		logear.setPreferredSize(new Dimension(150,50));
		logear.addActionListener(this);
		panel.add(logear);
		return panel;
	}
	public Boolean checkLogin() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.PROVIDER_URL, "ldaps://domoserver.domoticaf2.eus:636/DC=domoticaf2,DC=eus");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, usuario.getText());
		env.put(Context.SECURITY_CREDENTIALS, String.valueOf(password.getPassword()));
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			return true;
		} catch (NamingException er) {
			System.out.println("Problem occurs during context initialization !");
			er.printStackTrace();
			return false;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		loginCorrecto=checkLogin();
	}
	public Boolean esCorrecto() {
		return loginCorrecto;
	}
}
