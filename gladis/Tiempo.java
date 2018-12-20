package gladis;

import java.io.Serializable;

public class Tiempo  implements Serializable{

	int minutos;
	int segundos;
	
	public Tiempo() {
		minutos = 0;
		segundos = 0;
	}
	public void incrementar() {
		if (minutos<59) {
			minutos++;
		}
	}
	public void decrementar() {
		if (segundos== 0) {
		  if (minutos> 0) {
				segundos = 60;
				--minutos;
		  }else {
			  return;
		  }
		}
		--segundos;
	}
	public boolean isACero() {
		return (minutos == 0) &&  (segundos == 0);
	}
	@Override
	public String toString() {	
		return minutos+":"+segundos;
	}
	public int getMinutos() {
		return minutos;
	}
	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}
	public int getSegundos() {
		return segundos;
	}
	public void setSegundos(int segundos) {
		while(segundos>=60) {
			minutos+=1;
			segundos-=60;
		}
		this.segundos = segundos;
	}
}
