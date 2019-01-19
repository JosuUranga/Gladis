package reconocedor;
import javax.speech.*;
import javax.speech.recognition.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gladis.*;
public class Reconocedor {

	static Recognizer oreja;
	String palabra;
	Programas programas;
	Map<Habitacion,List<Dispositivo>> mapa;
	List<Dispositivo> lista;
	public Reconocedor(	Map<Habitacion,List<Dispositivo>> mapa, PropertyChangeListener p) {
		this.mapa=mapa;
		lista= new ArrayList<>();
		programas =new Programas(mapa);
		programas.addPropertyChangeListener(p);
		iniciarRec();

	}

	

	public void iniciarRec() {
  		
			try{
			//Se configura al reconocedor para que entienda el idioma
			oreja = Central.createRecognizer(new EngineModeDesc(Locale.ROOT));
			oreja.allocate();
			FileReader grammar1 =new FileReader("Comandos.txt"); //ruta donde esta el archivo con las Frases
			RuleGrammar rg = oreja.loadJSGF(grammar1);//Establece la forma en que debe de estar estructurado el archive grammar 
			rg.setEnabled(true); //acceso al archivo
			oreja.addResultListener(programas);  //Se hace referencia a la clase de escucha del reconocedor
			oreja.commitChanges();
			oreja.requestFocus();
			oreja.resume();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}	
	return;
	}
	public void actualizaReconocedor() {
		try {
			System.out.println("AAAAAAAAAAA");
			oreja.loadJSGF(new FileReader("Comandos.txt"));
			oreja.commitChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setMapa(Map<Habitacion,List<Dispositivo>> mapaa) {
		System.out.println(mapaa+"HOAL SOY EL MAPA RECONOCEDOR");
		this.mapa=mapaa;
	}
	public void setMapaAgrup(Map<String,List<Dispositivo>> mapa) {
		programas.setMapaAgrupaciones(mapa);
	}
}
