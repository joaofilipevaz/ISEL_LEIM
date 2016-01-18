package Trab2;

import java.io.IOException;
import java.util.ArrayList;

public class ProcessManager {
	//array de processos
	ArrayList<Processo> taskmanager;

	//constructor
	public ProcessManager() {
		taskmanager = new ArrayList<Processo>();
	} 

	public void lancaProcesso(String path){
		Processo proc = new Processo(path);
		//executa o processo windows
		if (path.endsWith(".exe")) {
			try { 
				ProcessBuilder pb= new ProcessBuilder(path);
				proc.p = pb.start();
				System.out.println("A Executar " +path);
				taskmanager.add(proc);
			} catch (IOException e) { System.err.println(e.getMessage()); 
			}
			//executa o processo java
		} else if (path.endsWith(".jar")) {
			try { 
				String[] command = {"java", "-jar", path};
				proc.p = Runtime.getRuntime().exec(command);
				System.out.println("A Executar " +path);
				taskmanager.add(proc);
			} catch (IOException e) { e.printStackTrace(); };
		} else {
			System.out.println("Ficheiro nao válido - Utilize apenas extensões .exe ou .jar");
		}
	}

	//verifica se o processo esta activo e actualiza o array
	public String verifyTermination(){
		for (int i=0; i<taskmanager.size(); i++) {
			if (!taskmanager.get(i).getProcesso().isAlive()) {
				String path = taskmanager.get(i).getPath();
				taskmanager.remove(i);
				return path;
			}
		}
		return null;
	}
}