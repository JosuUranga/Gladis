package sockets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class EnvioHabitaciones extends Thread{
	String hostname;
	String habitacion, modo;
	int portNumber=5001;
    public EnvioHabitaciones(String hostname,String habitacion, String modo) {
    	this.hostname=hostname;
    	this.habitacion=habitacion;
    	this.modo=modo;
    	this.portNumber=5001;
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
    							System.out.println("borrando");
    							out.writeObject("borrar");
    							break;
    						}
    						if(nombre && modo.equals("noMolestar")) {
    							System.out.println("nomolesteeen");
    							out.writeObject("noMolestar");
    							break;
    						}
    						if(object.equals("Conexion establecida"))out.writeObject(habitacion);
    					}
    						if(nombre==true && num<2) {
    							try (ObjectInputStream inf = new ObjectInputStream(
    									new FileInputStream(habitacion))) {
    								while(num<=1) {
    									out.writeObject(inf.readObject());
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
            System.err.println("Couldn't get I/O for the connection to " +
                hostname);
        }
    }
}