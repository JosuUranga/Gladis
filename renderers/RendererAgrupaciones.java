package renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class RendererAgrupaciones extends JLabel implements ListCellRenderer<String> {

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus){
				
		if (isSelected) {
			setFont( new Font("Times New Roman",Font.BOLD,30));
			setForeground(Color.BLACK);
			setBackground(new Color(75,140,255));		
		}else {
			setFont( new Font("Times New Roman",Font.ITALIC,26));
			setForeground(Color.BLUE);
			this.setBackground(new Color(230, 230, 230));
		}	
		this.setText(value.toString().toUpperCase());
		this.setOpaque(true);
		return this;
	}
	

}