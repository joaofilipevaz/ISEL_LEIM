package Projectos.aula_pratica01;

import java.util.Scanner;

import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NavXML {

	public static void main(String[] args) {

		try {
			String filepath = "src/Projectos/aula_pratica01/Ementa.xml";
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(filepath);
			Element root = document.getDocumentElement();
			System.out.println("Elemento Raíz :" + root.getNodeName() + "\n");

			NodeList lista_ing = document.getElementsByTagName("Ingredientes");
			NodeList lista_item = document.getElementsByTagName("Items");
			NodeList lista_ementa = document.getElementsByTagName("Ementas");

			System.out.println("------------------------------------------------------------------------------");
			System.out.println("Ingredientes: ");
			lista(lista_ing);

			System.out.println("Items: ");
			lista(lista_item);

			System.out.println("Ementas: ");
			lista(lista_ementa);

			Scanner keyboard = new Scanner(System.in);

			System.out.println("Escolha uma das opções:\n");
			System.out.print("1 - Acrescentar um item na ementa.   ");
			System.out.println("2 - Atualizar o preço de um item na ementa.");
			System.out.print("3 - Remover um item da ementa.       ");
			System.out.println("4 - Procurar os itens que tenham um determinado ingrediente.");

			int opcao = keyboard.nextInt();
			int index;

			// 1 - Acrescentar um item na ementa.
			if (opcao == 1) {

				Node node_item = document.getElementsByTagName("Items").item(0);

				Element it = document.createElement("item");
				node_item.appendChild(it);
				Attr at = document.createAttribute("idItem");
				System.out.print("Qual o Id que quer atribuir ao item? -> ");
				String idItem = keyboard.next();
				at.setValue(idItem);
				it.setAttributeNode(at);
				Attr at2 = document.createAttribute("tipo");
				System.out.print("Qual o tipo que quer atribuir ao item? -> ");
				String tipo = keyboard.next();
				at2.setValue(tipo);
				it.setAttributeNode(at2);

				Element nome_item = document.createElement("nome");
				it.appendChild(nome_item);
				System.out.print("Qual o nome que quer atribuir ao item? -> ");
				String nome = keyboard.next();
				keyboard.nextLine();
				nome_item.appendChild(document.createTextNode(nome));

				Element ing_item = document.createElement("ingrediente");
				it.appendChild(ing_item);
				Attr at3 = document.createAttribute("refIngrediente");
				System.out.print("Qual o Id de referencia que quer atribuir ao item? -> ");
				String Idref = keyboard.next();
				at3.setValue(Idref);
				ing_item.setAttributeNode(at3);
			}

			// 2 - Atualizar o preço de um item na ementa.
			if (opcao == 2) {

				NodeList nList_ementa = document.getElementsByTagName("ementa");

				System.out.println("Seleccione o nº do prato em que deseja alterar o preço? -> \n");
				// System.out.println(node_ementa);

				// lista as ementas
				getListItem(nList_ementa);

				index = keyboard.nextInt();

				Node preco_imenta = document.getElementsByTagName("refItem").item(index - 1);

				NamedNodeMap preco = preco_imenta.getAttributes();
				Node nodeAttr = preco.getNamedItem("preco");
				nodeAttr.getFirstChild();
				System.out.print("Introduza o novo preço? -> ");
				String novo_preco = keyboard.next();
				nodeAttr.setTextContent(novo_preco);

				System.out.println("Preço atualizado!");
			}

			// 3 - Remover um item da ementa.
			if (opcao == 3) {

				NodeList nList_item = document.getElementsByTagName("item");

				System.out.println("Items disponíveis para remoção: \n");

				// lista os items
				getListItem(nList_item);

				System.out.println("\nSeleccione o nº do item da ementa que pretende remover? -> ");

				index = keyboard.nextInt();

				String nome_elemento = "item";
				Node elem = document.getElementsByTagName("Items").item(0);
				Node elemRemove = document.getElementsByTagName(nome_elemento).item(index - 1);
				String elemText = elemRemove.getTextContent();
				elem.removeChild(elemRemove);
				System.out.println("Elemento " + nome_elemento + " removido: \n" + elemText);
			}

			// 4 - Procurar os itens que tenham um determinado ingrediente.
			if (opcao == 4) {

				System.out.println("\nIngredientes disponíveis para consulta de items: ");
				System.out.println("\nSeleccione o nº do ingrediente a pesquisar? \n");

				NodeList nomeIng = document.getElementsByTagName("ing");

				getListItem(nomeIng);

				index = keyboard.nextInt();

				Node item = document.getElementsByTagName("item_nome").item(index - 1);
				String itemNome = item.getTextContent();
				System.out.println("Nome do item com o ingrediente pesquisado: " + itemNome);
			}
			keyboard.close();

			outputXML(document, "outFile.xml");

			System.out.println("\nFinalizado!");
			System.out.println("--------------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.print("Erro ao analisar o documento XML.");
		}
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
	
	// método auxiliar para listar elementos
	public static void getListItem(NodeList nlist) {

		Node nNode = null;
		String nomeList = null;
		for (int i = 0; i < nlist.getLength(); i++) {
			nNode = nlist.item(i);
			nomeList = nNode.getTextContent().trim();
			System.out.println((i + 1) + " -> " + nomeList);
		}
	}
	
	// método auxiliar
	public static void trimWhitespace(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			String childString = child.getTextContent().trim();
			if (child.getNodeType() == Node.TEXT_NODE) {
				child.setTextContent(childString);
				System.out.println(childString);
			}
			trimWhitespace(child);
		}
	}

	public static void outputXML(Document document, String outFilename) {
		try {

			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);

			OutputStream out = new FileOutputStream(outFilename);
			StreamResult result = new StreamResult(out/* System.out */);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
