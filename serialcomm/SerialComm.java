package serialcomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SerialComm {
	InputStream in;
	OutputStream out;
	CommPort commPort;
	public SerialComm() {
		in=null;
		out=null;
		commPort=null;
	}
	
	public void conectar ( CommPortIdentifier portIdentifier ) throws Exception
    {
        if ( portIdentifier.isCurrentlyOwned() ){ //por si el pueto esta en uso
            System.out.println("Error puerta en uso");
        }else {
             commPort = portIdentifier.open("Mi programa",2000); //sino lo abre
            
            if ( commPort instanceof SerialPort ) { //se configura el puerto
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                serialPort.disableReceiveTimeout();		//estas dos funciones para que se pare en el .read hasta
                serialPort.enableReceiveThreshold(1); 	//que llegue algo que leer
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
            }else {
                System.out.println("Error este programa solo funciona con linea serie");
            }
        }     
    }
    
    /**
     * @throws IOException  */
    
    public String  leer () throws IOException //esta funcion lee lo que llege de la linea serie
    {
    	int max = 1024;
        byte[] buffer = new byte[max]; //aqui mete lo que va llegando
        int len = -1;
        int offset = 0;
        len = this.in.read(buffer,offset,max-offset); //va metiendo en buffer lo que llega byte a byte.
        offset+= len;

        return (new String (buffer,0,offset)); //devuelve lo que ha llegado en modo String, que sera una letra
     }
    

    /** */
    public void escribir  (String msg)
    {
        try
        {                
            this.out.write(msg.getBytes());   //el String a mandar lo pasa a bytes  y lo manda por la linea serie
            System.out.println(msg.getBytes());
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    }
    public CommPortIdentifier encontrarPuerto() //coje todos los puertos del pc y te devuelve si hay un serial
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (this.getPortTypeName(portIdentifier.getPortType()).equals("Serial")) { //si el puerto identificado es serial
            	return portIdentifier;
            }
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        } 
        return null;
    }
    
    public String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    public void cerrar() { //cierra el puerto
    	commPort.close();
    }
}
