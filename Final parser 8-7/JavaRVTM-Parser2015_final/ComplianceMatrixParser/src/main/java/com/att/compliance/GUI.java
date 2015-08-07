package com.att.compliance;

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListModel;
import com.att.compliance.FileUtils;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JComboBox;
import org.apache.commons.io.FilenameUtils;
import javax.swing.JRadioButton;

public class GUI {

	private JFrame frmGUI;
	private JTextField txtDoc;
	private JTextField txtKeyWord;
	private JTextField txtInvalidChar;
	private static JTextField txtMinLength;
	private static JTextField txtMaxLength;
	private JButton btnKeyAdd;
	private JButton btnKeyRemove;
	private JButton btnInvalidAdd;
	private JButton btnInvalidRemove;
	private JButton btnOpenDocument;
	private JButton btnRun;
	private JLabel lblDocumentForParsing;
	private JButton btnSearch;
	private JLabel lblKeyWord;
	private JLabel lblInvalidChar;
	private JLabel lblMaximumHeaderLength;
	private JLabel lblMinimumHeaderLength;
	private JLabel lblSettings;
	private JSeparator separator;
	private JCheckBox chkEdit;
	private static JList keyList;
	private static JList invalidList;
	private DefaultListModel keyListModel;
	private DefaultListModel invalidListModel;
	private JButton btnKeyClear;
	private JButton btnInvalidClear;
	private JButton btnDefault;
	private JComboBox cbConfig;
	private JLabel lblConfig;
	private JButton btnNewConfig;
	private JButton btnDeleteConfig;
	private File requirementsFile;
	private JButton binLoadConfig;
	private JLabel lbldoesTheDocument;
	private static JRadioButton rdYes;
	private static JRadioButton rdNo;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmGUI.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor. Create the GUI
	public GUI() {
		initialize();
	}

