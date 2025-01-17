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
		
	//	int receivedValue = pSendSocket.getInputStream().read();
	//	if (sentValue < receivedValue){
	//		timeValue = receivedValue;
	//	}
	//}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		ServerSocket serverSocket2 = new ServerSocket(8090);
		
		//evento 1. Local e informa valor de Lamport
		String serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 3. Recebe msg de outro socket e informa valor de Lamport
		System.out.println("Socket " + id + " Aguardando conex�o de Socket 1");
		serverSocket1LamportValue = serverSocket2.GetLamportValue(serverSocket2.accept());
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 4. Sincronizar ServerSocket3
		SetNewEventValue();
		System.out.println("Socket " + id + "| Preparando msg para socket 3");
		
		sendSocket3 = new Socket("localhost", 8095);
		sendSocket3.getOutputStream().write(timeValue);
		//CheckSentValue(ServerSocket.timeValue, sendSocket3);
		
		System.out.println("Socket " + id + "| Evento " + timeValue);
		
		//evento 5. Local e informa valor de Lamport
		serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 10. Recebe msg de outro socket e informa valor de Lamport
		System.out.println("Socket " + id + " Aguardando conex�o de Socket 1");
		serverSocket1LamportValue = serverSocket2.GetLamportValue(serverSocket2.accept());
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		//evento 11. Local e informa valor de Lamport
		serverSocket1LamportValue = SetNewLocalEventValue();
		System.out.println("Socket " + id + "| Evento " + serverSocket1LamportValue);
		
		System.out.println("Conclu�do");
		
		serverSocket2.close();
	}

}

