package t4;

import java.util.Random;
import RobotLego.RobotLego;

public class Vaguear extends MyThread {

	private int oldrnd;

	//variaveis do metodo vagueiaRobot
	private int intEstado, distancia, angulo, raio, rnd, sleepTimeReta, sleepTimeCurva;
	private double distcurva;
	private final int FRENTE= 0;
	private final int ESQ= 1;
	private final int DRT= 2;
	private int[] state = new int[3];

	public Vaguear(RobotLego robot) {
		super(robot);
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
					break;
				}
			} catch(InterruptedException e) {
				continue;
			}
		}
	}

	public void vagueiaRobot() throws InterruptedException {
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
			Thread.sleep(sleepTimeReta);
			break;
		case ESQ:
			robot.CurvarEsquerda(raio, angulo);
			//System.out.println("raio --> " + raio +", angulo --> " + angulo);
			robot.Parar(false);
			Thread.sleep(sleepTimeCurva);
			break;
		case DRT:
			robot.CurvarDireita(raio, angulo);
			//System.out.println("raio --> " + raio +", angulo --> " + angulo);
			robot.Parar(false);
			Thread.sleep(sleepTimeCurva);
			break;
		}
		oldrnd = rnd;
	}
}