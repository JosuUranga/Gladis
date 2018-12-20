package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import gladis.Dispositivo;
import gladis.Habitacion;

public class ListaDispositivos extends AbstractListModel<Dispositivo> {
	
	List<Dispositivo> lista;
	public ListaDispositivos() {
		lista = new ArrayList<>();
		
	}
	
	public List<Dispositivo> getLista() {
		return lista;
	}

	public void inicializar(Map<Habitacion, List<Dispositivo>> mapa) {
		this.add(mapa.get(mapa.keySet().toArray(new Habitacion[0])[0]));
	}

	public void add(Dispositivo a) {
		lista.add(a);
		this.fireContentsChanged(lista, 0, lista.size()-1);
	}
	public void add(List<Dispositivo> alumnos) {
		lista.addAll(alumnos);
		this.fireContentsChanged(lista, 0, lista.size()-1);
	}
	public void remove (int index) {
		lista.remove(index);
		this.fireContentsChanged(lista, 0, lista.size()-1);
	}
	public void remove (int indices []) {
		List<Dispositivo> alumnosSeleccionados = new ArrayList<>();
		for (int indice: indices) {
			alumnosSeleccionados.add(lista.get(indice));
		}
		lista.removeAll(alumnosSeleccionados);
		this.fireContentsChanged(lista, 0, lista.size()-1);
	}
	public boolean contains(Dispositivo a) {
		if (lista.contains(a))return true;
		else return false;	
	}

	@Override
	public int getSize() {
		
		return lista.size();
	}

	@Override
	public Dispositivo getElementAt(int index) {
		
		return lista.get(index);
	}

	public void clear() {
		lista.clear();
		this.fireContentsChanged(lista, 0, lista.size()-1);
		
	}

}
