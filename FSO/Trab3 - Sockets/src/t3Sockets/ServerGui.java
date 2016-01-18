package t3Sockets;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

public class ServerGui extends JFrame implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final int NUMEROPORTO= 40266;
	private int estadoLigacao, nRobosEmEspera, posNaFila;
	private boolean clientclosure;

	//estados
	private static int estado;
	private static final int ESPERA= 0;
	private static final int READWRITE= 1;
	private static final int CLOSE= 2;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintStream outStream;
	private BufferedReader inStream;

	private ArrayList<String> uidlist;

	private JTextField cliEspTf;
	private JTextField blueTf;
	private JTextArea debugTf;
	private final Color green = new Color(59,208,29);
	private final Color red = new Color(255,0,45);

	/**
	 * Construtor
	 */
	public ServerGui(){
		try { 
			serverSocket = new ServerSocket(NUMEROPORTO);
		} catch (IOException e) { e.printStackTrace(); }
		estado = ESPERA;
		estadoLigacao = 0;
		nRobosEmEspera = 0;
		uidlist = new ArrayList<String>();
		initServerGui();
		blueTf.setBackground(red);
		cliEspTf.setText(Integer.toString(nRobosEmEspera));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ServerGui frame = new ServerGui();
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			frame.setResizable(false);
			frame.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(true){
			switch (estado) {
			case ESPERA:
				openServer();
				break;			
			case READWRITE:
				read();
				if (clientclosure==false){
					write();
				}
				estado = CLOSE;				
				break;
			case CLOSE:
				closeServer();
				writeDebugTf("Estado da Ligação --> "+ estadoLigacao+"\nAplicações em Espera --> "+ nRobosEmEspera);
				int count = 1;
				for(Iterator<String> uidlistIter = uidlist.iterator(); uidlistIter.hasNext();){
					writeDebugTf("APP "+count+" --> "+uidlistIter.next());
					count++;
				}
				break;
			}
		}
	}

	public void openServer() {
		try {
			writeDebugTf("Em espera");
			clientSocket = serverSocket.accept();
			outStream = new PrintStream(clientSocket.getOutputStream());
			inStream = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
			estado = READWRITE;
			debugTf.setText("");
		} catch (IOException e) {
			writeDebugTf("Server Closed");
		}		
	}

	public void closeServer() {
		try {
			if (inStream != null && outStream != null && clientSocket != null) {
				outStream.close();
				inStream.close();
				clientSocket.close();
				estado = ESPERA;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read() {
		String s = null;
		String t = null;
		while(true){
			try {
				if (inStream.ready()) {
					s = inStream.readLine();
					if (s.equals("QUIT")){
						clientclosure = true;
						t = inStream.readLine();
						estadoLigacao = 0;
						blueTf.setBackground(red);
						uidlist.remove(uidlist.indexOf(t));
					} else {
						clientclosure = false;
						if (!uidlist.contains(s)){
							uidlist.add(s);
						}
						posNaFila = uidlist.indexOf(s)+1;
					}
					nRobosEmEspera = uidlist.size();
					cliEspTf.setText(Integer.toString(nRobosEmEspera));
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}

	public void write() {
		outStream.print(Integer.toString(estadoLigacao)+"\n");
		outStream.print(Integer.toString(posNaFila)+"\n");
		outStream.flush();
		if (posNaFila==1){
			estadoLigacao = 1;
			blueTf.setBackground(green);
		}		
	}

	public void writeDebugTf(String Debug) {
		debugTf.append(Debug+"\n");
	}

	/**
	 * Create the frame.
	 */
	public void initServerGui() {
		setTitle("T3 Server - João Vaz");
		//tem o cuidado de fechar o canal de comunicação quando a janela é fechada.
		//impede o encerramento enquanto está ligado ou em tentativa de ligação
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeServer();
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		} );
		setBounds(100, 100, 324, 264);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		cliEspTf = new JTextField();
		cliEspTf.setEditable(false);
		cliEspTf.setBounds(119, 0, 39, 24);
		contentPane.add(cliEspTf);
		cliEspTf.setColumns(10);

		debugTf = new JTextArea();
		debugTf.setEditable(false);
		debugTf.setBounds(0, 69, 309, 172);
		JScrollPane scroll = new JScrollPane(debugTf);
		scroll.setBounds(new Rectangle(0, 69, 309, 156));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scroll);

		JLabel debugLb = new JLabel("Informa\u00E7\u00E3o de Debug");
		debugLb.setHorizontalAlignment(SwingConstants.CENTER);
		debugLb.setBounds(0, 47, 309, 24);
		contentPane.add(debugLb);

		JLabel bluetoothLb = new JLabel("Bluetooth ");
		bluetoothLb.setHorizontalAlignment(SwingConstants.CENTER);
		bluetoothLb.setBounds(157, 0, 76, 24);
		contentPane.add(bluetoothLb);

		blueTf = new JTextField();
		blueTf.setEditable(false);
		blueTf.setBounds(233, 0, 76, 24);
		contentPane.add(blueTf);
		blueTf.setColumns(10);

		JLabel cliEspLb = new JLabel("Clientes Em Espera");
		cliEspLb.setBounds(0, 0, 112, 24);
		contentPane.add(cliEspLb);
		cliEspLb.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblStartServer = new JLabel("Server State");
		lblStartServer.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		lblStartServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblStartServer.setBounds(0, 25, 309, 24);
		lblStartServer.setForeground(green);
		contentPane.add(lblStartServer);
	}
}