package models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import dialogs.DialogoDispositivos;
import exceptions.DialogoNombreRepetidoException;
import gladis.*;

public class Agrupaciones extends AbstractListModel<String> {
	Map<String,List<Dispositivo>>mapa;
	Map<Habitacion,List<Dispositivo>>mapaCasa;
	public Map<Habitacion, List<Dispositivo>> getMapaCasa() {
		return mapaCasa;
	}
	

	PropertyChangeSupport soporte;
	Habitaciones casa;
	public Agrupaciones(Habitaciones casa) {
		mapa = new HashMap<>();
		mapaCasa=casa.getMapa();
		this.casa=casa;
		soporte=new PropertyChangeSupport(this);
	}
	public void anadirString(String nombre) {		
		mapa.put(nombre, new ArrayList<>());
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void agregarComandoAgrupacion(String nombre) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
				fr.write("\n"+"public <modo"+nombre+"> = <accionAgrupacion> "+nombre+";");
				fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//casa.Reconocedor.actualizaReconocedor();
	}
	public void eliminarString (String nombre) {	
		if (mapa.containsKey(nombre)) {
			mapa.remove(nombre);
			eliminarComandoAgrupacion(nombre);
			System.out.println("ELIMINANDO COMANDO: "+nombre);
			this.fireContentsChanged(mapa, 0, mapa.size());
		}
		
	}
	public void eliminarComandoAgrupacion(String nombre) {
		String fileName="Comandos.txt";
		String tmp ="tmp.txt";
		try (Stream<String> stream = Files.lines(Paths.get(fileName));
				FileWriter fr = new FileWriter(tmp)) {
			stream.filter(line->!line.trim().contains("public <modo"+nombre+">")).forEach(linea->{
				try {
					fr.write(linea+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		casa.reemplazar(fileName,tmp);
		//casa.Reconocedor.actualizaReconocedor();
		
	}
	public void escribirAgrupacion(String agrupacion,String casa) {
		Set<Entry<String,List<Dispositivo>>> datos = mapa.entrySet();
		datos.stream().forEach(set->{
			if(set.getKey().equals(agrupacion))escribirFichero(set,casa);
		});
	}
	public void escribirFichero(Entry<String,List<Dispositivo>> habitacion, String casa) {
		try (ObjectOutputStream out = new ObjectOutputStream(
			new FileOutputStream("files/"+casa+"/"+"agrupaciones/"+habitacion.getKey()+".dat"))) {
			out.writeObject(habitacion.getKey());
			out.writeObject(habitacion.getValue());
			this.fireContentsChanged(mapa, 0, mapa.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void descargarAgrupacion(Path p) {
		mapa.keySet().stream().forEach(keys->{
			if(keys.toString().equals(p.getFileName()))mapa.remove(keys);
		});
	}
	public void inicializar(String casa) {
		File file= new File("files/"+casa+"/agrupaciones/");
		File [] habitaciones=file.listFiles();
		for(int i=0;i<habitaciones.length;i++) {
			leerFichero("files/"+casa+"/"+"agrupaciones/"+habitaciones[i].getName());
		}
	}
	public void leerFichero(String filename)
	{
		try (ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(filename))) {
				String key= (String) in.readObject();
				@SuppressWarnings("unchecked")
				List<Dispositivo> value=(List<Dispositivo>) in.readObject();
				mapaCasa.entrySet().forEach(entry->{
					entry.getValue().forEach(disp->value.forEach(disp2->{
						if(disp.getNombre().equals(disp2.getNombre()))value.set(value.indexOf(disp2), disp);
					}));
				});
				if(mapa.containsKey(key))mapa.remove(key);
				mapa.put(key, value);
				/*for(Dispositivo d:value) {
					agregarComando(d);
				}
				Reconocedor.actualizaReconocedor();
				*/
				this.fireContentsChanged(mapa, 0, mapa.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	public String[] getAgrupacionesKeys() {
		return mapa.keySet().toArray(new String[0]);
	}
	public void anadirDispositivos (String nombre,List<Dispositivo> dispositivo) {
		List<Dispositivo>lista = mapa.get(nombre);
		if(lista==null)lista = new ArrayList<>();
		lista.addAll(dispositivo);
		mapa.put(nombre, lista);
		agregarComandoAgrupacion(nombre);
		System.out.println("ESCRIBIENDO AGRUPACION: "+nombre);
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public Map<String, List<Dispositivo>> getMapa() {
		return mapa;
	}
	public void eleminarDispositivoTodas (Dispositivo dispositivo) {
		List<String>asdAS=new ArrayList<>();
		mapa.entrySet().stream().forEach(entry->{
			entry.getValue().forEach(Disp->{
				if(Disp.equals(dispositivo))asdAS.add(entry.getKey());
			});
		});
		asdAS.forEach(key->mapa.get(key).remove(dispositivo));
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void eleminarDispositivo(String nombre, Dispositivo dispositivo) {
		mapa.get(nombre).remove(dispositivo);
		this.fireContentsChanged(mapa, 0, mapa.size());
		soporte.firePropertyChange("agrupacion", true, false);
	}
	public boolean isEmpty() {
		if(mapa.isEmpty())return true;
		return false;
	}
	public Dispositivo[] getDispositivosData(String nombre) {
		List<Dispositivo>lista = mapa.get(nombre);
		return lista.toArray(new Dispositivo[0]); 
	}
	@Override
	public int getSize() {
		return mapa.size();
	}
	@Override
	public String getElementAt(int index) {
		Set<String> s = mapa.keySet();
		String[] h=s.toArray(new String[0]);
		return h[index];
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}

	
	
	
}
