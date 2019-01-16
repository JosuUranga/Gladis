package dialogs;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exceptions.EspacioException;
import exceptions.NombreRepetidoException;
import exceptions.VacioException;
import gladis.Dispositivo;
import gladis.Habitacion;

@SuppressWarnings("serial")
public class DialogoHabitacion extends JDialog implements ActionListener {
	JTextField nombre;
	Habitacion habitacion;
	boolean errorRellenar=false;
	boolean errorIgual=false;
	Map<Habitacion,List<Dispositivo>> mapa;
	public DialogoHabitacion(JFrame ventana,Map<Habitacion,List<Dispositivo>> mapa) {
		super (ventana, "Añadir Habitacion",true);	
		this.mapa=mapa;
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
		this.getRootPane().setDefaultButton(boton1);

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
	public void hayRepetidoNombreHabitacion(String nombreVerificar) {
		errorIgual=false;
		mapa.entrySet().forEach(entry->{
			if(nombreVerificar.equals(entry.getKey().getNombre())) {
				try {
					throw new NombreRepetidoException("msg");
				} catch (NombreRepetidoException e) {
					JOptionPane.showMessageDialog(this, "Ya existe una habitacion con ese mismo nombre","Error",JOptionPane.ERROR_MESSAGE);
					errorIgual=true;
				}
			} 	
		});	
	}
	private void verificarVacio() throws VacioException{
		if(nombre.getText().length()==0) throw new VacioException("Debe rellenar todos los campos");
	}
	private void check(String nombreVerificar) {
		hayRepetidoNombreHabitacion(nombreVerificar);	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ok")) {
			try {
				verificarVacio();
				verificarEspacio();
				check(nombre.getText());	
				if(!errorIgual) {
					habitacion = new Habitacion(nombre.getText());
					this.dispose();
				}else {
					if(!errorIgual)JOptionPane.showMessageDialog(this,"Debes introducir un nombre", "Aviso",JOptionPane.WARNING_MESSAGE);
				}
			} catch (VacioException e1) {
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos", "Aviso" , JOptionPane.WARNING_MESSAGE);	
			} catch (EspacioException e1) {
				JOptionPane.showMessageDialog(this, "El nombre no puede contener espacios", "Aviso" , JOptionPane.WARNING_MESSAGE);	
			}
		}
		if(e.getActionCommand().equals("cancel")) {
			this.dispose();
		}
		
	}

	private void verificarEspacio() throws EspacioException{
		if(nombre.getText().contains(" ")) throw new EspacioException("El nombre no puede contener espacios");		
	}

}
