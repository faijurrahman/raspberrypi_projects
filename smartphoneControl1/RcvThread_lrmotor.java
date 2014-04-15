import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RcvThread implements Runnable{
    private static final int sizeBuf = 50;
    private Socket clientSock;
    private Logger logger;
    private SocketAddress clientAddress;

    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput pinlfwd = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "lfwd", PinState.LOW);
    final GpioPinDigitalOutput pinlrev = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17, "lrev", PinState.LOW);

    final GpioPinDigitalOutput pinrfwd = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "rfwd", PinState.LOW);
    final GpioPinDigitalOutput pinrrev = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "rrev", PinState.LOW);    

    public RcvThread(Socket clntSock, SocketAddress clientAddress, Logger logger) {
        this.clientSock = clntSock;
        this.logger = logger;
        this.clientAddress = clientAddress;
    }

    public void run(){
        try {
            InputStream ins   = clientSock.getInputStream();
            OutputStream outs = clientSock.getOutputStream();

            int rcvBufSize;
            byte[] rcvBuf = new byte[sizeBuf];
            while ((rcvBufSize = ins.read(rcvBuf)) != -1) {

                String rcvData = new String(rcvBuf, 0, rcvBufSize, "UTF-8");

        	if (rcvData.compareTo("Up") == 0) {
                    pinlfwd.high();
                    pinlrev.low();
                    pinrfwd.high();
                    pinrrev.low();

                    System.out.println("Go!");
                }

                if (rcvData.compareTo("LeftTurn") == 0) {
                    pinlfwd.low();
                    pinlrev.low();
                    pinrfwd.high();
                    pinrrev.low();

                    try{Thread.sleep(200);}
                    catch(InterruptedException e) {}

                    System.out.println("LeftTurn!");
	        }

                if (rcvData.compareTo("RightTurn") == 0) {
                    pinlfwd.high();
                    pinlrev.low();
                    pinrfwd.low();
                    pinrrev.low();

                    try{Thread.sleep(200);}
                    catch(InterruptedException e) {}

                    System.out.println("RightTurn!");
                }

                if (rcvData.compareTo("Down") == 0) {
                    pinlfwd.low();
                    pinlrev.high();
                    pinrfwd.low();
                    pinrrev.high();
                    System.out.println("Back!");
                }

           	if (rcvData.compareTo("Stop") == 0) {
                    pinlfwd.low();
                    pinlrev.low();
                    pinrfwd.low();
                    pinrrev.low();
                    System.out.println("Stop!");
                }

           	logger.info("Received data : " + rcvData + " (" + clientAddress + ")");
                outs.write(rcvBuf, 0, rcvBufSize);
            }
            logger.info(clientSock.getRemoteSocketAddress() + " Closed");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception in RcvThread", ex);
        } finally {
            try {
                clientSock.close();
                System.out.println("Disconnected! Client IP : " + clientAddress);
            } catch (IOException e) {}
        }
    }
}


