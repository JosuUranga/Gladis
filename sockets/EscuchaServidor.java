package sockets;


import java.net.*;
import java.beans.PropertyChangeListener;
import java.io.*;

public class EscuchaServidor extends Thread{
	PropertyChangeListener listener;
	public EscuchaServidor (PropertyChangeListener listener) {
		super("escuchaCliente");
		this.listener=listener;
	}
	
    public void run() {
    	Boolean escuchando=true;
        try ( 
            ServerSocket serverSocket = new ServerSocket(5001);
        ) {
        	
            while (escuchando) {
	            new ComunicacionServidor(serverSocket.accept(),listener).start();
	        }
    	} catch (IOException e) {

    	}
    }
}

