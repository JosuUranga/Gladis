package sockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPCmd;
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
			if(files!=null&&files.length>0) {
				for(File file:files) {
					File[] files2=file.listFiles();
					if(!file.isDirectory()) {
						ftpClient.changeWorkingDirectory("/"+casa);
						subirArchivo(ftpClient, file);
					}
					else {
					for(File file2:files2) {
						if(file2.isDirectory()){
							ftpClient.changeWorkingDirectory("/"+casa+"/"+file.getName()+"/"+file2.getName());
							File[] files3=file2.listFiles();
							for(File file3:files3) {
								subirArchivo(ftpClient, file3);
							}
						}else {
							ftpClient.changeWorkingDirectory("/"+casa+"/"+file.getName());
							subirArchivo(ftpClient, file2);
						}
					}
					}
				}
			}
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void subirArchivo(FTPSClient ftpClient, File file) {
		try(FileInputStream InputStream =new FileInputStream(file)) {
			System.out.println(ftpClient.printWorkingDirectory()+"/"+file.getName());
			ftpClient.storeFile(file.getName(),InputStream);
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
}
