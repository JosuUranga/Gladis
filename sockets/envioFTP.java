package sockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

public class envioFTP extends Thread{
	String hostname,name,password,casa;
	
	public envioFTP(String hostname,String casa,String name,String password) {
		this.hostname=hostname;
		this.name=name;
		this.casa=casa;
		this.password=password;
		
	}
	public void run() {
		FTPSClient ftpClient=new FTPSClient();
		try {
			ftpClient.connect(hostname);
			ftpClient.execPBSZ(0);
			ftpClient.execPROT("P");
			ftpClient.enterLocalPassiveMode();
			ftpClient.login(name, password);
			ftpClient.changeWorkingDirectory("/"+casa);
			FTPFile[]fils=ftpClient.listFiles();
			borrarTodoFTP(ftpClient,fils);
			ftpClient.changeWorkingDirectory("/"+casa);
			File file1=new File("files/"+casa);
			File[] files=file1.listFiles();
			enviarTodoFTP(ftpClient,files);
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (IOException e) {
			System.err.println("No se ha podido conectar con el servidor FTP");
		}
	}
	public void enviarTodoFTP(FTPSClient ftpClient,File[]files) {
		try {
			for(File file:files) {
				if(!file.isFile()) {
					File tmp=new File(file.getPath());
					ftpClient.changeWorkingDirectory(tmp.getName());
					enviarTodoFTP(ftpClient,tmp.listFiles());
					ftpClient.changeToParentDirectory();
				}else {
					subirArchivo(ftpClient,file);
				}
			}
		} catch (IOException e) {
			System.err.println("No se ha podido enviar al servidor FTP");
		}
	}
	public void subirArchivo(FTPSClient ftpClient, File file) {
		int num=0;
		System.out.println(file.getName());
		if(file.getName().equals("version.txt")) {
			try(FileInputStream InputStream =new FileInputStream(file)) {
				System.out.println(ftpClient.printWorkingDirectory());
				System.out.println(ftpClient.storeFile(file.getName(),InputStream));
			} catch (IOException e) {
				System.err.println("No se ha podido enviar al servidor FTP");
			}
		}else {
			try(ObjectInputStream InputStream = new ObjectInputStream(new FileInputStream(file))) {
				try(ObjectOutputStream OutputStream=new ObjectOutputStream(ftpClient.storeFileStream(file.getName()))){
					Object oj=InputStream.readObject();
					OutputStream.writeObject(oj);
					oj=InputStream.readObject();
					OutputStream.writeObject(oj);
					OutputStream.close();
					ftpClient.completePendingCommand();
			} catch (ClassNotFoundException e) {
					e.printStackTrace();
			}
			} catch (IOException e) {
				System.err.println("No se ha podido enviar al servidor FTP");
			}
		}
	}	
	public void borrarTodoFTP(FTPSClient ftpClient,FTPFile[]files) {
		try {
			for(FTPFile file:files) {
				if(!file.isFile()) {
					ftpClient.changeWorkingDirectory(file.getName());
					borrarTodoFTP(ftpClient,ftpClient.listFiles());
					ftpClient.changeToParentDirectory();
				}else {
					ftpClient.deleteFile(file.getName());
				}
			}
		} catch (IOException e) {
			System.err.println("No se ha podido borrar del servidor FTP");
		}
	}
}
