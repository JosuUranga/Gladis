package renderers;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import gladis.Dispositivo;

public class RendererDispositivos  implements ListCellRenderer<Dispositivo> {
	JPanel panel,panelBoton;
	JButton boton ;
	JLabel lImagen;
	JLabel lNombre;

	@Override
	public Component getListCellRendererComponent(JList<? extends Dispositivo> list,
			Dispositivo dispositivo ,int index,boolean isSelected,boolean cellHasFocus)
	     {
		panel= new JPanel(new GridLayout(1,3));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setPreferredSize(new Dimension(panel.getWidth(),100));
		panel.setBackground(new Color(230, 230, 230));//gris
		panel.setOpaque(true);
		
		
		panelBoton = new JPanel(new GridLayout(1,1));
		panelBoton.setBorder(BorderFactory.createEmptyBorder(35, 15, 35, 15));
		panelBoton.setBackground(new Color(230, 230, 230));
		panelBoton.setOpaque(true);
		
		boton = new JButton("Modificar");	
		boton.setPreferredSize(new Dimension(100,20));
		lImagen= new JLabel();
		lNombre= new JLabel(dispositivo.getNombre());//dispositivo.getNombre
		lNombre.setFont( new Font("Times New Roman",Font.ITALIC,30));
		lNombre.setForeground(Color.BLUE);
		lNombre.setHorizontalAlignment(JLabel.CENTER);
		
		lImagen.setIcon(new ImageIcon("iconos/cocina.png"));//getImagen
		
	
		
		panel.add(lImagen);
		panel.add(lNombre);
		panelBoton.add(boton);
		panel.add(panelBoton);
		
		if(isSelected) {
			boton.setBorder(BorderFactory.createLoweredBevelBorder());
			boton.setBackground(new Color(75,140,255));
			boton.setOpaque(true);
		}
		return panel;
	     }

	

	

}