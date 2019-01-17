package dialogs;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exceptions.NombreRepetidoException;
import gladis.*;


@SuppressWarnings("serial")
public class DialogoDispositivos extends JDialog implements ActionListener{
	
	DialogoVar dialogo;
	JFrame ventana;
	Dispositivo dispositivo;
	JComboBox<String> comboDisp;
	JComboBox<String> comboTipo;
	JLabel label, control1, control2;
	JTextField nombre,tvariables,tratio;
	JLabel lMensaje;
	public void setErrorIgual(boolean errorIgual) {
		this.errorIgual = errorIgual;
	}

	int numVariables;
	boolean errorRellenar=false;
	boolean errorIgual=false;
	Map<Habitacion, List<Dispositivo>> map;
	String[] dispositivos= {"Luces", "Electrodomesticos", "Aparatos Electricos", "Otros"};
	
	String[] luces= {"Luz Normal", "Luz RGB", "Luz Gradual"};
	String[] electrodomesticos= {"Frigorífico", "Microondas", "Lavadora", "Lavavajillas", "Horno", "Congelador"};
	String[] aparatos= {"Equipo de música", "Despertador", "Televisión", "Temperatura"};
	String[] otros= {"Programable tiempo","No programable"};
	
	public DialogoDispositivos (JFrame ventana,String titulo, boolean modo,Map<Habitacion, List<Dispositivo>> map) {
		super(ventana,titulo,modo);
		this.ventana=ventana;
		dispositivo=null;
		this.map=map;
		this.setSize(600,600);
		this.setLocation (100,100);
		this.setContentPane(crearPanelDialogo());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		this.setVisible(true);
		
	}
	public Dispositivo getDispositivo() {
		return dispositivo;
	}

	private Container crearPanelDialogo() {
		JPanel panel = new JPanel (new BorderLayout(0,20));
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		panel.add(crearPanelNorte(), BorderLayout.NORTH);
		panel.add(crearPanelCentro(),BorderLayout.CENTER);
		panel.add(crearPanelBotones(),BorderLayout.SOUTH);
		return panel;
	}

	private Component crearPanelCentro() {
		JPanel panel = new JPanel(new GridLayout(3,2));

		label = new JLabel("Identificador: ");
		panel.add(new JLabel("Tipo: "));
		panel.add(crearPanelComboTipo());
		panel.add(label);
		panel.add(crearPanelNombre());
		panel.add(crearPanelCantidadVariables());
		panel.add(crearPanelRatio());
		return panel;
	}

	private Component crearPanelRatio() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Sumador de tiempo (segundos)"), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
		
