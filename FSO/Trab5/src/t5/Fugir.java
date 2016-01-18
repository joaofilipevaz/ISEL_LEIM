package t5;

import java.io.IOException;
import RobotLego.RobotLego;

/** 
 * A Classe Fugir implementa o comportamento fugir
 */
public class Fugir extends MyThread {

	private int dist;
	private ReadGui readGui;
	private int dmin, dmax, dseg;
	private Vaguear vaguear;
	private MemMap bufferCom;

	@SuppressWarnings("static-access")
	public Fugir(RobotLego robot, MemMap bufferCom, ReadGui readGui, Vaguear vaguear) {
		super(robot);
		this.bufferCom = bufferCom;
		this.readGui = readGui;
		this.vaguear = vaguear;
		robot.SetSensorLowspeed(robot.S_4);
		dmin = 0;
		dmax = 0;
		dseg = dmax+10;
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
					dist = robot.SensorUS(robot.S_4);
					//System.out.println("Distancia Fugir --> " + dist);
					dmax = readGui.getdMax();
					dmin = readGui.getdMin();
					dseg = dmax+10;
					if (dist<dmax && dist>=dmin){
						int vaguearOldState;
						vaguearOldState = vaguear.mySuspend();
						escape();

						//se fui eu que te pus a dormir acorda
						if (vaguearOldState!=2){
							vaguear.myResume();
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

	//metodo que implementa o comportamento de fugir
	@SuppressWarnings("static-access")
	public void escape() throws InterruptedException {
		int speed = 50;
		int step = 10;
		int sleepTimeReta = ((step*5500)/100);
		robot.SetSpeed(speed);
		//enquanto a distancia for menor que a distância de segurança
		while (dist<dseg){
			robot.Reta(step);
			if (bufferCom.lerRecState()==1) {
				try {
					bufferCom.writeCmd("Reta_"+step);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(sleepTimeReta);
			int newDist = robot.SensorUS(robot.S_4);
			//se a distancia diminuir acelera progressivamente
			if (newDist<=dist){
				if (speed<100){
					speed += 10;
					robot.SetSpeed(speed);
				}	
			}
			dist=newDist;
			if (this.estado==SUSPENSO){
				break;
			}
		}
		robot.Parar(false);
	}
}