package LamportClock;

import java.io.IOException;
import java.net.Socket;

public class ServerSocket extends java.net.ServerSocket {
	static int id;
	Socket clientSocket;
	static int timeValue;
	static Socket sendSocket;
	static Socket sendSocket3;
	
	public ServerSocket(int port) throws IOException {
		super(port);
		//id de classe
		id = 2;
		//valor inicial
		timeValue = 0;
	}
	
	public String GetLamportValue(Socket socket) throws IOException, InterruptedException{
		
		clientSocket = socket;
		
		int outerSocketTimeValue = clientSocket.getInputStream().read();
		timeValue = timeValue > outerSocketTimeValue ? timeValue + 1 : outerSocketTimeValue + 1;
		return Integer.toString(timeValue);
	}
	
	public static String SetNewLocalEventValue() throws InterruptedException{
		
		//Realiza alguma atividade e incrementa valor de tempo
		Thread.sleep(1000);
		
		timeValue += 1;
		
		return Integer.toString(timeValue);
	}
	
	public static String SetNewEventValue(){
		
		timeValue += 1;
		
		return Integer.toString(timeValue);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		ServerSocket serverSocket2 = new ServerSocket(8090);
		
		//evento 1. Local e informa valor de Lamport
		String serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 2 
		sendSocket = new Socket("localhost", 8085);
		SetNewEventValue();
		sendSocket.getOutputStream().write(timeValue);
		System.out.println("Socket " + id + "| Evento " + timeValue);
		
		//evento 5. Recebe msg de outro socket e informa valor de Lamport
		System.out.println("Socket " + id + " Aguardando conex�o de Socket 1");
		serverSocket1LamportValue = serverSocket2.GetLamportValue(serverSocket2.accept());
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		sendSocket.close();
		
		//evento 6. Sincronizar ServerSocket3
		SetNewEventValue();
		System.out.println("Socket " + id + "| Preparando msg para socket 3");
		
		Thread.sleep(5000);
		sendSocket3 = new Socket("localhost", 8095);
		sendSocket3.getOutputStream().write(timeValue);
		System.out.println("Socket " + id + "| Evento " + timeValue);
		
		serverSocket2.close();
	}

}

