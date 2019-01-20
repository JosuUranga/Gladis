package models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.AbstractListModel;

import gladis.*;

public class Agrupaciones extends AbstractListModel<String> {
	Map<String,List<Dispositivo>>mapa;
	Map<String,List<Dispositivo>>mapaEstados;
	Map<Habitacion,List<Dispositivo>>mapaCasa;
	public Map<Habitacion, List<Dispositivo>> getMapaCasa() {
		return mapaCasa;
	}
	

	PropertyChangeSupport soporte;
	Habitaciones casa;
	String nCasa;
	List<String>oldVal;
	public Agrupaciones(Habitaciones casa,String nCasa) {
		mapa = new HashMap<>();
		mapaEstados=new HashMap<>();
		mapaCasa=casa.getMapa();
		this.casa=casa;
		this.nCasa=nCasa;
		casa.getReconocedor().setMapaAgrup(mapa); // le añade a la clase Programas el mapa de las agrupaciones mediante el Reconocedor
		soporte=new PropertyChangeSupport(this);
	}
	public void encenderAgrupacion(String keyAgrup) {
		List<Dispositivo>disps=mapa.get(keyAgrup);
		List<String>keys=buscarDispositivos(disps); //Busca en que agrupaciones se encuentra este disp y guarda la lista de agrupaciones
		List<String>agrupsAEscribir=new ArrayList<>();
		agrupsAEscribir.add(keyAgrup);
		for(Dispositivo disp:disps) {
			int a=disps.indexOf(disp);
			Dispositivo disp2=mapaEstados.get(keyAgrup).get(a);
			for(String key:keys) {		//Recorre la lista de agrupaciones en las que se encuentra el dispositivo a cambiar y los cambia por el que se ha encendido
				for(Dispositivo dispss:mapa.get(key)) {
					if(dispss.getNombre().equals(disp2.getNombre())) {
						mapa.get(key).set(mapa.get(key).indexOf(dispss), disp2);
						agrupsAEscribir.add(key);
					}
				}
			}
			disp=disp2;
			disps.set(a, disp);
			disp2=disp.clone();		//Crea un clon del dispositivo en mapaEstados para que dejen de ser el mismo dispositivo y no se hagan los cambios en el si se cambia el disp verdadero
			mapaEstados.get(keyAgrup).set(a, disp2);
		}
		mapa.put(keyAgrup, disps);
		List<Habitacion>habAEscribir=new ArrayList<>();
		mapaCasa.entrySet().forEach(entry->{
			entry.getValue().forEach(disp3->disps.forEach(disp4->{
				if(disp4.getNombre().equals(disp3.getNombre())) {
					entry.getValue().set(entry.getValue().indexOf(disp3), disp4);
					habAEscribir.add(entry.getKey());
				}
			}));
		}); 
		habAEscribir.forEach(hab->casa.escribirHabitacion(hab, nCasa));
		agrupsAEscribir.forEach(agr->escribirAgrupacion(agr));
	}
	public void anadirString(String nombre) {		
		mapa.put(nombre, new ArrayList<>());
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	
	//Esta funcion añade al Comandos.txt el comando de la agrupacion necesario
	public void agregarComandoAgrupacion(String nombre) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
			fr.write("\n"+"public <modo"+nombre+"> = modo "+nombre+";"); 	
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void eliminarString (String nombre) {	
		if (mapa.containsKey(nombre)) {
			mapa.remove(nombre);
			mapaEstados.remove(nombre);
			eliminarComandoAgrupacion(nombre); //eliminar el comando
			casa.Reconocedor.actualizaReconocedor(); //actualizar el reconocedor
			this.fireContentsChanged(mapa, 0, mapa.size());
		}
		
	}
	
	//esta funcion elimina el comando de la agrupacion correspondiente de la misma manera que borra todos los demas 
	//comandos, vease la clase Habitaciones.
	public void eliminarComandoAgrupacion(String nombre) {
		String fileName="Comandos.txt";
		String tmp ="tmp.txt";
		String line; 
		try(FileWriter fr = new FileWriter(tmp); 
					BufferedReader br = new BufferedReader(new FileReader(fileName))){ 
			while((line=br.readLine())!=null) { 
				if(!line.contains("public <modo"+nombre+">")) fr.write(line+"\n"); 
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		casa.reemplazar(fileName,tmp); //se reutiliza la funcion en Habitaciones
		
	}
	public void escribirAgrupacion(String agrupacion) {
		Set<Entry<String,List<Dispositivo>>> datos = mapa.entrySet();
		Set<Entry<String,List<Dispositivo>>> datosEstados = mapaEstados.entrySet();
		datos.stream().forEach(set->{
			if(set.getKey().equals(agrupacion))escribirFichero(set,"files/"+nCasa+"/"+"agrupaciones/originales/");
		});
		datosEstados.stream().forEach(set->{
			if(set.getKey().equals(agrupacion)) {
				escribirFichero(set,"files/"+nCasa+"/"+"agrupaciones/estados/");
			}
		});
		oldVal=new ArrayList<>();
		oldVal.add("enviar");
		oldVal.add("nada");
		oldVal=null;
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
	public void descargarAgrupacion(File p) {
		List<String>asd=new ArrayList<>();
		mapa.keySet().stream().forEach(keys->{
			if(keys.toString().equals(p.getName().toString().replaceAll(".dat", "")))asd.add(keys);
		});
		asd.forEach(key->{
			if(p.toString().contains("agrupaciones/estados")){
				mapaEstados.remove(key);
			}else {
				mapa.remove(key);
			}
		});
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void inicializar(String casa) {
		mapa.clear();
		mapaEstados.clear();
		mapaCasa=this.casa.getMapa();
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
				if(filename.toString().contains("estados")) {
					map.put(key, value);
				}else {
					mapaCasa.entrySet().forEach(entry->{
						entry.getValue().forEach(disp->value.forEach(disp2->{
							if(disp.getNombre().equals(disp2.getNombre()))value.set(value.indexOf(disp2), disp);
						}));
					});
					map.put(key, value);
					if(casa.isFtp())eliminarComandoAgrupacion(key); //borra el comando por si ya exite
					agregarComandoAgrupacion(key);  //agrega el comando correspondiente
					casa.Reconocedor.actualizaReconocedor(); //actualiza el reconocedor
					  
					this.fireContentsChanged(map, 0, map.size()); 
				}
				oldVal=new ArrayList<>();
				oldVal.add("noFTP");
				oldVal.add("nada");
				soporte.firePropertyChange("envioAgrupacion", oldVal, key);
				oldVal=null;
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
		agregarComandoAgrupacion(nombre); //añade el comandode la agrupacion
		casa.Reconocedor.actualizaReconocedor(); //actualiza el reconocedor
		listaCopy.forEach(disp->disp.modificar(principal));
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public Map<String, List<Dispositivo>> getMapa() {
		return mapa;
	}
	public Map<String, List<Dispositivo>> getMapaEstados() {
		return mapaEstados;
	}
	public List<String> buscarDispositivos(List<Dispositivo> lista) {
		List<String>asdAS=new ArrayList<>();
		mapa.entrySet().stream().forEach(entry->{
			entry.getValue().forEach(Disp->{
				lista.forEach(disp2->{
					if(Disp.equals(disp2))asdAS.add(entry.getKey());
				});
			});
		});
		return asdAS;
	}
	public void eleminarDispositivoTodas (List<Dispositivo> lista) {
		List<String>asdAS=buscarDispositivos(lista);
		asdAS.forEach(key->{
			lista.forEach(disp->mapa.get(key).remove(disp));			
		});
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void dispositivoModificado(Dispositivo dispositivo) {
		List<String>asdAS=new ArrayList<>();
		mapa.entrySet().stream().forEach(entry->{
			entry.getValue().forEach(Disp->{
				if(Disp.equals(dispositivo))asdAS.add(entry.getKey());
			});
		});
		oldVal=new ArrayList<>();
		oldVal.add("enviar");
		oldVal.add("nada");
		asdAS.forEach(key->soporte.firePropertyChange("envioAgrupacion", oldVal, key));
		oldVal=null;
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
