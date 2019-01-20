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

		webcam.open(); //abre la webcam
		
		try {
			TimeUnit.SECONDS.sleep(3); //espera 3s
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		do {
		
		 k=0; cont=0;
		try {
			ImageIO.write(webcam.getImage(), "JPG", new File("capturas/firstCapture.jpg")); //saca una foto
			TimeUnit.MILLISECONDS.sleep(200);
			ImageIO.write(webcam.getImage(), "JPG", new File("capturas/secondCapture.jpg")); //saca otra foto
			File archivo=new File("capturas/firstCapture.jpg");
			
			File archivo2=new File("capturas/secondCapture.jpg");
			
			
			
			
			BufferedImage image1=ImageIO.read(archivo); //lee las imagenes y las mete en un BufferedImage
			BufferedImage image2=ImageIO.read(archivo2);
			
			int width=image1.getWidth();
			int height=image1.getHeight();
			

			//compara el color de los pixels y si su diferencia es menor a un tercio se consideran iguales
			//asi las sombras no afectan a la deteccion
			for(int y=0; y<height;y++){
	            for(int x=0; x<width;x++){
	                    Color c =new Color(image1.getRGB(x, y));
	                    Color z = new Color(image2.getRGB(x, y)); 
	                    cont++;
	                    if((c.getRGB()-z.getRGB())<5000000){ //sino, k++
	                    	k++;
	                    }
	            }
	        
			}
			System.out.print((float)k/cont);		
			System.out.println(" ESTO"); //calcula el % de igualdad de la foto	
		
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		}while(((float)k/cont)>=0.98); //repite esto mientras el las fotos sean iguales en un 98%
		System.out.println("MOVIMIENTO DETECTADO");
		lineaSerie.escribir("B");		 //si sale del while es que ha detectado movimiento y envia una B a la basys
		System.out.println("Enviando B");//y esta pasa a modo movimiento detectado
		soporte.firePropertyChange("empezarTimer", false, true); //empieza el timer
		
		
			try {
				TimeUnit.SECONDS.sleep(3); //espera 3s
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		webcam.close(); //cierra la camara
	}
}
		
	

