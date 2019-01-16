package gladis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import dialogs.DialogoModificar;

@SuppressWarnings("serial")
public class DispositivoTmp extends Dispositivo {
	Tiempo tiempo;
	Timer timer;
	int ratio;
	
	public DispositivoTmp(String nombre, String imagen, String ip, String tipo, int ratio) {
		super(nombre, imagen, ip, tipo);
		tiempo = new Tiempo();
		timer = new Timer(1000,new MiTimer());
		this.ratio=ratio;
	}
	@Override
	public void setEstado(boolean estado) {
		// TODO Auto-generated method stub
		super.setEstado(estado);
		if(this.estado)this.start();
		else this.stop();
	}
	public Tiempo getTiempo() {
		return tiempo;
	}

	public void start() {
		if (timer.isRunning()) return;
		estado = true;
		timer.start();
		aumentarUso();
	}
	public void stop() {
		if(timer.isRunning()) timer.stop();
	}
	public class MiTimer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			tiempo.decrementar();
			if (tiempo.isACero()) {
				timer.stop();
				estado = false;
			}
		}
	}
	public void aumentarTiempo() {
		tiempo.setMinutos(tiempo.getMinutos()+ratio);
		aumentarUso();
	}
	
	
	@Override
	public void modificar(Principal d) {
		dialogo = new DialogoModificar(d, this);
		 if(dialogo.getVariables()!=null) {
			this.variables=dialogo.getVariables();	
			this.estado=dialogo.isEstado();
			if(dialogo.isEstado()==true) this.start();
			else this.stop();
			this.tiempo=dialogo.getTiempo();
		 }
		dialogo=null;
		aumentarUso();
	}

}
