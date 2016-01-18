package t3Sockets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.Color;
import RobotLego.*;

public class GuiCliente extends JFrame implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	//botões
	private JRadioButton onOff;
	private JCheckBox debugFlag;
	private JButton btnParar;
	private JButton btnFrente;
	private JButton btnRectaguarda;
	private JButton btnEsquerda;
	private JButton btnDireita;

	//JTextFields
	private JTextField offEsqTf;
	private JTextField offDrtTf;
	private JTextField nomeRobotTf;
	private JTextField raioTf;
	private JTextField anguloTf;
	private JTextField distanciaTf;
	private JTextField logTf;

	//variaveis robot
	private int distancia, angulo, raio, offsetEsq, offsetDrt;
	private String nomeRobot;

	//ligado é um boleano do estado da ligação entre o programa e o robot
	private boolean debug, ligado;
	private RobotLego robot;
	private Cliente client;

	//estados
	static int estado;
	private static final int OFF= 0;
	private static final int LIGACAO= 1;
	private static final int READWRITE= 2;
	private static final int CLOSE= 3;
	private static final int WAIT= 4;
	private static final int ON= 5;
	private static final int DESLIGA= 6;

	/**
	 * Construtor da gui
	 */
	public GuiCliente() {
		//inicialização variaveis
		distancia = 0;
		angulo = 0;
		raio = 0;
		offsetEsq = 0;
		offsetDrt = 0;
		nomeRobot = null;
		debug = false;
		estado = OFF;
		//instancia robot
		robot = new RobotLego();
		try {
			client = new Cliente();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initGUI();
		guiDisabled();
		debugFlag.doClick();
	}

	public void run() {
		while(true){
			try {
				switch (estado) {
				case OFF:
					Thread.sleep(500);
					break;
				case LIGACAO:
					client.open();
					if (estado==OFF){
						logTf.setText("Server em baixo, Tente mais tarde");
						onOff.setSelected(false);
						onOff.setEnabled(true);
					}
					break;
				case READWRITE:
					client.writeUid();
					client.read();
					estado = CLOSE;
					break;
				case CLOSE:
					client.close();
					if (client.getEstado() == 0 && client.getEspera()== 1) {
						robotOn();
						estado = ON;
					} else {
						estado = WAIT;
						onOff.setEnabled(false);
						nomeRobotTf.setEnabled(false);
						logTf.setText("A Aguardar Ligação --> "+client.getEspera()+" Lugar na Fila");
					}
					break;
				case WAIT:
					Thread.sleep(5000);
					estado = LIGACAO;
					break;
				case ON:
					Thread.sleep(50);
					break;
				case DESLIGA:
					desliga();
					estado = OFF;
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} 

	private void robotOn(){
		//ligado = robot.OpenNXT(nomeRobot);
		ligado = true;
		onOff.setEnabled(true);
		nomeRobotTf.setEnabled(true);
		if (ligado){
			onOff.setSelected(true);
			logTf.setText("O Robot está ON");
			guiEnabled();
			estado = ON;
		} else {
			estado = OFF;
			robotOff();
			logTf.setText("Não Ligado - check nome Robot");
		}
	} 

	private void robotOff(){
		onOff.setSelected(false);					
		//robot.CloseNXT();
		ligado = false;
		logTf.setText("O Robot está OFF");	
		guiDisabled();
	}

	//efectua pedido de desconexão ao servidor
	private void desliga(){
		client.open();
		client.requestClosure();
		client.close();
		robotOff();
	}

	/**
	 * MAIN.
	 */
	public static void main(String[] args) {
		try {
			GuiCliente frame = new GuiCliente();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initGUI() {
		JPanel contentPane;

		//JLabels
		JLabel lblLog;
		JLabel lblCm_1;
		JLabel lblCm_2;
		JLabel lblGraus;
		JLabel lblDistancia;
		JLabel lblAngulo;
		JLabel lblRaio;
		JLabel lblOffsetDireita;
		JLabel lbllblOffsetEsq;
		JLabel lblRobot;

		setTitle("T3 Client - ID: " + client.uid.toString());
		//tem o cuidado de fechar o canal de comunicação quando a janela é fechada.
		//impede o encerramento enquanto está ligado ou em tentativa de ligação
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (ligado){
					logTf.setText("Erro -->> Desligue a ligação");
				} else if (ligado == false && (estado == WAIT || estado == CLOSE || estado == READWRITE || estado == LIGACAO)){
					logTf.setText("Erro -->> Em espera");
				} else {
					System.exit(0);
				}
			}
		} );
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//onoff e nome robot
		{
			onOff = new JRadioButton("On / Off");
			onOff.setFont(new Font("Calibri", Font.BOLD, 16));
			onOff.setBounds(304, 18, 82, 23);
			contentPane.add(onOff);
			onOff.setAction(get_onOffAct());
		}
		{
			lblRobot = new JLabel("Nome Robot:");
			lblRobot.setFont(new Font("Calibri", Font.BOLD, 16));
			lblRobot.setBounds(48, 23, 97, 14);
			contentPane.add(lblRobot);
		}
		{
			nomeRobotTf = new JTextField();
			nomeRobotTf.setBounds(143, 21, 137, 20);
			contentPane.add(nomeRobotTf);
			nomeRobotTf.setColumns(10);
			nomeRobotTf.setText(nomeRobot);
			nomeRobotTf.setAction(get_nomeRobotAct());;
		}

		//Botoes movimentação
		{
			btnParar = new JButton();
			btnParar.setBackground(Color.RED);
			btnParar.setForeground(Color.BLACK);
			btnParar.setBounds(153, 127, 127, 48);
			contentPane.add(btnParar);
			btnParar.setAction(get_pararAct());
			btnParar.setText("PARAR");
		}
		{
			btnFrente = new JButton();

			btnFrente.setBackground(Color.GREEN);
			btnFrente.setForeground(Color.BLACK);
			btnFrente.setBounds(153, 80, 127, 48);
			btnFrente.setAction(get_frenteAct());
			btnFrente.setText("FRENTE");
			contentPane.add(btnFrente);
		}
		{
			btnRectaguarda = new JButton();
			contentPane.add(btnRectaguarda);
			btnRectaguarda.setBackground(Color.BLUE);
			btnRectaguarda.setForeground(Color.BLACK);
			btnRectaguarda.setBounds(153, 174, 127, 48);


			btnRectaguarda.setAction(get_rectaguardaAct());
			btnRectaguarda.setText("RETAGUARDA");
		}
		{
			btnEsquerda = new JButton();
			btnEsquerda.setBackground(Color.YELLOW);
			btnEsquerda.setForeground(Color.BLACK);
			btnEsquerda.setBounds(27, 127, 127, 48);
			contentPane.add(btnEsquerda);
			btnEsquerda.setAction(get_esquerdaAct());
			btnEsquerda.setText("ESQUERDA");
		}
		{
			btnDireita = new JButton();
			btnDireita.setBackground(Color.MAGENTA);
			btnDireita.setForeground(Color.BLACK);
			btnDireita.setBounds(279, 127, 127, 48);
			contentPane.add(btnDireita);
			btnDireita.setAction(get_direitaAct());
			btnDireita.setText("DIREITA");
		}

		//variaveis
		{
			lbllblOffsetEsq = new JLabel("Offset Esquerda");
			lbllblOffsetEsq.setFont(new Font("Calibri", Font.BOLD, 16));
			lbllblOffsetEsq.setBounds(33, 3, 121, 14);
			contentPane.add(lbllblOffsetEsq);
		}
		{
			offEsqTf = new JTextField();
			offEsqTf.setBounds(0, 0, 23, 20);
			contentPane.add(offEsqTf);
			offEsqTf.setColumns(10);
			offEsqTf.setAction(get_offEsqAct());
		}
		{
			lblOffsetDireita = new JLabel("Offset Direita");
			lblOffsetDireita.setFont(new Font("Calibri", Font.BOLD, 16));
			lblOffsetDireita.setBounds(315, 3, 97, 14);
			contentPane.add(lblOffsetDireita);
		}
		{
			offDrtTf = new JTextField();
			offDrtTf.setColumns(10);
			offDrtTf.setBounds(411, 0, 23, 20);
			contentPane.add(offDrtTf);
			offDrtTf.setAction(get_offDrtAct());
		}
		{
			lblRaio = new JLabel("Raio");
			lblRaio.setFont(new Font("Calibri", Font.BOLD, 16));
			lblRaio.setBounds(9, 52, 38, 14);
			contentPane.add(lblRaio);
		}
		{
			raioTf = new JTextField();
			raioTf.setColumns(10);
			raioTf.setBounds(47, 49, 29, 20);
			contentPane.add(raioTf);
			raioTf.setAction(get_raioAct());
		}
		{
			lblAngulo = new JLabel("Angulo");
			lblAngulo.setFont(new Font("Calibri", Font.BOLD, 16));
			lblAngulo.setBounds(148, 52, 56, 14);
			contentPane.add(lblAngulo);
		}
		{
			anguloTf = new JTextField();
			anguloTf.setColumns(10);
			anguloTf.setBounds(201, 49, 23, 20);
			contentPane.add(anguloTf);
			anguloTf.setAction(get_anguloAct());
		}
		{
			lblDistancia = new JLabel("Distancia");
			lblDistancia.setFont(new Font("Calibri", Font.BOLD, 16));
			lblDistancia.setBounds(304, 52, 73, 14);
			contentPane.add(lblDistancia);
		}
		{
			distanciaTf = new JTextField();
			distanciaTf.setColumns(10);
			distanciaTf.setBounds(368, 49, 23, 20);
			contentPane.add(distanciaTf);
			distanciaTf.setAction(get_distanciaAct());
		}
		{
			lblCm_1 = new JLabel("Cm");
			lblCm_1.setFont(new Font("Calibri", Font.BOLD, 16));
			lblCm_1.setBounds(82, 52, 29, 14);
			contentPane.add(lblCm_1);
		}
		{
			lblGraus = new JLabel("Graus");
			lblGraus.setFont(new Font("Calibri", Font.BOLD, 16));
			lblGraus.setBounds(230, 52, 46, 14);
			contentPane.add(lblGraus);
		}
		{
			lblCm_2 = new JLabel("Cm");
			lblCm_2.setFont(new Font("Calibri", Font.BOLD, 16));
			lblCm_2.setBounds(401, 52, 23, 14);
			contentPane.add(lblCm_2);
		}

		//debug e logs
		{
			debugFlag = new JCheckBox("Debug");
			debugFlag.setFont(new Font("Calibri", Font.BOLD, 16));
			debugFlag.setBounds(27, 230, 84, 25);
			contentPane.add(debugFlag);
			debugFlag.setAction(get_debugAct());
		}
		{
			lblLog = new JLabel("Log");
			lblLog.setFont(new Font("Calibri", Font.BOLD, 16));
			lblLog.setBounds(153, 235, 29, 14);
			contentPane.add(lblLog);
		}
		{
			logTf = new JTextField();
			logTf.setColumns(10);
			logTf.setBounds(186, 233, 220, 20);
			logTf.setEditable(false);
			contentPane.add(logTf);
		}
	}

	private void guiDisabled(){
		offEsqTf.setEditable(false);
		offDrtTf.setEditable(false);
		distanciaTf.setEditable(false);
		raioTf.setEditable(false);
		anguloTf.setEditable(false);
		btnFrente.setEnabled(false);
		btnEsquerda.setEnabled(false);
		btnRectaguarda.setEnabled(false);
		btnDireita.setEnabled(false);
		btnParar.setEnabled(false);
		nomeRobotTf.setEditable(true);
	}

	private void guiEnabled(){
		nomeRobotTf.setEditable(false);
		offEsqTf.setEditable(true);
		offDrtTf.setEditable(true);
		distanciaTf.setEditable(true);
		raioTf.setEditable(true);
		anguloTf.setEditable(true);
		btnFrente.setEnabled(true);
		btnEsquerda.setEnabled(true);
		btnRectaguarda.setEnabled(true);
		btnDireita.setEnabled(true);
		btnParar.setEnabled(true);
	}

	/**
	 * Metodos que implementam os actionListeners
	 */

	private AbstractAction get_onOffAct() {
		AbstractAction onOffAct = null;
		if (onOffAct == null) {
			onOffAct = new AbstractAction("On/Off", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (estado == OFF) {
						estado = LIGACAO;
					} else {
						estado = DESLIGA;
					}
				}
			};
		}
		return onOffAct;
	}

	private AbstractAction get_nomeRobotAct() {
		AbstractAction nomeRobotAct = null;
		if (nomeRobotAct == null) {
			nomeRobotAct = new AbstractAction("Nome Robot", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					nomeRobot = nomeRobotTf.getText();
					if (debug) {
						logTf.setText("O nome do Robot é " + nomeRobot);
					}
				}
			};
		}
		return nomeRobotAct;
	}

	private AbstractAction get_offEsqAct() {
		AbstractAction offEsqAct = null;
		if (offEsqAct == null) {
			offEsqAct = new AbstractAction("Offset Esquerda", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					offsetEsq = Integer.parseInt(offEsqTf.getText());
					if (ligado){
						robot.AjustarVME(offsetEsq);
						robot.Parar(false);
					}
					if (debug) {
						logTf.setText(offsetEsq + " Offset Esquerda");
					}
				}
			};
		}
		return offEsqAct;
	}

	private AbstractAction get_offDrtAct() {
		AbstractAction offDrtAct = null;
		if (offDrtAct == null) {
			offDrtAct = new AbstractAction("Offset Direita",null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					offsetDrt = Integer.parseInt(offDrtTf.getText());	
					if (ligado){
						robot.AjustarVMD(offsetDrt);
						robot.Parar(false);
					}
					if (debug) {
						logTf.setText(offsetDrt + " Offset Direita");
					}
				}
			};
		}
		return offDrtAct;
	}

	private AbstractAction get_distanciaAct() {
		AbstractAction distanciaAct = null;
		if (distanciaAct == null) {
			distanciaAct = new AbstractAction("distancia", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					distancia = Integer.parseInt(distanciaTf.getText());
					if (debug) {
						logTf.setText(distancia + " Distância");
					}
				}
			};
		}
		return distanciaAct;
	}

	private AbstractAction get_raioAct() {
		AbstractAction raioAct = null;
		if (raioAct == null) {
			raioAct = new AbstractAction("raio", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					raio = Integer.parseInt(raioTf.getText());
					if (debug) {
						logTf.setText(raio + " raio");
					}
				}
			};
		}
		return raioAct;
	}

	private AbstractAction get_anguloAct() {
		AbstractAction anguloAct = null;
		if (anguloAct == null) {
			anguloAct = new AbstractAction("angulo", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					angulo = Integer.parseInt(anguloTf.getText());
					if (debug) {
						logTf.setText(angulo + " angulo");
					}
				}
			};
		}
		return anguloAct;
	}

	private AbstractAction get_debugAct() {
		AbstractAction debugAct = null;
		if (debugAct == null) {
			debugAct = new AbstractAction("debug", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					debug = debugFlag.isSelected();
					if (debug) {
						logTf.setText("Modo Debug Activado");
					} else {
						logTf.setText("");
					}
				}
			};
		}
		return debugAct;
	}

	private AbstractAction get_frenteAct() {
		AbstractAction frenteAct = null;
		if (frenteAct == null) {
			frenteAct = new AbstractAction("frente", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.Reta(distancia);
						robot.Parar(false);
					}
					if (debug && estado==ON) {
						logTf.setText("Em frente " + distancia + " cm");
					}
				}
			};
		}
		return frenteAct;
	}

	private AbstractAction get_esquerdaAct() {
		AbstractAction esquerdaAct = null;
		if (esquerdaAct == null) {
			esquerdaAct = new AbstractAction("esquerda", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.CurvarEsquerda(raio, angulo);
						robot.Parar(false);
					}
					if (debug && estado==ON) {
						logTf.setText("Virou esquerda " + angulo
								+ " com " + raio + " cm");
					}
				}
			};
		}
		return esquerdaAct;
	}

	private AbstractAction get_rectaguardaAct() {
		AbstractAction rectaguardaAct = null;
		if (rectaguardaAct == null) {
			rectaguardaAct = new AbstractAction("retagauarda", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						System.out.println(-distancia);
						robot.Reta(-distancia);
						robot.Parar(false);
					}
					if (debug && estado==ON) {
						logTf.setText("Rectaguarda " + distancia + " cm");
					}
				}
			};
		}
		return rectaguardaAct;
	}

	private AbstractAction get_direitaAct() {
		AbstractAction direitaAct = null;
		if (direitaAct == null) {
			direitaAct = new AbstractAction("direita", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.CurvarDireita(raio, angulo);
						robot.Parar(false);
					}
					if (debug && estado==ON) {
						logTf.setText("Virou direita " + angulo
								+ " com " + raio + " cm");
					}
				}
			};
		}
		return direitaAct;
	}

	private AbstractAction get_pararAct() {
		AbstractAction pararAct = null;
		if (pararAct == null) {
			pararAct = new AbstractAction("parar", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.Parar(true);
					}
					if (debug && estado==ON) {
						logTf.setText("Parou");
					} 
				}
			};
		}
		return pararAct;
	}
}