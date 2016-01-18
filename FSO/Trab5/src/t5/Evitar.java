package t5;

import java.io.IOException;
import RobotLego.RobotLego;

/** 
 * A Classe Evitar implementa o comportamento evitar Obstaculo
 */
public class Evitar extends MyThread {

	private Vaguear vaguear;
	private Fugir fugir;
	private MemMap bufferCom;
	private int vaguearOldState, fugirOldState;

	@SuppressWarnings("static-access")
	public Evitar(RobotLego robot, MemMap bufferCom, Vaguear vaguear, Fugir fugir) {
		super(robot);
		this.bufferCom = bufferCom;
		robot.SetSensorTouch(robot.S_2);
		this.vaguear = vaguear;
		this.fugir = fugir;
	}

	@SuppressWarnings("static-access")
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
					int choque = robot.Sensor(robot.S_2);
					//System.out.println("Sensor Choque --> " + choque);
					if (choque==1){
						vaguearOldState = vaguear.mySuspend();
						fugirOldState = fugir.mySuspend();
						evitaObstaculo();

						//se fui eu que te pus a dormir acorda
						if (vaguearOldState!=2){
							vaguear.myResume();
						}

						//se fui eu que te pus a dormir acorda
						if (fugirOldState!=2){
							fugir.myResume();
						}
					}
					Thread.sleep(150);
					break;
				}
			} catch(InterruptedException e) {
				continue;
			}
		}
	}

	//metodo que implementa o comportamento de evitar obstaculo
	public void evitaObstaculo() throws InterruptedException {
		int sleepTimeReta = ((20*5500)/100);
		int sleepTimeCurva = 1000;
		robot.Parar(true);
		if (bufferCom.lerRecState()==1) {
			try {
				bufferCom.writeCmd("Parar_true");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		robot.Reta(-20);
		robot.Parar(false);
		if (bufferCom.lerRecState()==1) {
			try {
				bufferCom.writeCmd("Reta_"+(-20));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Thread.sleep(sleepTimeReta);
		robot.CurvarEsquerda(0, 90);
		robot.Parar(false);
		if (bufferCom.lerRecState()==1) {
			try {
				bufferCom.writeCmd("CurvarEsquerda_"+0+"_"+90);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		Thread.sleep(sleepTimeCurva);
	}
}