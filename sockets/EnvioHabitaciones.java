package sockets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class EnvioHabitaciones extends Thread{
	String hostname;
	String habitacion;
	String modo;
	int portNumber=5001;
	 PropertyChangeSupport soporte;
    public EnvioHabitaciones(String hostname,String habitacion,String modo,PropertyChangeListener listene) {
    	this.hostname=hostname;
    	this.habitacion=habitacion;
    	this.modo=modo;
    	this.portNumber=5001;
    	soporte=new PropertyChangeSupport(this);
    	this.addPropertyChangeListener(listene);
    }
    public void run () {
        try (
            Socket socket = new Socket(hostname, portNumber);
        	ObjectInputStream  in = new ObjectInputStream(socket.getInputStream());
        	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {
        	if(habitacion!=null) {
        		Object object;
                int num=0;
                Boolean nombre=false;
                try {
    				while ((object=in.readObject())!= null) {
    					if(object instanceof String) {
    						if(object.equals("Nombre Recibido"))nombre=true;
    						if(nombre && modo.equals("borrar")) {
    							out.writeObject("borrar");
    							break;
    						}
    						if(object.equals("Conexion establecida"))out.writeObject(habitacion);
    					}
    						if(nombre && num<2) {
    							System.out.println(habitacion);
    							try (ObjectInputStream inf = new ObjectInputStream(
    									new FileInputStream(habitacion))) {
    								while(num<=1) {
    									Object obj=inf.readObject();
    									System.out.println(obj);
    									out.writeObject(obj);
    									num++;
    								}
    								out.writeObject("Finalizado");
    								break;
    								} catch (FileNotFoundException e) {
    									e.printStackTrace();
    								} catch (IOException e) {
    									e.printStackTrace();
    								} catch (ClassNotFoundException e) {
    									e.printStackTrace();
    								}
    						}
    					}
    			
    			} catch (ClassNotFoundException e) {
    				e.printStackTrace();
    			}
        	}
        	else {
        		out.writeObject("Hola!");
        	}
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostname);
        } catch (IOException e) {
            System.err.println("No se ha podido conectar con: " +
                hostname+", quitandolo de la lista...");
            	soporte.firePropertyChange("quitarIp", true, hostname);
        }
    }
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
}