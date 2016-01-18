package t4;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.Color;
import RobotLego.*;
import javax.swing.SwingConstants;

public class GuiRobot extends JFrame implements Runnable, Serializable, ReadGui {

	private static final long serialVersionUID = 1L;

	//botões
	private JRadioButton onOff;
	private JRadioButton vaguearButton;
	private JRadioButton evitarButton;
	private JRadioButton fugirButton;
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
	private JTextField dMaxTf;
	private JTextField dMinTf;

	//variaveis robot
	private int distancia, angulo, raio, offsetEsq, offsetDrt;
	private String nomeRobot;

	//ligado é um boleano do estado da ligação entre o programa e o robot
	private boolean debug, ligado;
	private int dmin, dmax;
	private RobotLego robot;
	Vaguear vag;
	Evitar evi;
	Fugir fug;

	//estados
	private static int estado;
	private static final int OFF= 0;
	private static final int LIGAR= 1;
	private static final int MANUAL= 2;
	private static final int AUTO= 3;
	private static final int DESLIGA= 6;

	/**
	 * Construtor da gui
	 */
	public GuiRobot() {
		//inicialização variaveis
		distancia = 0;
		angulo = 0;
		raio = 0;
		offsetEsq = 0;
		offsetDrt = 0;
		dmin = 0;
		dmax = 0;
		nomeRobot = null;
		debug = false;
		estado = OFF;
		//instancia robot
		robot = new RobotLego();
		initGUI();
		guiDisabled();
		debugFlag.doClick();
	}

