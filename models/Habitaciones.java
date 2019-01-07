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

import reconocedor.Reconocedor;

@SuppressWarnings("serial")
public class Habitaciones extends AbstractListModel<Habitacion> {
	Reconocedor Reconocedor;
	Map<Habitacion,List<Dispositivo>>mapa;
	PropertyChangeSupport soporte;
	
	public Habitaciones() {
		mapa = new HashMap<>();
		soporte=new PropertyChangeSupport(this);
		Reconocedor = new Reconocedor(mapa);
	}
	public void inicializar(String casa) {
		File file= new File("files/"+casa+"/habitaciones/");
		File [] habitaciones=file.listFiles();
		for(int i=0;i<habitaciones.length;i++) {
			leerFichero("files/"+casa+"/"+"habitaciones/"+habitaciones[i].getName());
		}
	}
	public void descargarHabitacion(Path p) {
		mapa.keySet().stream().forEach(keys->{
			if(keys.toString().equals(p.getFileName()))mapa.remove(keys);
		});
	}
	public void anadirHabitacion(Habitacion habitacion) {		
		mapa.put(habitacion, new ArrayList<>());		
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void eliminarHabitacion (Habitacion habitacion) {	
		if (mapa.containsKey(habitacion)) {
			mapa.remove(habitacion);	
			this.fireContentsChanged(mapa, 0, mapa.size());
		}
	}
	public void anadirDispositivo (Habitacion habitacion,Dispositivo dispositivo) {
		List<Dispositivo>lista = mapa.get(habitacion);
		if(lista==null)lista = new ArrayList<>();
		lista.add(dispositivo);
		mapa.put(habitacion, lista);
		agregarComando(dispositivo);
//		Reconocedor.actualizaReconocedor();
		soporte.firePropertyChange("dispositivos", false, true);
		
	}
	public void eleminarDispositivo (Habitacion habitacion,Dispositivo dispositivo) {
		System.out.println(dispositivo.getNombre());
		List<Dispositivo>lista = mapa.get(habitacion);
		lista.remove(dispositivo);
		mapa.replace(habitacion, mapa.get(habitacion), lista);
		if(!dispositivo.getTipo().equals("Programable tiempo") &&!dispositivo.getTipo().equals("No programable") ) {
			eliminarComando(dispositivo,"public <"+dispositivo.getNombre()+"> = <accion> [<variables>] "+dispositivo.getNombre()+";");
		}
		else {
			eliminarComando(dispositivo,"public <"+dispositivo.getNombre()+"> = <accion> [<"+dispositivo.getNombre()+"Variables>] "+dispositivo.getNombre()+";");
			String lineToRemove=leerLineaVariables(dispositivo);
			System.out.println("ESTO HA BORRADO: "+lineToRemove);
			eliminarComando(dispositivo, lineToRemove);
		}
		if(dispositivo instanceof DispositivoTmp) eliminarComando(dispositivo, "public <"+dispositivo.getNombre()+"Tiempo> = <tiempo> "+dispositivo.getNombre()+";");
		soporte.firePropertyChange("dispositivos", true, false);
		
	}
	public String leerLineaVariables(Dispositivo d) {
		String fileName="Comandos.txt";
		String linea=null;
		File f = new File(fileName);
		try (FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr)){
			while((linea=br.readLine())!=null) {
				if(linea.contains(d.getNombre()+"Variables")) return linea;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return linea;
	}
	public void eliminarDispositivosHabitacion(Habitacion habitacion) {
		List<Dispositivo> l = mapa.get(habitacion);
		int size = l.size();
	for(int i=0;i<size;i++) {
			l= mapa.get(habitacion);
			eleminarDispositivo(habitacion,l.get(0));
			System.out.println("dispositivo eliminado");
			mapa.put(habitacion, l);
		}
	}
	public void eliminarComando(Dispositivo dispositivo, String lineToRemove) {
		String fileName="Comandos.txt";
		String tmp ="tmp.txt";
		try (Stream<String> stream = Files.lines(Paths.get(fileName));
				FileWriter fr = new FileWriter(tmp)) {
			stream.filter(line->!line.trim().equals(lineToRemove)).forEach(linea->{
				try {
					fr.write(linea+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		reemplazar(fileName,tmp);
//		Reconocedor.actualizaReconocedor();
	}
	public void reemplazar(String fileName, String tmp) {
		String s;
		try(FileReader fr = new FileReader(tmp);
				FileWriter fw = new FileWriter(fileName,false);
				BufferedReader br = new BufferedReader(fr)) {
			
			while((s=br.readLine())!=null) {
				fw.write(s+"\n");
				fw.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public void agregarComando(Dispositivo dispositivo) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
			if(!dispositivo.getTipo().equals("Programable tiempo") && !dispositivo.getTipo().equals("No programable")) 
				fr.write("\n"+"public <"+dispositivo.getNombre()+"> = <accion> [<variables>] "+dispositivo.getNombre()+";");
			else {
				List<Variable> v = dispositivo.getVariables();
				if(!v.isEmpty()) if(!v.get(0).getVar().equals(" ")) agregarComandoVar(dispositivo);
			}
			if(dispositivo instanceof DispositivoTmp) fr.write("\n"+"public <"+dispositivo.getNombre()+"Tiempo> = <tiempo> "+dispositivo.getNombre()+";");
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void agregarComandoVar(Dispositivo dispositivo) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
			fr.write("\n"+"public <"+dispositivo.getNombre()+"Variables> = (");
			for(Variable v: dispositivo.getVariables()) {
				fr.write(v.getVar()+" | ");
			}
			fr.write(");");
			fr.write("\n"+"public <"+dispositivo.getNombre()+"> = <accion> [<"+dispositivo.getNombre()+"Variables>] "+dispositivo.getNombre()+";");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	Reconocedor.actualizaReconocedor();
	}
	
	public void escribirHabitacion(Habitacion habitacion,String casa) {
		Set<Entry<Habitacion,List<Dispositivo>>> datos = mapa.entrySet();
		datos.stream().forEach(set->{
			if(set.getKey().equals(habitacion))escribirFichero(set,casa);
		});
	}
	public void escribirFichero(Entry<Habitacion,List<Dispositivo>> habitacion, String casa) {
		try (ObjectOutputStream out = new ObjectOutputStream(
			new FileOutputStream("files/"+casa+"/"+"habitaciones/"+habitacion.getKey().getNombre()+".dat"))) {
			out.writeObject(habitacion.getKey());
			out.writeObject(habitacion.getValue());
			this.fireContentsChanged(mapa, 0, mapa.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void leerFichero(String filename)
	{
		try (ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(filename))) {
				Habitacion key= (Habitacion) in.readObject();
				@SuppressWarnings("unchecked")
				List<Dispositivo> value=(List<Dispositivo>) in.readObject();
				if(mapa.containsKey(key))mapa.remove(key);
				mapa.put(key, value);
				for(Dispositivo d:value) {
					agregarComando(d);
					if(d.getTipo().equals("Programable tiempo")||d.getTipo().equals("No Programable")) agregarComandoVar(d);
				}
				//Reconocedor.actualizaReconocedor();
				
				this.fireContentsChanged(mapa, 0, mapa.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	public boolean isEmpty() {
		if(mapa.isEmpty())return true;
		return false;
	}
	public Dispositivo[] getDispositivosData(Habitacion habitacion) {
		List<Dispositivo>lista = mapa.get(habitacion);
		return lista.toArray(new Dispositivo[0]);
	}
	
	public Map<Habitacion, List<Dispositivo>> getMapa() {
		return mapa;
	}
	@Override
	public int getSize() {
		return mapa.size();
	}
	@Override
	public Habitacion getElementAt(int index) {
		Set<Habitacion> s = mapa.keySet();
		Habitacion[] h=s.toArray(new Habitacion[0]);
		return h[index];
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}

}
