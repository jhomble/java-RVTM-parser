package com.att.compliance;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//Things to test: Headers, Section numbers, Grabbing requirements, whether results are the same for different document types
/**
 * This is a test suite intended to test the parsing capabilities of the application. To make your own tests, 
 * create a test file(.doc or .docx format), making sure to abide by the formatting of the requirements doc. Then, 
 * create a key file(.txt format), with the following format for keys:
 * <p>"requirement/heading--section#--page#--text"
 * @author Eric Woods
 *
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { TestBasicParse1.class , TestDifferentDocTypes.class})
 class TestSuite1{}


@RunWith(Suite.class)
@SuiteClasses({ TestSuite1.class })
public class ParseTestSuite {
	

}
