package Trab2;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Gui2 extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTextField tfProcesso;
	private JTextField tfTerminou;
	private JTextField tfProcessActivos; 

	ProcessManager gestor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Gui2 frame = new Gui2();
			frame.setVisible(true);
			frame.setResizable(false);
			frame.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true){
			try {
				//pausa a execução por 200 ms
				Thread.sleep(200);
				String localpath = gestor.verifyTermination();
				tfProcessActivos.setText(Integer.toString(gestor.taskmanager.size()));
				if (localpath != null) {
					tfTerminou.setText(localpath);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public Gui2() {
		setResizable(false);
		gestor = new ProcessManager();
		initGUI();
	}
	private void initGUI() {
		
		JButton btnLaunch;
		
		//JLabels
		JLabel lblProcesso;
		JLabel lblProcessoActivo;
		JLabel lblTerminou;
		
		try {
			setTitle("Gestor de Processos");
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					System.exit(0);
				}
			});
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 520, 209);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			{
				lblProcesso = new JLabel("Processo");
				lblProcesso.setFont(new Font("Tahoma", Font.BOLD, 11));
				lblProcesso.setBounds(10, 31, 62, 14);
				contentPane.add(lblProcesso);
			}
			{
				lblProcessoActivo = new JLabel("N\u00BA Processos Activos");
				lblProcessoActivo.setFont(new Font("Tahoma", Font.BOLD, 11));
				lblProcessoActivo.setBounds(10, 96, 127, 14);
				contentPane.add(lblProcessoActivo);
			}
			{
				lblTerminou = new JLabel("Terminou");
				lblTerminou.setFont(new Font("Tahoma", Font.BOLD, 11));
				lblTerminou.setBounds(10, 161, 54, 20);
				contentPane.add(lblTerminou);
			}
			{
				tfProcesso = new JTextField();
				tfProcesso.setAction(get_tfProcessoAct());
				tfProcesso.setBounds(68, 31, 199, 20);
				contentPane.add(tfProcesso);
				tfProcesso.setColumns(10);
			}
			{
				tfTerminou = new JTextField();
				tfTerminou.setEditable(false);
				tfTerminou.setBounds(68, 161, 446, 20);
				contentPane.add(tfTerminou);
				tfTerminou.setColumns(10);
			}
			{
				tfProcessActivos = new JTextField();
				tfProcessActivos.setEditable(false);
				tfProcessActivos.setBounds(139, 93, 22, 20);
				contentPane.add(tfProcessActivos);
				tfProcessActivos.setColumns(10);

			}
			{
				btnLaunch = new JButton("");
				btnLaunch.setAction(get_btnLaunchAct());
				btnLaunch.setToolTipText("Click to Launch...");
				btnLaunch.setBorderPainted(false);
				btnLaunch.setCursor(new Cursor(Cursor.HAND_CURSOR));
				btnLaunch.setIcon(new ImageIcon(Gui2.class.getResource("/img/launch_button_optimized.jpg")));
				btnLaunch.setBounds(280, 0, 224, 153);
				contentPane.add(btnLaunch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Metodos que implementam os actionListeners
	 */

	private AbstractAction get_btnLaunchAct() {
		AbstractAction btnLaunchAct = null;
		if (btnLaunchAct == null) {
			btnLaunchAct = new AbstractAction("Launch", null) {
				public void actionPerformed(ActionEvent evt) {
					String path = tfProcesso.getText();
					gestor.lancaProcesso(path);
					tfProcessActivos.setText(Integer.toString(gestor.taskmanager.size()));
				}
			};
		}
		return btnLaunchAct;
	}

	private AbstractAction get_tfProcessoAct() {
		AbstractAction tfProcessoAct = null;
		if (tfProcessoAct == null) {
			tfProcessoAct = new AbstractAction("", null) {
				public void actionPerformed(ActionEvent evt) {
					String path = tfProcesso.getText();
					gestor.lancaProcesso(path);
					tfProcessActivos.setText(Integer.toString(gestor.taskmanager.size()));
				}
			};
		}
		return tfProcessoAct;
	}
}