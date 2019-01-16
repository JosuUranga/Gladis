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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import gladis.*;

@SuppressWarnings("serial")
public class Agrupaciones extends AbstractListModel<String> {
	Map<String,List<Dispositivo>>mapa;
	Map<String,List<Dispositivo>>mapaEstados;
	Map<Habitacion,List<Dispositivo>>mapaCasa;
	public Map<Habitacion, List<Dispositivo>> getMapaCasa() {
		return mapaCasa;
	}
	

	PropertyChangeSupport soporte;
	Habitaciones casa;
	public Agrupaciones(Habitaciones casa) {
		mapa = new HashMap<>();
		mapaEstados=new HashMap<>();
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
			mapaEstados.remove(nombre);
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
		Set<Entry<String,List<Dispositivo>>> datosEstados = mapaEstados.entrySet();
		datos.stream().forEach(set->{
			if(set.getKey().equals(agrupacion))escribirFichero(set,"files/"+casa+"/"+"agrupaciones/originales/");
		});
		datosEstados.stream().forEach(set->{
			if(set.getKey().equals(agrupacion)) {
				escribirFichero(set,"files/"+casa+"/"+"agrupaciones/estados/");
			}
		});
		soporte.firePropertyChange("envioAgrupacion", "enviar", agrupacion);
	}
	public void escribirFichero(Entry<String,List<Dispositivo>> habitacion, String casa) {
		try (ObjectOutputStream out = new ObjectOutputStream(
			new FileOutputStream(casa+habitacion.getKey()+".dat"))) {
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
		List<String>asd=new ArrayList<>(); 
		mapa.keySet().stream().forEach(keys->{ 
			if(keys.toString().equals(p.getFileName().toString().replaceAll(".dat", "")))asd.add(keys);
		});
		asd.forEach(key->{
			if(p.toString().contains("/estados")) {
				mapaEstados.remove(key);
			}else {
				mapa.remove(key);				
			}
		}); 
		this.fireContentsChanged(mapa, 0, mapa.size()); 
	}
	public void inicializar(String casa) {
		File file= new File("files/"+casa+"/agrupaciones/originales");
		File [] habitaciones=file.listFiles();
		for(int i=0;i<habitaciones.length;i++) {
			leerFichero("files/"+casa+"/"+"agrupaciones/originales/"+habitaciones[i].getName(),mapa);
		}
		file= new File("files/"+casa+"/agrupaciones/estados");
		habitaciones=file.listFiles();
		for(int i=0;i<habitaciones.length;i++) {
			leerFichero("files/"+casa+"/"+"agrupaciones/estados/"+habitaciones[i].getName(),mapaEstados);
		}
	}
	public void leerFichero(String filename,Map<String,List<Dispositivo>>map)
	{
		try (ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(filename))) {
				String key= (String) in.readObject();
				@SuppressWarnings("unchecked")
				List<Dispositivo> value=(List<Dispositivo>) in.readObject();
			
				map.put(key, value);
				if(filename.toString().contains("/agrupaciones/estados/")) {
					mapaEstados.put(key, value);
				}else {
					mapaCasa.entrySet().forEach(entry->{
						entry.getValue().forEach(disp->value.forEach(disp2->{
							if(disp.getNombre().equals(disp2.getNombre()))value.set(value.indexOf(disp2), disp);
						}));
					});
					if(map.containsKey(key))map.remove(key);
					/*for(Dispositivo d:value) {
					agregarComando(d);
					}
					Reconocedor.actualizaReconocedor();
					 */
					this.fireContentsChanged(map, 0, map.size());
				}
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
	public void anadirDispositivos (String nombre,List<Dispositivo> dispositivo, Principal principal) {
		List<Dispositivo>lista = mapa.get(nombre);
		if(lista==null)lista = new ArrayList<>();
		lista.addAll(dispositivo);
		mapa.put(nombre, lista);
		List<Dispositivo>listaCopy=new ArrayList<>();
		lista.forEach(disp->listaCopy.add((Dispositivo)disp.clone()));
		mapaEstados.put(nombre, listaCopy);
		agregarComandoAgrupacion(nombre);
		listaCopy.forEach(disp->disp.modificar(principal));
		System.out.println("ESCRIBIENDO AGRUPACION: "+nombre);
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public Map<String, List<Dispositivo>> getMapa() {
		return mapa;
	}
	public Map<String, List<Dispositivo>> getMapaEstados() {
		return mapaEstados;
	}
	public void encenderAgrupacion(String keyAgrup) {
		
		List<Dispositivo>disps=mapa.get(keyAgrup); 
		for(Dispositivo disp:disps) { 
			int a=disps.indexOf(disp); 
			Dispositivo disp2=mapaEstados.get(keyAgrup).get(a); 
			disp=disp2; 
			disps.set(a, disp); 
			disp2=disp.clone(); 
			mapaEstados.get(keyAgrup).set(a, disp2); 
		} 
		mapa.put(keyAgrup, disps); 
		mapaCasa.entrySet().forEach(entry->{ 
			entry.getValue().forEach(disp3->disps.forEach(disp4->{ 
				if(disp4.getNombre().equals(disp3.getNombre()))entry.getValue().set(entry.getValue().indexOf(disp3), disp4); 
			})); 
		});  
		
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
		mapaEstados.get(nombre).remove(dispositivo);
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
