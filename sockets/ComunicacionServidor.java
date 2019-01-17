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
							 if (!ips.contains(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")))) {
								 ips.add(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
							 }
							 break;
						 }
						 if(((String)object).equals("borrar")) {
							 File file = new File(nombreArchivo);
							 List<String>oldValue=new ArrayList<>();
							 oldValue.add("borrar");
							 oldValue.add(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
							 if(esAgrupacion) {
								 soporte.firePropertyChange("envioAgrupacion", oldValue, file.getName().replaceAll(".dat", ""));
								 soporte.firePropertyChange("borrarAgrupacion", true, nombreArchivo);
							 }
							 else {
								 soporte.firePropertyChange("envioHabitacion", oldValue, file.getName().replaceAll(".dat", ""));
								 soporte.firePropertyChange("borrarHabitacion", true, nombreArchivo);
							 }
							 oldValue=null;
							 file.delete();
							 break;
						 }
						 if(((String)object).equals("noMolestar")) {
							 File file = new File(nombreArchivo);
							 List<String>oldValue=new ArrayList<>();
							 oldValue.add("noMolestar");
							 oldValue.add(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
							 soporte.firePropertyChange("envioHabitacion", oldValue, file.getName().replaceAll(".dat", ""));
							 soporte.firePropertyChange("noMolestar", true,file.getName().replaceAll(".dat", ""));
							 oldValue=null;
							 break;
						 }
						 if(object.equals("Finalizado")) {
							 try (ObjectOutputStream outf = new ObjectOutputStream(
										new FileOutputStream(nombreArchivo))) {
											for(Object a:archivo) {
		
												outf.writeObject(a);
											}
											File file = new File(nombreArchivo);
											List<String>oldValue=new ArrayList<>();
											 oldValue.add("enviar");
											 oldValue.add(socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
											if(esAgrupacion) {
												soporte.firePropertyChange("envioAgrupacion", oldValue, file.getName().replaceAll(".dat", ""));
												soporte.firePropertyChange("agrupacionRecibida", true, nombreArchivo);
											}
											else {
												soporte.firePropertyChange("envioHabitacion", oldValue, file.getName().replaceAll(".dat", ""));
												soporte.firePropertyChange("habitacionRecibida", true, nombreArchivo);
											}
											oldValue=null;
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
            System.out.println("Conexion perdida con:"+socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":"))+", sacandolo de la lista de ips..");
            soporte.firePropertyChange("quitarIp", true, socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().lastIndexOf(":")));
        }
    }
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
}