	public void run() {
		while (true){
			try {
				switch (estado) {
				case OFF:
					Thread.sleep(500);
					break;
				case LIGAR:
					robotOn();
					break;
				case MANUAL:
					Thread.sleep(50);
					break;
				case AUTO:
					Thread.sleep(50);
					break;
				case DESLIGA:
					robotOff();
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void toManual(){
		vag.mySuspend();
		evi.mySuspend();
		fug.mySuspend();
		vaguearButton.setSelected(false);
		evitarButton.setSelected(false);
		fugirButton.setSelected(false);
	}

	private void robotOn(){
		ligado = robot.OpenNXT(nomeRobot);
		//ligado = true;
		onOff.setEnabled(true);
		nomeRobotTf.setEnabled(true);
		if (ligado){
			onOff.setSelected(true);
			logTf.setText("O Robot está ON");
			guiEnabled();
			estado = MANUAL;
			//cria instancias da classes
			vag = new Vaguear(robot);
			fug = new Fugir(robot, GuiRobot.this, vag);
			evi = new Evitar(robot, vag, fug);
			//inicia threads
			vag.start();
			evi.start();
			fug.start();
		} else {
			estado = OFF;
			onOff.setSelected(false);
			robotOff();
			logTf.setText("Não Ligado - check nome Robot");
		}
	} 

	private void robotOff(){
		onOff.setSelected(false);					
		robot.CloseNXT();
		logTf.setText("O Robot está OFF");	
		guiDisabled();
		estado = OFF;
		if (ligado){
			vag.myStop();
			evi.myStop();
			fug.myStop();
		}
		ligado = false;
	}

	public int getdMax(){
		return dmax;
	}

	public int getdMin(){
		return dmin;
	}

	/**
	 * MAIN.
	 */
	public static void main(String[] args) {
		try {
			GuiRobot frame = new GuiRobot();
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
		JLabel dMaxLbl;
		JLabel dMinLbl;

		setTitle("T4 - João Vaz - 40266");
		//tem o cuidado de fechar o canal de comunicação quando a janela é fechada.
		//impede o encerramento enquanto está ligado ou em tentativa de ligação
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (ligado){
					logTf.setText("Erro -->> Desligue a ligação");
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

		vaguearButton = new JRadioButton("Vaguear");
		vaguearButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		vaguearButton.setHorizontalAlignment(SwingConstants.CENTER);
		vaguearButton.setBounds(27, 92, 127, 23);
		contentPane.add(vaguearButton);
		vaguearButton.setAction(get_vaguear());

		evitarButton = new JRadioButton("Evitar Obst\u00E1culos");
		evitarButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		evitarButton.setHorizontalAlignment(SwingConstants.CENTER);
		evitarButton.setBounds(279, 93, 127, 23);
		contentPane.add(evitarButton);
		evitarButton.setAction(get_evitar());

		fugirButton = new JRadioButton("Fugir");
		fugirButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		fugirButton.setHorizontalAlignment(SwingConstants.CENTER);
		fugirButton.setBounds(27, 186, 127, 23);
		contentPane.add(fugirButton);
		fugirButton.setAction(get_fugir());

		dMaxTf = new JTextField();
		dMaxTf.setBounds(329, 175, 36, 23);
		contentPane.add(dMaxTf);
		dMaxTf.setColumns(10);
		dMaxTf.setAction(get_dMax());

		dMinTf = new JTextField();
		dMinTf.setBounds(329, 198, 36, 23);
		contentPane.add(dMinTf);
		dMinTf.setColumns(10);
		dMinTf.setAction(get_dMin());

		dMaxLbl = new JLabel("DMAX");
		dMaxLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		dMaxLbl.setBounds(366, 174, 40, 23);
		contentPane.add(dMaxLbl);

		dMinLbl = new JLabel("DMIN");
		dMinLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		dMinLbl.setHorizontalAlignment(SwingConstants.LEFT);
		dMinLbl.setBounds(366, 199, 40, 23);
		contentPane.add(dMinLbl);
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
		vaguearButton.setEnabled(false);
		evitarButton.setEnabled(false);
		fugirButton.setEnabled(false);
		dMaxTf.setEditable(false);
		dMinTf.setEditable(false);
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
		vaguearButton.setEnabled(true);
		evitarButton.setEnabled(true);
		fugirButton.setEnabled(true);
		dMaxTf.setEditable(true);
		dMinTf.setEditable(true);
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
						estado = LIGAR;
					} else if (evi.getEstado()==1 || fug.getEstado()==1 || vag.getEstado()==1){
						logTf.setText("Erro -->> Desligue em estado Manual");
						onOff.setSelected(true);
					}
					else {
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
					if (debug && estado==MANUAL) {
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
					if (debug && estado==MANUAL) {
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
					if (debug && estado==MANUAL) {
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
					if (debug && estado==MANUAL) {
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
						toManual();
						estado = MANUAL;
						robot.Parar(true);

					}
					if (debug && estado==MANUAL) {
						logTf.setText("Parou - Modo Manual Activo");
					} 
				}
			};
		}
		return pararAct;
	}

	private AbstractAction get_vaguear() {
		AbstractAction vaguearAct = null;
		if (vaguearAct == null) {
			vaguearAct = new AbstractAction("vaguear", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						if (vag.getEstado() == 1) {
							vag.mySuspend();
							if (fug.getEstado() != 1){
								estado = MANUAL;
							}
						} else {
							vag.myResume();
							estado = AUTO;
						}
					}
					if (debug && (vag.getEstado() == 1)) {
						logTf.setText("Modo Vaguear Activo - AUTO");
					} else {
						logTf.setText("Modo Vaguear Desactivado");
					} 
				}
			};
		}
		return vaguearAct;
	}

	private AbstractAction get_evitar() {
		AbstractAction evitarAct = null;
		if (evitarAct == null) {
			evitarAct = new AbstractAction("evitar", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						if (evi.getEstado()==2) {
							evi.myResume();
						} else {
							evi.mySuspend();
						}
					}

					if (debug && (evi.getEstado() == 1)) {
						logTf.setText("Modo Evitar Obstáculo Activo");
					} else {
						logTf.setText("Modo Evitar Obstáculo Desactivado");
					} 
				}
			};
		}
		return evitarAct;
	}

	private AbstractAction get_fugir() {
		AbstractAction fugirAct = null;
		if (fugirAct == null) {
			fugirAct = new AbstractAction("fugir", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						if (fug.getEstado() == 1) {
							fug.mySuspend();
							if (vag.getEstado() != 1){
								estado = MANUAL;
							}
						} else {
							if (dmax!=0){
								fug.myResume();
								estado = AUTO;
							} else {
								logTf.setText("Inserir Dmin e Dmax");
								fugirButton.setSelected(false);
							}
						}
					}
					if (debug && (fug.getEstado() == 1)) {
						logTf.setText("Modo Fugir Activo - AUTO");
					} else if (debug && dmax!=0) {
						logTf.setText("Modo Fugir Desactivado");
					}
				}
			};
		}
		return fugirAct;
	}

	private AbstractAction get_dMax() {
		AbstractAction dMaxact = null;
		if (dMaxact == null) {
			dMaxact = new AbstractAction("dmax", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					dmax = Integer.parseInt(dMaxTf.getText());
					if (debug) {
						logTf.setText("Distância Máxima: " + dmax);
					}
				}
			};
		}
		return dMaxact;
	}

	private AbstractAction get_dMin() {
		AbstractAction dMinact = null;
		if (dMinact == null) {
			dMinact = new AbstractAction("dmin", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					dmin = Integer.parseInt(dMinTf.getText());
					if (debug) {
						logTf.setText("Distância Mínima: " + dmin);
					}
				}
			};
		}
		return dMinact;
	}

}