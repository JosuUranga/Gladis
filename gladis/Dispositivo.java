package gladis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dialogs.DialogoModificar;

@SuppressWarnings("serial")
public class Dispositivo implements Serializable, Cloneable,Comparable<Dispositivo>{
	String nombre;
	String imagen;
	String ip;
	String tipo;
	boolean noMolestar; 
	boolean estado;
	boolean favorito;
	int usos;
	List<Variable> variables;
	DialogoModificar dialogo;
	
	public Dispositivo(String nombre, String imagen, String ip, String tipo) {
		this.nombre=nombre;
		this.imagen=imagen;
		this.tipo=tipo;
		this.ip=ip;
		this.noMolestar=false; 
		this.variables= new ArrayList<>();
		this.estado=false;
		this.favorito=false;
		this.usos=0;
	}
	public void aumentarUso() {
		usos++;
		System.out.println("Usos del idspositivo "+this.getNombre()+": "+usos);
	}
	public DialogoModificar getDialogo() {
		return dialogo;
	}
	public void setDialogo(DialogoModificar dialogo) {
		this.dialogo = dialogo;
	}
	public boolean isNoMolestar() { 
		return noMolestar; 
	} 
	public void setNoMolestar(boolean noMolestar) { 
		this.noMolestar = noMolestar; 
	} 
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public List<Variable> getVariablesCopia() {
		List<Variable> copia = new ArrayList<>();
		copia.addAll(variables);
		return copia;
	}
	
	
	public List<Variable> getVariables() {
		return variables;
	}
	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	public void addVariable(Variable v) {
		variables.add(v);
	}
	
	public String getTipo() {
		return tipo;
	}
	public String getImagen() {
		return imagen;
	}
	public void cambiarEstado() {
		estado = !estado;
		aumentarUso();
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		if(this.estado!=estado) {
			this.estado = estado;
			System.out.println((estado)?"Encendiendo "+this.nombre:"Apagando "+this.nombre);
		}
		else {
			System.out.println((estado)?"El "+this.nombre+" ya esta encendido":"El "+this.nombre+" ya esta apagado");
		}
		aumentarUso();
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isFavorito() {
		return favorito;
	}
	
	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}
	
	public int getUsos() {
		return usos;
	}
	
	public void setUsos(int usos) {
		this.usos = usos;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void modificar(Principal d) {
		dialogo = new DialogoModificar(d, this);
		 if(dialogo.getVariables()!=null) {
			this.variables=dialogo.getVariables();	
			this.estado=dialogo.isEstado();
		 }
		 this.dialogo=null;
		aumentarUso();
	}
	@Override
	public String toString() {
		return nombre;
	}
	public Dispositivo clone(){
        try {
			return (Dispositivo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
    }
	@Override 
	public int compareTo(Dispositivo b) { 
		int ret=0; 
		Dispositivo a=this; 
		if (a.isFavorito()&&!b.isFavorito())ret=-1; 
		else if (a.isFavorito()&&!b.isFavorito())ret=-1; 
		else if (!a.isFavorito()&&b.isFavorito())ret=1; 
		else if (a.isFavorito()&&b.isFavorito()) { 
			if(a.getUsos()>b.getUsos())ret=-1; 
			else if(a.getUsos()<b.getUsos())ret=1; 
			else ret=0; 
		} 
		else if (!a.isFavorito()&&!b.isFavorito()) { 
			if(a.getUsos()>b.getUsos())ret=-1; 
			else if(a.getUsos()<b.getUsos())ret=1; 
			else ret=0; 
		} 
		else ret=0; 
		 
		return ret ; 
	} 
}
