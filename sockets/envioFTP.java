package sockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
		FTPSClient ftpClient=new FTPSClient(false);
		try {
			ftpClient.connect(hostname);
			ftpClient.execPBSZ(0);
			ftpClient.execPROT("P");
			ftpClient.enterLocalPassiveMode();
			ftpClient.login(name, password);
			ftpClient.changeWorkingDirectory("/"+casa);
			File file1=new File("files/"+casa);
			File[] files=file1.listFiles();
			if(files!=null&&files.length>0) {
				for(File file:files) {
					System.out.println(file.getName());
					File[] files2=file.listFiles();
					for(File file2:files2) {
						System.out.println(file2.getName());
						if(file2.isDirectory()){
							ftpClient.changeWorkingDirectory("/"+casa+"/"+file.getName()+"/"+file2.getName());
							File[] files3=file2.listFiles();
							for(File file3:files3) {
								System.out.println(file3.getName());
								subirArchivo(ftpClient, file3);
							}
						}else subirArchivo(ftpClient, file2);
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
			ftpClient.storeFile("/"+file.toString().replaceAll("files/", ""),InputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
