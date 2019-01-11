package sockets;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

public class controladorVersion extends Thread{
	String hostname,name,password,casa;
	
	public controladorVersion(String hostname,String casa,String name,String password) {
		this.hostname=hostname;
		this.casa=casa;
		this.name=name;
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
			System.out.println("1");
			FTPFile[] files=ftpClient.listFiles();
			if(files!=null&&files.length>0) {
				for(FTPFile file:files) {
					System.out.println(file.getName());
					if(file.isDirectory())ftpClient.changeWorkingDirectory("/"+casa+"/"+file.getName());
					FTPFile[] files2=ftpClient.listFiles();
					for(FTPFile file2:files2) {
						System.out.println(file2.getName());
						if(file2.isDirectory()){
							ftpClient.changeWorkingDirectory("/"+casa+"/"+file.getName()+"/"+file2.getName());
							FTPFile[] files3=ftpClient.listFiles();
							for(FTPFile file3:files3) {
								System.out.println(file3.getName());
								guardarArchivo(ftpClient, file3);
							}
						}else guardarArchivo(ftpClient, file2);
					}
				}
			}
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void guardarArchivo(FTPSClient ftpClient,FTPFile file) {

		try(FileOutputStream OutStream=new FileOutputStream("files"+ftpClient.printWorkingDirectory()+"/"+file.getName())) {
			ftpClient.retrieveFile(file.getName(), OutStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
