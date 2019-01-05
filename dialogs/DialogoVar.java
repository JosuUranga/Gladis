package dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import exceptions.MaxMinException;
import exceptions.NombreVariableException;
import exceptions.VacioException;
import exceptions.ValorInicialException;
import gladis.Dispositivo;

@SuppressWarnings("serial")
public class DialogoVar extends JDialog implements ActionListener{
	List <JTextField> textMax;
	List <JTextField> textMin;
	List <JTextField> textVal;
	List <JTextField> variables;
	Dispositivo dispositivo;
	PropertyChangeSupport soporte;
	public DialogoVar(JFrame ventana, Dispositivo dispositivo){
		super(ventana);
		this.dispositivo=dispositivo;
		variables=new ArrayList<>();
		crearTamano();
		soporte = new PropertyChangeSupport(this);
		textMax = new ArrayList<>();
		textMin = new ArrayList<>();
		textVal = new ArrayList<>();
		this.setContentPane(crearPanelVentana());
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void crearTamano() {
		if(dispositivo.getTipo().equals("Programable tiempo")||dispositivo.getTipo().equals("No programable"))	{
			if(dispositivo.getVariables().size()>=5)this.setSize(270*5,530);
			else this.setSize(270*dispositivo.getVariables().size(), 530);
			if(dispositivo.getVariables().size()>=5)this.setLocation(350,400);
			else this.setLocation(900-100*dispositivo.getVariables().size(), 300);
		}
		else {
			if(dispositivo.getVariables().size()>=5)this.setSize(270*5,400);
			else this.setSize(270*dispositivo.getVariables().size(), 400);
			if(dispositivo.getVariables().size()>=5)this.setLocation(350,400);
			else this.setLocation(900-100*dispositivo.getVariables().size(), 400);
		}		
	}

	private Container crearPanelVentana() {
		JPanel panel = new JPanel (new BorderLayout());
		
		panel.add(crearPanelDatos(),BorderLayout.CENTER);

		panel.add(crearPanelBotones(),BorderLayout.SOUTH);
		return panel;
	}
	

	private Component crearPanelBotones() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10,this.getWidth()/4,10,this.getWidth()/4));
		JButton boton1 = new JButton ("OK");
		boton1.addActionListener(this);
		panel.add(boton1,BorderLayout.CENTER);
		return panel;
	}
	private Component crearPanelDatos() {
		JPanel panelView = new JPanel (new GridLayout(1,dispositivo.getVariables().size(),10,10));
		JScrollPane panel =new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setViewportView(panelView);
		
		if(dispositivo.getTipo().equals("Programable tiempo")||dispositivo.getTipo().equals("No programable"))	{
			dispositivo.getVariables().forEach(variable->panelView.add(crearGrupoDatosVacios()));
		}
		else dispositivo.getVariables().forEach(variable->panelView.add(crearGrupoDatosDefinidos(variable.getVar())));
		

		return panel;
	}
	private Component crearGrupoDatosVacios() {
		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10),BorderFactory.createLineBorder(Color.GRAY)));
		JTextField field;
		field=new JTextField();
		variables.add(field);
		panel.add(crearTextField(field, "Variable:"));
		field=new JTextField();
		textMax.add(field);
		panel.add(crearTextField(field, "Valor Max."));
		field=new JTextField();
		textMin.add(field);
		panel.add(crearTextField(field, "Valor Min."));
		field=new JTextField();
		textVal.add(field);
		panel.add(crearTextField(field, "Valor Inicial"));
		return panel;
	}

	private Component crearGrupoDatosDefinidos(String string) {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),BorderFactory.createTitledBorder(string)));
		JTextField field;
		field=new JTextField();
		textMax.add(field);
		panel.add(crearTextField(field, "Valor Max."));
		field=new JTextField();
		textMin.add(field);
		panel.add(crearTextField(field, "Valor Min."));
		field=new JTextField();
		textVal.add(field);
		panel.add(crearTextField(field, "Valor Inicial"));
		return panel;
	}
	private Component crearTextField(JTextField text, String titulo) {
		JPanel panel = new JPanel(new GridLayout(1,2));
		JLabel label = new JLabel(titulo);	
		panel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
		panel.add(label);
		text.setHorizontalAlignment(0);
		panel.add(text);
		return panel;
	}
	
	private void verificarInt() {
		for(JTextField a:textMax) {
			Integer.parseInt(a.getText());
		}
		for(JTextField a:textMin) {
			Integer.parseInt(a.getText());
		}
		for(JTextField a:textVal) {
			Integer.parseInt(a.getText());
		}
	}
	private void verificarVacio() throws VacioException{
		if(dispositivo.getTipo().equals("Programable tiempo")||dispositivo.getTipo().equals("No programable"))	{
			for(JTextField a:variables) {
				if(a.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
			}
		}
		for(JTextField a:textMax) {
			if(a.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
		}
		for(JTextField a:textMin) {
			if(a.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
		}
		for(JTextField a:textVal) {
			if(a.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
		}
		
	}
	private void guardarValoresDefinidos() {
		for(int i=0;i<dispositivo.getVariables().size();i++) {
			dispositivo.getVariables().get(i).setMax(Integer.valueOf(textMax.get(i).getText()));
			dispositivo.getVariables().get(i).setMin(Integer.valueOf(textMin.get(i).getText()));
			dispositivo.getVariables().get(i).setVal(Integer.valueOf(textVal.get(i).getText()));
		}
	}
	private void guardarValoresVacios() {
		for(int i=0;i<dispositivo.getVariables().size();i++) {
			dispositivo.getVariables().get(i).setVar((variables.get(i).getText()));
			dispositivo.getVariables().get(i).setMax(Integer.valueOf(textMax.get(i).getText()));
			dispositivo.getVariables().get(i).setMin(Integer.valueOf(textMin.get(i).getText()));
			dispositivo.getVariables().get(i).setVal(Integer.valueOf(textVal.get(i).getText()));
		}		
	}
	private void verificarMaxMin() throws MaxMinException{
		
		for (int i=0; i<textMax.size(); i++) {
			if (Integer.valueOf(textMax.get(i).getText())<Integer.valueOf(textMin.get(i).getText()))throw new MaxMinException("El valor maximo debe de ser mayor al del minimo");
		}
				
	}
	private void verificarValorInicial() throws ValorInicialException{
		for (int i=0; i<textVal.size(); i++) {
			if (Integer.valueOf(textVal.get(i).getText())<Integer.valueOf(textMin.get(i).getText())
					||
					Integer.valueOf(textVal.get(i).getText())>Integer.valueOf(textMax.get(i).getText()))throw new ValorInicialException("El valor inicial tiene que estar entre los valores Maximos y Minimos");;
		}
	}
	private void verificarNombre() throws NombreVariableException{
		
		   for (int i=0; i<variables.size(); i++) {
			   if(!variables.get(i).getText().matches("[a-zA-Z]+"))throw new NombreVariableException("Introduzca un nombre válido");
			}
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")) {
			try {
			     verificarVacio();
			     verificarInt();
			     verificarMaxMin();
			     verificarValorInicial();
			     if(dispositivo.getTipo().equals("Programable tiempo")||dispositivo.getTipo().equals("No programable")) {
			    	 verificarNombre();
			    	 guardarValoresVacios();
			    	 soporte.firePropertyChange("comandosDisp", false, dispositivo);
			     }
				 else guardarValoresDefinidos();
			     this.dispose();
			}
			catch(VacioException c) {
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos", "Aviso" , JOptionPane.WARNING_MESSAGE);
			}
			catch (NumberFormatException c) {
				JOptionPane.showMessageDialog(this, "Debe de introducir solo numeros", "Aviso" , JOptionPane.WARNING_MESSAGE);
			} catch (MaxMinException c) {
				JOptionPane.showMessageDialog(this, "El valor maximo debe de ser mayor al del minimo", "Aviso" , JOptionPane.WARNING_MESSAGE);
			} catch (ValorInicialException c) {
				JOptionPane.showMessageDialog(this, "El valor inicial tiene que estar entre los valores Maximos y Minimos", "Aviso" , JOptionPane.WARNING_MESSAGE);
			} catch (NombreVariableException c) {
				JOptionPane.showMessageDialog(this, "Introduce un nombre valido", "Aviso" , JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}



