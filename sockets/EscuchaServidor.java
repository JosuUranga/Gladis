package sockets;


import java.net.*;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.io.*;

public class EscuchaServidor extends Thread{
	PropertyChangeListener listener;
	List<String>ips;
	public EscuchaServidor (PropertyChangeListener listener,List<String>ips) {
		super("escuchaCliente");
		this.listener=listener;
		this.ips=ips;
	}
	
    public void run() {
    	Boolean escuchando=true;
        try ( 
            ServerSocket serverSocket = new ServerSocket(5001);
        ) {
            while (escuchando) {
	            new ComunicacionServidor(serverSocket.accept(),listener,ips).start(); //Siempre que alguien nos hable por el puerto 5001 se iniciara un ComunicacionServidor.
	        }
    	} catch (IOException e) {

    	}
    }
}

