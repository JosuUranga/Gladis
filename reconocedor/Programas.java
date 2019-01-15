package reconocedor;
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

import gladis.*;
public class Programas extends ResultAdapter {
	static Recognizer oreja;
	String Programa;
	String comando="";
	List<Dispositivo> lista;
	Map<Habitacion,List<Dispositivo>> mapa;

	public Programas(Map<Habitacion,List<Dispositivo>> mapa) {
		this.mapa=mapa;
		lista = new ArrayList<>();
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
				System.out.println(Programa + " ");
			}
			for(String a : Frase) {
				comando+=a;
				comando+=" ";
			}
			comando= comando.substring(0, comando.length()-2);

			System.out.println();
			System.out.println("Este es el comando :"+comando+":");
			if(comando.length()>128) comando=".";
			if(Programa.equals("gladis")) {
				comando="";
			}
			else {
				Set<Entry<Habitacion,List<Dispositivo>>> set = mapa.entrySet();
				
				for(Entry<Habitacion,List<Dispositivo>> entry:set) {
					lista=entry.getValue();
					if(!entry.getKey().isActivo()&& !entry.getKey().isNoMolestar()) {
						for(Dispositivo a: lista) {
							if(comando.equals("encender "+ a.getNombre())) {
								a.setEstado(true);
								comando="";
							}
							else if(comando.equals("apagar "+a.getNombre())) {
								a.setEstado(false);
								comando="";
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
									}
									else if(comando.equals("bajar "+v.getVar()+" "+a.getNombre())) {
										System.out.println("Bajando "+v.getVar()+" "+a.getNombre());
										v.setVal(v.getVal()-1);
										System.out.println(v.getVal());
										comando="";
									}
									else if(comando.contains("cambiar "+v.getVar()+" "+a.getNombre())) {
										String []split=comando.split(" ");
										try {
											int valor=Integer.parseInt(split[3]);
											v.setVal(valor);
											System.out.println("Cambiando "+v.getVar()+" "+a.getNombre());
											System.out.println(v.getVal());

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
			System.out.println(a.getVar());
			if(comando.contains(a.getVar())) return true;
		}
		return false;
	}
	
	public String getPrograma(){
		return Programa;
	}
}
