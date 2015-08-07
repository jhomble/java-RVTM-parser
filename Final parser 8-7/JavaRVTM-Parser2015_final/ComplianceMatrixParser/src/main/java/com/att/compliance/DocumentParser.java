package com.att.compliance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * This is the class used for parsing the text for requirements, pages, and
 * sections
 * 
 * 
 * 2015 interns
 * @edited by Andrew Cleary, Matt Lipshultz, Julien Homble
 * @version 2.0
 * 
 * @original authors Eric Woods, Mark Sholund
 * @version 1.0
 *
 */
public class DocumentParser {

	/**
	 * Takes a string and returns the parsed list of TextHolder objects
	 * 
	 * @param text
	 *            - The text inputted
	 * @return Returns an ArrayList of TextHolder objects
	 */


	private static final ArrayList<String> classifications = new ArrayList<String>();
	private String currclassification = "";

	
	// True if numbered headers are in document
	// false if not
	private boolean numbered_headers = GUI.getHeaderBool();
	
	
	boolean typeofpage = true;
	int page = 0;

	//set this to set the classification 
	//of each requirement to the most classified part of the requirement
	//otherwise it is the first previous classification or first classification
	private boolean MOST_CLASSIFIED = true;


	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");
	private static final String SECTION_STRING = "((\\d+\\p{Space}?\\.\\p{Space}?)+\\d+)+";

	//private static final String SECTION_STRING = "^((\\d)*[.]*)*\\p{Space}*\\(\\p{Upper}+(//)?\\p{Upper}*\\)";

	private static final Pattern SECTION_PATTERN = Pattern.compile(SECTION_STRING);
	private static final Pattern CLEANING_PATTERN = Pattern.compile(
			"^(.){0,10}\\d\\p{Space}?$", Pattern.MULTILINE);

	
	
	
	public Collection<TextHolder> parse(String text) throws SAXException,
	IOException, ParserConfigurationException, InterruptedException {
		// Read section by section, adding requirements to array
		// list of words to look for
		// Splits the text by section number
	
		File file = new File("output.txt");
		file.delete();

		//		String newText2 = text.replaceAll("(([A-Z/\\p{Space}^]{40,100})+)", "");
		//		System.out.print(newText2);
		//String[] splitText = text.split("(?=(^\\p{Space}*[\\d+\\.]*\\d*\\p{Space}*\\(\\p{Upper}+.*\\)))");
		List<TextHolder> result = new ArrayList<TextHolder>();
		String[] splitText = null;
		
		
		//replace ..... consecutive periods for a bug
		text = text.replaceAll("\\.\\.+", "__________________________________________________________");
		
		if(numbered_headers){
			splitText  = text.split("(?=((\\d+\\.)+\\d+))");
			result = parseHelper(splitText, text);
		}else{
			result = getReq(text);
			//System.out.println(result.size());
		}
		
		String capitalLetters = "([A-Z\\p{Space}]+\\/\\/[A-Z\\p{Space}]+ )";
		Pattern CapPattern = Pattern.compile(capitalLetters);

		for (TextHolder t:result){
			Matcher CapMatch = CapPattern.matcher(t.text);
			t.text = CapMatch.replaceAll("\n");
		}
		
		classify(result);

		return removeDuplicates(result);
	}

