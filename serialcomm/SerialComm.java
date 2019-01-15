package serialcomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SerialComm {
	public static final String CARACTERFIN = "#";
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
        if ( portIdentifier.isCurrentlyOwned() ){
            System.out.println("Error puerta en uso");
        }else {
             commPort = portIdentifier.open("Mi programa",2000);
            
            if ( commPort instanceof SerialPort ) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                serialPort.disableReceiveTimeout();
                serialPort.enableReceiveThreshold(1);
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
            }else {
                System.out.println("Error este programa solo funciona con linea serie");
            }
        }     
    }
    
    /**
     * @throws IOException  */
    
    public String  leer () throws IOException
    {
    	int max = 1024;
        byte[] buffer = new byte[max];
        int len = -1;
        int offset = 0;
        len = this.in.read(buffer,offset,max-offset);
        offset+= len;

        return (new String (buffer,0,offset));
     }
    

    /** */
    public void escribir  (String msg)
    {
        try
        {                
            this.out.write(msg.getBytes());   
            System.out.println(msg.getBytes());
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    }
    public CommPortIdentifier encontrarPuerto()
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (this.getPortTypeName(portIdentifier.getPortType()).equals("Serial")) {
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
    public void cerrar() {
    	commPort.close();
    }
}
