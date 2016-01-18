package t5;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDesktopPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JTextPane;
import java.awt.Color;

/** 
 * A Classe Player_Gui implementa a Gui do Player 
 */
public class Player_Gui extends JFrame implements Runnable {

	private static final long serialVersionUID = 6390501288654510951L;
	
	// variaveis de comunicação
	private JPanel contentPane;
	private JButton playButton, recButton, rewindButton, stopButton;
	
	// imagems
	private ImageIcon playIcon, rewindIcon;
	private MemMap bufferCom;
	
	// textPane para debug
	private JTextPane textPane;

	/**
	 * MAIN
	 */
	public static void main(String[] args) {
		try {
			Player_Gui frame = new Player_Gui();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//contrutor
	public Player_Gui() {
		initGUI();
		bufferCom = new MemMap();
		bufferCom.setRecState(0);
		bufferCom.setPlayState(0);
		bufferCom.setRewindState(0);
		bufferCom.setAlert(0);
		bufferCom.setStop(0);
		bufferCom.setExecTime(0);
	}

	/**
	 * metodo run que apenas verifica se a Gui do robot emite um alerta
	 * de fim de reprodução para resetar visualmente os botões do player
	 */
	@Override
	public void run() {
		while (true){
			try {
				if (bufferCom.lerAlert()==1){
					playButton.setIcon(playIcon);
					rewindButton.setIcon(rewindIcon);
					recButton.setEnabled(true);
					rewindButton.setEnabled(true);
					playButton.setEnabled(true);
					bufferCom.setAlert(0);
				}
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inicializa a Gui do Player
	 */
	private void initGUI() {
		//tem o cuidado de fechar o canal de comunicacao quando a janela é fechada.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				bufferCom.fecharCanal();
				System.exit(0);
			}
		} );
		setBounds(100, 100, 506, 437);
		setFont(new Font("Consolas", Font.PLAIN, 14));
		setTitle("T5 - João Vaz - 40266 - Robot Player");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		ImageIcon backgroundIcon = new ImageIcon(Player_Gui.class.getResource("/t5/images/LEGOWallpaper.jpg"));
		Image imgtemp = backgroundIcon.getImage();
		Image backgroundImage = imgtemp.getScaledInstance(480, 300, Image.SCALE_SMOOTH);
		backgroundIcon = new ImageIcon(backgroundImage);
		setContentPane(contentPane);

		JDesktopPane desktopPane = new JDesktopPane();
		contentPane.add(desktopPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(250, 235, 215));
		desktopPane.setLayer(panel, 0);
		panel.setBorder(null);
		panel.setOpaque(false);
		panel.setBounds(0, 300, 480, 61);
		desktopPane.add(panel);
		desktopPane.setOpaque(false);
		JLabel picLabel = new JLabel(backgroundIcon);
		picLabel.setBounds(0, 0, 480, 300);
		desktopPane.add(picLabel);
		picLabel.setToolTipText("LEGO MINDSTORMS Logo");

		recButton = new JButton();
		recButton.setSelected(false);
		recButton.setToolTipText("Gravar trajetoria");
		recButton.setBounds(36, 5, 48, 55);
		ImageIcon recIcon = new ImageIcon(Player_Gui.class.getResource("/t5/images/record-48_black.png"));
		ImageIcon recRedIcon = new ImageIcon(Player_Gui.class.getResource("/t5/images/record-48.png"));
		panel.setLayout(null);
		recButton.setOpaque(false);
		recButton.setContentAreaFilled(false);
		recButton.setBorderPainted(false);
		recButton.setFocusPainted(false);
		recButton.setIcon(recIcon);
		panel.add(recButton);
		
		/**
		 * Implementa um actionListener para o botão de Rec
		 */
		recButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (bufferCom.lerRecState()==0){
					bufferCom.setRecState(1);
					recButton.setIcon(recRedIcon);
					playButton.setEnabled(false);
					rewindButton.setEnabled(false);
					textPane.setText("Rec Mode ON");
				} else{
					bufferCom.setRecState(0);
					recButton.setIcon(recIcon);
					playButton.setEnabled(true);
					rewindButton.setEnabled(true);
					textPane.setText("");
				}

			}
		});

