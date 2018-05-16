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
		id = 1;
		//valor inicial
		timeValue = 0;
	}
	
	public String GetLamportValue(Socket socket) throws IOException, InterruptedException{
		
		clientSocket = socket;
		
		int outerSocketTimeValue = clientSocket.getInputStream().read();
		
		CheckReceivedValue(outerSocketTimeValue, clientSocket);
		
		timeValue = timeValue > outerSocketTimeValue ? timeValue + 1 : outerSocketTimeValue + 1;
		return Integer.toString(timeValue);
	}
	
	public static String SetNewLocalEventValue() throws InterruptedException{
		
		timeValue += 1;
		
		return Integer.toString(timeValue);
	}
	
	public static String SetNewEventValue(){
		
		timeValue += 1;
		
		return Integer.toString(timeValue);
	}
	
	public static void CheckReceivedValue(int receivedValue, Socket pReceivedSocket) throws IOException{				
		if (receivedValue < timeValue){
			pReceivedSocket.getOutputStream().write(timeValue + 1);
		}else{
			pReceivedSocket.getOutputStream().write(receivedValue);
		}
	}
	
	public static void CheckSentValue(int sentValue, Socket pSendSocket) throws IOException{				
		
		while (pSendSocket.getInputStream().available() == 0){
			
		}
		
		int receivedValue = pSendSocket.getInputStream().read();
		if (sentValue < receivedValue){
			timeValue = receivedValue + 1;
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		ServerSocket serverSocket1 = new ServerSocket(8085);
		
		//evento 1. Local e informa valor de Lamport
		String serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " +  serverSocket1LamportValue);
		
		//evento 2 envia mensagem 
		sendSocket = new Socket("localhost", 8090);
		SetNewEventValue();
		sendSocket.getOutputStream().write(timeValue);
		
		CheckSentValue(ServerSocket.timeValue, sendSocket);
		System.out.println("Socket " + id + "| Evento " + timeValue);
		sendSocket.close();
		
		//evento 3. Local e informa valor de Lamport
		serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " +  serverSocket1LamportValue);
				
		Thread.sleep(5000);
		
		//evento 8. Atualizado de outro socket. Envia msg para outro socket para sincronização
		sendSocket3 = new Socket("localhost", 8095);
		SetNewEventValue();
		sendSocket3.getOutputStream().write(ServerSocket.timeValue);
		CheckSentValue(ServerSocket.timeValue, sendSocket3);
		System.out.println("Socket " + id + "| Evento " + ServerSocket.timeValue);
	
		Thread.sleep(5000);
		
		//evento 9. Envia mensagem para socket 2 
		sendSocket = new Socket("localhost", 8090);
		SetNewEventValue();
		sendSocket.getOutputStream().write(timeValue);
		
		CheckSentValue(ServerSocket.timeValue, sendSocket);
		System.out.println("Socket " + id + "| Evento " + timeValue);
		sendSocket.close();
		
		System.out.println("Concluído");
		
		serverSocket1.close();
	}

}

