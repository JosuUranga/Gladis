package sockets;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;

public class ComunicacionServidor extends Thread {
    private Socket socket = null;
    PropertyChangeSupport soporte;
    public ComunicacionServidor(Socket socket,PropertyChangeListener listener) {
        super("comunicacionCliente");
        this.socket = socket;
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
					 System.out.println(object);
					if(object instanceof String&& !nombreArchivo.contains((String) object)) {
						 if(((String) object).endsWith(".dat")) {
						    	nombreArchivo=(String)object;
						    	if (nombreArchivo.contains("/agrupaciones/")) esAgrupacion=true;
						    	out.writeObject("Nombre Recibido");
						 }
						 if(object.equals("borrar")) {
							 File file= new File(nombreArchivo);
							 if(esAgrupacion)soporte.firePropertyChange("borrarAgrupacion", true, nombreArchivo);
								else soporte.firePropertyChange("borrarHabitacion", true, nombreArchivo);
							 file.delete();
							 break;
						 }
						 if(object.equals("noMolestar")) {
							 File file= new File(nombreArchivo);
							 soporte.firePropertyChange("noMolestar", true, file.getName().replaceAll(".dat", ""));
							 break;
						 }
						 if(((String)object).equals("encenderAgrupacion")) {
							 File file = new File(nombreArchivo);
							 soporte.firePropertyChange("envioHabitacion", "encenderAgrupacion", file.getName().replaceAll(".dat", ""));
							 soporte.firePropertyChange("encenderAgrupacion", true,file.getName().replaceAll(".dat", ""));
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