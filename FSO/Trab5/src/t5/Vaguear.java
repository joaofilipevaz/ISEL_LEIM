package t5;

import java.io.IOException;
import java.util.Random;
import RobotLego.RobotLego;

/** 
 * A Classe Vaguear implementa o comportamento vaguear
 */
public class Vaguear extends MyThread {

	//variaveis do metodo vagueiaRobot
	private int intEstado, distancia, angulo, raio, rnd, sleepTimeReta, sleepTimeCurva;
	//variavel que memoriza o estado anterior
	private int oldrnd;
	private double distcurva;
	//estados do comportamento vaguear
	private final int FRENTE = 0;
	private final int ESQ = 1;
	private final int DRT = 2;
	//array de estados
	private int[] state = new int[3];

	private MemMap bufferCom;

	public Vaguear(RobotLego robot, MemMap bufferCom) {
		super(robot);
		this.bufferCom = bufferCom;
		oldrnd=5;
	}

	public void run() {
		while (this.estado!=TERMINAR) {
			try{
				switch (estado) {
				case SUSPENSO:
					this.s.acquire();
					break;
				case TERMINAR:
					break;
				case EXECUCAO:
					vagueiaRobot();
					Thread.sleep(50);
					break;
				}
			} catch(InterruptedException | IOException e) {
				continue;
			}
		}
	}

	//metodo que implementa o comportamento de vaguear
	public void vagueiaRobot() throws InterruptedException, IOException {
		//calculo random
		distancia = new Random().nextInt(150);
		angulo = new Random().nextInt(180);
		raio = new Random().nextInt(50);
		rnd = new Random().nextInt(state.length);
		//calculadora do tempo de sleep
		sleepTimeReta = ((distancia*5500)/100);
		distcurva = ((angulo*(2*Math.PI*raio))/360);
		sleepTimeCurva = (int) ((distcurva*5500)/100);
		//garante não repetição do estado
		while (rnd==oldrnd) {
			rnd = new Random().nextInt(state.length);
		}
		intEstado=rnd;
		switch (intEstado) {
		case FRENTE:
			robot.Reta(distancia);
			//System.out.println("distancia --> " + distancia);
			robot.Parar(false);
			if (bufferCom.lerRecState()==1) {
				try {
					bufferCom.writeCmd("Reta_"+distancia);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(sleepTimeReta);
			break;
		case ESQ:
			robot.CurvarEsquerda(raio, angulo);
			//System.out.println("raio --> " + raio +", angulo --> " + angulo);
			robot.Parar(false);
			if (bufferCom.lerRecState()==1) {
				try {
					bufferCom.writeCmd("CurvarEsquerda_"+raio+"_"+angulo);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			Thread.sleep(sleepTimeCurva);
			break;
		case DRT:
			robot.CurvarDireita(raio, angulo);
			//System.out.println("raio --> " + raio +", angulo --> " + angulo);
			robot.Parar(false);
			if (bufferCom.lerRecState()==1) {
				try {
					bufferCom.writeCmd("CurvarDireita_"+raio+"_"+angulo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(sleepTimeCurva);
			break;
		}
		oldrnd = rnd;
	}
}