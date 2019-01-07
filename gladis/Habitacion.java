package gladis;

import java.io.Serializable;

public class Habitacion implements Serializable{
	String nombre;
	boolean activo;
	boolean noMolestar;
	public Habitacion(String nombre) {
		this.nombre= nombre;
		activo = false;
	}
	public String getNombre() {
		return nombre;
	}
	public boolean isNoMolestar() {
		return noMolestar;
	}
	public void setNoMolestar(boolean noMolestar) {
		this.noMolestar = noMolestar;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	@Override
	public String toString() {
		
		return nombre;
	}

	
}
