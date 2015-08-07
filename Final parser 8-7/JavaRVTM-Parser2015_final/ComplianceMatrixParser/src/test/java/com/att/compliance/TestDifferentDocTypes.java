package com.att.compliance;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestDifferentDocTypes {

	private ArrayList<TextHolder> getReqsKey(String file2) throws IOException {
		String txtFileName = file2.replace(".docx", ".txt");
		txtFileName = txtFileName.replace(".doc", ".txt");
		File f = new File(txtFileName);
		ArrayList<TextHolder> thAry = new ArrayList<TextHolder>();

		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				// Getting headings and requirements from key file
				String[] toks = line.split("--");
				if (toks[0].equals("heading")) {
					thAry.add(new Heading(toks[1], toks[3], toks[2]));
				}
				if (toks[0].equals("requirement")) {
					thAry.add(new Requirement(toks[1], toks[3], toks[2]));
				}
			}
		} finally {
			if (fr != null) {
				fr.close();
			}
			if (br != null) {
				br.close();
			}
		}
		return thAry;
	}

	@Test
	public void testDiffTypes1() throws IOException, InterruptedException,
			DocumentException, ParserConfigurationException, SAXException {

		String fileName1 = "testdocx.docx";
		String fileName2 = "testdoc.doc";

		Collection<TextHolder> reqsDoc = new DocumentParser().parse(DocReader
				.getFileContents(new File(fileName2)));
		Collection<TextHolder> reqsDocX = new DocumentParser().parse(DocReader
				.getFileContents(new File(fileName1)));
		Collection<TextHolder> reqsKey = getReqsKey(fileName1);

		assertEquals(reqsDoc, reqsKey);
		assertEquals(reqsDocX, reqsDoc);
		assertEquals(reqsDocX, reqsKey);
	}
}
