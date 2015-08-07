package com.att.compliance;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestBasicParse1 {

	@Before
	public void getReqs() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<TextHolder> getReqsKey(String file2) {
		String txtFileName = file2.replace(".docx", ".txt");
		txtFileName = txtFileName.replace(".doc", ".txt");
		File f = new File(txtFileName);
		List<TextHolder> thAry = new ArrayList<TextHolder>();

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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {

			}
		}
		return thAry;
	}

	@Test
	public void testParse1() throws IOException, InterruptedException,
			DocumentException, ParserConfigurationException, SAXException {

		String file = "testdocx.docx";

		Collection<TextHolder> reqs = new DocumentParser().parse(DocReader
				.getFileContents(new File(file)));
		List<TextHolder> reqsKey = getReqsKey(file);
		assertEquals(reqs, reqsKey);
	}
}
