package renderers;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import gladis.Dispositivo;

public class RendererDispositivos  implements ListCellRenderer<Dispositivo> {
	JPanel panel,panelBoton, panelContenido, panelFav;
	JButton boton ;
	JLabel lImagen, fav;
	JLabel lNombre;

	@Override
	public Component getListCellRendererComponent(JList<? extends Dispositivo> list,
			Dispositivo dispositivo ,int index,boolean isSelected,boolean cellHasFocus)
	     {
		panelFav=new JPanel(new GridLayout());
		panel= new JPanel(new BorderLayout(1,2));
		panelContenido=new JPanel(new GridLayout(1,3)); 
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setPreferredSize(new Dimension(panel.getWidth(),100));
		panel.setBackground(new Color(230, 230, 230));//gris
		panel.setOpaque(true);
		
		
		panelBoton = new JPanel(new GridLayout(1,1));
		panelBoton.setBorder(BorderFactory.createEmptyBorder(35, 15, 35, 15));
		panelBoton.setBackground(new Color(230, 230, 230));
		panelBoton.setOpaque(false);
		
		boton = new JButton("Modificar");	
		boton.setPreferredSize(new Dimension(100,20));
		lImagen= new JLabel();
		fav= new JLabel();
		lNombre= new JLabel(dispositivo.toString());//dispositivo.getNombre
		lNombre.setFont( new Font("Times New Roman",Font.ITALIC,30));
		lNombre.setForeground(Color.BLUE);
		lNombre.setHorizontalAlignment(JLabel.CENTER);
		
		Image img = (new ImageIcon(dispositivo.getImagen())).getImage();		
		Image newimg = img.getScaledInstance(80, 80,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(newimg); 
		lImagen.setIcon(imageIcon);
		fav.setIcon(new ImageIcon("img/estrella.png"));
	
		panelFav.add(fav);
		panelContenido.add(lImagen);
		panelContenido.add(lNombre);
		panelBoton.add(boton);
		panelContenido.add(panelBoton);
		
		panel.add(panelContenido, BorderLayout.CENTER);
		panel.add(panelFav, BorderLayout.EAST);
		
		if(dispositivo.isNoMolestar()) {
			boton.setEnabled(false);
			
		}
		else {
			boton.setEnabled(true);
		}
		
		if(isSelected && !dispositivo.isNoMolestar()) {
			boton.setBorder(BorderFactory.createLoweredBevelBorder());
			boton.setBackground(new Color(75,140,255));
			boton.setOpaque(true);
		}
		if(dispositivo.isFavorito()) {
			fav.setVisible(true);
		}
		else fav.setVisible(false);
		if(dispositivo.isEstado()) {
			panel.setBorder(BorderFactory.createLineBorder(Color.green));
		}
		return panel;
	     }

	

	

}