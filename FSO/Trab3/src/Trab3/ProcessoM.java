package Trab3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class ProcessoM {

	// ficheiro
	File ficheiro;
	// canal que liga o conteúdo do ficheiro ao Buffer
	FileChannel canal;
	// buffer
	MappedByteBuffer buffer;
	// dimensão máxima em bytes do buffer
	final static int BUFFER_MAX= 248, UIDSIZE=16, LONGSIZE=8;
	private int estadoLigacao, nRobosEmEspera;

	// construtor onde se cria o canal
	@SuppressWarnings("resource")
	ProcessoM(){
		// cria um ficheiro com o nome comunicacao.dat
		ficheiro = new File("comunicacao.dat");

		//cria um canal de comunicação de leitura e escrita
		try {
			canal = new RandomAccessFile(ficheiro, "rw").getChannel();
		} catch (FileNotFoundException e) {e.printStackTrace(); }

		// mapeia para memória o conteúdo do ficheiro
		try {
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_MAX);
		} catch (IOException e) { e.printStackTrace(); }
	}

	int lerEstado() {
		estadoLigacao = buffer.getInt(0);
		return estadoLigacao;
	}

	void setEstado(int estadoLigacao) {
		buffer.putInt(0, estadoLigacao);
	}

	int getnrobosEmEspera(){
		nRobosEmEspera = buffer.getInt(4);
		return nRobosEmEspera;
	}

	void incrementaRobos(){
		nRobosEmEspera = buffer.getInt(4);
		nRobosEmEspera++;
		buffer.putInt(4, nRobosEmEspera);
	}

	void decrementaRobos(){
		nRobosEmEspera = buffer.getInt(4);
		nRobosEmEspera--;
		buffer.putInt(4, nRobosEmEspera);
	}
	
	//metodo de escrita do uid no buffer
	void enviarUid(UUID uid) {
		int queue = this.getnrobosEmEspera()*2;
		if (queue==0){
			buffer.putLong(LONGSIZE, uid.getMostSignificantBits());
			buffer.putLong(LONGSIZE*2, uid.getLeastSignificantBits());
		} else{
			buffer.putLong(LONGSIZE*(queue+1), uid.getMostSignificantBits());
			buffer.putLong(LONGSIZE*(queue+2), uid.getLeastSignificantBits());
		}
	}

	//metodo de leitura para array da zona do buffer onde se encontram os uids
	long[] lerUidfromBuffer() {
		int queue = this.getnrobosEmEspera()*2;
		long[] uid = new long[queue];
		if (queue>0){
			for (int i=1; i <= queue ;i++){
				uid[i-1] = buffer.getLong(LONGSIZE*i);
			}
		}
		return uid;
	}
	
	//actualiza a ordem dos uids no buffer
	void updateUidBuffer(int index) {
		int bufferindex = index+1; 
		long[] uids = this.lerUidfromBuffer();
		//update se for o ultimo uid
		if (bufferindex == uids.length-1){
			buffer.putLong(LONGSIZE*bufferindex, 0);
			buffer.putLong(LONGSIZE*(bufferindex+1), 0);
		//update se for o unico uid em lista de espera
		} else if (getnrobosEmEspera()==1){
			for (int j=1; j <= 2 ;j++){
				buffer.putLong(LONGSIZE*j, 0);
			}
		//update uid se tiver outros uid's em espera atras de si
		} else {
			for (int i= bufferindex; i <= (uids.length-2) ;i++){
				buffer.putLong(LONGSIZE*(i), uids[i+1]);
				buffer.putLong(LONGSIZE*(i+2), 0);
			}
		}
	}
	
	//imprime para a consola a fila de espera de uids
	void debug(){
		String msg, msg2, s1,s2,s3;
		String[] uidlist = new String[this.getnrobosEmEspera()];
		long uidMost, uidLeast;
		int counter = 0;
		estadoLigacao = buffer.getInt(0);
		nRobosEmEspera = buffer.getInt(4);
		
		long[] uids = lerUidfromBuffer();

		for (int i=0; i < (uids.length/2) ; i++){
			int indice = i*2;
			uidMost =  uids[indice];
			uidLeast = uids[indice+1];
			s1=Long.toString(uidMost);    
			s2=Long.toString(uidLeast);
			s3=s1+s2;
			uidlist[counter] = s3;
			counter++;
		}

		msg = "O estado da Ligação é "+estadoLigacao+", existem "+nRobosEmEspera+" robos em espera.";
		msg2 = "Os identificadores unicos destes robos são:";

		System.out.println(msg);
		if (getnrobosEmEspera()>0){
			System.out.println(msg2);
			for (int i=0; i < uidlist.length; i++){
				System.out.println(uidlist[i]);
			}
		}
	}

	// fecha o canal entre o buffer e o ficheiro
	void fecharCanal() {
		try {
			canal.close();
			System.out.println("Canal Fechado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}