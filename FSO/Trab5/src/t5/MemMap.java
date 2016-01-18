package t5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import RobotLego.RobotLego;

/** 
 * A Classe MemMap implementa um sistema de Comunicação utilizando Memoria Partilhada
 *  e o protocolo de comunicação associado entre o Player e a Gui do Robot 
 */
public class MemMap {

	// ficheiro com as variaveis de comunicação
	private File fileCom;
	
	// ficheiro com o registo da trajectoria
	private File fileTrajec;
	
	// canal que liga o conteúdo do ficheiro ao Buffer
	private FileChannel canal;
	
	// buffer
	private MappedByteBuffer buffer;
	
	// dimensão máxima em bytes do buffer
	private final static int BUFFER_MAX = 248;
	
	//writer e reader
	protected BufferedReader bufferedReader;
	protected BufferedWriter bufferedWriter;
	
	//arraylist para reprodução da trajectoria play
	private ArrayList<String> trajectoria;
	
	//arraylist para reprodução da trajectoria Rewind
	private ArrayList<String> trajectoriaRewind;

	//calculadora do tempo de sleep
	private int sleepTimeReta, sleepTimeCurva;
	double distcurva;

	/** 
	 * Inteiros que representam os estados a comunicar entre as guis.
	 * os inteiros representam boleanos com 0=false e 1=true excepto o execTime
	 *  recState --> estado do botão de rec do player
	 *  playState --> estado do botão de play do player
	 *  rewindState --> estado do botão de rewind do player
	 *  alert --> alerta o player para o fim da reprodução dos comandos
	 *  stop --> permite a interrupção da reprodução de comandos
	 *  execTime --> tempo (em ms) previsto de sleep para a execução do comando.
	 */
	private int recState, playState, rewindState, alert, stop, execTime;

