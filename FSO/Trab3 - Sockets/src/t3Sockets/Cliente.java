package t3Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.UUID;

public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int NUMEROPORTO= 40266;
	private int estadoLigacao, posNaFila;
	private Socket cliente;
	private String hostName;
	private PrintStream outStream;
	private BufferedReader inStream;
	UUID uid;

	public Cliente() throws IOException {
		hostName = java.net.InetAddress.getLocalHost().getHostName();
		uid = UUID.randomUUID();
	}

	public void open() {
		try {
			cliente = new Socket(hostName, NUMEROPORTO);
			inStream = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			outStream = new PrintStream(cliente.getOutputStream());
			GuiCliente.estado = 2;
		} catch (IOException e) {
			GuiCliente.estado = 0;
		}
	}

	public void close() {
		try {
			inStream.close();
			outStream.close();
			cliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read() {
		while(true){
			try {
				if (inStream.ready()) {
					estadoLigacao = Integer.parseInt(inStream.readLine());
					posNaFila = Integer.parseInt(inStream.readLine());
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeUid() {
		String s = this.uid.toString()+"\n";
		outStream.print(s);
		outStream.flush();
	}
	
	public void requestClosure() {
		String q = "QUIT"+"\n";
		String s = this.uid.toString()+"\n";
		outStream.print(q);
		outStream.print(s);
		outStream.flush();
	}

	public int getEstado() {
		return estadoLigacao;
	}  

	public int getEspera() {
		return posNaFila;
	} 
}