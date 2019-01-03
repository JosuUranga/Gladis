package sockets;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;

public class ComunicacionServidor extends Thread {
    private Socket socket = null;
    List<String>ips;
    PropertyChangeSupport soporte;
    public ComunicacionServidor(Socket socket,PropertyChangeListener listener,List<String>ips) {
        super("comunicacionCliente");
        this.socket = socket;
        this.ips=ips;
        soporte=new PropertyChangeSupport(this);
        this.addPropertyChangeListener(listener);
       
    }
    public void run() {
        try (
        		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            String nombreArchivo=null;
            Object object;
            List<Object>archivo=new ArrayList<>();
            out.writeObject("Conexion establecida");
            try {
				while ((object = in.readObject()) != null) {
					if(object instanceof String) {
						 if(((String) object).endsWith(".dat")) {
						    	nombreArchivo=(String)object;
						    	out.writeObject("Nombre Recibido");
						 }
						 if(((String)object).equals("Hola!")) {
							 ips.add(socket.getRemoteSocketAddress().toString());
							 break;
						 }
						 if(object.equals("Finalizado")) {
							 try (ObjectOutputStream outf = new ObjectOutputStream(
										new FileOutputStream(nombreArchivo))) {
											for(Object a:archivo) {
												outf.writeObject(a);
											}
											soporte.firePropertyChange("habitacionRecibida", true, nombreArchivo);
								} catch (FileNotFoundException e) {
										e.printStackTrace();
								} catch (IOException e) {
										e.printStackTrace();
								}
						 	}
						 } else {
							 if(nombreArchivo!=null) {
								 archivo.add(object);
								
							}
						 }
					}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
}