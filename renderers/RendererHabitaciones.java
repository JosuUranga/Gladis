package renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import gladis.Habitacion;

@SuppressWarnings("serial")
public class RendererHabitaciones extends JLabel implements ListCellRenderer<Habitacion> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Habitacion> list,
			Habitacion value, int index, boolean isSelected, boolean cellHasFocus){
		if (isSelected) {
			setFont( new Font("Garamond",Font.BOLD,28));
			
			if(value.isNoMolestar()) this.setForeground(Color.RED);
			else setForeground(Color.BLACK); 

			setBackground(new Color(230,230,180));	
		}else {
			setFont( new Font("Garamond",Font.PLAIN,26));
			
			if(value.isNoMolestar()) this.setForeground(Color.RED);
			else setForeground(new Color(35,35,5)); 
			
			this.setBackground(new Color(230, 230, 230));
		}	
		this.setText(value.toString().toUpperCase());
		this.setOpaque(true);
		return this;
	}
	

}