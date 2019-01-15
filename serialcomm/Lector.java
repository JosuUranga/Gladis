package serialcomm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import gnu.io.CommPortIdentifier;

public class Lector extends Thread {
	SerialComm lineaSerie;
	CommPortIdentifier puerto;
	File sonido;
	Clip clip;
	volatile boolean parar = false;
	int cont;
	PropertyChangeSupport soporte;
	public Lector(SerialComm lineaSerie, CommPortIdentifier puerto) {
		sonido =new File("alarma.wav");
		this.lineaSerie = lineaSerie;
		this.puerto = puerto;
		cont=0;
		clip=null;
		soporte= new PropertyChangeSupport(this);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	@Override
	public void run() {
		String mensaje = null;
		
		try {
			do {
				  mensaje = lineaSerie.leer();
				  System.out.println("Recivido: "+mensaje);//0010 0011 #
				  if(mensaje.equals("D")) {
					  lineaSerie.escribir("D");
					  System.out.println("Apagando alarma");
					  this.parar();
					  soporte.firePropertyChange("apagarAlarma", true, false);
				  }
				  else {
					  cont++;
				  }
				  if(cont>=3) {
					  cont=0;
					  lineaSerie.escribir("C");
					  ruido();
				  }
			}while (!parar);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("fin hilo lector");
	}
	public void parar() {
		
		if(clip!=null)if(clip.isActive())clip.stop();
		parar = true;
	}
	public void ruido() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sonido));
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clip.start();
	}
	
}