	/***************************************************************************
	 ***************************************************************************
	 Initialize: Creates all components of the GUI and their
	 * settings/functions
	 ***************************************************************************
	 ***************************************************************************/
	// Initialize all components
	private void initialize() {
		// Create the frame and its settings
		frmGUI = new JFrame();
		frmGUI.getContentPane().setEnabled(true);
		frmGUI.setTitle("Compilance Matrix Generator");
		frmGUI.setBounds(100, 100, 548, 757);
		frmGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/***********************************************************************/

		// Button that adds the key word in the text box to the list
		btnKeyAdd = new JButton("Add");
		btnKeyAdd.setBounds(10, 425, 70, 23);
		btnKeyAdd.setEnabled(false);
		// Anonymous class action listener for adding key word button
		btnKeyAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key;
				// Store the string from the text box
				// Do nothing if empty, otherwise add to list
				try {
					key = txtKeyWord.getText();
					// Clean edges and check size
					if (key.trim().length() != 0) {
						if (!keyListModel.contains(key))
							keyListModel.addElement(key);
					}
					// Reset text box
					txtKeyWord.setText(null);
				} catch (NullPointerException exception) {
					JOptionPane.showMessageDialog(null,
							"Invalid Key Word/Phrase");

				}

			}
		});
		frmGUI.getContentPane().setLayout(null);
		frmGUI.getContentPane().add(btnKeyAdd);
		/***********************************************************************/

		// Button that removes an item from the key list
		btnKeyRemove = new JButton("Remove");
		btnKeyRemove.setBounds(83, 425, 89, 23);
		btnKeyRemove.setEnabled(false);
		btnKeyRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Removes the selected key word from the list
				try {
					// Remove from list
					keyListModel.remove(keyList.getSelectedIndex());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null,
							"Error Removing Key Word/Phrase");

				}

			}
		});
		frmGUI.getContentPane().add(btnKeyRemove);
		/***********************************************************************/

		// Same functionality as the keyAdd but for InvalidList
		btnInvalidAdd = new JButton("Add");
		btnInvalidAdd.setBounds(280, 425, 70, 23);
		btnInvalidAdd.setEnabled(false);
		btnInvalidAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key;
				try {
					// Retrieve string in text box
					key = txtInvalidChar.getText();
					// Check to see if it is a single character
					if (key.trim().length() == 1) {
						// Check to see if the character already exists
						if (!invalidListModel.contains(key)) {
							// Check to see if the character is a symbol
							if (!Character.isDigit(key.charAt(0))
									&& !Character.isLetter(key.charAt(0))) {
								invalidListModel.addElement(key);
							}
						}
					}
					// Reset text field
					txtInvalidChar.setText(null);
				} catch (NullPointerException exception) {
					JOptionPane.showMessageDialog(null, "Invalid Character");
				}

			}
		});
		frmGUI.getContentPane().add(btnInvalidAdd);
		/***********************************************************************/

		// Same functionality as the KeyRemove but for InvalidList
		btnInvalidRemove = new JButton("Remove");
		btnInvalidRemove.setBounds(353, 425, 86, 23);
		btnInvalidRemove.setEnabled(false);
		btnInvalidRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Try to remove or else throw error
				try {
					invalidListModel.remove(invalidList.getSelectedIndex());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null,
							"Error Removing Character");

				}

			}
		});
		frmGUI.getContentPane().add(btnInvalidRemove);
		/***********************************************************************/

		// Text box containing the file path location
		txtDoc = new JTextField();
		txtDoc.setBounds(109, 88, 337, 20);
		txtDoc.setEditable(false);
		frmGUI.getContentPane().add(txtDoc);
		txtDoc.setColumns(10);
		/***********************************************************************/

		// Button to open the document specified in txtDoc
		btnOpenDocument = new JButton("Open Document");
		btnOpenDocument.setBounds(109, 119, 143, 23);
		btnOpenDocument.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleFileOpen();

			}
		});
		btnOpenDocument.setEnabled(false);
		frmGUI.getContentPane().add(btnOpenDocument);
		/***********************************************************************/

		// Button to run the parsing program on the document specified in txtDoc
		btnRun = new JButton("RUN");
		btnRun.setBounds(259, 119, 186, 23);
		btnRun.setEnabled(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleParse();
			}

		});
		frmGUI.getContentPane().add(btnRun);
		/***********************************************************************/

		// Labels txtDoc/btnSearch
		lblDocumentForParsing = new JLabel("Document for Parsing");
		lblDocumentForParsing.setBounds(109, 59, 171, 23);
		lblDocumentForParsing.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmGUI.getContentPane().add(lblDocumentForParsing);
		/***********************************************************************/

		// Button that allows the user to search for a pdf document to parse
		btnSearch = new JButton("Search");
		btnSearch.setBounds(10, 87, 89, 23);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}

		});
		frmGUI.getContentPane().add(btnSearch);
		/***********************************************************************/

		// Text box to add a new key word to the keyList
		txtKeyWord = new JTextField();
		txtKeyWord.setBounds(10, 394, 242, 20);
		txtKeyWord.setEnabled(false);
		frmGUI.getContentPane().add(txtKeyWord);
		txtKeyWord.setColumns(10);
		/***********************************************************************/

		// Label for txtKeyWord
		lblKeyWord = new JLabel("Enter Key Word or Phrase");
		lblKeyWord.setBounds(10, 369, 188, 14);
		lblKeyWord.setEnabled(false);
		lblKeyWord
				.setToolTipText("Key words and phrases are searched for in the document. Any instances of them will be included into the final matrix");
		frmGUI.getContentPane().add(lblKeyWord);
		/***********************************************************************/

		// Text box to enter an invalid header character
		txtInvalidChar = new JTextField();
		txtInvalidChar.setBounds(280, 394, 242, 20);
		txtInvalidChar.setEnabled(false);
		frmGUI.getContentPane().add(txtInvalidChar);
		txtInvalidChar.setColumns(10);
		/***********************************************************************/

		// Label for txtInvalid
		lblInvalidChar = new JLabel("Invalid header characters");
		lblInvalidChar.setBounds(280, 369, 187, 14);
		lblInvalidChar.setEnabled(false);
		lblInvalidChar
				.setToolTipText("These characters can not be a header. Any section header starting with these values will not be included.");
		frmGUI.getContentPane().add(lblInvalidChar);
		/***********************************************************************/

		// Text box to store minimum length of header
		txtMinLength = new JTextField();
		txtMinLength.setBounds(163, 338, 35, 20);
		txtMinLength.addFocusListener(new FocusAdapter() {
			// Make sure user entered an integer, if not set to default
			public void focusLost(FocusEvent e) {
				String input = txtMinLength.getText();
				// Check for any input
				if (input.trim().length() == 0) {
					JOptionPane.showMessageDialog(null,
							"Enter a Positive Integer");
					txtMinLength.setText("4");
				} else {
					// Check if input was an integer
					if (!isPosNumeric(input)) {
						JOptionPane.showMessageDialog(null,
								"Enter a Positive Integer");
						txtMinLength.setText("4");
					} else {
						if (Integer.parseInt(txtMinLength.getText()) > Integer
								.parseInt(txtMaxLength.getText())) {
							JOptionPane.showMessageDialog(null,
									"Minimum length cannot exceed Maximum");
							txtMinLength.setText("4");
						}
					}
				}

			}
		});
		txtMinLength.setEnabled(false);
		frmGUI.getContentPane().add(txtMinLength);
		txtMinLength.setColumns(10);
		/***********************************************************************/

		// Text box to store maximum length of header
		txtMaxLength = new JTextField();
		txtMaxLength.setBounds(432, 338, 35, 20);
		txtMaxLength.setEnabled(false);
		txtMaxLength.addFocusListener(new FocusAdapter() {
			// Make sure user entered an integer, if not set to default
			public void focusLost(FocusEvent e) {
				String input = txtMaxLength.getText();
				// Check for any input
				if (input.trim().length() == 0) {
					JOptionPane.showMessageDialog(null,
							"Enter a Positive Integer");
					txtMaxLength.setText("50");
				} else {
					// Check if input was an integer
					if (!isPosNumeric(input)) {
						JOptionPane.showMessageDialog(null,
								"Enter a Positive Integer");
						txtMaxLength.setText("50");
					} else {
						if (Integer.parseInt(txtMaxLength.getText()) < Integer
								.parseInt(txtMinLength.getText())) {
							JOptionPane.showMessageDialog(null,
									"Minimum length cannot exceed Maximum");
							txtMaxLength.setText("50");
						}
					}

				}
			}
		});
		frmGUI.getContentPane().add(txtMaxLength);
		txtMaxLength.setColumns(10);
		/***********************************************************************/

		// Label for txtMinLength
		lblMinimumHeaderLength = new JLabel("Minimum header length");
		lblMinimumHeaderLength.setBounds(10, 338, 135, 14);
		lblMinimumHeaderLength.setEnabled(false);
		frmGUI.getContentPane().add(lblMinimumHeaderLength);
		/***********************************************************************/

		// Label for txtMaxLength
		lblMaximumHeaderLength = new JLabel("Maximum header length");
		lblMaximumHeaderLength.setBounds(279, 341, 143, 14);
		lblMaximumHeaderLength.setEnabled(false);
		frmGUI.getContentPane().add(lblMaximumHeaderLength);
		/***********************************************************************/

		// Label for Settings
		lblSettings = new JLabel("Settings");
		lblSettings.setBounds(10, 215, 89, 28);
		lblSettings.setEnabled(false);
		lblSettings.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmGUI.getContentPane().add(lblSettings);
		/***********************************************************************/

		// Separator for running program and settings
		separator = new JSeparator();
		separator.setBounds(10, 201, 512, 2);
		separator.setForeground(Color.BLACK);
		frmGUI.getContentPane().add(separator);
		/***********************************************************************/

		// Check box to enable settings
		chkEdit = new JCheckBox("Enable Settings");
		chkEdit.setBounds(397, 148, 152, 23);
		chkEdit.addActionListener(new ActionListener() {
			// Toggle, enable/disable all setting options
			public void actionPerformed(ActionEvent e) {
				chkToggle(e);
			}
		});
		frmGUI.getContentPane().add(chkEdit);
		/***********************************************************************/

		// List of all key words to be searched for in the specified document
		// DefaultListModel is used to add the elements in the list
		// JScrollPane is used to make a scroll bar
		keyListModel = new DefaultListModel();
		JScrollPane keyScroll = new JScrollPane();
		keyScroll.setBounds(10, 459, 242, 249);
		keyList = new JList(keyListModel);
		keyList.setBounds(10, 408, 242, 249);
		keyList.setEnabled(false);
		keyScroll.setViewportView(keyList);
		frmGUI.getContentPane().add(keyScroll);
		/***********************************************************************/

		// List of all characters to be searched in headers for them to be
		// invalid
		invalidListModel = new DefaultListModel();
		JScrollPane invalidScroll = new JScrollPane();
		invalidScroll.setBounds(280, 459, 242, 249);
		invalidList = new JList(invalidListModel);
		invalidList = new JList(invalidListModel);
		invalidList.setBounds(280, 408, 242, 249);
		invalidList.setBackground(Color.WHITE);
		invalidList.setEnabled(false);
		invalidScroll.setViewportView(invalidList);
		frmGUI.getContentPane().add(invalidScroll);
		/***********************************************************************/

		// Button that clears the contents of the Key List
		btnKeyClear = new JButton("Clear");
		btnKeyClear.setBounds(182, 425, 70, 23);
		btnKeyClear.setEnabled(false);
		// On click of the button, loop through list and clear
		btnKeyClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Loop through the list, removing each entry
				int size = keyListModel.size();
				for (int i = size - 1; i >= 0; i--) {
					keyListModel.remove(i);
				}

			}
		});
		frmGUI.getContentPane().add(btnKeyClear);
		/***********************************************************************/

		// Button that clears the contents of the Invalid list
		btnInvalidClear = new JButton("Clear");
		btnInvalidClear.setBounds(451, 425, 70, 23);
		btnInvalidClear.setEnabled(false);
		btnInvalidClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Loop through the list, removing each entry
				int size = invalidListModel.size();
				for (int i = size - 1; i >= 0; i--) {
					invalidListModel.remove(i);
				}
			}

		});
		frmGUI.getContentPane().add(btnInvalidClear);
		/***********************************************************************/

		// Button to reset default settings found in FileUtils Class
		btnDefault = new JButton("Default");
		btnDefault.setBounds(401, 171, 100, 23);
		btnDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetToDefault();
			}
		});
		frmGUI.getContentPane().add(btnDefault);
		/***********************************************************************/

		// Combo box to save different configurations
		cbConfig = new JComboBox();
		cbConfig.setBounds(387, 221, 135, 20);
		cbConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		cbConfig.setEnabled(false);
		cbConfig.setMaximumRowCount(10);
		frmGUI.getContentPane().add(cbConfig);
		/***********************************************************************/

		// Label for configuration combo box
		lblConfig = new JLabel("Configurations");
		lblConfig.setBounds(280, 215, 89, 28);
		lblConfig.setEnabled(false);
		frmGUI.getContentPane().add(lblConfig);
		/***********************************************************************/

		// Button to save a new configuration
		btnNewConfig = new JButton("Save Config");
		btnNewConfig.setBounds(280, 258, 125, 23);
		btnNewConfig.setEnabled(false);
		btnNewConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save the current settings into the config folder
				// Ask the user for a filename
				String filename = JOptionPane
						.showInputDialog("Enter File Name");
				FileUtils.saveConfigs(filename, txtMinLength.getText(),
						txtMaxLength.getText(), getKeyList(), getInvalidList());
				// Re-populate the configuration combo box
				populateConfig();
				cbConfig.setSelectedItem(filename);
			}
		});
		frmGUI.getContentPane().add(btnNewConfig);
		/***********************************************************************/

		// Button for deleting the selected configuration
		btnDeleteConfig = new JButton("Delete Config");
		btnDeleteConfig.setBounds(280, 282, 125, 23);
		btnDeleteConfig.setEnabled(false);
		btnDeleteConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// First check if a configuration is chosen
				String config = (String) cbConfig.getSelectedItem();
				FileUtils.deleteConfig(config);
				populateConfig();
				resetToDefault();
			}
		});
		frmGUI.getContentPane().add(btnDeleteConfig);
		/***********************************************************************/

		// Button that sets takes the file selected in the config combo box and
		// populates the fields accordingly
		binLoadConfig = new JButton("Load Config");
		binLoadConfig.setBounds(415, 258, 107, 47);
		binLoadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Retrieve the filename from the combo box
				String filename = (String) cbConfig.getSelectedItem();
				// Put the contents into an arraylist an extract the information
				ArrayList<String> info = FileUtils.getConfig(filename);
				String[] keys;
				String[] badChars;
				keys = info.get(2).split("  ");
				if (info.size() == 4)
					badChars = info.get(3).split("  ");

				else
					badChars = new String[0];
				// Send the individual parameters to populateMinMaxLists which
				// fills in the actual UI
				populateMinMaxLists(info.get(0), info.get(1), keys, badChars);
			}
		});
		binLoadConfig.setEnabled(false);
		frmGUI.getContentPane().add(binLoadConfig);
		
		/***********************************************************************/
		
		rdYes = new JRadioButton("Yes");
		rdYes.setBounds(10, 171, 50, 23);
		frmGUI.getContentPane().add(rdYes);
		rdYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdYes.isSelected())
					rdToggle(e);
				rdYes.setSelected(true);
			}
		});
		
		/***********************************************************************/

		rdNo = new JRadioButton("No");
		rdNo.setSelected(true);
		rdNo.setBounds(62, 171, 50, 23);
		frmGUI.getContentPane().add(rdNo);
		rdNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdNo.isSelected())
					rdToggle(e);
				rdNo.setSelected(true);
			}
		});
		
		/***********************************************************************/
		
		lbldoesTheDocument = new JLabel("***Numbered Headers");
		lbldoesTheDocument.setToolTipText("Does the document selected use numbers to determine headers?");
		lbldoesTheDocument.setBounds(10, 148, 340, 23);
		frmGUI.getContentPane().add(lbldoesTheDocument);

		// On start set default settings
		populateConfig();
		resetToDefault();
	}


	/****************************************************************
	 ****************************************************************
	 Event and Helper Functions
	 ****************************************************************
	 ****************************************************************/

	// Default settings
	private void resetToDefault() {
		ArrayList<String> info = FileUtils.getConfig("Default");
		String[] keys = info.get(2).split("  ");
		String[] badChars = info.get(3).split("  ");
		populateMinMaxLists(info.get(0), info.get(1), keys, badChars);
		cbConfig.setSelectedItem("Default");
	}

	// Given the settings, populates the minimum and maximum text fields,
	// and the key list and the invalid list
	private void populateMinMaxLists(String min, String max, String[] keys,
			String[] badChars) {

		// Clear the lists
		keyListModel.clear();
		invalidListModel.clear();
		// Re-populate the configurations
		txtMinLength.setText(min);
		txtMaxLength.setText(max);
		for (int i = 0; i < keys.length; i++)
			keyListModel.addElement(keys[i]);
		for (int i = 0; i < badChars.length; i++)
			invalidListModel.addElement(badChars[i]);
	}

	// Used to create a browser to choose a file to parse. Used for search
	// button
	private void chooseFile() {
		// Create a file chooser
		JFileChooser chooser = new JFileChooser(FileUtils.PREFERENCES.get(
				FileUtils.LAST_USED_FOLDER, new File(".").getAbsolutePath()));
		// Set filters f.or what document should be searched for
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"DOCX, DOC, TXT & PDF Files", "docx", "doc", "pdf", "txt");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			FileUtils.PREFERENCES.put(FileUtils.LAST_USED_FOLDER, chooser
					.getSelectedFile().getParent());
			// Set files and text fields to the file
			requirementsFile = chooser.getSelectedFile();
			txtDoc.setText(requirementsFile.getAbsolutePath());
			txtDoc.setToolTipText(requirementsFile.getAbsolutePath());
			// Enable the open and run buttons
			btnOpenDocument.setEnabled(true);
			btnRun.setEnabled(true);
		}
	}

	// Opens the file specified in the txtDoc
	private void handleFileOpen() {
		if (requirementsFile != null) {
			try {
				String mimeType = FileUtils.getMimeType(requirementsFile);
				if (FileUtils.DOCX_MIMETYPE.equals(mimeType)
						|| FileUtils.DOC_MIMETYPE.equals(mimeType)
						|| FileUtils.PDF_MIMETYPE.equals(mimeType)
						|| (mimeType != null && mimeType
								.startsWith(FileUtils.TXT_MIMETYPE))) {
					FileUtils.open(requirementsFile);
				}
			} catch (IOException e1) {
				FileUtils.handleException(e1);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please select a document.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Runs the parsing program on the file specified in txtDoc and creates the
	// Matrix
	private void handleParse() {
		if (requirementsFile != null) {
			try {
				String text = DocReader.getFileContents(requirementsFile);
				Collection<TextHolder> th = new DocumentParser().parse(text);
				MakeTable.make(th, getKeyList());
			} catch (Exception e1) {
				FileUtils.handleException(e1);
			}
		} else {
			if (requirementsFile == null) {
				JOptionPane
						.showMessageDialog(null, "Please select a document.");
			}
		}
	}

	// Helper function to determine whether a string is an integer
	public static boolean isPosNumeric(String str) {
		try {
			int i = Integer.parseInt(str);
			if (i > 0)
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	// Re-populates the config combo box
	public void populateConfig() {
		// Retrieve the files in the config file
		File[] files = FileUtils.getConfigFiles();
		// Clear the config combo box, add the default, add the files
		cbConfig.removeAllItems();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				cbConfig.addItem(FilenameUtils.removeExtension(files[i]
						.getName()));
		}
	}
	
	private void rdToggle (ActionEvent e){
		if(e.getSource() == rdYes){
			if (rdYes.isSelected()){
				rdNo.setSelected(false);
			} else rdNo.setSelected(true);
		} else {
			if (rdNo.isSelected()){
				rdYes.setSelected(false);
			} else rdYes.setSelected(true);
		}
		
	}

	// Toggle enable on setting options
	private void chkToggle(ActionEvent e) {
		if (chkEdit.isSelected()) {
			lblSettings.setEnabled(true);
			lblMaximumHeaderLength.setEnabled(true);
			lblMinimumHeaderLength.setEnabled(true);
			txtMinLength.setEnabled(true);
			txtMaxLength.setEnabled(true);
			btnInvalidAdd.setEnabled(true);
			btnInvalidRemove.setEnabled(true);
			lblKeyWord.setEnabled(true);
			lblInvalidChar.setEnabled(true);
			txtKeyWord.setEnabled(true);
			txtInvalidChar.setEnabled(true);
			btnKeyAdd.setEnabled(true);
			btnKeyRemove.setEnabled(true);
			btnKeyClear.setEnabled(true);
			btnInvalidClear.setEnabled(true);
			keyList.setEnabled(true);
			invalidList.setEnabled(true);
			lblConfig.setEnabled(true);
			cbConfig.setEnabled(true);
			btnNewConfig.setEnabled(true);
			btnDeleteConfig.setEnabled(true);
			binLoadConfig.setEnabled(true);

		} else {
			lblSettings.setEnabled(false);
			lblMaximumHeaderLength.setEnabled(false);
			lblMinimumHeaderLength.setEnabled(false);
			txtMinLength.setEnabled(false);
			txtMaxLength.setEnabled(false);
			btnInvalidAdd.setEnabled(false);
			btnInvalidRemove.setEnabled(false);
			lblKeyWord.setEnabled(false);
			lblInvalidChar.setEnabled(false);
			txtKeyWord.setEnabled(false);
			txtInvalidChar.setEnabled(false);
			btnKeyAdd.setEnabled(false);
			btnKeyRemove.setEnabled(false);
			btnKeyClear.setEnabled(false);
			btnInvalidClear.setEnabled(false);
			keyList.setEnabled(false);
			invalidList.setEnabled(false);
			lblConfig.setEnabled(false);
			cbConfig.setEnabled(false);
			btnNewConfig.setEnabled(false);
			btnDeleteConfig.setEnabled(false);
			binLoadConfig.setEnabled(false);

		}
	}

	/*****************************************************************************
	 *****************************************************************************
	 Getters for List Items
	 *****************************************************************************
	 *****************************************************************************/
	// returns a collection of all keys listed in the keyList
	public static Collection<String> getKeyList() {

		Collection<String> keys = new ArrayList<String>();
		ListModel model = keyList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object o = model.getElementAt(i);
			keys.add((String) o);
		}
		return keys;
	}

	// returns all the invalid characters in the invalidList
	public static char[] getInvalidList() {

		ListModel model = invalidList.getModel();
		char[] keys = new char[model.getSize()];
		for (int i = 0; i < model.getSize(); i++) {
			Object o = model.getElementAt(i);
			keys[i] = ((String) o).charAt(0);
		}
		return keys;
	}
	public static String getMaxLen(){
		return txtMaxLength.getText();
	}
	public static String getMinLen(){
		return txtMinLength.getText();
	}
	public static boolean getHeaderBool(){
		return rdYes.isSelected();
		
	}
}