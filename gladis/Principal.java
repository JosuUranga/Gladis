package gladis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dialogs.DialogoAgrupaciones;
import dialogs.DialogoDispositivos;
import dialogs.DialogoHabitacion;
import models.Agrupaciones;
import models.Habitaciones;
import renderers.RendererAgrupaciones;
import renderers.RendererDispositivos;
import renderers.RendererHabitaciones;
import serialcomm.Alarma;
import sockets.EnvioHabitaciones;
import sockets.EscuchaServidor;
import sockets.controladorVersion;
import sockets.envioFTP;

public class Principal extends JFrame implements ActionListener, ListSelectionListener, PropertyChangeListener {
	JMenuBar barra;	
	JMenu editar,salir;
	JMenuItem anadirHabitacion,quitarHabitacion,anadirDispositivo,quitarDispositivo,cerrar;
	JButton banadirHabitacion,bquitarHabitacion,banadirDispositivo,bquitarDispositivo,bcerrar,banadirAgrupacion,bquitarAgrupacion,bactivarAgrupacion,botonAlarma, noMolestar; 
	Boolean eliminar;
	JList<Habitacion>listaHabitaciones;
	Habitaciones controlador;
	RendererHabitaciones renderer;
	RendererDispositivos renderer2;
	RendererAgrupaciones renderer3;
	JList<Dispositivo>listaDispositivos;
	String casa,habitacion;
	Agrupaciones controladorAgrupaciones;
	JList<String>listaAgrupaciones;
	DialogoAgrupaciones dialogoAgrupacion;
	List<String>ips;
	controladorVersion cVersion;
	Alarma alarma;
	List<String>oldVal;
	String username,password;
	JLabel lNombrePanelDispositivos; 

	public Principal(){		
		super ("Gladis");	
		casa="test";
		username="Administrador";
		password="123456789aA@";
		this.ips=new ArrayList<>();
		cVersion=new controladorVersion("172.17.23.143",casa,username,password,this);
		cVersion.start();
		new EscuchaServidor(this,ips).start();
		controlador= new Habitaciones(casa,this);
		controlador.addPropertyChangeListener(this);
		controladorAgrupaciones = new Agrupaciones(controlador,casa);
		controladorAgrupaciones.addPropertyChangeListener(this);
		renderer= new RendererHabitaciones();
		renderer2= new RendererDispositivos();
		renderer3= new RendererAgrupaciones(); 
		eliminar=false;
		habitacion="salon";
		this.setExtendedState(MAXIMIZED_BOTH); 
		this.setLocation(0,0); 
		this.setContentPane(crearPanelVentana());
		controlador.inicializar(casa);
		controladorAgrupaciones.inicializar(casa);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	private Container crearPanelVentana() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(crearPanelCentral(),BorderLayout.CENTER);
		return panel;
	}
	private Component crearPanelToolbarHabitaciones() {
		JToolBar toolbar = new JToolBar();
		banadirHabitacion = new JButton(new ImageIcon("img/anadir.png"));
		banadirHabitacion.addActionListener(this);
		banadirHabitacion.setActionCommand("anadirHabitacion");
		toolbar.add(banadirHabitacion);
		bquitarHabitacion = new JButton(new ImageIcon("img/quitar.png"));
		bquitarHabitacion.addActionListener(this);
		bquitarHabitacion.setActionCommand("quitarHabitacion");
		bquitarHabitacion.setEnabled(false);
		toolbar.add(bquitarHabitacion);
		toolbar.add(Box.createGlue()); 
		 
		JLabel lNombre = new JLabel("HABITACIONES"); 
		lNombre.setFont(new Font("Garamond",Font.PLAIN,12)); 
		lNombre.setForeground(new Color(35,35,5)); 
		toolbar.add(lNombre); 
		 
		toolbar.add(Box.createHorizontalGlue()); 
		return toolbar;
	}
	private Component crearPanelToolbarDispositivos() {
		JToolBar toolbar = new JToolBar();
		banadirDispositivo = new JButton(new ImageIcon("img/anadir.png"));
		banadirDispositivo.addActionListener(this);
		banadirDispositivo.setActionCommand("anadirDispositivo");
		banadirDispositivo.setEnabled(false);
		toolbar.add(banadirDispositivo);
		bquitarDispositivo = new JButton(new ImageIcon("img/quitar.png"));
		bquitarDispositivo.addActionListener(this);
		bquitarDispositivo.setActionCommand("quitarDispositivo");
		bquitarDispositivo.setEnabled(false);
		toolbar.add(bquitarDispositivo);
		toolbar.add(Box.createGlue()); 
		 
		JLabel lNombre = new JLabel("DISPOSITIVOS"); 
		lNombre.setFont(new Font("Garamond",Font.PLAIN,12)); 
		lNombre.setForeground(new Color(35,35,5)); 
		toolbar.add(lNombre); 
 
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(Box.createHorizontalGlue()); 
		botonAlarma = new JButton(); 
		Image img = (new ImageIcon("img/alarma.png")).getImage();		
		Image newimg = img.getScaledInstance(16, 16,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(newimg); 
		botonAlarma.setIcon(imageIcon);
		botonAlarma.addActionListener(this); 
		botonAlarma.setActionCommand("activarAlarma"); 
		toolbar.add(botonAlarma); 
		lNombrePanelDispositivos = new JLabel("DISPOSITIVOS"); 
		lNombrePanelDispositivos.setFont(new Font("Garamond",Font.PLAIN,12)); 
		lNombrePanelDispositivos.setForeground(new Color(35,35,5)); 
		toolbar.add(lNombrePanelDispositivos); 
		toolbar.add(Box.createHorizontalGlue()); 
		 
		noMolestar=new JButton(new ImageIcon("img/noMolestar.png")); 
		noMolestar.addActionListener(this); 
		noMolestar.setActionCommand("noMolestar"); 
		noMolestar.setEnabled(false); 
		toolbar.add(noMolestar); 
		return toolbar;
	}

	private Component crearPanelCentral() {
		JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,crearPanelIzquierda(),crearPanelDerecha());
		panel.setDividerLocation(500); 
		return panel;
	}
	
