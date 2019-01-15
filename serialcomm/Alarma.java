package serialcomm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import javax.swing.Timer;

import gnu.io.CommPortIdentifier;

public class Alarma implements PropertyChangeListener, ActionListener {
	SerialComm lineaSerie;
	Lector hiloLectura;
	CommPortIdentifier puerto;
	Scanner teclado = new Scanner (System.in);
	Cam2 cam;
	Timer cuentaAtras;
	int tiempo;
	public Alarma(){
		lineaSerie = new SerialComm();
		cuentaAtras= new Timer(1000,this);
		tiempo=0;
		puerto = lineaSerie.encontrarPuerto();
		
		if (puerto == null) {
			System.out.println("No se ha encontrado una linea serie");
			System.exit(0);
		}else {
			try {
				lineaSerie.conectar(puerto);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			System.out.println("Linea serie encontrada en: "+ puerto.getName());
			hiloLectura = new Lector(lineaSerie, puerto);
			hiloLectura.addPropertyChangeListener(this);
		}
	}
	public void accion() {
		hiloLectura.start();
		lineaSerie.escribir("A");
		cam = new Cam2(lineaSerie,puerto);
		cam.addPropertyChangeListener(this);
		cam.start();
		System.out.println("Encendiendo alarma");

	}
	
	public void escribir(String msg) {
		lineaSerie.escribir(msg);
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()) {
		case "apagarAlarma":
			hiloLectura.parar();
			lineaSerie.cerrar();
			hiloLectura.interrupt();
			if(cuentaAtras.isRunning()) cuentaAtras.stop();
			break;
		case "empezarTimer":
			cuentaAtras.start();
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		tiempo++;
		if(tiempo==30) {
			lineaSerie.escribir("C");
			hiloLectura.ruido();
			cuentaAtras.stop();
		}
	}
}
