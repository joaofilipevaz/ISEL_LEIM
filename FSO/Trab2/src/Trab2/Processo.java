package Trab2;

public class Processo {

	//variaveis da classe
	String percursoProcesso;
	Process p;

	public Processo(String path){
		percursoProcesso = path;
		p = null;
	}

	public Process getProcesso(){
		return p;
	}

	public String getPath(){
		return percursoProcesso;
	}
}