		tratio= new JTextField("");
		tratio.setVisible(true);
		tratio.setEditable(false);
		panel.add(tratio,BorderLayout.CENTER);
		return panel;
	}
	private Component crearPanelCantidadVariables() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Cantidad de variables"), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
		
		tvariables= new JTextField("");
		tvariables.setVisible(true);
		tvariables.setEditable(false);
		panel.add(tvariables,BorderLayout.CENTER);
		return panel;
	}

	private Component crearPanelNombre() {
		JPanel panel = new JPanel(new BorderLayout(0,0));
		panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
	
		nombre = new JTextField("");
		panel.add(nombre, BorderLayout.CENTER);
		return panel;
	}

	private Component crearPanelComboTipo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
		
		comboTipo= new JComboBox<>();
		comboTipo.addActionListener(this);
		comboTipo.setActionCommand("tipo");
		comboTipo.setVisible(true);
		
		anadirCombo(luces);
		
		panel.add(comboTipo, BorderLayout.CENTER);
		return panel;
	}

	private void anadirCombo(String[] array) {
		comboTipo.removeAllItems();
		for(int i=0; i<array.length; i++) {
			comboTipo.addItem(array[i]);
		}		
	}

	private Container crearPanelNorte() {
		JPanel panel = new JPanel (new BorderLayout(0,20));
		panel.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
		panel.add(crearPanelDispositivos(), BorderLayout.CENTER);
		return panel;
	}

	private Component crearPanelBotones() {
		JPanel panel = new JPanel(new GridLayout(1,2,20,0));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		JButton boton1 = new JButton ("OK");
		
	
		boton1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				check(nombre.getText());	
				if(!errorRellenar && !errorIgual) {
				System.out.println("entro");
					switch((String) comboTipo.getSelectedItem()) {
					case "Luz Normal":{
						dispositivo=new Dispositivo(nombre.getText(),"img/luz.png",null,(String) comboTipo.getSelectedItem());
						break;
					}
					case "Luz RGB":{
						dispositivo=new Dispositivo(nombre.getText(),"img/luz.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Color"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Luz Gradual":{
						dispositivo=new Dispositivo(nombre.getText(),"img/luz.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Intensidad"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Frigorífico":{
						dispositivo=new Dispositivo(nombre.getText(),"img/frigorifico.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Temperatura"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Lavadora":{
						dispositivo=new Dispositivo(nombre.getText(),"img/lavadora.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Programa"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Lavavajillas":{
						dispositivo=new Dispositivo(nombre.getText(),"img/lavavajillas.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Modo"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Microondas":{
						dispositivo=new DispositivoTmp(nombre.getText(),"img/microondas.png",null,(String) comboTipo.getSelectedItem(), 60);
						dispositivo.addVariable(new Variable("Modo"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Horno":{
						dispositivo=new DispositivoTmp(nombre.getText(),"img/horno.png",null,(String) comboTipo.getSelectedItem(), 300);
						dispositivo.addVariable(new Variable("Temperatura"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Equipo de música": {
						dispositivo=new Dispositivo(nombre.getText(),"img/equipodemusica.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Volumen"));
						dispositivo.addVariable(new Variable("Cancion"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Despertador":{
						dispositivo=new DispositivoTmp(nombre.getText(),"img/despertador.png",null,(String) comboTipo.getSelectedItem(),60);
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Temperatura":{
						dispositivo=new DispositivoTmp(nombre.getText(),"img/temperatura.png",null,(String) comboTipo.getSelectedItem(), 300);
						dispositivo.addVariable(new Variable("Temperatura"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Televisión":{
						dispositivo=new Dispositivo(nombre.getText(),"img/television.png",null,(String) comboTipo.getSelectedItem());
						dispositivo.addVariable(new Variable("Volumen"));
						dispositivo.addVariable(new Variable("Canal"));
						 dialogo=new DialogoVar(ventana,dispositivo);
						break;
					}
					case "Programable tiempo":
						try {
							numVariables= Integer.parseInt(tvariables.getText());
							int intRatio= Integer.parseInt(tratio.getText());
							dispositivo=new DispositivoTmp(nombre.getText(),"img/otros.png",null,(String) comboTipo.getSelectedItem(), intRatio);
							for( int i =0; i<numVariables; i++) {
								dispositivo.addVariable(new Variable(" "));
							}
							if(numVariables>0) {
								dialogo=new DialogoVar(ventana,dispositivo);
								dialogo.addPropertyChangeListener((Principal)ventana);
							}
						} catch (NumberFormatException e1) {
							errorRellenar=true;
						}
						break;
					case "No programable":
						try {
							numVariables= Integer.parseInt(tvariables.getText());
							dispositivo=new Dispositivo(nombre.getText(),"img/otros.png",null,(String) comboTipo.getSelectedItem());
							for( int i =0; i<numVariables; i++) {
								dispositivo.addVariable(new Variable(" "));
							}
							if(numVariables>0) {
								dialogo=new DialogoVar(ventana,dispositivo);
								dialogo.addPropertyChangeListener((Principal)ventana);
							}
							break;
						} catch (NumberFormatException e) {
							errorRellenar=true;
						}
						break;				
					default: break;
					}
					DialogoDispositivos.this.dispose();
				}
				else {
					if(!errorIgual)JOptionPane.showMessageDialog(DialogoDispositivos.this, "Debe rellenar todos los camposxd","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	
		JButton boton2 = new JButton ("Cancel");
		boton2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				DialogoDispositivos.this.dispose();
			}
		});
		panel.add(boton1);
		this.getRootPane().setDefaultButton(boton1);

		panel.add(boton2);
		return panel;
	}
	private void check(String nombreVerificar) {
		errorRellenar=false;
		errorIgual=false;
		if(nombre.getText().length()==0) {
			errorRellenar=true;
		}

		hayRepetidoNombreDispositivo(nombreVerificar);	
	}
	public void hayRepetidoNombreDispositivo(String nombreVerificar){
		map.entrySet().forEach(entry->{
			entry.getValue().forEach(disp2->{
				if(nombreVerificar.toLowerCase().equals(disp2.getNombre().toLowerCase())) {
					try {
						throw new NombreRepetidoException("msg");
					} catch (NombreRepetidoException e) {
						JOptionPane.showMessageDialog(this, "Ya existe un dispositivo con ese mismo nombre","Error",JOptionPane.ERROR_MESSAGE);
						this.setErrorIgual(true);
					}
				} 
			});
		});
	}
	
	private Component crearPanelDispositivos() {
		JPanel panel = new JPanel (new GridLayout(1,2));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		comboDisp = new JComboBox<>(dispositivos);
		comboDisp.setSelectedItem(0);
		comboDisp.addActionListener(this);
		comboDisp.setActionCommand("disp");
		panel.add(new JLabel("Dispositivo: "));
		panel.add (comboDisp);
		return panel;
	}
	
	public String getText() {
		return comboTipo.getSelectedItem().toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "disp":{
			comboTipo.removeAllItems();
			switch((String) comboDisp.getSelectedItem()) {
			case "Luces":{
				anadirCombo(luces);
				tvariables.setEditable(false);
				tratio.setEditable(false);		
				tvariables.setText("");
				tratio.setText("");		
				break;
			}
			case "Electrodomesticos": {
				anadirCombo(electrodomesticos);
				tvariables.setEditable(false);
				tratio.setEditable(false);
				tvariables.setText("");
				tratio.setText("");
				break;
			}
			case "Aparatos Electricos": {
				anadirCombo(aparatos);
				tvariables.setEditable(false);
				tratio.setEditable(false);
				tvariables.setText("");
				tratio.setText("");
				break;
			}case "Otros": {
				anadirCombo(otros);
				tvariables.setEditable(true);
				tratio.setEditable(true);
				tvariables.setText("");
				tratio.setText("");
				break;
			}
			default: break;
			}
			break;
		}
		case "tipo":{
			if(comboTipo.getSelectedItem()!=null) {
				switch((String) comboTipo.getSelectedItem()) {
				case "Programable tiempo":{
					tvariables.setEditable(true);
					tratio.setEditable(true);
					break;
				}
				case "No programable": {
					tvariables.setEditable(true);
					tratio.setEditable(false);
					break;
				}
				default: break;
				}	
			}
		}
		default: break;
		}
		
		
	}

}
