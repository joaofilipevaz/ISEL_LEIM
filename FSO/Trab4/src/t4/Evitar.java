package t4;

import RobotLego.RobotLego;

public class Evitar extends MyThread {

	private Vaguear vaguear;
	private Fugir fugir;
	private int vaguearOldState, fugirOldState;

	public Evitar(RobotLego robot, Vaguear vaguear, Fugir fugir) {
		super(robot);
		robot.SetSensorTouch(robot.S_2);
		this.vaguear = vaguear;
		this.fugir = fugir;
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

	public void evitaObstaculo() throws InterruptedException {
		int sleepTimeReta = ((20*5500)/100);
		int sleepTimeCurva = 1000;
		robot.Parar(true);
		robot.Reta(-20);
		robot.Parar(false);
		Thread.sleep(sleepTimeReta);
		robot.CurvarEsquerda(0, 90);
		robot.Parar(false);
		Thread.sleep(sleepTimeCurva);
	}
}