		playButton = new JButton();
		playButton.setToolTipText("Reproduzir trajetória");
		playButton.setBounds(165, 5, 48, 55);
		playIcon =new ImageIcon(Player_Gui.class.getResource("/t5/images/play-2-48.png"));
		ImageIcon playBlueIcon =new ImageIcon(Player_Gui.class.getResource("/t5/images/play-2-48_rb.png"));
		playButton.setIcon(playIcon);
		playButton.setOpaque(false);
		playButton.setContentAreaFilled(false);
		playButton.setBorderPainted(false);
		playButton.setFocusPainted(false);
		panel.add(playButton);
		
		/**
		 * Implementa um actionListener para o botão de PLay
		 */
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (bufferCom.lerPlayState()==0){
					bufferCom.setPlayState(1);
					playButton.setIcon(playBlueIcon);
					recButton.setEnabled(false);
					rewindButton.setEnabled(false);
					textPane.setText("Play Mode ON");
				} else{
					textPane.setText("Press Stop Button to Interrupt Robot");
				}
			}
		});

		rewindButton = new JButton();
		rewindButton.setToolTipText("Reproduzir a trajetória inversa");
		rewindButton.setBounds(276, 5, 48, 55);
		rewindIcon = new ImageIcon(Player_Gui.class.getResource("/t5/images/rewind-48.png"));
		ImageIcon rewindBlueIcon =new ImageIcon(Player_Gui.class.getResource("/t5/images/rewind-48_rb.png"));
		rewindButton.setIcon(rewindIcon);
		rewindButton.setOpaque(false);
		rewindButton.setContentAreaFilled(false);
		rewindButton.setBorderPainted(false);
		rewindButton.setFocusPainted(false);
		panel.add(rewindButton);
		
		/**
		 * Implementa um actionListener para o botão de RW
		 */
		rewindButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (bufferCom.lerRewindState()==0){
					bufferCom.setRewindState(1);
					rewindButton.setIcon(rewindBlueIcon);
					playButton.setEnabled(false);
					recButton.setEnabled(false);
					textPane.setText("RW Mode ON");
				} else{
					textPane.setText("Press Stop Button to Interrupt Robot");
				}
			}
		});

		stopButton = new JButton();
		stopButton.setToolTipText("Parar");
		stopButton.setBounds(396, 5, 48, 55);
		ImageIcon stopIcon =new ImageIcon(Player_Gui.class.getResource("/t5/images/stop-48.png"));
		ImageIcon stopBlueIcon =new ImageIcon(Player_Gui.class.getResource("/t5/images/stop-48_rb.png"));
		stopButton.setIcon(stopIcon);
		stopButton.setPressedIcon(stopBlueIcon);
		stopButton.setOpaque(false);
		stopButton.setContentAreaFilled(false);
		stopButton.setBorderPainted(false);
		stopButton.setFocusPainted(false);
		panel.add(stopButton);
		
		/**
		 * Implementa um actionListener para o botão de Stop
		 */
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textPane.setText("");
					if (bufferCom.lerPlayState() == 1){
						bufferCom.setStop(1);
						bufferCom.setPlayState(0);
						// verifica se o robot esta em execução e se sim pausa a gui
						// para permitir ao robot acabar o seu movimento
						if (bufferCom.lerExecTime() != 0){
							textPane.setText("Concluindo Movimento Robot --> " + 
									((float)bufferCom.lerExecTime())/1000.0+" seg restantes");
							//força actualização do textpane
							textPane.update(textPane.getGraphics());
							Thread.sleep(bufferCom.lerExecTime());
						}
						playButton.setIcon(playIcon);
						recButton.setEnabled(true);
						rewindButton.setEnabled(true);
						bufferCom.setStop(0);
					} else if (bufferCom.lerRewindState() == 1){
						bufferCom.setStop(1);
						bufferCom.setRewindState(0);
						if (bufferCom.lerExecTime() != 0){
							textPane.setText("Concluindo Movimento Robot --> " + 
									((float)bufferCom.lerExecTime())/1000.0+" seg restantes");
							textPane.update(textPane.getGraphics());
							Thread.sleep(bufferCom.lerExecTime());
						}
						rewindButton.setIcon(rewindIcon);
						playButton.setEnabled(true);
						recButton.setEnabled(true);
					} else if (bufferCom.lerRecState() == 1) { 
						bufferCom.setRecState(0);
						recButton.setIcon(recIcon);
						playButton.setEnabled(true);
						rewindButton.setEnabled(true);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} finally{textPane.setText("");}
			}
		});
		textPane = new JTextPane();
		textPane.setFont(new Font("Consolas", Font.PLAIN, 16));
		textPane.setBackground(new Color(255, 255, 255));
		textPane.setBounds(10, 361, 470, 27);
		desktopPane.add(textPane);
	}
}