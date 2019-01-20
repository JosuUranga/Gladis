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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import dialogs.DialogoDispositivos;
import exceptions.NombreRepetidoException;
import gladis.*;

import reconocedor.Reconocedor;

@SuppressWarnings("serial")
public class Habitaciones extends AbstractListModel<Habitacion> {
	Reconocedor Reconocedor; //el reconocedor de voz
	

	Map<Habitacion,List<Dispositivo>>mapa;
	PropertyChangeSupport soporte;
	String casa;
	List<String>oldValue;
	public Habitaciones(String casa,PropertyChangeListener principal) {
		mapa = new HashMap<>();
		this.casa=casa;
		soporte=new PropertyChangeSupport(this);
		Reconocedor = new Reconocedor(mapa, principal); //se inicializa el reconocedor de voz
	}
	public void inicializar(String casa) {
		mapa.clear();
		this.fireContentsChanged(mapa, 0, mapa.size());
		File file= new File("files/"+casa+"/habitaciones/");
		File [] habitaciones=file.listFiles();
		for(int i=0;i<habitaciones.length;i++) {
			leerFichero("files/"+casa+"/"+"habitaciones/"+habitaciones[i].getName());
		}

		Reconocedor.setMapa(mapa); //esta funcion se hace al leer del ftp y hay que actualizar el mapa del reconocedor
	}
	public void ordenarListas() { 
		Set<Habitacion>mapakeys=this.mapa.keySet(); 
		for(Habitacion habitacion: mapakeys) { 
			List<Dispositivo>lista=mapa.get(habitacion); 
			Collections.sort(lista); 
		}
	}
	public void descargarHabitacion(Path p,Agrupaciones controladorAgrupaciones) {
		List<Habitacion>asd=new ArrayList<>();
		mapa.keySet().stream().forEach(keys->{
			if(keys.toString().equals(p.getFileName().toString().replaceAll(".dat", "")))asd.add(keys);
		});
		asd.forEach(key->{
			List<Dispositivo>disp=new ArrayList<>();
			mapa.get(key).forEach(dispo->{
				disp.add(dispo);
			});
			controladorAgrupaciones.eleminarDispositivoTodas(disp);
			eliminarDispositivosHabitacion(key); //se eliminan los dispositivos de la habitacion primero, antes de borrarla, para eliminar sus comandos
			eliminarComandoHabitacion(key); //se elimina el comando de la habitacion(no molestar)
			mapa.remove(key);
		});
		Reconocedor.actualizaReconocedor(); //se actualiza el reconocedor con los cambios en Comandos.txt
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void anadirHabitacion(Habitacion habitacion) {		
		mapa.put(habitacion, new ArrayList<>()); 
		escribirComandoHabitacion(habitacion); 	//añade el comando necesario para la habitacion
		this.fireContentsChanged(mapa, 0, mapa.size());
		Reconocedor.actualizaReconocedor(); 
	}
	
	//Escribe el comando de no molestar de la habitacion
	private void escribirComandoHabitacion(Habitacion habitacion) { 
		File file = new File("Comandos.txt"); 
		try (FileWriter fr= new FileWriter(file, true)){ //hace un append del Comandos.txt (gracias al true)
			fr.write("\n"+"public <noMolestar"+habitacion+"> = <noMolestar> "+habitacion+";");  //escribe la linea necesaria
			fr.close(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}		 
	} 
	public void noMolestar() { 
		this.fireContentsChanged(mapa, 0, mapa.size()); 
	} 
	public void eliminarHabitacion (Habitacion habitacion) {	
		if (mapa.containsKey(habitacion)) {
			eliminarComandoHabitacion(habitacion);  //elimina el comando de la habitacion
			mapa.remove(habitacion);
			this.fireContentsChanged(mapa, 0, mapa.size());
			Reconocedor.actualizaReconocedor(); 
		}
	}
	
	//Esta funcion busca la linea del no molestar d ela habitacion en comandos y copia todas las lineas menos esa
	//en el tmp.txt para luego reemplazarlo por el Comandos.txt sin el comando de la habitacion.
	private void eliminarComandoHabitacion(Habitacion habitacion) {  
		String fileName="Comandos.txt"; 
		String tmp ="tmp.txt"; 
		String line; 
		try(FileWriter fr = new FileWriter(tmp); 
					BufferedReader br = new BufferedReader(new FileReader(fileName))){ 
			while((line=br.readLine())!=null) { 
				if(!line.contains("public <noMolestar"+habitacion)) fr.write(line+"\n"); 
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		reemplazar(fileName,tmp); 
	} 
	
	//Esta funcion añade un dispositivo a la lista de una habitacion
	public void anadirDispositivo (Habitacion habitacion,Dispositivo dispositivo) {
		List<Dispositivo>lista = mapa.get(habitacion);
		if(lista==null)lista = new ArrayList<>();
		lista.add(dispositivo);
		mapa.put(habitacion, lista);
		agregarComando(dispositivo); //agrega el comando al .txt depende el dispositivo que sea
		Reconocedor.actualizaReconocedor(); //actualiza el reconocedor con el nuevo Comandos.txt
		soporte.firePropertyChange("dispositivos", false, true); //avisa de acutalizar la pantalla
		
	}
	
	//Esta funcion elimina un dispositivo de una habitacion
	public void eleminarDispositivo (Habitacion habitacion,Dispositivo dispositivo) {
		List<Dispositivo>lista = mapa.get(habitacion);
		lista.remove(dispositivo);
		mapa.replace(habitacion, mapa.get(habitacion), lista);
		eliminarComandoDispositivoCompleto(dispositivo); //elimina el comando necesario depende el tipo de dispositivo que sea
		Reconocedor.actualizaReconocedor(); //actualiza el reconocedor
		soporte.firePropertyChange("dispositivos", true, false); //avisa al principal pa que actualice la pantalla
		
	}
	
	//determina la linea de las variables personalizadas del dispositivo OTROS para returnearla y borrarla despues
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
			e.printStackTrace();
		}
		
		return linea;
	}
	
	//esta funcion elimina TODOS los dispositivos de una habitacion, asi como sus comandos
	public void eliminarDispositivosHabitacion(Habitacion habitacion) {
		List<Dispositivo> l = mapa.get(habitacion);
		int size = l.size();
	for(int i=0;i<size;i++) {
			l= mapa.get(habitacion);
			eleminarDispositivo(habitacion,l.get(0));//funcion para eliminar un dispositivo
			System.out.println("dispositivo eliminado");
			mapa.put(habitacion, l);
		}
	}
	
	//elimina el comando necesario depende el tipo de dispositivo que sea
	public void eliminarComandoDispositivoCompleto(Dispositivo dispositivo) { 
		
			//si el comando no es OTROS, llama al eliminar comando con la linea que hay que borrar.
		if(!dispositivo.getTipo().equals("Programable tiempo") &&!dispositivo.getTipo().equals("No programable") ) { 
			eliminarComando(dispositivo,"public <"+dispositivo.getNombre()+"> = <accion> [<variables>] "+dispositivo.getNombre()+" [<numeros>];"); 
		} 
		
			//si el dispositicos es OTROS, se borran sus comandos especificos
		else { 
			eliminarComando(dispositivo,"public <"+dispositivo.getNombre()+"> = <accion> [<"+dispositivo.getNombre()+"Variables>] "+dispositivo.getNombre()+" [<numeros>];"); 
			String lineToRemove=leerLineaVariables(dispositivo);	 //determina la linea de las variables personalizadas del dispositivo OTROS para returnearla y borrarla despues
			eliminarComando(dispositivo, lineToRemove); 	//elimina la linea de las variables
		} 
		if(dispositivo instanceof DispositivoTmp) eliminarComando(dispositivo, "public <"+dispositivo.getNombre()+"Tiempo> = <tiempo> "+dispositivo.getNombre()+";"); 

	} 
	
	//esta funcion elimina del COmandos.txt una linea en especifico, el lineToRemove
	//Para ello copia todas las lineas menos la lineToRemove en el tmp.txt para luego reemplazarlo en el Comandos.txt
	//original sin la lineToRemove
	public void eliminarComando(Dispositivo dispositivo, String lineToRemove) {
		String fileName="Comandos.txt";
		String tmp ="tmp.txt";
		String line; 
		try(FileWriter fr = new FileWriter(tmp); 
					BufferedReader br = new BufferedReader(new FileReader(fileName))){ 
			while((line=br.readLine())!=null) { 
				if(!line.equals(lineToRemove)) fr.write(line+"\n"); //si la linea no es la lineToRemove la copia al tmp.txt
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		reemplazar(fileName,tmp);//reemplaza lo del tmp.txt en comandos.txt
	}
	
	//reemplaza lo del tmp.txt en comandos.txt
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
	
	
	//Esta funcion agrega el/los comando/s del dispostivo necesarios dependiendo de su tipo
	public void agregarComando(Dispositivo dispositivo) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
			
				//Si el disp no es un OTROS, 
			if(!dispositivo.getTipo().equals("Programable tiempo") && !dispositivo.getTipo().equals("No programable")) 
				fr.write("\n"+"public <"+dispositivo.getNombre()+"> = <accion> [<variables>] "+dispositivo.getNombre()+" [<numeros>];");
			else {
				
				//Si es un OTROS, 
				List<Variable> v = dispositivo.getVariables();
				if(!v.isEmpty()) if(!v.get(0).getVar().equals(" ")) agregarComandoVar(dispositivo); //agrega los comandos necesarios para un disp OTROS
			}
			
			//Independientemente del tipo, si es de Tiempo, el comando del tiempo
			if(dispositivo instanceof DispositivoTmp) fr.write("\n"+"public <"+dispositivo.getNombre()+"Tiempo> = <tiempo> "+dispositivo.getNombre()+";");
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Esta funcion escribe los comandos necesarios con las variables personalizadas de los dispositivos OTROS
	public void agregarComandoVar(Dispositivo dispositivo) {
		File file = new File("Comandos.txt");
		try (FileWriter fr= new FileWriter(file, true)){
			
				//Va escribiendo el comando de sus variables
			fr.write("\n"+"public <"+dispositivo.getNombre()+"Variables> = (");
			for(Variable v: dispositivo.getVariables()) {
				fr.write(v.getVar()+" | ");
			}
			fr.write(");");
				
				//Escribe el comando especial por tener variables personalizadas.
			fr.write("\n"+"public <"+dispositivo.getNombre()+"> = <accion> [<"+dispositivo.getNombre()+"Variables>] "+dispositivo.getNombre()+" [<numeros>];");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				mapa.put(key, value);
				oldValue=new ArrayList<>();
				oldValue.add("noFTP");
				oldValue.add("nada");
				soporte.firePropertyChange("envioHabitacion", oldValue, key.getNombre());
				oldValue=null;
				
				//Al leer fichero se borran primero los comandos que hayan de los dispositivos que esten ya en local
				//para luego cuando los vuelva a leer que no esten repetidos. Si éste no existia simplemente no
				//borrará nada
				for(Dispositivo d:value) { 		
					eliminarComandoDispositivoCompleto(d); 
					agregarComando(d);
					if(d.getTipo().equals("Programable tiempo")||d.getTipo().equals("No Programable")) agregarComandoVar(d);
				}
				//Borra tambien el de la habitacion por el mismo motivo
				eliminarComandoHabitacion(key); 
				escribirComandoHabitacion(key); 
				Reconocedor.actualizaReconocedor(); //actualiza el reconocedor
				soporte.firePropertyChange("dispositivos", false, true); //avisa a la pantalla para que se actualize
				
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
	public Reconocedor getReconocedor() { //funcion necesaria para que la clase Agrupaciones tenga el Reconocedor
		return Reconocedor;
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
