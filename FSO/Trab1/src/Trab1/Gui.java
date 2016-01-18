package Trab1;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.Color;
import RobotLego.*;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	//botões
	private JRadioButton onOff;
	private JCheckBox debugFlag;

	//JTextFields
	private JTextField offEsqTf;
	private JTextField offDrtTf;
	private JTextField nomeRobotTf;
	private JTextField raioTf;
	private JTextField anguloTf;
	private JTextField distanciaTf;
	private JTextField logTf;

	//variaveis robot
	int distancia, angulo, raio, offsetEsq, offsetDrt;
	String nomeRobot;
	//power é um boleano do botão OnOFF
	//ligado é um boleano do estado da ligação entre o programa e o robot
	boolean power, debug, ligado;
	RobotLego robot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Gui frame = new Gui();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construtor da gui
	 */
	public Gui() {
		//inicialização variaveis
		distancia = 0;
		angulo = 0;
		raio = 0;
		offsetEsq = 0;
		offsetDrt = 0;
		nomeRobot=null;
		debug = false;
		power = false;
		ligado = false;
		//instancia robot
		robot = new RobotLego();
		initGUI();
	}

	private void initGUI() {
		JPanel contentPane;
		//botões
		JButton btnParar;
		JButton btnFrente;
		JButton btnRectaguarda;
		JButton btnEsquerda;
		JButton btnDireita;
		
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
		
		setTitle("Trabalho 1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
			contentPane.add(logTf);
		}
	}

	/**
	 * Metodos que implementam os actionListeners
	 */

	private AbstractAction get_onOffAct() {
		AbstractAction onOffAct = null;
		if (onOffAct == null) {
			onOffAct = new AbstractAction("On/Off", null) {
				public void actionPerformed(ActionEvent evt) {
					if (power==false) {
						onOff.setSelected(true);
						power = true;
						ligado = robot.OpenNXT(nomeRobot);
						logTf.setText("O Robot está ON");
					} else {
						onOff.setSelected(false);					
						robot.CloseNXT();
						ligado = false;
						power = false;
						logTf.setText("O Robot está OFF");
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
				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.Reta(distancia);
						robot.Parar(false);
					}
					if (debug && power) {
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
				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.CurvarEsquerda(raio, angulo);
						robot.Parar(false);
					}
					if (debug && power) {
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
				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						System.out.println(-distancia);
						robot.Reta(-distancia);
						robot.Parar(false);
					}
					if (debug && power) {
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
				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.CurvarDireita(raio, angulo);
						robot.Parar(false);
					}
					if (debug && power) {
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
				public void actionPerformed(ActionEvent evt) {
					if (ligado){
						robot.Parar(true);
					}
					if (debug && power) {
						logTf.setText("Parou");
					} 
				}
			};
		}
		return pararAct;
	}
}