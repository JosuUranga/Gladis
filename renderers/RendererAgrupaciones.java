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
			setFont(new Font("Garamond",Font.BOLD,28));
			setForeground(Color.BLACK);
			setBackground(new Color(230,230,180));	
		}else {
			setFont(new Font("Garamond",Font.PLAIN,26));
			setForeground(new Color(35,35,5));
			this.setBackground(new Color(230, 230, 230));
		}	
		this.setText(value.toString().toUpperCase());
		this.setOpaque(true);
		return this;
	}
	

}