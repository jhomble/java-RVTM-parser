package com.att.compliance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.att.compliance.FileUtils;
/**
 * This class is used to handle the UI for selecting files, as well as reading
 * in the PDF or Word document.
 * 
 * @author Eric Woods, Mark Sholund
 * @version 1.0
 * 
 */
public class DocReader {

	private static final Logger logger = LoggerFactory
			.getLogger(DocReader.class);

	private static final String DIVIDER = "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";

	/**
	 * This method launches a VB script that takes a filename (DOC, or DOCX mime
	 * type), and saves it as a .pdf file
	 * 
	 * @param filename
	 *            - The name of the file.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static File saveWordAsPDF(File file) throws InterruptedException,
			IOException {

		String scriptName = "wordtopdf.vbs";
		File tempFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + scriptName);
		tempFile.deleteOnExit();

		InputStream is = null;
		OutputStream os = null;
		try {
			is = DocReader.class.getClassLoader().getResourceAsStream(
					scriptName);
			os = new FileOutputStream(tempFile);
			IOUtils.copy(is, os);
		} catch (IOException e) {
			logger.info("Exception while copying " + scriptName, e);
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}

		Runtime.getRuntime()
				.exec(String.format("wscript %s %s",
						tempFile.getAbsolutePath(), file.getAbsolutePath()))
				.waitFor();

		return new File(file.getAbsolutePath().replace(".docx", ".pdf")
				.replace(".doc", ".pdf"));

	}

	/**
	 * This method gets the contents of a word .doc file
	 * 
	 * @param docName
	 *            - Name of the document.
	 * @return Returns the contents of the file, in plain text.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static String getWordDocContents(File file)
			throws InterruptedException, IOException {

		File newFile = saveWordAsPDF(file);
		return getPDFContents(newFile, true);
	}

	private static String getPDFContents(File file) throws IOException {
		return getPDFContents(file, false);
	}

	/**
	 * Method to read text from PDF
	 * 
	 * @param filename
	 *            - The name of the document.
	 * @param toDelete
	 *            - Whether the PDF document should be deleted after reading in
	 *            or not.
	 * @return Returns the contents of the file, in plain text.
	 * @throws IOException
	 */
	private static String getPDFContents(File file, boolean toDelete)
			throws IOException {
		PDDocument document = null;
		if (toDelete) {
			file.deleteOnExit();
		}
		document = PDDocument.load(file);
		PDFTextStripper s = new PDFTextStripper("UTF-8");
		String content = "";
		content = s.getText(document);
		document.close();
		return content;

	}

	/**
	 * Decides which method to call based on the filename
	 * 
	 * @param filename
	 *            -The name of the file.
	 * @return Returns the contents of the file, in plain text.
	 * @throws IOException
	 * @throws FileTypeException
	 *             if the filetype is not supported.
	 * @throws InterruptedException
	 */
	static String getFileContents(File file) throws
			IOException, InterruptedException {
		String text = "";
		if (file == null) {
			throw new IOException("No File Selected");
		}

		String mimeType = FileUtils.getMimeType(file);

		logger.info("Detected MimeType: {}", mimeType);

		if (FileUtils.PDF_MIMETYPE.equals(mimeType)) {
			text = getPDFContents(file);
		} else if (FileUtils.DOCX_MIMETYPE.equals(mimeType)
				|| FileUtils.DOC_MIMETYPE.equals(mimeType)) {
			text = getWordDocContents(file);
		} else if (mimeType != null
				&& mimeType.startsWith(FileUtils.TXT_MIMETYPE)) {

			// for 1.6
			//byte[] encoded = FileUtils.readFileToByteArray(file);

			// for 1.7
			byte[] encoded = Files.readAllBytes(file.toPath());

			text = new String(encoded, "UTF-8");
			
			
			
		} else {
			throw new IOException("File Type \"" + mimeType
					+ "\" is Not Supported");
		}

		logger.debug("\n{}\n{}\n{}", new Object[] { DIVIDER, text, DIVIDER });
		
//		int i = text.length()-119;
//		char c = text.charAt(i);
//		System.out.print(Double.toHexString(c));
//		System.out.print(c);
		
		
		

		return text;
	}
}