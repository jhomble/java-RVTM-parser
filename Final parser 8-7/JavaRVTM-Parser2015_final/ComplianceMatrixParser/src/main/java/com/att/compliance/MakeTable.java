package com.att.compliance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Class that handles the exporting of requirements/headings to the compliance
 * matrix.
 * 
 * @author Eric Woods, Kelsey Terrell, Mark Sholund
 * @version 2.0
 */

public class MakeTable {
	/**
	 * Populates a compliance matrix with the requirements and headings given.
	 * Prompts the user to select a file name/location.
	 * 
	 * @param reqList
	 *            - List of requirements/headers from the requirements document.
	 * @param propList
	 *            - List of requirements/headers from the proposal document.
	 * @param needles
	 *            - List of keywords to be parsed for.
	 */
	static int reqnumb = 1;
	public static void make(Collection<TextHolder> reqList,
			Collection<String> needles) throws IOException {

		File file = null;
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Specify Output File");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"DOCX Files", "docx");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				XWPFDocument doc = createMatrix(reqList, needles);

				OutputStream os = null;
				try {
					os = new FileOutputStream(file);
					doc.write(os);
				} catch (Exception e) {

				} finally {
					if (os != null) {
						os.close();
					}
				}
				FileUtils.open(file);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static XWPFDocument createMatrix(
			Collection<TextHolder> requirements, Collection<String> keywords) {
		XWPFDocument doc = new XWPFDocument();
		XWPFTable table = doc.createTable();
		XWPFTableRow row0 = table.getRow(0);
		XWPFTableCell cell0 = row0.getCell(0);

		String[] headers = new String[] { "Req. number", "Project Name Section Requirements",
				"Page No. ", "Classification"};
		XWPFParagraph p = null;
		XWPFRun r = null;

		// Sets the size of the table
		//10072
		table.getCTTbl().addNewTblPr().addNewTblW()
				.setW(BigInteger.valueOf(10072));

		// Setting the header cell's text, text color and background color
		for (int i = 0; i < headers.length; i++) {
			p = cell0.getParagraphs().get(0);
			r = p.createRun();
			// Background color--blue
			cell0.setColor("137aa0");
			r.setText(headers[i]);
			// Text color--white
			r.setColor("FFFFFF");
			if (i != 3) {
				cell0 = row0.createCell();
			}
		}

		XWPFTableRow newRow;

		// Determines which list is bigger and sets number to the largest size

		for (TextHolder t : requirements) {
			newRow = table.createRow();

			// Checks to make sure there is something left in the list and that
			// it is not a header
			if (t != null && !t.isHeader()) {
				XWPFParagraph para = newRow.getCell(1).getParagraphs().get(0);
				// Sets the size of the first two columns in the new row
				newRow.getCell(0).getCTTc().addNewTcPr().addNewTcW()
					.setW(BigInteger.valueOf(500));
				newRow.getCell(1).getCTTc().addNewTcPr().addNewTcW()
						.setW(BigInteger.valueOf(7000));
				newRow.getCell(2).getCTTc().addNewTcPr().addNewTcW()
						.setW(BigInteger.valueOf(900));
				newRow.getCell(3).getCTTc().addNewTcPr().addNewTcW()
				.setW(BigInteger.valueOf(2000));

				XWPFRun run = para.createRun();
				run.setText(t.section.trim());
				// Since it isn't a header, we want to break after the section
				// number
				
				newRow.getCell(3).setText(t.getClassification());
				//new run to center the req number
				XWPFParagraph para2 = newRow.getCell(0).getParagraphs().get(0);
				XWPFRun run2 = para2.createRun();
				run2.setText(Integer.toString(reqnumb));
				para2.setAlignment(ParagraphAlignment.CENTER);
				//newRow.getCell(0).setText(Integer.toString(reqnumb));
				
				reqnumb++;
				run.addBreak();
				// Adds section textS
				
				
				//This is to populate the word doc with the specific requirements. Every instance of a keyword will be bolded.
				//A map was used to find the position of all key words 
				TreeMap<Integer, String> map = new TreeMap<Integer, String>();
				//int i1 = 0, i2 = 0, last = 0;
				
				int currind=0, ind = 0;
				for (String s: keywords){
					currind = 0;
					while((ind = t.text.indexOf(s,currind+1)) != -1){
						map.put(ind, s);
						currind = ind;
					}
				}
				int index = 0;
				int curr = 0;
				//The text before a keyword is added, then the bolded keyword, then the text after the keyword. 
				//If there are multiple keywords in the text, this loop will run once for each keyword.
				for (Integer keywordPos : map.keySet()) {
					//System.out.println(map.keySet());
				   index++;
				   String keyword = map.get(keywordPos);
				   run = para.createRun();
				   run.setText(t.text.substring(curr, keywordPos).trim());
				   run = para.createRun();
				   run.setBold(true);
				   run.setText(" " + keyword+ " ");
				   if (index == map.size()){
					   run = para.createRun();
					   run.setText(t.text.substring(keywordPos+keyword.length()).trim());
				   }
				   curr = keywordPos+keyword.length()+1;
				}
				if (map.size() == 0){
					run = para.createRun();
					run.setText(t.text);
				}
				
//				
			} else {
				if (t != null) {
					// The text is a heading
					newRow.getCell(3).setText(t.getClassification());
					newRow.getCell(1).setColor("FFFF00");
					newRow.getCell(1).setText(t.toString());
				}
			}

			if (t != null) {
				// Adds the page number corresponding to that section and
				// centers it
				XWPFParagraph pa = newRow.getCell(2).getParagraphs().get(0);
				pa.setAlignment(ParagraphAlignment.CENTER);
				XWPFRun ru = pa.createRun();
				ru.setText(t.page);
			}
		}
		reqnumb = 1;
		return doc;
	}
}
