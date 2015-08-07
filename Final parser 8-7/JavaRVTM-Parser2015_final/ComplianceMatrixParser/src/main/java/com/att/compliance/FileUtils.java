package com.att.compliance;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.nio.file.Files;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	public static final String PDF_MIMETYPE = "application/pdf";
	public static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String DOC_MIMETYPE = "application/msword";
	public static final String TXT_MIMETYPE = "text/plain";
	public static final String LAST_USED_FOLDER = "lastFolder";
	public static final Preferences PREFERENCES = Preferences.userRoot().node(
			"Compliance Matrix");
	private static final Logger logger = LoggerFactory
			.getLogger(FileUtils.class);

	public FileUtils() {
	};

	public static void open(File file) {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(file);
			}
		} catch (IOException e) {
			logger.error("Error opening matrix file", e);
		}

	}

	/**
	 * Takes a filename and returns the parsed list of TextHolder objects of the
	 * file contents
	 * 
	 * @param fileName
	 *            - The text fileName
	 * @return Returns an ArrayList of Strings of size 4 representing in order:
	 *         minheading, maxheading, keywords(separated by 2 spaces),
	 *         badchars(separated by 2 spaces)
	 */
	public static ArrayList<String> getConfig(String fileName) {

		// This will reference one line at a time
		String line = null;

		ArrayList<String> configarray = new ArrayList<String>();

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader("config\\" + fileName
					+ ".txt");

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				configarray.add(line);
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, "Unable to open file '"
					+ fileName + "'");
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Error reading file '"
					+ fileName + "'");
		}

		return configarray;
	}

	// Given the parameters, create a text file with the information in the
	// config folder
	public static void saveConfigs(String fileName, String minHeading,
			String maxHeading, Collection<String> keywords, char[] badchars) {
		// Create the writer and stream
		BufferedWriter out = null;
		if(fileName.compareTo("Default") == 0){
			JOptionPane.showMessageDialog(null, "Cannot overwrite default");
			return;
		}
		try {
			FileWriter fstream = new FileWriter("config\\" + fileName + ".txt");
			out = new BufferedWriter(fstream);
			// Add each parameter
			out.write(minHeading + "\n");
			out.write(maxHeading + "\n");
			for (String str : keywords) {
				out.write(str + "  ");
			}
			out.write("\n");
			
			for (char c : badchars) {
				
				out.write(c + "  ");
			}
		} // error
		catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Error reading the custom config file: " + e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void deleteConfig(String config) {
		// First check if a configuration is chosen
		if (config == null)
			JOptionPane.showMessageDialog(null, "No configuration selected");
		// Check if trying to delete default
		else if (config.compareTo("Default") == 0)
			JOptionPane.showMessageDialog(null, "Cannot delete default");
		else {
			// Try to delete the file in the config file
			config = config + ".txt";
			try {
				File file = new File("config\\" + config);
				// Delete the file and re-populate the config combo box
				file.delete();
				JOptionPane.showMessageDialog(null, config + " was deleted");
			} catch (Exception x) {
				JOptionPane.showMessageDialog(null, config
						+ " Unable to delete");
			}
		}
	}

	// Returns the config files
	public static File[] getConfigFiles() {

		File folder = new File("config");
		return folder.listFiles();
	}

	// Determines file type
	public static String getMimeType(File file) throws IOException {

		String versionString = System.getProperty("java.version");
		float versionNum = Float.parseFloat(versionString.substring(0, 3));
		logger.info("Detected Java Version:  {}", versionNum);
		 if (versionNum >= 1.7) {
			 return Files.probeContentType(file.toPath());
		 }

		String mimeType = null;

		// Java 1.6 - go by extension
//		String fileName = file.getName();
//		if (fileName.endsWith(".pdf")) {
//			mimeType = CompilanceMatrixDriver.PDF_MIMETYPE;
//		} else if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
//			mimeType = CompilanceMatrixDriver.DOC_MIMETYPE;
//		} else if (fileName.endsWith(".txt")) {
//			mimeType = CompilanceMatrixDriver.TXT_MIMETYPE;
//		}

		return mimeType;
	}

	public static void handleException(Exception e) {
		logger.error("Exception thrown", e);
		JOptionPane.showMessageDialog(null, e.toString(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}