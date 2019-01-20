package reconocedor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.speech.recognition.Recognizer;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultAdapter;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultToken;

import gladis.Dispositivo;
import gladis.DispositivoTmp;
import gladis.Habitacion;
import gladis.Variable;
public class Programas extends ResultAdapter {//Es un result adapter del reconocedor
	static Recognizer oreja;	
	String Programa;	//una vez reconocido el comando completo, al result adapter se lo van diciendo palabra por palabra metiendolo aqui
	String comando="";	//el string que se compara para ver si es un comando
	PropertyChangeSupport soporte;
	List<Dispositivo> lista;
	Map<Habitacion,List<Dispositivo>> mapa;
	Map<String,List<Dispositivo>> mapaAgrupaciones;

	public Programas(Map<Habitacion,List<Dispositivo>> mapa) {
		this.mapa=mapa;
		mapaAgrupaciones=null;
		lista = new ArrayList<>();
		soporte=new PropertyChangeSupport(this);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	public void resultAccepted(ResultEvent e){ //el resulAccepted que llamara cuando el reconocedor reconozca algo
		try {
			Result res = (Result)(e.getSource()); //convierte el ResultEvent en Result
			ResultToken tokens[] = res.getBestTokens(); //el comando completo reconocido lo mete en este array de ResultTokens (encender microondas serian dos tokens)
			String Frase[]= new String[1];		
			Frase[0]="";

			for (int i=0; i < tokens.length; i++){
				Programa = tokens[i].getSpokenText(); //va metiendo en el String Programa los tokens, para luego meterlo en un Array de Strings y asi tener  en un array de Strings el comando completo
				Frase[0]+=Programa+" "; 
			}
			for(String a : Frase) { //ese array de strings lo pasamos a un solo string para los equals.
				comando+=a;
				comando+=" ";
			}
			comando= comando.substring(0, comando.length()-2); //se le quitan los espacios que tiene al final

			System.out.println("Este es el comando :"+comando+":");
			if(comando.length()>128) comando="."; //si es muy largo se borra
			if(Programa.equals("gladis")) { //si es gladis se vacia
				comando="";
			}
			else {
				Set<Entry<Habitacion,List<Dispositivo>>> set = mapa.entrySet(); //cojes el mapa de habitaciones y lo metes en un Set de entrys.
				for(Entry<Habitacion,List<Dispositivo>> entry:set) { //miras todas las habitaciones
					
					if(comando.equals("modo no molestar "+entry.getKey())) { //compara si el comando es no molestar
						lista=entry.getValue();
						lista.forEach(disp->disp.setNoMolestar(true));
						soporte.firePropertyChange("noMolestar", false, entry.getKey().toString());
						System.out.println("Cambiando modo no molestar");
						comando="";
						avisar(entry); //avisa de que tiene que mandar cambios
					}
					
					lista=entry.getValue();
					if(!entry.getKey().isActivo()&& !entry.getKey().isNoMolestar()) { //por cada habitacion, si no esta ni en modo no molestar o esta actualmente activa,
						for(Dispositivo a: lista) {
							if(comando.equals("encender "+ a.getNombre())) { //si estas encendiendo un disp.
								a.setEstado(true);
								System.out.println("Encendiendo "+a.getNombre());
								comando="";								//se vacia el comando por si quieres decir otro
								soporte.firePropertyChange("dispositivos", false, true); //avisa a la pantalla de que se actualice por si el dispositivo encendido esta en el selected del jlist actual
								avisar(entry);
							}
							else if(comando.equals("apagar "+a.getNombre())) { //comando pa apagar disp
								a.setEstado(false);
								comando="";
								soporte.firePropertyChange("dispositivos", false, true);
								avisar(entry);

							}
							else if(checkVariable(a)) { //mira si el dispositivo que toca tiene una variable que este en el comando dicho, para aligerar el programa
								for(Variable v : a.getVariables()) { //se recorren las variables para ver cual es la dicha en el comando y comprobar que se quería hacer algo con ella, es decir si se quiere modificar
									if(comando.equals("subir "+v.getVar()+" "+a.getNombre())) { //si el comando es de subir un punto a esa variable
										System.out.println("Subiendo "+v.getVar()+" "+a.getNombre());
										System.out.println(v.getVal());
										v.setVal(v.getVal()+1);
										comando="";
										avisar(entry);

									}
									else if(comando.equals("bajar "+v.getVar()+" "+a.getNombre())) { //si el comando es pa bajar un punto
										System.out.println("Bajando "+v.getVar()+" "+a.getNombre());
										v.setVal(v.getVal()-1);
										System.out.println(v.getVal());
										comando="";
										avisar(entry);

									}
									else if(comando.contains("cambiar "+v.getVar()+" "+a.getNombre())) {//si el comando es pa cambiar esa variable
										String []split=comando.split(" ");//se hace un split
										try {
											int valor=Integer.parseInt(split[3]); //si el comando esta bien dicho, el 4to elemento siempre sera el integer al cual cambiar
											v.setVal(valor);					  //si el 4to elemento no era un integer saltaria al catch y esto no se ejecutaria, si se ejecuta cambia el valor
											System.out.println("Cambiando "+v.getVar()+" "+a.getNombre());
											System.out.println(v.getVal());
											avisar(entry);


										}catch(NumberFormatException aposjd) {
											System.out.println("El 4to valor no es un integer"); 
										}finally {
											comando="";// siempre queremos que se borre el comando, sea o no correcto (por el contains del else if)
										}
											
									}
								}
							}
							//si el disp es Tmp mira  aver si el comando era aumentar el tiempo de éste
							else if(comando.equals("aumentar Tiempo "+a.getNombre()) && a instanceof DispositivoTmp){
								((DispositivoTmp) a).aumentarTiempo();
								System.out.println(((DispositivoTmp) a).getTiempo());
								avisar(entry);

		
							}
						}
					}
				}
				//termina de mirar el mapa de habitaciones y empieza con el de agrupaciones
				Set<Entry<String,List<Dispositivo>>> setAgrup = mapaAgrupaciones.entrySet(); //un set de entrys del mapa agrupaciones
				for(Entry<String,List<Dispositivo>> entryAgrup : setAgrup) {
					if(comando.equals("modo "+entryAgrup.getKey().toString())) {// comapara si el comando es encender una agrupacion
						System.out.println("Encendiendo agrupacion "+entryAgrup.getKey());
						soporte.firePropertyChange("encenderAgrupacionVoz", false, entryAgrup.getKey()); //avisa al principal pa que la encienda ya que este no tiene la clase Agrupaciones
					}
				}
				getPrograma();
				oreja.suspend();		
				oreja.resume();
			}
		}catch(Exception ex) {
		}
	}
	public boolean checkVariable(Dispositivo d) {//mira si el dispositivo que toca tiene una variable que este en el comando dicho, para aligerar el programa
		for(Variable a : d.getVariables()) {
			if(comando.contains(a.getVar())) return true;
		}
		return false;
	}
	
	public String getPrograma(){
		return Programa;
	}
	public void setMapa(Map<Habitacion,List<Dispositivo>> mapa) {
		this.mapa=mapa;
	}
	public void setMapaAgrupaciones(Map<String,List<Dispositivo>> mapaAgrup) {
		this.mapaAgrupaciones=mapaAgrup;
	}
	public void avisar(Entry<Habitacion,List<Dispositivo>> entry) {
		List<String> lista = new ArrayList<>();
		lista.add("enviar");
		lista.add("nada");
		soporte.firePropertyChange("envioHabitacion", lista, entry.getKey().toString()); //avisa de que tiene que enviar la habitacion
		lista=null;
	}
}

