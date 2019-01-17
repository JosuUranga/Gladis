package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

public class controladorVersion extends Thread{
	String hostname,name,password,casa;
	Long versionProg,versionTMP;
	Boolean inicializar;
	
	public controladorVersion(String hostname,String casa,String name,String password) {
		this.hostname=hostname;
		this.casa=casa;
		this.name=name;
		versionProg=0L;
		inicializar=true;
		this.password=password;
		this.subirVersion();
		
	}
	public void run() {
		FTPSClient ftpClient=new FTPSClient(false);
		try {
			while(true) {
				ftpClient.connect(hostname);
				ftpClient.execPBSZ(0);
				ftpClient.execPROT("P");
				ftpClient.enterLocalPassiveMode();
				ftpClient.login(name, password);
				ftpClient.changeWorkingDirectory("/"+casa);
				FTPFile[] files=ftpClient.listFiles();
				if(files!=null&&files.length>0) {
					for(FTPFile fileV:files) {
						if(fileV.getName().contains("version")) {
							try(DataInputStream version=new DataInputStream(ftpClient.retrieveFileStream(fileV.getName()))){
								versionTMP=version.readLong();
								ftpClient.completePendingCommand();
							if(versionProg<versionTMP && !inicializar) {
								versionProg=versionTMP;
								File file=new File("files/"+casa+"/");
								borrarTodoLocal(file.listFiles());
								recibirFTP(ftpClient,files);
							}
							System.out.println(versionProg);
							inicializar=false;
							if(versionProg<versionTMP)versionProg=versionTMP;
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
		}
	}
	public void recibirFTP(FTPSClient ftpClient,FTPFile[] files) {
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
				file.delete();
			}
		}
	}
	public void guardarArchivo(FTPSClient ftpClient,FTPFile file) {
		int num=0;
		if(file.getName().equals("version.txt")){
			try(FileOutputStream OutStream=new FileOutputStream("files"+ftpClient.printWorkingDirectory()+"/"+file.getName())) {
				ftpClient.retrieveFile(file.getName(), OutStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try(ObjectInputStream InputStream=new ObjectInputStream(ftpClient.retrieveFileStream(file.getName()))){
				try(ObjectOutputStream OutStream= new ObjectOutputStream(new FileOutputStream("files"+ftpClient.printWorkingDirectory()+"/"+file.getName()))) {
					try {
						while(num>1) {
							OutStream.writeObject(InputStream.readObject());
							num++;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void subirVersion() {
		versionProg=versionProg+1;
		try(DataOutputStream Sversion=new DataOutputStream(new FileOutputStream("files/"+casa+"/version.txt"))){
			Sversion.writeLong(versionProg);
		}catch(IOException e) {
			System.out.println("No se ha podido abrir version.txt");
		}
	}
}
