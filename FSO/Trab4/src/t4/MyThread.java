package t4;

import java.util.concurrent.Semaphore;
import RobotLego.RobotLego;

public class MyThread extends Thread {
	
	protected RobotLego robot;
	protected Semaphore s;
	protected int estado, oldstate;
	protected final static int EXECUCAO=1;
	protected final static int SUSPENSO=2;
	protected final static int TERMINAR=3;

	public MyThread(RobotLego robot) {
		this.s = new Semaphore(0);
		this.estado = SUSPENSO;
		this.robot = robot;
	}

	public int myStop(){
		oldstate = this.estado;
		this.estado=TERMINAR;
		this.interrupt();
		return oldstate;
	}

	public int mySuspend(){
		oldstate = this.estado;
		this.estado=SUSPENSO;
		//this.interrupt();
		return oldstate;
	}

	public int myResume(){
		oldstate = this.estado;
		this.estado=EXECUCAO;
		this.s.release();
		return oldstate;
	}
	
	public int getOldState(){
		return oldstate;
	}
	
	public int getEstado(){
		return this.estado;
	}
}