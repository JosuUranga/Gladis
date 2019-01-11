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
            String nombreArchivo="";
            Object object;
            Boolean esAgrupacion=false;
            List<Object>archivo=new ArrayList<>();
            out.writeObject("Conexion establecida");
            try {
				while ((object = in.readObject()) != null) {
					if(object instanceof String && !nombreArchivo.contains((String) object)) {
						 if(((String) object).endsWith(".dat")) {
						    	nombreArchivo=(String)object;
						    	if (nombreArchivo.contains("/agrupaciones/")) esAgrupacion=true;
						    	out.writeObject("Nombre Recibido");
						 }
						 if(((String)object).equals("Hola!")) {
							 ips.add(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
							 break;
						 }
						 if(((String)object).equals("borrar")) {
							 File file = new File(nombreArchivo);
							 if(esAgrupacion)soporte.firePropertyChange("borrarAgrupacion", true, nombreArchivo);
							 else soporte.firePropertyChange("borrarHabitacion", true, nombreArchivo);
							 file.delete();
							 break;
						 }
						 if(object.equals("Finalizado")) {
							 try (ObjectOutputStream outf = new ObjectOutputStream(
										new FileOutputStream(nombreArchivo))) {
											for(Object a:archivo) {
		
												outf.writeObject(a);
											}
											if(esAgrupacion)soporte.firePropertyChange("agrupacionRecibida", true, nombreArchivo);
											else soporte.firePropertyChange("habitacionRecibida", true, nombreArchivo);
											break;
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