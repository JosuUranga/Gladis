package dialogs;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gladis.Habitacion;

@SuppressWarnings("serial")
public class DialogoHabitacion extends JDialog implements ActionListener {
	JTextField nombre;
	Habitacion habitacion;
	public DialogoHabitacion(JFrame ventana) {
		super (ventana, "A�adir Habitacion",true);	
		
		this.setSize(550,250);
		this.setLocation(260,180);
		this.setContentPane(crearPanelVentana());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
	}

	private Container crearPanelVentana() {
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(crearPanelDatos(),BorderLayout.CENTER);
		panel.add(crearPanelBotones(),BorderLayout.SOUTH);
		
		return panel;
	}

	private Component crearPanelBotones() {
		JPanel panel = new JPanel(new GridLayout(1,2,20,0));

		panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		JButton boton1 = new JButton ("OK");
		boton1.setActionCommand("ok");
		boton1.addActionListener(this);
		boton1.setDefaultCapable(true);
		JButton boton2 = new JButton ("Cancel");
		boton2.setActionCommand("cancel");
		boton2.addActionListener(this);
		
		panel.add(boton1);
		this.getRootPane().setDefaultButton(boton1);;

		panel.add(boton2);


		return panel;
	}

	
	private Component crearPanelDatos() {
		JPanel panel = new JPanel(new GridLayout(1,2));
		panel.setBorder(BorderFactory.createEmptyBorder(60,60,60,60));
		nombre = new JTextField(20);
		JLabel label = new JLabel ("Introduce nombre de la habitacion:");
		
		panel.add(label);
		panel.add(nombre);
				
		
		return panel;
	}
	

	public Habitacion getHabitacion() {
		return habitacion;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "ok":if (nombre.getText().length()>0) {
				habitacion = new Habitacion(nombre.getText());
				this.dispose();
			}else {
				JOptionPane.showMessageDialog(this,"Debes introducir un nombre", "Aviso",JOptionPane.WARNING_MESSAGE);
				
			}
			break;
		case"cancel": 
			this.dispose();
		}
		
	}

}
