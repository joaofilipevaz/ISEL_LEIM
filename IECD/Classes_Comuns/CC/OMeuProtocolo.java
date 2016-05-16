package cc;

import java.net.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;

public class OMeuProtocolo {

	private int passo = 1;

	public String processInput(String entrada) throws SAXException, IOException, ParserConfigurationException {
		
		String saida = null;
		try {
			String filepath = "src/Projecto_ClienteServidor/Ementa_2.xml";
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(filepath);
			Element root = document.getDocumentElement();
			//System.out.println("Elemento Raíz :" + root.getNodeName() + "\n");

			NodeList lista_ing = document.getElementsByTagName("Ingredientes");
			NodeList lista_item = document.getElementsByTagName("Items");
			NodeList lista_ementa = document.getElementsByTagName("Ementas");
			
			int index = 0;
			//String novo_preco = null;
			Node preco_imenta = document.getElementsByTagName("refItem").item(index - 1);

			NamedNodeMap preco = preco_imenta.getAttributes();
			Node nodeAttr = preco.getNamedItem("preco");
			nodeAttr.getFirstChild();
			String novo_preco = null;
			nodeAttr.setTextContent(novo_preco);
			
			
			if (passo == 1) {
				saida = novo_preco;
				passo = 2;
			/*} else if (passo == 2) {
				if (entrada.equalsIgnoreCase("1")) {
					saida = getXml("matricula");
					passo = 3;
				} else
					saida = "O veículo não se encontra no estacionamento.";
			} else if (passo == 3) {
				if (entrada.equalsIgnoreCase("2")) {
					saida = getXml("lugaresOcupados");
					passo = 4;
				} else
					saida = "Não temos essa informação.";
			} else if (passo == 4) {
				if (entrada.equalsIgnoreCase("3")) {
					saida = getXml("marca");
					passo = 5;
				} else
					saida = "Mais alguma informação?";
			} else if (passo == 5) {
				if (entrada.equalsIgnoreCase("4")) {
					saida = "Ok. Terminado.";
					passo = 6;
				} else if (entrada.equalsIgnoreCase("Sim")) {
					saida = "Qual a informação?";
				} else
					passo = 6;*/
			} else if (passo == 2) {
				if (entrada.equalsIgnoreCase("Terminado.")) {
					saida = "Terminado!";
					passo = 1; // Voltar demonstração ao início
				} else {
					saida = "Erro"; // Esta mensagem não deverá aparecer
				}
			}
			return saida;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.print("Erro ao analisar o documento XML.");
		}
		return saida;
	}

	// Métodos auxiliares
	public static void lista(NodeList list) {

		Node elem = null;
		String elem_nome = null;
		System.out.println("------------------------------------------------------------------------------");
		for (int i = 0; i < list.getLength(); i++) {
			elem = list.item(i);
			// trimWhitespace(elem);
			elem_nome = elem.getTextContent();
			System.out.println(elem_nome);
		}
		System.out.println("------------------------------------------------------------------------------");
	}
	
	
}
