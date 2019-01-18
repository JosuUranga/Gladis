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
public class Programas extends ResultAdapter {
	static Recognizer oreja;
	String Programa;
	String comando="";
	PropertyChangeSupport soporte;
	List<Dispositivo> lista;
	Map<Habitacion,List<Dispositivo>> mapa;

	public Programas(Map<Habitacion,List<Dispositivo>> mapa) {
		this.mapa=mapa;
		lista = new ArrayList<>();
		soporte=new PropertyChangeSupport(this);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	public void resultAccepted(ResultEvent e){
		try {
			Result res = (Result)(e.getSource());
			ResultToken tokens[] = res.getBestTokens();
			String Frase[]= new String[1];		
			Frase[0]="";

			for (int i=0; i < tokens.length; i++){
				Programa = tokens[i].getSpokenText();
				Frase[0]+=Programa+" "; 
			}
			for(String a : Frase) {
				comando+=a;
				comando+=" ";
			}
			comando= comando.substring(0, comando.length()-2);

			System.out.println("Este es el comando :"+comando+":");
			if(comando.length()>128) comando=".";
			if(Programa.equals("gladis")) {
				comando="";
			}
			else {
				System.out.println("LLEGA");

				Set<Entry<Habitacion,List<Dispositivo>>> set = mapa.entrySet();
				System.out.println(mapa);
				for(Entry<Habitacion,List<Dispositivo>> entry:set) {
					
					if(comando.equals("modo no molestar "+entry.getKey())) {
						lista=entry.getValue();
						lista.forEach(disp->disp.setNoMolestar(true));
						soporte.firePropertyChange("noMolestar", false, entry.getKey().toString());
						System.out.println("Cambiando modo no molestar");
						System.out.println(soporte.hasListeners(""));
						comando="";
						avisar(entry);
					}
					
					lista=entry.getValue();
					System.out.println(entry.getKey().isActivo()+" IS ACTIVO");
					if(!entry.getKey().isActivo()&& !entry.getKey().isNoMolestar()) {
						for(Dispositivo a: lista) {
							System.out.println(a.getNombre()+"ANJKASDBUABIUBAF");
							if(comando.equals("encender "+ a.getNombre())) {
								a.setEstado(true);
								System.out.println("Encendiendo luces");
								comando="";
								soporte.firePropertyChange("dispositivos", false, true);
								avisar(entry);
							}
							else if(comando.equals("apagar "+a.getNombre())) {
								a.setEstado(false);
								comando="";
								soporte.firePropertyChange("dispositivos", false, true);
								avisar(entry);

							}
							else if(checkVariable(a)) {
								for(Variable v : a.getVariables()) {
									System.out.println("El nombre de la variable :"+v.getVar()+":");
									System.out.println(comando);
									if(comando.equals("subir "+v.getVar()+" "+a.getNombre())) {
										System.out.println("Subiendo "+v.getVar()+" "+a.getNombre());
										v.setVal(v.getVal()+1);
										System.out.println(v.getVal());
										comando="";
										avisar(entry);

									}
									else if(comando.equals("bajar "+v.getVar()+" "+a.getNombre())) {
										System.out.println("Bajando "+v.getVar()+" "+a.getNombre());
										v.setVal(v.getVal()-1);
										System.out.println(v.getVal());
										comando="";
										avisar(entry);

									}
									else if(comando.contains("cambiar "+v.getVar()+" "+a.getNombre())) {
										String []split=comando.split(" ");
										try {
											int valor=Integer.parseInt(split[3]);
											v.setVal(valor);
											System.out.println("Cambiando "+v.getVar()+" "+a.getNombre());
											System.out.println(v.getVal());
											avisar(entry);


										}catch(NumberFormatException aposjd) {
											System.out.println("EXCEPTIOOON");
										}finally {
											comando="";
										}
											
									}
								}
							}
							else if(comando.equals("aumentar Tiempo "+a.getNombre()) && a instanceof DispositivoTmp){
								((DispositivoTmp) a).aumentarTiempo();
								System.out.println(((DispositivoTmp) a).getTiempo());
								avisar(entry);

		
							}
						}
					}
				}
				getPrograma();
				oreja.suspend();		
				oreja.resume();
			}
		}catch(Exception ex) {
		}
	}
	public boolean checkVariable(Dispositivo d) {
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
	public void avisar(Entry<Habitacion,List<Dispositivo>> entry) {
		List<String> lista = new ArrayList<>();
		lista.add("enviar");
		lista.add("nada");
		soporte.firePropertyChange("envioHabitacion", lista, entry.getKey().toString());
		lista=null;
	}
}

