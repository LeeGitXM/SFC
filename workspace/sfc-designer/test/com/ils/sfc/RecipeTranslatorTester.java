package com.ils.sfc;

import java.io.InputStream;

import javax.swing.JFrame;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.RecipeDataTranslator;
import com.ils.sfc.designer.recipeEditor.RecipeBrowserPane;


public class RecipeTranslatorTester {
	public static void main(String[] args) {
		try {
			String file1 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-1/TEST-UNIT-PROCEDURE-1.xml";
			String file2 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-2/TEST-UNIT-PROCEDURE-2.xml";
			String file3 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-3/TEST-UNIT-PROCEDURE-3.xml";
			String file4 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-4/TEST-UNIT-PROCEDURE-4.xml";
			String file5 = "C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-5/TEST-UNIT-PROCEDURE-5.xml";
			String testFile = file4;
			InputStream in = new java.io.FileInputStream(testFile);
			RecipeDataTranslator rdTranslator = new RecipeDataTranslator(in);
			Data data = rdTranslator.G2ToData();
			for(String errMsg: rdTranslator.getErrors()) {
				System.out.println(errMsg);
			}
			in.close();
			JFrame frame = new JFrame();
			frame.setTitle(testFile);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,800);
			RecipeBrowserPane browser = new RecipeBrowserPane(data);
			frame.getContentPane().add(browser);
			frame.setVisible(true);			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
