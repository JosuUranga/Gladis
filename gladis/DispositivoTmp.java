package gladis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Timer;

import dialogs.DialogoModificar;

public class DispositivoTmp extends Dispositivo {
	Tiempo tiempo;
	Timer timer=null;
	PropertyChangeSupport soporte;
	int ratio;
	
	public DispositivoTmp(String nombre, String imagen, String ip, String tipo, int ratio) {
		super(nombre, imagen, ip, tipo);
		tiempo = new Tiempo();
		timer = new Timer(1000,new MiTimer());
		soporte = new PropertyChangeSupport (this);
		this.ratio=ratio;
	}
	public Tiempo getTiempo() {
		return tiempo;
	}

	public void start() {
		if (timer.isRunning()) return;
		estado = true;
		timer.start();
		aumentarUso();
		soporte.firePropertyChange("dispositivoTmp", null, this);
	}
	public void stop() {
		if(timer.isRunning()) timer.stop();
	}
	public void addPropertyChangeListener (PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener (PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	public class MiTimer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			tiempo.decrementar();
			if (tiempo.isACero()) {
				timer.stop();
				estado = false;
			}
			DispositivoTmp.this.soporte.firePropertyChange("dispositivoTmp", null, DispositivoTmp.this);
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
			this.tiempo=dialogo.getTiempo();
		 }
			aumentarUso();
	}

}
