package com.ils.sfc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.RecipeDataTranslator;
import com.ils.sfc.designer.recipeEditor.RecipeBrowserPane;
import com.inductiveautomation.ignition.common.config.BasicProperty;


public class RecipeTranslatorTester {
	static String file1 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-1/TEST-UNIT-PROCEDURE-1.xml";
	static String file2 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-2/TEST-UNIT-PROCEDURE-2.xml";
	static String file3 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-3/TEST-UNIT-PROCEDURE-3.xml";
	static String file4 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-4/TEST-UNIT-PROCEDURE-4.xml";
	static String file5 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-5/TEST-UNIT-PROCEDURE-5.xml";
	
	public static void testDOM() throws Exception {
		File fXmlFile = new File(file4);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		NodeList nList = doc.getElementsByTagName("block");
		for (int temp = 0; temp < nList.getLength(); temp++) {			 
			Node nNode = nList.item(temp);	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
				Element blockElement = (Element) nNode;
				RecipeDataTranslator trans = new RecipeDataTranslator(blockElement);
				List<Data> recipeData = trans.DOMToData();
				for(String errMsg: trans.getErrors()) {
					System.out.println(errMsg);
				}

				JFrame frame = new JFrame();
				frame.setTitle(fXmlFile.getName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800,800);
				RecipeBrowserPane browser = new RecipeBrowserPane(recipeData);
				frame.getContentPane().add(browser);
				frame.setVisible(true);			
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			//testDOM();

			BasicProperty<String> prop1 = new BasicProperty<String>("key", String.class);
			BasicProperty<String> prop2 = new BasicProperty<String>();
			prop2.setName("key");
			System.out.println(prop1.equals(prop2));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
