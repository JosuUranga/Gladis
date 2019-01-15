package serialcomm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

import gnu.io.CommPortIdentifier;

public class Cam2 extends Thread{
	SerialComm lineaSerie;
	CommPortIdentifier puerto;
	int k,cont;
	Webcam webcam;
	PropertyChangeSupport soporte;

	public Cam2(SerialComm lineaSerie, CommPortIdentifier puerto)  {
		this.lineaSerie=lineaSerie;
		this.puerto=puerto;
		soporte=new PropertyChangeSupport(this);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	public void run() {
		webcam=Webcam.getDefault();

		webcam.open();
		
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		do {
		
		 k=0; cont=0;
		try {
			ImageIO.write(webcam.getImage(), "JPG", new File("C:/Users/Densuke/Desktop/firstCapture.jpg"));
			TimeUnit.MILLISECONDS.sleep(200);
			ImageIO.write(webcam.getImage(), "JPG", new File("C:/Users/Densuke/Desktop/secondCapture.jpg"));
			File archivo=new File("C:/Users/Densuke/Desktop/firstCapture.jpg");
			
			File archivo2=new File("C:/Users/Densuke/Desktop/secondCapture.jpg");
			
			
			
			
			BufferedImage image1=ImageIO.read(archivo);
			BufferedImage image2=ImageIO.read(archivo2);
			
			int width=image1.getWidth();
			int height=image1.getHeight();
			

			
			for(int y=0; y<height;y++){
	            for(int x=0; x<width;x++){
	                    Color c =new Color(image1.getRGB(x, y));
	                    Color z = new Color(image2.getRGB(x, y)); 
	                    cont++;
	                    if((c.getRGB()-z.getRGB())<5000000){
	                    	k++;
	                    }
	            }
	        
			}
			System.out.print((float)k/cont);		System.out.println(" ESTO");	
		
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}while(((float)k/cont)>=0.98);
		System.out.println("MOVIMIENTO DETECTADO");
		lineaSerie.escribir("B");
		System.out.println("Enviando B");
		soporte.firePropertyChange("empezarTimer", false, true);
		
		
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
		webcam.close();
	}
}
		
	

