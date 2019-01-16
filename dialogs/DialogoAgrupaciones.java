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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import exceptions.DialogoNombreRepetidoException;
import gladis.Dispositivo;
import gladis.Habitacion;
import models.Agrupaciones;
import models.ListaDispositivos;

public class DialogoAgrupaciones extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 8045981208595000561L;
	JButton boton1, botonOK, botonCA;
	JFrame ventana;
	JComboBox<Habitacion> combobox;
	Habitacion[] habitaciones;
	Map<Habitacion,List<Dispositivo>> mapa;
	Map<String,List<Dispositivo>>mapaAgrupacion;
	JList<Dispositivo> lista, listaAgrupacion;
	ListaDispositivos modeloHabitacion;
	ListaDispositivos modeloAgrupacion;
	JTextField tnombre;
	boolean crear=false;
	public void setErrorIgual(boolean errorIgual) {
		this.errorIgual = errorIgual;
	}


	boolean	errorIgual=false;
	public DialogoAgrupaciones(JFrame ventana, Agrupaciones controladorAgrupaciones) {
		super(ventana,"Nueva Agrupacion", true);
		
		habitaciones=controladorAgrupaciones.getMapaCasa().keySet().toArray(new Habitacion[0]);
		this.mapa=controladorAgrupaciones.getMapaCasa();
		this.mapaAgrupacion=controladorAgrupaciones.getMapa();
		modeloHabitacion= new ListaDispositivos();
		modeloAgrupacion= new ListaDispositivos();
		this.ventana=ventana;
		this.setSize(600,600);
		this.setLocation (100,100);
		this.setContentPane(crearPanelGeneral());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		this.setVisible(true);
	}

	public String getNombre() {
		return tnombre.getText();
	}
	public List<Dispositivo> getListaAgrupacion(){
		return modeloAgrupacion.getLista();
	}
	private Container crearPanelGeneral() {
		JPanel panel = new JPanel(new BorderLayout(20,20));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.add(crearPanelNombre(), BorderLayout.NORTH);
		panel.add(crearPanelDialogo(), BorderLayout.CENTER);
		panel.add(crearPanelBotones(), BorderLayout.SOUTH);		
		
		return panel;
	}

	private Component crearPanelNombre() {
		JPanel panel = new JPanel(new GridLayout(1,2,20,20));

		tnombre = new JTextField();
		panel.add(new JLabel("Nombre de la agrupacion: "));
		panel.add(tnombre);
		
		return panel;
	}

	private Component crearPanelBotones() {
		JPanel panel = new JPanel(new GridLayout(1,2,20,20));
		
		botonOK=new JButton("OK");
		botonCA=new JButton("Cancel");
		
		botonOK.addActionListener(this);
		botonOK.setActionCommand("ok");
		botonCA.addActionListener(this);
		botonCA.setActionCommand("cancel");
		
		panel.add(botonOK);
		this.getRootPane().setDefaultButton(botonOK);;

		panel.add(botonCA);		
		
		return panel;
	}

	private Container crearPanelDialogo() {
		JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelIzqda(), crearPanelAgrupacion());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));		
		panel.setDividerLocation(300);
		return panel;
	}

	private Component crearPanelAgrupacion() {
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(crearPanelFlechas(),BorderLayout.WEST);
		panel.add(crearPanelLista(), BorderLayout.CENTER);		
		
		return panel;
	}

	private Component crearPanelLista() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listaAgrupacion= new JList<>();
		listaAgrupacion.setModel(modeloAgrupacion);
		listaAgrupacion.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		panel.setViewportView(listaAgrupacion);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return panel;
	}

	private Component crearPanelFlechas() {
		JPanel panel = new JPanel(new GridLayout(1,1,20,20));
		panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
		boton1 = new JButton(new ImageIcon("img/rightArrow.png"));
		boton1.addActionListener(this);
		boton1.setActionCommand("dcha");
		panel.add(boton1);
		return panel;
	}

	private Component crearPanelIzqda() {
		JPanel panel = new JPanel(new BorderLayout(20,20));
		panel.add(crearPanelCombo(), BorderLayout.NORTH);
		panel.add(crearPanelDispositivos(),BorderLayout.CENTER);
		
		return panel;
	}

	private Component crearPanelDispositivos() {
		JScrollPane panel = new JScrollPane();
		lista = new JList<>();
		lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lista.setModel(modeloHabitacion);
		panel.setViewportView(lista);
		mapa.get(combobox.getSelectedItem()).forEach(disp->modeloHabitacion.add(disp));
		return panel;
	}

	private Component crearPanelCombo() {
		JPanel panel = new JPanel();
		combobox=new JComboBox<>();		
		for(int i=0;i<habitaciones.length;i++) {
			combobox.addItem(habitaciones[i]);
		}
		combobox.setSelectedIndex(0);
		combobox.addActionListener(this);
		panel.add(combobox);
		
		return panel;
	}
	public void transferirAGrupo(int[] seleccionados) {
		boolean repetido=false;
		for(int indice : seleccionados) {
			if(!modeloAgrupacion.contains((Dispositivo)modeloHabitacion.getElementAt(indice)))modeloAgrupacion.add((Dispositivo)modeloHabitacion.getElementAt(indice));
			else repetido = true;
		}
		if(repetido)JOptionPane.showMessageDialog(this, "Hay uno o mÃ¡s dispositivos repetidos","Error",JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("dcha")) {
			int[] seleccionados= lista.getSelectedIndices();
			if (seleccionados.length ==0) {
				JOptionPane.showMessageDialog(this, "No hay dispositivos seleccionados","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}			
			lista.clearSelection();
			transferirAGrupo(seleccionados);
			
		}
		if(e.getSource()==combobox) {
			/*int i=0;
			for(i=0; i<habitaciones.length;i++) {
				if(habitaciones[i].equals(combobox.getSelectedItem())) break;
			}
			lista.setListData(mapaCasa.get(habitaciones[i]).toArray(new Dispositivo[0]));*/
			modeloHabitacion.clear();
			mapa.get(combobox.getSelectedItem()).forEach(disp->modeloHabitacion.add(disp));
			
		}
		if(e.getActionCommand().equals("ok")) {
			
			if(this.getListaAgrupacion().isEmpty()) {
				JOptionPane.showMessageDialog(this, "La lista de la agrupación está vacía","Error",JOptionPane.ERROR_MESSAGE);
			}
			else if(tnombre.getText().length()==0) {
				JOptionPane.showMessageDialog(this, "Debe de introducir un nombre","Error",JOptionPane.ERROR_MESSAGE);
			}
			else{
			hayRepetidoNombreAgrupacion(tnombre.getText());
				if(!errorIgual) {
				crear=true;
				this.dispose();
				}
			}
		}
		if(e.getActionCommand().equals("cancel")) {
			this.dispose();
		}
		
		
		
	}
	public void hayRepetidoNombreAgrupacion(String nombreVerificar) {
		mapaAgrupacion.entrySet().forEach(entry->{
			if(nombreVerificar.equals(entry.getKey())) {
					try {
						throw new DialogoNombreRepetidoException("msg");
					} catch (DialogoNombreRepetidoException e) {
						JOptionPane.showMessageDialog(this, "Ya existe una agrupación con ese mismo nombre","Error",JOptionPane.ERROR_MESSAGE);
						this.setErrorIgual(true);
					}
				} 
		});
	}


	public boolean isCrear() {
		return crear;
	}
}