	// Constructor
	@SuppressWarnings("resource")
	public MemMap(){
		fileCom = new File("com.rcf");
		try {
			canal = new RandomAccessFile(fileCom, "rw").getChannel();
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_MAX);
		} catch (IOException e) {e.printStackTrace(); }
		//init arrays
		trajectoria = new ArrayList<String>();
		trajectoriaRewind = new ArrayList<String>();
	}
	
	/** 
	 * Metodos Get and Set das variaveis de comunicação
	 */
	
	public synchronized int lerRecState() {
		recState = buffer.getInt(0);
		return recState;
	}
	public synchronized void setRecState(int recState) {
		buffer.putInt(0, recState);
	}

	public synchronized int lerPlayState() {
		playState = buffer.getInt(4);
		return playState;
	}
	public synchronized void setPlayState(int playState) {
		buffer.putInt(4, playState);
	}

	public synchronized int lerRewindState() {
		rewindState = buffer.getInt(8);
		return rewindState;
	}
	public synchronized void setRewindState(int rewindState) {
		buffer.putInt(8, rewindState);
	}

	public int lerAlert() {
		alert = buffer.getInt(12);
		return alert;
	}
	public void setAlert(int alert) {
		buffer.putInt(12, alert);
	}

	public synchronized int lerStop() {
		stop = buffer.getInt(16);
		return stop;
	}
	public synchronized void setStop(int stop) {
		buffer.putInt(16, stop);
	}
	
	public synchronized int lerExecTime() {
		execTime = buffer.getInt(20);
		return execTime;
	}
	public synchronized void setExecTime(int execTime) {
		buffer.putInt(20, execTime);
	}

	//metodo que inicializa ficheiro de escrita da trajectoria
	public void initFile(){
		try {
			fileTrajec = new File("trajectoria.rcf");
			bufferedWriter = new BufferedWriter(new FileWriter(fileTrajec,false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//metodo que permite a escrita de comandos para ficheiro
	public void writeCmd(String cmd) throws IOException{
		bufferedWriter.write(cmd);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}

	//metodo que fecha o writer de escrita da trajectoria
	public void closeFile(){
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//faz reset ao arraylist para forçar um nova leitura
		// sempre que se efectua uma gravação de trajectoria
		trajectoria.clear();
	}

	//metodo que reproduz trajetoria do robot a partir do ficheiro
	public synchronized void readCmd() throws IOException, InterruptedException{
		bufferedReader = new BufferedReader(new FileReader(fileTrajec));
		while (bufferedReader.ready()){
			trajectoria.add(bufferedReader.readLine());
		}	
		bufferedReader.close();
		//copia o array para um outro objecto do mesmo tipo
		trajectoriaRewind = new ArrayList<String>(trajectoria);
		//inverte a ordem da lista para leitura rewind
		Collections.reverse(trajectoriaRewind);
	}

	//metodo que reproduz trajetoria
	public synchronized void play(RobotLego robot) throws IOException, InterruptedException{
		//le o ficheiro sempre que o array esteja vazio
		if (trajectoria.isEmpty()){
			readCmd();
		}
		int len = trajectoria.size();
		for (int i = 0; i < len; i++) {
			//verifica se o botao de stop foi pressionado
			if (lerStop()==1){
				setStop(0);
				break;
			}
			protocol(trajectoria.get(i), robot);
		}		
		setPlayState(0);
		setAlert(1);
	}

	//metodo que reproduz trajetoria em rewind
	public void rewind(RobotLego robot) throws IOException, InterruptedException{
		//le o ficheiro sempre que o array esteja vazio
		if (trajectoriaRewind.isEmpty()){
			readCmd();
		}
		int len = trajectoriaRewind.size();
		//roda o robot 180 graus para iniciar o percurso inverso
		//robot.CurvarEsquerda(0,180);
		//robot.Parar(false);
		for (int i = 0; i < len; i++) {
			if (lerStop()==1){
				break;
			}
			protocolRewind(trajectoriaRewind.get(i), robot);
		}
		//roda o robot 180 graus para o por na posição original
		if (lerStop()==0){
			//robot.CurvarEsquerda(0,180);
			//robot.Parar(false);
		} else {setStop(0);}
		setRewindState(0);
		setAlert(1);
	}

	/** 
	 * Metodo que implementa o protocolo que interpreta a leitura dos dados 
	 * do ficheiro e os mapeia em comandos do robot
	 */
	private void protocol(String line, RobotLego robot) throws InterruptedException{
		String[] breakline;
		int raio, angulo, distancia;
		breakline = line.split("_");
		if (line.startsWith("Parar")){
			//robot.Parar(true);
			System.out.println(line);
		} else if (line.startsWith("Reta")){
			distancia = Integer.parseInt(breakline[1]);
			sleepTimeReta = ((Math.abs(distancia)*5500)/100);
			System.out.println(line);
			//robot.Reta(distancia);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeReta);
			setExecTime(0);
		} else if (line.startsWith("CurvarEsquerda")){
			raio = Integer.parseInt(breakline[1]);
			angulo = Integer.parseInt(breakline[2]);
			distcurva = ((angulo*(2*Math.PI*raio))/360);
			sleepTimeCurva = (int) ((distcurva*5500)/100);
			System.out.println(line);
			//robot.CurvarEsquerda(raio,angulo);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeCurva);
			setExecTime(0);
		} else if (line.startsWith("CurvarDireita")){
			raio = Integer.parseInt(breakline[1]);
			angulo = Integer.parseInt(breakline[2]);
			distcurva = ((angulo*(2*Math.PI*raio))/360);
			sleepTimeCurva = (int) ((distcurva*5500)/100);
			System.out.println(line);
			//robot.CurvarDireita(raio,angulo);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeCurva);
			setExecTime(0);
		} else {System.out.println("ERRO --> comando não reconhecido");}
	}

	//igual ao superior mas para o rewind
	private void protocolRewind(String line, RobotLego robot) throws InterruptedException{
		String[] breakline;
		int raio, angulo, distancia;
		breakline = line.split("_");
		if (line.startsWith("Parar")){
			//robot.Parar(true);
			System.out.println(line);
		} else if (line.startsWith("Reta")){
			distancia = Integer.parseInt(breakline[1]);
			sleepTimeReta = ((Math.abs(distancia)*5500)/100);
			System.out.println(line);
			//robot.Reta(distancia);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeReta);
			setExecTime(0);
		} else if (line.startsWith("CurvarEsquerda")){
			raio = Integer.parseInt(breakline[1]);
			angulo = Integer.parseInt(breakline[2]);
			distcurva = ((angulo*(2*Math.PI*raio))/360);
			sleepTimeCurva = (int) ((distcurva*5500)/100);
			System.out.println(line);
			//robot.CurvarDireita(raio,angulo);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeCurva);
			setExecTime(0);
		} else if (line.startsWith("CurvarDireita")){
			raio = Integer.parseInt(breakline[1]);
			angulo = Integer.parseInt(breakline[2]);
			distcurva = ((angulo*(2*Math.PI*raio))/360);
			sleepTimeCurva = (int) ((distcurva*5500)/100);
			System.out.println(line);
			//robot.CurvarEsquerda(raio,angulo);
			//robot.Parar(false);
			setExecTime(sleepTimeReta);
			Thread.sleep(sleepTimeCurva);
			setExecTime(0);
		} else {System.out.println("ERRO --> comando não reconhecido");}
	}

	// fecha o canal entre o buffer e o ficheiro
	public void fecharCanal() {
		try {
			canal.close();
			System.out.println("Canal Fechado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}