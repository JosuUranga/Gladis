package sockets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

public class controladorVersion extends Thread{
	String hostname,name,password,casa;
	Long versionProg,versionTMP;
	Boolean inicializar;
	PropertyChangeSupport soporte;
	PropertyChangeListener principal;
	public controladorVersion(String hostname,String casa,String name,String password,PropertyChangeListener principal) {
		this.hostname=hostname;
		this.casa=casa;
		this.name=name;
		this.password=password;
		versionProg=0L;
		inicializar=true;
		this.subirVersion();
		soporte=new PropertyChangeSupport(this);
		this.principal=principal;
		this.addPropertyChangeListener(principal);
		
		
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		soporte.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		soporte.removePropertyChangeListener(listener);
	}
	public void run() {
		FTPSClient ftpClient=new FTPSClient(false);
		try {
			while(true) {
				ftpClient.connect(hostname);
				ftpClient.execPBSZ(0);
				ftpClient.execPROT("P");
				ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();
				ftpClient.login(name, password);
				ftpClient.changeWorkingDirectory("/"+casa);
				FTPFile[] files=ftpClient.listFiles();
				if(files!=null&&files.length>0) {
					for(FTPFile fileV:files) {
						if(fileV.getName().contains("version")) {
							try(DataInputStream version=new DataInputStream(ftpClient.retrieveFileStream(fileV.getName()))){ //Lee la version.txt del servidor y lo guarda en versionTMP
								versionTMP=version.readLong();
								version.close();
								ftpClient.completePendingCommand();
							if(versionProg<versionTMP && !inicializar) {  //Si la version del servidor es mayor y no estamos inicializandonos vamos a recibir todos los ficheros del ftp
								versionProg=versionTMP;
								File file=new File("files/"+casa+"/");
								borrarTodoLocal(file.listFiles());   //Borrar todos los .dats y version.txt locales
								recibirFTP(ftpClient,files);		//Recibir todos los .dats y version.txt del servidor
								soporte.firePropertyChange("inicializar", false, true);   //Volver a reinicializar los mapas con los archivos recibidos y esto se encargara de mandarlos a las pantallas
								soporte.firePropertyChange("dispositivos", false, true); //Actualizar lista de disp
								soporte.firePropertyChange("agrupaciones", true, false);	//Actualizar lista de disp
							}
							System.out.println(versionProg);
							inicializar=false;
							}
						}				
					}
					ftpClient.logout();
					ftpClient.disconnect();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}	
		} catch (IOException e) {
			System.err.println("No se ha podido conectar con el servidor FTP");
			e.printStackTrace();
			this.run(); //Si hay algun fallo se volvera a reiniciar controladorVersion ya que este hilo siempre tiene que estar funcionando y probablemente el fallo no sera mas que un error de concurrencia y no afectara a la funcionalidad del programa
			
		}
	}
	public void recibirFTP(FTPSClient ftpClient,FTPFile[] files) {					//Funciones recursivas para borrar escribir etc....
		try {
			for(FTPFile file:files) {
				if(!file.isFile()) {
					ftpClient.changeWorkingDirectory(file.getName());
					recibirFTP(ftpClient,ftpClient.listFiles());
					ftpClient.changeToParentDirectory();
				}else {
					guardarArchivo(ftpClient,file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void borrarTodoLocal(File[]files) {
		for(File file:files) {
			if(!file.isFile()) {
				File tmp=new File(file.getPath());
				borrarTodoLocal(tmp.listFiles());
			}else {
				List<String>lista=new ArrayList<>();
				lista.add("borrar");
				lista.add("nada");
				if(file.toString().contains("agrupaciones")) {
					soporte.firePropertyChange("envioAgrupaciones", lista, file.getName());
				}else {
					soporte.firePropertyChange("envioHabitaciones", lista, file.getName());
				}
				
				lista=null;
				file.delete();
			}
		}
	}
	public void guardarArchivo(FTPSClient ftpClient,FTPFile file) {
		try(FileOutputStream OutStream=new FileOutputStream("files"+ftpClient.printWorkingDirectory()+"/"+file.getName())) {
			ftpClient.retrieveFile(file.getName(), OutStream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void subirVersion() {   //Cada vez que hagamos un cambio se subira la version +1 y se iniciara un envioFTP solo en la inicializacion.
		versionProg=versionProg+1;
		try(DataOutputStream Sversion=new DataOutputStream(new FileOutputStream("files/"+casa+"/version.txt"))){
			Sversion.writeLong(versionProg);
			Sversion.close();
			if(inicializar)new envioFTP(hostname,casa,name,password).start();
		}catch(IOException e) {
			System.out.println("No se ha podido abrir version.txt");
		}
	}
}
