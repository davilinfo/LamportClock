package LamportClock;

import java.io.IOException;
import java.net.Socket;

public class ServerSocket extends java.net.ServerSocket {
	static int id;
	Socket clientSocket;
	static int timeValue;
	static Socket sendSocket;
	
	public ServerSocket(int port) throws IOException {
		super(port);
		//id de classe
		id = 3;
		//valor inicial
		timeValue = 0;
	}
	
	public String GetLamportValue(Socket socket) throws IOException, InterruptedException{
		
		clientSocket = socket;
		
		int outerSocketTimeValue = clientSocket.getInputStream().read();
		
		//CheckReceivedValue(outerSocketTimeValue, clientSocket);
		
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
	
	//public static void CheckReceivedValue(int receivedValue, Socket pReceivedSocket) throws IOException{				
	//	if (receivedValue < timeValue){
	//		pReceivedSocket.getOutputStream().write(timeValue + 1);
	//	}else{
	//		pReceivedSocket.getOutputStream().write(receivedValue);
	//	}
	//}
	
	//public static void CheckSentValue(int sentValue, Socket pSendSocket) throws IOException{				
	//	
	//	while (pSendSocket.getInputStream().available() == 0){
	//		
	//	}
	//	
	//	int receivedValue = pSendSocket.getInputStream().read();
	//	if (sentValue < receivedValue){
	//		timeValue = receivedValue;
	//	}
	//}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		ServerSocket serverSocket3 = new ServerSocket(8095);
		
		//evento 1. Local e informa valor de Lamport
		String serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 5. Recebe msg de socket 2 e informa valor de Lamport
		System.out.println("Socket " + id + " Aguardando conex�o de Socket 2");
		serverSocket1LamportValue = serverSocket3.GetLamportValue(serverSocket3.accept());
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 6. Local e informa valor de Lamport
		serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 7. Envia msg para outro socket para sincroniza��o
		sendSocket = new Socket("localhost", 8085);
		SetNewEventValue();
		sendSocket.getOutputStream().write(ServerSocket.timeValue);
		//CheckSentValue(ServerSocket.timeValue, sendSocket3);
		System.out.println("Socket " + id + "| Evento " + ServerSocket.timeValue);
		
		
		System.out.println("Conclu�do");
		
		serverSocket3.close();
	}

}