	private Component crearPanelIzquierdaArriba() {
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(crearPanelToolbarHabitaciones(),BorderLayout.NORTH);
		panel.add(crearPanelListaHabitaciones(),BorderLayout.CENTER);
		
		return panel;
	}
	private Component crearPanelIzquierdaAbajo() {
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(crearPanelToolbarAgrupaciones(),BorderLayout.NORTH);
		panel.add(crearPanelListaAgrupaciones(),BorderLayout.CENTER);
		
		return panel;
	}
	private Component crearPanelListaAgrupaciones() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setBackground(new Color(230, 230, 230));
		panel.setOpaque(true);
		listaAgrupaciones = new JList<>();	
		listaAgrupaciones.setModel(controladorAgrupaciones);
		listaAgrupaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaAgrupaciones.setCellRenderer(renderer3); 	
		listaAgrupaciones.setBackground(new Color(230, 230, 230));
		listaAgrupaciones.setOpaque(true);
		listaAgrupaciones.addListSelectionListener(this);
		
		panel.setViewportView(listaAgrupaciones);
		return panel;
	}
	private Component crearPanelToolbarAgrupaciones() {
		JToolBar toolbar = new JToolBar();
		
		
		banadirAgrupacion = new JButton(new ImageIcon("img/anadir.png"));
		banadirAgrupacion.addActionListener(this);
		banadirAgrupacion.setActionCommand("anadirAgrupacion");
		
		toolbar.add(banadirAgrupacion);
		
		bquitarAgrupacion = new JButton(new ImageIcon("img/quitar.png"));
		bquitarAgrupacion.addActionListener(this);
		bquitarAgrupacion.setActionCommand("quitarAgrupacion");
		bquitarAgrupacion.setEnabled(false);
		toolbar.add(bquitarAgrupacion);
		toolbar.add(Box.createGlue()); 
		 
		JLabel lNombre = new JLabel("AGRUPACIONES"); 
		lNombre.setFont(new Font("Garamond",Font.PLAIN,12)); 
		lNombre.setForeground(new Color(35,35,5)); 
		toolbar.add(lNombre); 
		toolbar.add(Box.createHorizontalGlue());

		bactivarAgrupacion = new JButton(new ImageIcon("img/salir.png"));
		bactivarAgrupacion.addActionListener(this);
		bactivarAgrupacion.setActionCommand("activarAgrupacion");
		bactivarAgrupacion.setEnabled(true);
		toolbar.add(bactivarAgrupacion);
		
		
		return toolbar;
	}
	private Component crearPanelIzquierda() {
		JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,crearPanelIzquierdaArriba(),crearPanelIzquierdaAbajo());
		panel.setDividerLocation(500); 
		return panel;
	}
	
	private Component crearPanelDerecha() {
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(crearPanelToolbarDispositivos(),BorderLayout.NORTH);
		panel.add(crearPanelListaDispositivos(),BorderLayout.CENTER);
		
		return panel;
	}
	private Component crearPanelListaDispositivos() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		listaDispositivos = new JList<>();
		listaDispositivos.setBackground(new Color(230, 230, 230));
		listaDispositivos.setOpaque(true);
		listaDispositivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaDispositivos.addListSelectionListener(this);
		listaDispositivos.setCellRenderer(renderer2);
		panel.setViewportView(listaDispositivos);
		return panel;
	}
	private Component crearPanelListaHabitaciones() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setBackground(new Color(230, 230, 230));
		panel.setOpaque(true);
		listaHabitaciones = new JList<>();	
		listaHabitaciones.setModel(controlador);
		listaHabitaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaHabitaciones.setBackground(new Color(230, 230, 230));
		listaHabitaciones.setOpaque(true);
		listaHabitaciones.addListSelectionListener(this);
		
		panel.setViewportView(listaHabitaciones);
		listaHabitaciones.setCellRenderer(renderer);
		return panel;
	}

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			
		}
		Principal programa = new Principal();	

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch(e.getActionCommand()) {		
		case "quitarHabitacion":
			listaDispositivos.clearSelection();
			oldVal=new ArrayList<>();
			oldVal.add("borrar");
			oldVal.add("nada");
			propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, listaHabitaciones.getSelectedValue().getNombre()));
			oldVal=null;
			controladorAgrupaciones.eleminarDispositivoTodas(controlador.getMapa().get(listaHabitaciones.getSelectedValue()));
			controlador.eliminarDispositivosHabitacion(listaHabitaciones.getSelectedValue());
			controlador.eliminarHabitacion(listaHabitaciones.getSelectedValue());
			listaHabitaciones.clearSelection();
			listaDispositivos.setListData(new Dispositivo[0]);
			bquitarDispositivo.setEnabled(false);
			listaHabitaciones.clearSelection();
			banadirDispositivo.setEnabled(false);
			if(controlador.isEmpty()) {
				bquitarHabitacion.setEnabled(false);
			}
			listaHabitaciones.clearSelection();
			break;
		
		case "anadirHabitacion":
			DialogoHabitacion dialogoHabitacion = new DialogoHabitacion(this,controlador.getMapa());
			Habitacion habitacion = dialogoHabitacion.getHabitacion();
			if(habitacion!=null) {
				controlador.anadirHabitacion(habitacion);
				controlador.escribirHabitacion(habitacion, casa);
				oldVal=new ArrayList<>();
				oldVal.add("enviar");
				oldVal.add("nada");
				propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, habitacion.getNombre()));
				oldVal=null;
			}
			listaHabitaciones.clearSelection();
			listaDispositivos.setListData(new Dispositivo[0]);
			break;
		case "activarAgrupacion":
			oldVal=new ArrayList<>();
			oldVal.add("activarAgrupacion");
			oldVal.add("nada");
			controladorAgrupaciones.encenderAgrupacion(listaAgrupaciones.getSelectedValue());
			propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, listaAgrupaciones.getSelectedValue()));
			oldVal=null;
			break;
		case "anadirDispositivo":
			DialogoDispositivos dialogoDispositivo = new DialogoDispositivos(this,"Anadir dispositivo",true,controlador.getMapa());
			if(dialogoDispositivo.getDispositivo()!=null) {
				controlador.anadirDispositivo(listaHabitaciones.getSelectedValue(), dialogoDispositivo.getDispositivo());
				controlador.escribirHabitacion(listaHabitaciones.getSelectedValue(), casa);
				oldVal=new ArrayList<>();
				oldVal.add("enviar");
				oldVal.add("nada");
				propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, listaHabitaciones.getSelectedValue().getNombre()));
				oldVal=null;
			}
			break;

		case "anadirAgrupacion":{
			if(!controlador.getMapa().keySet().isEmpty()) {
				dialogoAgrupacion=new DialogoAgrupaciones(this, controladorAgrupaciones);
				if(dialogoAgrupacion.isCrear()) {
					controladorAgrupaciones.anadirDispositivos(dialogoAgrupacion.getNombre(), dialogoAgrupacion.getListaAgrupacion(), this);
					controladorAgrupaciones.escribirAgrupacion(dialogoAgrupacion.getNombre());
					
				}
				listaAgrupaciones.clearSelection();
				listaHabitaciones.clearSelection();
				listaDispositivos.setListData(new Dispositivo[0]);
			}else {
				JOptionPane.showMessageDialog(this, "No hay habitaciones", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
			break;
		case "quitarAgrupacion":{
			if(listaAgrupaciones.getSelectedValue()!=null) {
				oldVal=new ArrayList<>();
				oldVal.add("borrar");
				oldVal.add("nada");
				propertyChange(new PropertyChangeEvent(this, "envioAgrupacion", oldVal, listaAgrupaciones.getSelectedValue()));
				oldVal=null;
				controladorAgrupaciones.eliminarString(listaAgrupaciones.getSelectedValue());
			}
			listaAgrupaciones.clearSelection();
			listaHabitaciones.clearSelection();
			listaDispositivos.setListData(new Dispositivo[0]);
		}
			break;
		
		case "quitarDispositivo":
			eliminar=true;
			lNombrePanelDispositivos.setText("PULSE EN UN DISPOSITIVO PARA ELIMINARLO"); 
			listaDispositivos.clearSelection();
			break;
		case "activarAlarma":
			alarma=null;
			alarma = new Alarma();
			alarma.accion();
			break;
		case "noMolestar": 
			oldVal=new ArrayList<>();
			oldVal.add("noMolestar");
			oldVal.add("nada");
			propertyChange(new PropertyChangeEvent(this,"noMolestar",true,listaHabitaciones.getSelectedValue().getNombre()));
			controlador.escribirHabitacion(listaHabitaciones.getSelectedValue(), casa);
			controladorAgrupaciones.buscarDispositivos(controlador.getMapa().get(listaHabitaciones.getSelectedValue())).forEach(key->{
				controladorAgrupaciones.escribirAgrupacion(key);
			});
			propertyChange(new PropertyChangeEvent(this,"envioHabitacion",oldVal,listaHabitaciones.getSelectedValue().getNombre())); 
			oldVal=null;
			break; 
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if(arg0.getSource()==listaHabitaciones) {
			listaAgrupaciones.clearSelection();
			if(listaHabitaciones.getSelectedIndex()==-1) {
				bquitarHabitacion.setEnabled(false);
				noMolestar.setEnabled(false); 
			}else {
				propertyChange(new PropertyChangeEvent(this, "dispositivos", true, null));
				bquitarHabitacion.setEnabled(true);
				if(!listaHabitaciones.getSelectedValue().isNoMolestar())banadirDispositivo.setEnabled(true);
				else banadirDispositivo.setEnabled(false);
				banadirHabitacion.setEnabled(true);
				noMolestar.setEnabled(true);
			}
		}else if(arg0.getSource()==listaAgrupaciones) {
			listaHabitaciones.clearSelection();
			if(listaAgrupaciones.getSelectedIndex()!=-1) {
				listaDispositivos.setListData(controladorAgrupaciones.getDispositivosData(listaAgrupaciones.getSelectedValue()));
				bquitarAgrupacion.setEnabled(true);
				banadirDispositivo.setEnabled(false);
				noMolestar.setEnabled(false); 
			}else {
				bquitarAgrupacion.setEnabled(false);
			}
		}else if (eliminar==true && listaDispositivos.getSelectedIndex()!=-1) {
			if(listaHabitaciones.getSelectedIndex()!=-1) {
				List<Dispositivo>disp=new ArrayList<>();
				disp.add(listaDispositivos.getSelectedValue());
				controladorAgrupaciones.eleminarDispositivoTodas(disp);
				disp=null;
				controlador.eleminarDispositivo(listaHabitaciones.getSelectedValue(), listaDispositivos.getSelectedValue());
				controlador.escribirHabitacion(listaHabitaciones.getSelectedValue(), casa);
				oldVal=new ArrayList<>();
				oldVal.add("enviar");
				oldVal.add("nada");
				propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, listaHabitaciones.getSelectedValue().getNombre()));
				oldVal=null;
			}else {
				controladorAgrupaciones.eleminarDispositivo(listaAgrupaciones.getSelectedValue(),listaDispositivos.getSelectedValue());	
			}
			listaDispositivos.clearSelection();
			eliminar=false;
			lNombrePanelDispositivos.setText("DISPOSITIVOS"); 

		}else if(arg0.getSource()==listaDispositivos) {
			if(listaDispositivos.getSelectedIndex()!=-1) {
				if(listaHabitaciones.getSelectedIndex()!=-1&&!listaHabitaciones.getSelectedValue().isNoMolestar()) { 
					listaDispositivos.getSelectedValue().modificar(this);
					controlador.escribirHabitacion(listaHabitaciones.getSelectedValue(), casa);
					oldVal=new ArrayList<>();
					oldVal.add("enviar");
					oldVal.add("nada");
					propertyChange(new PropertyChangeEvent(this, "envioHabitacion", oldVal, listaHabitaciones.getSelectedValue().getNombre()));
					oldVal=null;
					controladorAgrupaciones.dispositivoModificado(listaDispositivos.getSelectedValue());
				}
				else if(listaAgrupaciones.getSelectedIndex()!=-1) {
					controladorAgrupaciones.getMapaEstados().get(listaAgrupaciones.getSelectedValue()).get(listaDispositivos.getSelectedIndex()).modificar(this);
					controladorAgrupaciones.escribirAgrupacion(listaAgrupaciones.getSelectedValue());
				}
				listaDispositivos.clearSelection();	
				controlador.ordenarListas(); 
				if(listaHabitaciones.getSelectedValue()!=null)listaDispositivos.setListData(controlador.getDispositivosData(listaHabitaciones.getSelectedValue())); 
			}
		}
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()) {
		case "dispositivos":
			if(listaHabitaciones.getSelectedIndex()>-1) {
				listaDispositivos.setListData(controlador.getDispositivosData(listaHabitaciones.getSelectedValue()));
				bquitarDispositivo.setEnabled(true);
				if(listaDispositivos.getModel().getSize()==0)bquitarDispositivo.setEnabled(false);
			}
			else listaDispositivos.setListData(new Dispositivo[0]);
			break;
		case "agrupacion":
			if(listaAgrupaciones.getSelectedIndex()>-1) {
				listaDispositivos.setListData(controladorAgrupaciones.getDispositivosData(listaAgrupaciones.getSelectedValue()));
				bquitarDispositivo.setEnabled(true);
				if(listaDispositivos.getModel().getSize()==0)bquitarDispositivo.setEnabled(false);
			}
			break;
		case "envioHabitacion":
			@SuppressWarnings("unchecked") List<String>oldV=(List<String>) evt.getOldValue();
			ips.stream().filter(ipf->!ipf.equals(oldV.get(1))).forEach(ip->new EnvioHabitaciones(ip,"files/"+casa+"/habitaciones/"+evt.getNewValue()+".dat",oldV.get(0),this).start());
			if((oldV.get(0)).equals("borrar")) {
				File file = new File("files/"+casa+"/habitaciones/"+evt.getNewValue()+".dat");
				file.delete();
			}
			cVersion.subirVersion();
			new envioFTP("172.17.23.143",casa,username,password).start();
			break;
		case "envioAgrupacion":
			@SuppressWarnings("unchecked") List<String>oldV2=(List<String>) evt.getOldValue();
			ips.stream().filter(ipf->!ipf.equals(oldV2.get(1))).forEach(ip->{
				new EnvioHabitaciones(ip,"files/"+casa+"/agrupaciones/originales/"+evt.getNewValue()+".dat",oldV2.get(0),this).start();
				new EnvioHabitaciones(ip,"files/"+casa+"/agrupaciones/estados/"+evt.getNewValue()+".dat",oldV2.get(0),this).start();
			});
			if((oldV2.get(0)).equals("borrar")) {
				File file = new File("files/"+casa+"/agrupaciones/originales/"+evt.getNewValue()+".dat");
				file.delete();
				file = new File("files/"+casa+"/agrupaciones/estados/"+evt.getNewValue()+".dat");
				file.delete();
			}
			cVersion.subirVersion();
			new envioFTP("172.17.23.143",casa,username,password).start();
			break;
		case "habitacionRecibida":
			Path p= Paths.get((String)evt.getNewValue());
			controlador.descargarHabitacion(p,controladorAgrupaciones);
			controlador.leerFichero(p.toString());
			break;
		case "agrupacionRecibida":
			File w= new File((String)evt.getNewValue());
			controladorAgrupaciones.descargarAgrupacion(w);
			if(w.toString().contains("estados")) {
				controladorAgrupaciones.leerFichero(w.toString(),controladorAgrupaciones.getMapaEstados());
			}else {
				controladorAgrupaciones.leerFichero(w.toString(),controladorAgrupaciones.getMapa());
			}
			break;
		case "quitarIp":
			ips.remove(evt.getNewValue());
			ips.forEach(ip->System.out.println(ip));
			break;
		case "borrarHabitacion":
			Path p2= Paths.get((String)evt.getNewValue());
			controlador.descargarHabitacion(p2,controladorAgrupaciones);
			break;
		case "borrarAgrupacion":
			File p3= new File((String)evt.getNewValue());
			controladorAgrupaciones.descargarAgrupacion(p3);
			break;
		case "noMolestar":
			List<Habitacion>habi=new ArrayList<>();
			controlador.getMapa().keySet().forEach(key->{
				if(key.getNombre().equals((String)evt.getNewValue()))habi.add(key);
			});
			Habitacion hab=habi.get(0);
			if(!hab.isNoMolestar()) { 
				for(Dispositivo disp: controlador.getMapa().get(hab)) { 
					disp.setNoMolestar(true); 
				} 
				hab.setNoMolestar(true); 
				listaDispositivos.setListData(controlador.getDispositivosData(hab)); 
				banadirDispositivo.setEnabled(false); 
				bquitarDispositivo.setEnabled(false); 
			} 
			else { 
				for(Dispositivo disp: controlador.getMapa().get(hab)) { 
					disp.setNoMolestar(false); 
				} 
				hab.setNoMolestar(false); 
				listaDispositivos.setListData(controlador.getDispositivosData(hab)); 
				banadirDispositivo.setEnabled(true); 
				if(listaDispositivos.getModel().getSize()==0)bquitarDispositivo.setEnabled(false);
				else bquitarDispositivo.setEnabled(true); 
				
			}
			controlador.noMolestar(); 
			break;
		case "inicializar":
			controlador.inicializar(casa);
			controladorAgrupaciones.inicializar(casa);
			break;
		case "encenderAgrupacion":
			controladorAgrupaciones.encenderAgrupacion(listaAgrupaciones.getSelectedValue());
			break;
		case "comandosDisp": controlador.agregarComandoVar((Dispositivo)evt.getNewValue());
		System.out.println("ASDALSIHDLASHFOAI�FJAM�SOIFASPODIAOPSDMAOPSD");
		break;
		}
		
	}

}
