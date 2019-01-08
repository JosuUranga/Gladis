package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import exceptions.TiempoFormatException;
import exceptions.VacioException;
import gladis.Dispositivo;
import gladis.DispositivoTmp;
import gladis.Principal;
import gladis.Tiempo;
import gladis.Variable;

public class DialogoModificar extends JDialog implements ActionListener{
	
	JRadioButton on, off;
	Dispositivo dispositivo;
	JPanel panelContenido;
	boolean estado;
	JTextField text1, text2;
	
	List<Variable>variables;
	
	Tiempo tiempo;
	String tipo;
	Font f;
	
	
	public DialogoModificar(Principal p ,Dispositivo dispositivo) {
		super(p, "Modificar", true);
		variables=dispositivo.getVariablesCopia();
		tipo="";
		tiempo=new Tiempo();
		f=new Font ("Arial",Font.PLAIN,18);
	

		this.dispositivo=dispositivo;
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setContentPane(crearPanel());
		this.setResizable(true);
		this.setVisible(true);
	}

	private Container crearPanel() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20),BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Editar")));
		
		panel.add(crearContenido(), BorderLayout.CENTER);
		panel.add(crearBotones(), BorderLayout.SOUTH);
		return panel;
	}

	private Component crearContenido() {
		panelContenido = new JPanel(new GridLayout(1,1, 50, 50));
		panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JPanel panel =new JPanel(new GridLayout(1,2, 10, 10)); 
		ButtonGroup grupo=new ButtonGroup();
		
		on=new JRadioButton("ON");
		grupo.add(on);
		panel.add(on);
		
		
		off=new JRadioButton("OFF");
		grupo.add(off);
		panel.add(off);
		panelContenido.add(panel);
		
		if(dispositivo.isEstado())
			on.setSelected(true);
		else
			off.setSelected(true);
		
		
		rellenarPanel();
		
		return panelContenido;
	}

	private void rellenarPanel() {
		int i=0;
		for(Variable variable: variables) {
			panelContenido.add(crearVariable(variable, i));
			i++;
		}
		
		if(dispositivo instanceof DispositivoTmp) {
			panelContenido.add(panelTiempo());
			panelContenido.setLayout(new GridLayout(dispositivo.getVariables().size()+2,1, 10, 10));
			this.setSize(500,((dispositivo.getVariables().size()+2)*100)+200);
			
		}
			
		else {
			panelContenido.setLayout(new GridLayout(dispositivo.getVariables().size()+1,1, 10, 10));
			this.setSize(500,((dispositivo.getVariables().size()+1)*100)+200);
		}
	
	}
	
	private Component crearVariable(Variable variable, int i) {
		JPanel panel =new JPanel(new GridLayout(1,2, 10, 10));
		JSlider slider=new JSlider(JSlider.HORIZONTAL, variable.getMin(), variable.getMax(), variable.getVal());
		JLabel label=new JLabel(variable.getVar()+": "+variable.getVal());
		
		slider.setMajorTickSpacing(variable.getMax()/4);
		slider.setMinorTickSpacing(variable.getMax()/8);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setForeground(Color.black);
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				variable.setVal(slider.getValue());
				label.setText(variable.getVar()+": "+variable.getVal());
				
			}
			
		});
		
		panel.add(label);
		panel.add(slider);
		
		return panel;
	}
	

	private Component panelTiempo() {
		JPanel panelProgramar=new JPanel(new GridLayout(1,2,10,10));
		JLabel dosPuntos = new JLabel(":");
		panelProgramar.add(new JLabel("Programar Tiempo"));
		
		
		JPanel panel=new JPanel(new GridLayout(1, 3));
		JPanel Ptext1 = new JPanel(new BorderLayout());
		Ptext1.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		JPanel Ptext2 = new JPanel(new BorderLayout());
		Ptext2.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		text1=new JTextField("0");
		text1.setHorizontalAlignment(0);
		Ptext1.add(text1,BorderLayout.CENTER);
		panel.add(Ptext1);
		dosPuntos.setHorizontalAlignment(0);
		dosPuntos.setFont(f);
		panel.add(dosPuntos);
		
		text2=new JTextField("0");
		text2.setHorizontalAlignment(0);
		Ptext2.add(text2,BorderLayout.CENTER);
		panel.add(Ptext2);
		panelProgramar.add(panel);
		
		return panel;
	}

	

	private Component crearBotones() {
		JPanel panel = new JPanel(new GridLayout(1,1,20,0));
		panel.setBorder(BorderFactory.createEmptyBorder(20,100,20,100));
		
		JButton boton1 = new JButton ("OK");
		boton1.setActionCommand("OK");
		boton1.addActionListener(this);
		
		JButton boton2 = new JButton ("Cancel");
		boton2.setActionCommand("Cancel");
		boton2.addActionListener(this);
		
		panel.add(boton1);
		this.getRootPane().setDefaultButton(boton1);;

		panel.add(boton2);

		
		return panel;
	}
	



	public boolean isEstado() {
		return estado;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public Tiempo getTiempo() {
		return tiempo;
	}
	private void verificarInt() {
		if(text1!=null)	Integer.parseInt(text1.getText());
		if(text2!=null)	Integer.parseInt(text2.getText());		
	}
	private void verificarVacio() throws VacioException{
		if(dispositivo.getTipo().equals("Programable tiempo")||dispositivo.getTipo().equals("No programable"))	{
			if(text1!=null)if(text1.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
			if(text2!=null)if(text2.getText().length()==0)	throw new VacioException("Debe rellenar todos los campos");
		}
	}

	private void verificarTiempo() throws TiempoFormatException{
		if(text2!=null)if(Integer.valueOf(text2.getText())>59)throw new TiempoFormatException(" ");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i;
		if(on.isSelected()) {
			estado=true;
		}
		else estado=false;
		switch(e.getActionCommand()) {
		case "Cancel":
			variables=null;
			estado=dispositivo.isEstado();
			this.dispose();
			break;
		case "OK":
			try {
			    verificarVacio();
				verificarInt();
				verificarTiempo();
				if(dispositivo instanceof DispositivoTmp) {
					tiempo.setMinutos(Integer.parseInt(text1.getText()));
					tiempo.setSegundos(Integer.parseInt(text2.getText()));
				}
				this.dispose();

			}catch(NumberFormatException e1) {
				JOptionPane.showMessageDialog(this, "Debe de introducir solo numeros", "Aviso" , JOptionPane.WARNING_MESSAGE);
			} catch (VacioException e1) {
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos", "Aviso" , JOptionPane.WARNING_MESSAGE);
			} catch (TiempoFormatException e1) {
				JOptionPane.showMessageDialog(this, "Debe introducir un tiempo válido", "Aviso" , JOptionPane.WARNING_MESSAGE);

			}
			break;
		}
	
	}

	


}