	private List<TextHolder> getReq(String text) {
		Collection<String> keywords = GUI.getKeyList();

		List<TextHolder> requirements = new ArrayList<TextHolder>();
		
		
		ArrayList<String> sentences = new ArrayList<String>();
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sen = document.get(SentencesAnnotation.class);
		//delete old output , write to output file
		
		
		PrintWriter outp=null;
		try {
			outp = new PrintWriter(new BufferedWriter(new FileWriter("output.txt",true)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (CoreMap sentence : sen) {

			String s = sentence.toString();
			

			Pattern p = Pattern.compile("^(\\d{1,3})\\p{Space}?$", Pattern.MULTILINE);
			//Pattern p = Pattern.compile("\\p{Space}(\\d{1,3})\\p{Space}", Pattern.MULTILINE);
			Pattern n = Pattern.compile("(Page )\\d+( of )\\d+", Pattern.MULTILINE);
			Matcher m = p.matcher(s);
			Matcher mn = n.matcher(s);
			//search the sentence for a page number
			if(mn.find()) {
				typeofpage = false;
				if (Integer.parseInt(mn.group(0).substring(5, 7).trim()) == page+1){
					page ++;
					s = s.replaceAll("(Page )\\d+( of )\\d+", " ");
				}
			}
			while(m.find() && typeofpage) {
				if (Integer.parseInt(m.group(0).trim()) == page+1){
					page ++;
					s = s.replaceAll("^(\\d{1,3})\\p{Space}?$", " ");
				}
			}

			//format it and add it
			s = s.replaceAll("\\s+", " ").trim();
			s = s.replaceAll("", "•");
			s = s.replaceAll(" o ", "• --");
			sentences.add(s);
			

			//if you find a sentence once don't find it again
			boolean b = true;
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

				//each word in the sentence
				String word = token.get(TextAnnotation.class);


				//each keyword
				for (String keyword : keywords) {

					//checks each word accurately to see if its a keyword
					// words like can't and won't do not work(don't tokenize into 1 word)
					// if not surrounded by spaces
					if(b && (word.equals( keyword ) 
							|| s.contains(" " + keyword + " ")
							|| s.contains(" " + keyword + ":")
							|| s.contains(" " + keyword + "."))){
						if (s.contains("•") ){
							int i = 0;
							for(String bullet : s.split("(?=•)")){
								if(i == 0){
									bullet = bullet.concat(" < REQUIREMENTS BELOW APPLY TO THIS >");
								}
								requirements.add(new Requirement("", bullet ,String.valueOf(page)));
								i ++ ;
							}
						}else {
							requirements.add(new Requirement("", s ,String.valueOf(page)));
						}
						outp.println(s);
						outp.println();
						b = false;
					}else if(b && (keyword.contains(" ")|| keyword.contains("'")) && s.contains(keyword)){
						//checks for phrases included in document
						if (s.contains("•") ){
							int i = 0;
							for(String bullet : s.split("(?=•)")){
								if(i == 0){
									bullet = bullet.concat(" < REQUIREMENTS BELOW APPLY TO THIS >");
								}
								
								requirements.add(new Requirement("", bullet ,String.valueOf(page)));
								i ++ ;
							}
						}else {
							requirements.add(new Requirement("", s ,String.valueOf(page)));
						}
						outp.println(s);
						outp.println();
						b = false;
					}

				}
			}
			
			
			
		}

		outp.println();
		outp.println();
		outp.println("NEW SECTION-------------------------");
		outp.println();
		outp.println();
		outp.println();
		outp.close();


		return requirements;
	}

	private Collection<TextHolder> removeDuplicates(
			Collection<TextHolder> result2) {
		return new LinkedHashSet<TextHolder>(result2);
	}

	/**
	 * Takes the text split by section number, and parses it to determine
	 * requirements and what section/page they belong to.
	 * 
	 * @param splitText
	 *            - The text split up by section numbers.
	 * @param allText
	 *            - Holds all of the text, for parsing purposes.
	 * @return Returns all the requirements for the text.
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	private ArrayList<TextHolder> parseHelper(String[] splitText, String allText)
			throws InterruptedException, UnsupportedEncodingException {
		ArrayList<TextHolder> reqList = new ArrayList<TextHolder>();
		// This method will grab requirements/titles from each section
		String sectAccumulator = "";
		String newText = allText;
		
		Pattern p = Pattern.compile("^(\\d{1,3})\\p{Space}?$", Pattern.MULTILINE);
		//Pattern n = Pattern.compile("(Page )\\d+( of )\\d+", Pattern.MULTILINE);
		//Pattern pi = Pattern.compile("^(\\d)(\\d?)(\\d?)\\p{Space}?$", Pattern.MULTILINE);
		//Pattern p = Pattern.compile("^((\\d)*[.]*)*\\p{Space}*[\\(\\p{Upper}+(//)?\\p{Upper}*]$", Pattern.MULTILINE);
		String prevSection = "";
		for (int i = 0; i < splitText.length; i++) {
			String s = splitText[i];
			String section = "";
			// If the section number is more than 1 number we know that it is
			// the full section number, otherwise its just part of the section
			// number
			//Pattern p = Pattern.compile("\\p{Space}(\\d{1,3})\\p{Space}", Pattern.MULTILINE);
			Pattern n = Pattern.compile("(Page )\\d+( of )\\d+", Pattern.MULTILINE);
			Matcher m = p.matcher(s);
			Matcher mn = n.matcher(s);
			//search the sentence for a page number
			if(mn.find()) {
				typeofpage = false;
				if (Integer.parseInt(mn.group(0).substring(5, 7).trim()) == page+1){
					page ++;
					s = s.replaceAll("(Page )\\d+( of )\\d+", " ");
				}
			}
			while(m.find() && typeofpage) {
				if (Integer.parseInt(m.group(0).trim()) == page+1){
					page ++;
					s = s.replaceAll("^(\\d{1,3})\\p{Space}?$", " ");
				}
			}
		
			

			if (s.split(SECTION_STRING).length > 1) {
				String text = s.split(SECTION_STRING)[1];
				Matcher matcher = SECTION_PATTERN.matcher(s);
				if (matcher.find() ) {
					section = sectAccumulator + matcher.group(0);
				}



				//System.out.println(section);
				String first_line = text.substring(0, text.indexOf("\n"));
				//System.out.println(first_line);


				char[] bad = GUI.getInvalidList();
				for (char c : bad){
					String str = "" + c;
					if ( first_line.contains(str)){
						section = prevSection;
					}
				}


				prevSection = section;


				
				int max = Integer.parseInt(GUI.getMaxLen());
				int min = Integer.parseInt(GUI.getMinLen());
				// Checks if its a section header
				if (isSectionHeader(section, text, allText) 
						&& first_line.length() < max 
						&& first_line.length() > min ) {
					reqList.add(new Heading(section, cleanHeading(text), String.valueOf(page)));
				}
				// Gets all of the requirements from that section
				else {
					if (isFirstLineHeader(section, text, allText) 
							&& first_line.length() < max 
							&& first_line.length() > min ) {
						reqList.add(new Heading(section, getHeaderFromText(text), String.valueOf(page)));
						text = stripHeaderFromText(text);
					}

					reqList.addAll(getRequirements(section, text, String.valueOf(page)));
				}

				sectAccumulator = "";

			}

			else {
				sectAccumulator += s;
			}
		}
		return reqList;
	}

	private String getHeaderFromText(String text) {
		return text.split(LINE_SEPARATOR)[0];
	}

	private String stripHeaderFromText(String text) {
		return text.substring(text.indexOf(LINE_SEPARATOR));
	}

	private boolean isFirstLineHeader(String section, String text,
			String allText) {
		String firstLine = text.split(LINE_SEPARATOR)[0];

		return isSectionHeader(section, firstLine, allText);
	}


	/**
	 * Method to get all requirements from a section of text
	 * <p>
	 * This method looks for the words 'shall', 'will', 'must', 'should', 'may',
	 * and 'is responsible for'.
	 * 
	 * @param section
	 *            - The section number of the text being parsed.
	 * @param text
	 *            - The text being searched for requirements.
	 * @param page
	 *            - The page number of the text being parse.
	 * @return An ArrayList of requirements from the text.
	 * @throws InterruptedException
	 */
	private Collection<Requirement> getRequirements(String section,
			String text, String page1) throws InterruptedException {
		Collection<String> keywords = GUI.getKeyList();

		LinkedHashSet<Requirement> requirements = new LinkedHashSet<Requirement>();

		ArrayList<String> sentences = new ArrayList<String>();
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sen = document.get(SentencesAnnotation.class);
		//delete old output , write to output file
		
		
		PrintWriter outp=null;
		try {
			outp = new PrintWriter(new BufferedWriter(new FileWriter("output.txt",true)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CoreMap sentence : sen) {

			String s = sentence.toString().replaceAll("\\s+", " ").trim();
			
			s = s.replaceAll("", "•");
			s = s.replaceAll(" o ", "• --");
			sentences.add(s);
			

			//if you find a sentence once don't find it again
			boolean b = true;
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

				//each word in the sentence
				String word = token.get(TextAnnotation.class);


				//each keyword
				for (String keyword : keywords) {

					//checks each word accurately to see if its a keyword
					// words like can't and won't do not work(don't tokenize into 1 word)
					// if not surrounded by spaces
					if(b && (word.equals( keyword ) 
							|| s.contains(" " + keyword + " ")
							|| s.contains(" " + keyword + ":")
							|| s.contains(" " + keyword + "."))){
						if (s.contains("•") ){
							int i = 0;
							for(String bullet : s.split("(?=•)")){
								if(i == 0){
									bullet = bullet.concat(" < REQUIREMENTS BELOW APPLY TO THIS >");
								}
								requirements.add(new Requirement(section, bullet ,String.valueOf(page)));
								i ++ ;
							}
						}else {
							requirements.add(new Requirement(section, s ,String.valueOf(page)));
						}
						outp.println(s);
						outp.println();
						b = false;
					}else if(b && (keyword.contains(" ")|| keyword.contains("'")) && s.contains(keyword)){
						//checks for phrases included in document
						if (s.contains("•") ){
							int i = 0;
							for(String bullet : s.split("(?=•)")){
								if(i == 0){
									bullet = bullet.concat(" < REQUIREMENTS BELOW APPLY TO THIS >");
								}
								
								requirements.add(new Requirement(section, bullet ,String.valueOf(page)));
								i ++ ;
							}
						}else {
							requirements.add(new Requirement(section, s ,String.valueOf(page)));
						}
						outp.println(s);
						outp.println();
						b = false;
					}

				}
			}
			
			
			
		}

		outp.println();
		outp.println();
		outp.println("NEW SECTION-------------------------");
		outp.println();
		outp.println();
		outp.println();
		outp.close();


		return requirements;
	}


	/**
	 * Classifies all requirements changing the original requirements
	 * 
	 * @param Collection<Requirement> 
	 *            - The parsed requirements.
	 * @return Collection<Requirement> each requirement classified with the closest previous classification
	 */
	private void classify(List<TextHolder> result) {		
		//  puts classification on all requirements
		classifications.add("U");
		classifications.add("U//FOUO");
		classifications.add("S");
		classifications.add("TS");


		// if it does not have a new classification at the beggining
		// then use the closest previous classification
		for( TextHolder r : result){

			if ( r.isHeader){
				r.setText(r.text.trim());
			}
			//set classification of the beginning
			boolean b = true;
			for(String str : classifications){
				if(r.text.startsWith("("+str+")")){
					r.setText(r.text.substring(str.length()+2, r.text.length()));
					r.setClassification("("+str+")");
					currclassification = "("+str+")";
					b=false;
					break;
				}
			}

			// if there is no classification at the beginning of the requirement
			// then use the closest previous one
			if(b){
				r.setClassification(currclassification);
			}
			// find the final classification in this requirement and remember it 
			// in case the next requirement does not have a classification in the beginning
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(r.text);
			while(m.find()) {
				for(String str : classifications){
					if(m.group(1).equals(str)){
						currclassification = "("+str+")";
						//sets the classification of the requirement to the most classified 
						// comment out to set it to the correct classification 
						if(MOST_CLASSIFIED)
							r.setClassification(currclassification);

					}
				} 
			}

			//toggle on to show in line classification
			//String temp = r.getClassification() + " " + r.text;
			//r.setText(temp);	
		}
	}

	// Cleans the heading, stripping new lines, spaces, page numbers, etc.
	private String cleanHeading(String heading) {
		String newHeading = heading;
		newHeading = CLEANING_PATTERN.matcher(heading).replaceAll("");
		newHeading = newHeading.replaceAll("(?m)\\p{Space}?[ \t]*\r?\n", " ")
				.replace(":", "");
		return newHeading.trim();
	}

	/**
	 * Method to determine if a section of text is a section header. Need to
	 * test this.
	 * 
	 * @param text
	 *            - The section of text.
	 * @param allText
	 * @param text2
	 * @return Returns true if it is determined to be a section header, and
	 *         false otherwise.
	 */
	private boolean isSectionHeader(String section, String text, String allText) {
		String strippedText = text.replaceAll("\\p{Punct}", "").trim();
		if (strippedText.length() < Integer.parseInt(GUI.getMinLen())) {
			return false;
		}
		if (text.indexOf(LINE_SEPARATOR) == 1) {
			return false;
		}

		char[] badCharacters = GUI.getInvalidList();
		if (strippedText.length() < Integer.parseInt(GUI.getMaxLen())) {
			if (badCharacters != null) {
				for (char c : badCharacters) {
					if (text.indexOf(c) != -1) {
						return false;
					}
				}
			}
			return true;
		}
		if (text.toUpperCase().equals(text)) {
			return true;
		}
		return false;
	}
}

/**
 * Abstract class to define text holder. Can be either a requirement or a
 * heading.
 * 
 * @author Eric Woods
 *
 */
abstract class TextHolder {
	protected String section;
	protected String text;
	protected String page;
	protected String classification;
	protected boolean isHeader;

	public TextHolder(String sect, String t, String p) {
		section = sect.replace(" ", "");
		text = t;
		page = p.replace(" ", "");
	}

	public String toString() {
		return section;
	}

	public String getClassification() {
		return classification;
	}
	public void setClassification(String cl) {
		classification = cl;
	}

	void setText(String s){
		text = s;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this != obj)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextHolder other = (TextHolder) obj;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		return true;
	}

	public boolean isHeader() {
		return isHeader;
	}
}

/**
 * Requirement class
 */
class Requirement extends TextHolder {
	public Requirement(String sect, String t, String p) {
		super(sect, t, p);
		isHeader = false;
	}
	public String toString() {
		return super.toString() + "\n" + text;
	}

}

/**
 * Heading class
 */
class Heading extends TextHolder {
	public Heading(String sect, String t, String p) {
		super(sect, t, p);
		isHeader = true;
	}

	public String toString() {
		return super.toString() + " " + text;
	}
}
