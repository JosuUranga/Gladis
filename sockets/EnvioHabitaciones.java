package sockets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;


public class EnvioHabitaciones extends Thread{
	String hostname;
	String habitacion;
	int portNumber=5001;
    public EnvioHabitaciones(String hostname,String habitacion) {
    	this.hostname=hostname;
    	this.habitacion=habitacion;
    	this.portNumber=5001;
    }
    public void run () {
        try (
            Socket socket = new Socket(hostname, portNumber);
        	ObjectInputStream  in = new ObjectInputStream(socket.getInputStream());
        	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {
        	if(habitacion==null) {
        		Object object;
                int num=0;
                Boolean nombre=false;
                try {
    				while ((object=in.readObject())!= null) {
    					if(object instanceof String) {
    						if(object.equals("Nombre recibido"))nombre=true;
    						if(object.equals("Conexion establecida"))out.writeObject(habitacion);
    					}
    						if(nombre=true && num<2) {
    							try (ObjectInputStream inf = new ObjectInputStream(
    									new FileInputStream(habitacion))) {
    								while(num<=1) {
    									out.writeObject(inf.readObject());
    									num++;
    								}
    								out.writeObject("Finalizado");
    									
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
            System.err.println("Couldn't get I/O for the connection to " +
                hostname);
        }
    }
}