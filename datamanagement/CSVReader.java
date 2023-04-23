package edu.upenn.cit594.datamanagement;

/*
 * I attest that the code in this file is entirely my own except for the starter
 * code provided with the assignment and the following exceptions:
 * <Enter all external resources and collaborations here. Note external code may
 * reduce your score but appropriate citation is required to avoid academic
 * integrity violations. Please see the Course Syllabus as well as the
 * university code of academic integrity:
 *  https://catalog.upenn.edu/pennbook/code-of-academic-integrity/ >
 * Signed,
 * Author: Song Li
 * Penn email: <lisong3@seas.upenn.edu>
 * Date: 2023-03-10
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * {@code CSVReader} provides a stateful API for streaming individual CSV rows
 * as arrays of strings that have been read from a given CSV file.
 *
 * @author Song Li
 */
public class CSVReader {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5130409650040L;
    private final CharacterReader reader;

    enum State { START, TEXTDATA, ESCAPED, INSIDEQUOTE, CR };
    State state = State.START;
    
    public static final int COMMA = 44;
    public static final int CR = 13;
    public static final int LF = 10;
    public static final int DQUOTE = 34;
    
    
    public CSVReader(CharacterReader reader) {
        this.reader = reader;
    }

    /**
     * This method uses the class's {@code CharacterReader} to read in just enough
     * characters to process a single valid CSV row, represented as an array of
     * strings where each element of the array is a field of the row. If formatting
     * errors are encountered during reading, this method throws a
     * {@code CSVFormatException} that specifies the exact point at which the error
     * occurred.
     *
     * @return a single row of CSV represented as a string array, where each
     *         element of the array is a field of the row; or {@code null} when
     *         there are no more rows left to be read.
     * @throws IOException when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public String[] readRow() throws IOException, CSVFormatException {
    	
    	ArrayList<String> list = new ArrayList<String>();
    	
    	StringBuilder str = new StringBuilder();
    	boolean EOL  = false;
    	boolean EOF = true;
    	int charValue;
    	
    	while( (charValue = reader.read()) != -1) {      		
	
    		char c = (char) charValue;    
    		EOF = false;

    		switch (state) {
    		
		 	case START: 
		 		
    			switch(charValue) {
    			case COMMA:
    				list.add(str.toString());
    				str.setLength(0);
	    			break;
    			case DQUOTE:
    				state = State.ESCAPED;
    				break;
    			case CR:
    				state = State.CR;
    				break;
    			case LF:
    				list.add(str.toString());
    				state = State.START;
    				EOL = true;
					break;
    			default :
    				str.append(c);
    				state = State.TEXTDATA;
    				break;
    			}
    			break;
    			
    		case TEXTDATA: 

					switch(charValue) {
					case COMMA:
						list.add(str.toString());
						str.setLength(0);;
						state = State.START;
						break;
					case DQUOTE:
						throw new CSVFormatException();
					case CR:
						state = State.CR;
						break;
					case LF:
						list.add(str.toString());
						EOL = true;
						state = State.START;
						break;
					default :
						str.append(c);
						break;
					}
					break;
    		
    		case ESCAPED: 
    			
    			switch(charValue) {
    			case DQUOTE: 
    				state = State.INSIDEQUOTE;
    				break;
    			default:
    				str.append(c);
    				break;	
    			}
    			break;
    			
    		case CR:
    			switch (charValue) {
    			case LF:	
    				list.add(str.toString());
    				state = State.START;
    				EOL = true;
    				break;
    			default:
    				throw new CSVFormatException ();
    			}
    			break;
    			
    		case INSIDEQUOTE:
    			
    			switch(charValue) {
    			case COMMA:
    				list.add(str.toString());
    				str.setLength(0);;
    				state = State.START;
    				break;
    			case DQUOTE:
    				str.append(c);
    				state = State.ESCAPED;
    				break;
    			case CR:
    				state = State.CR;
    				break;
				case LF:
    				list.add(str.toString());
    				state = State.START;
    				EOL = true;
					break;
    			default:
    				throw new CSVFormatException();   			
    			}
    			break;
    			
    		default:
    			break;
    		
    		}
    		if (EOL) {
    			break;
    		}
    	}
    	
    	if(EOF) {
    		return null;
    	}
		if (state == state.ESCAPED) {
    		throw new CSVFormatException();
    	}
		if (EOF == false && charValue == -1) {
			list.add(str.toString());
		}
    	String [] stringArray = list.toArray(new String[0]);
        return stringArray;
    }

    /**
     * Feel free to edit this method for your own testing purposes. As given, it
     * simply processes the CSV file supplied on the command line and prints each
     * resulting array of strings to standard out. Any reading or formatting errors
     * are printed to standard error.
     *
     * @param args command line arguments (1 expected)
     */
//    public static void main(String[] args) {
////        if (args.length != 1) {
////            System.out.println("usage: CSVReader <filename.csv>");
////            return;
////        }
//
//        /*
//         * This block of code demonstrates basic usage of CSVReader's row-oriented API:
//         * initialize the reader inside try-with-resources, initialize the CSVReader
//         * using the reader, and repeatedly call readRow() until null is encountered. Since
//         * CharacterReader implements AutoCloseable, the reader will be automatically
//         * closed once the try block is exited.
//         */
//  
//        try (var reader = new CharacterReader() {
//            var csvReader = new CSVReader(reader);
//            String[] row;
//            while ((row = csvReader.readRow()) != null) {
//            	System.out.print(row[0]+ " ");
//            	System.out.print(row[1]+ " :");
//                System.out.println(Arrays.toString(row));
//            } catch (IOException | CSVFormatException e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//       }
//    }
//    }
}
