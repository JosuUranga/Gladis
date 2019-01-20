package reconocedor;
import javax.speech.*;
import javax.speech.recognition.*;

import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gladis.*;
public class Reconocedor {

	static Recognizer oreja; //el reconocedor de java speech
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
			FileReader grammar1 =new FileReader("Comandos.txt"); //ruta donde esta el archivo con los comandos
			RuleGrammar rg = oreja.loadJSGF(grammar1);//Establece la forma en que debe de estar estructurado el archive grammar 
			rg.setEnabled(true); //acceso al archivo
			oreja.addResultListener(programas);  //Se hace referencia a la clase de escucha del reconocedor
			oreja.commitChanges();	//aplica los cambios al reconocedor
			oreja.requestFocus();	
			oreja.resume();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}	
	return;
	}
	public void actualizaReconocedor() { //vuelve a cargar el comandos.txt con los comandos nuevos
		try {
			oreja.loadJSGF(new FileReader("Comandos.txt"));
			oreja.commitChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setMapa(Map<Habitacion,List<Dispositivo>> mapaa) { //cuando descarga del ftp como se hace un clear le da el mapa otra vez
		this.mapa=mapaa;
	}
	public void setMapaAgrup(Map<String,List<Dispositivo>> mapa) { //se le mete el mapa de las agrupaiones a programas
		programas.setMapaAgrupaciones(mapa);
	}
}
