package models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractListModel;

import gladis.Dispositivo;

public class Agrupaciones extends AbstractListModel<String> {
	Map<String,List<Dispositivo>>mapa;
	PropertyChangeSupport soporte;
	public Agrupaciones() {
		mapa = new HashMap<>();
		soporte=new PropertyChangeSupport(this);
	}
	public void anadirString(String nombre) {		
		mapa.put(nombre, new ArrayList<>());		
		this.fireContentsChanged(mapa, 0, mapa.size());
	}
	public void eliminarString (String nombre) {	
		if (mapa.containsKey(nombre)) {
			mapa.remove(nombre);	
			this.fireContentsChanged(mapa, 0, mapa.size());
		}
		
	}
	public String[] getAgrupacionesKeys() {
		return mapa.keySet().toArray(new String[0]);
	}
	public void anadirDispositivos (String nombre,List<Dispositivo> dispositivo) {
		List<Dispositivo>lista = mapa.get(nombre);
		if(lista==null)lista = new ArrayList<>();
		lista.addAll(dispositivo);
		mapa.put(nombre, lista);
		this.fireContentsChanged(mapa, 0, mapa.size());
		soporte.firePropertyChange("agrupacion", true, false);
	}
	public Map<String, List<Dispositivo>> getMapa() {
		return mapa;
	}
	public void eleminarDispositivo (String nombre,Dispositivo dispositivo) {
		List<Dispositivo>lista = mapa.get(nombre);
		lista.remove(dispositivo);
		mapa.replace(nombre, mapa.get(nombre), lista);
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
