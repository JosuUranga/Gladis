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
		lineaSerie = new SerialComm(); //crea un serialComm
		cuentaAtras= new Timer(1000,this); //el timer para cuando salte la alarma
		tiempo=0;							//el tiempo para cuando salte la alarma
		puerto = lineaSerie.encontrarPuerto();	//busca un peurto en el pc
		
		if (puerto == null) {
			System.out.println("No se ha encontrado una linea serie");
			System.exit(0);
		}else {
			try {
				lineaSerie.conectar(puerto); //si ha encontrado puerto lo conecta
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			System.out.println("Linea serie encontrada en: "+ puerto.getName()); 
			hiloLectura = new Lector(lineaSerie, puerto);		//una vez todo funcione se crea un hilo lector
			hiloLectura.addPropertyChangeListener(this);
		}
	}
	public void accion() { 		
		hiloLectura.start(); 		//empieza el hilo lector ya creado
		lineaSerie.escribir("A");	//le manda a la basys una A para que pase a modo alarma activada
		cam = new Cam2(lineaSerie,puerto); //crea la camara
		cam.addPropertyChangeListener(this);
		cam.start();						//empeiza la camara
		System.out.println("Encendiendo alarma");

	}
	
	public void escribir(String msg) { //funcion para mandar el msg llamando al escribir de la linea serie
		lineaSerie.escribir(msg);
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()) {
		case "apagarAlarma":		//cuando se le avisa de apagar la alarma 
			hiloLectura.parar();	//para el hilo lectura
			lineaSerie.cerrar();	//Cierra la linea serie
			hiloLectura.interrupt();//Le dice al hilo lectura que se apague cuando pueda
			if(cuentaAtras.isRunning()) cuentaAtras.stop(); //para la cuenta atras por si esta encendida
			break;
		case "empezarTimer":
			cuentaAtras.start(); //empieza la cuenta atras 
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) { //el timer de la cuenta atras
		tiempo++;
		if(tiempo==30) {					//cuando pasen 30s le manda una C a la basys para que se ponga modo
			lineaSerie.escribir("C"); 		//alarma sonando
			hiloLectura.ruido();			//suena el alarma.wav
			cuentaAtras.stop();				//para la cuenta atras
		}
	}
}
