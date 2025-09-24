package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String validUsernameMsg = UserNameRecognizer.checkForValidUserName(userName); //Check if the username is valid
            String password = passwordField.getText();
            String validPasswordMsg = PasswordEvaluator.evaluatePassword(password);		//Check if the password is valid
            String code = inviteCodeField.getText();
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, "user");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            		else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		errorLabel.setText("This userName is taken!!.. Please use another to setup an account");
            	}
            	
            	//Display why the username is invalid
            	if(validPasswordMsg != "") {
            		errorLabel.setText(validPasswordMsg);
            	}
            	//Display why the password is invalid
            	if(validUsernameMsg != "") {
            		errorLabel.setText(validUsernameMsg);
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
    

    class PasswordEvaluator {
    	/**
    	 * <p> Title: Directed Graph-translated Password Assessor. </p>
    	 * 
    	 * <p> Description: A demonstration of the mechanical translation of Directed Graph 
    	 * diagram into an executable Java program using the Password Evaluator Directed Graph. 
    	 * The code detailed design is based on a while loop with a cascade of if statements</p>
    	 * 
    	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
    	 * 
    	 * @author Lynn Robert Carter
    	 * 
    	 * @version 0.00		2018-02-22	Initial baseline 
    	 * 
    	 */

    	/**********************************************************************************************
    	 * 
    	 * Result attributes to be used for GUI applications where a detailed error message and a 
    	 * pointer to the character of the error will enhance the user experience.
    	 * 
    	 */

    	public static String passwordErrorMessage = "";		// The error message text
    	public static String passwordInput = "";			// The input being processed
    	public static int passwordIndexofError = -1;		// The index where the error was located
    	public static boolean foundUpperCase = false;
    	public static boolean foundLowerCase = false;
    	public static boolean foundNumericDigit = false;
    	public static boolean foundSpecialChar = false;
    	public static boolean foundLongEnough = false;
    	public static boolean foundOtherChar = false;
    	private static String inputLine = "";				// The input line
    	private static char currentChar;					// The current character in the line
    	private static int currentCharNdx;					// The index of the current character
    	private static boolean running;						// The flag that specifies if the FSM is 
    														// running

    	/**********
    	 * This private method display the input line and then on a line under it displays an up arrow
    	 * at the point where an error should one be detected.  This method is designed to be used to 
    	 * display the error message on the console terminal.
    	 * 
    	 * @param input				The input string
    	 * @param currentCharNdx	The location where an error was found
    	 * @return					Two lines, the entire input line followed by a line with an up arrow
    	 */
    	private static void displayInputState() {
    		// Display the entire input line
    		System.out.println(inputLine);
    		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
    		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
    				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
    	}

    	/**********
    	 * This method is a mechanical transformation of a Directed Graph diagram into a Java
    	 * method.
    	 * 
    	 * @param input		The input string for directed graph processing
    	 * @return			An output string that is empty if every things is okay or it will be
    	 * 						a string with a help description of the error follow by two lines
    	 * 						that shows the input line follow by a line with an up arrow at the
    	 *						point where the error was found.
    	 */
    	public static String evaluatePassword(String input) {
    		// The following are the local variable used to perform the Directed Graph simulation
    		passwordErrorMessage = "";
    		passwordIndexofError = 0;			// Initialize the IndexofError
    		inputLine = input;					// Save the reference to the input line as a global
    		currentCharNdx = 0;					// The index of the current character
    		
    		if(input.length() <= 0) return "Please input a password";
    		
    		// The input is not empty, so we can access the first character
    		currentChar = input.charAt(0);		// The current character from the above indexed position

    		// The Directed Graph simulation continues until the end of the input is reached or at some 
    		// state the current character does not match any valid transition to a next state

    		passwordInput = input;				// Save a copy of the input
    		foundUpperCase = false;				// Reset the Boolean flag
    		foundLowerCase = false;				// Reset the Boolean flag
    		foundNumericDigit = false;			// Reset the Boolean flag
    		foundSpecialChar = false;			// Reset the Boolean flag
    		foundLongEnough = false;			// Reset the Boolean flag
    		running = true;						// Start the loop

    		// The Directed Graph simulation continues until the end of the input is reached or at some 
    		// state the current character does not match any valid transition
    		while (running) {
    			displayInputState();
    			// The cascading if statement sequentially tries the current character against all of the
    			// valid transitions
    			if (currentChar >= 'A' && currentChar <= 'Z') {
    				System.out.println("Upper case letter found");
    				foundUpperCase = true;
    			} else if (currentChar >= 'a' && currentChar <= 'z') {
    				System.out.println("Lower case letter found");
    				foundLowerCase = true;
    			} else if (currentChar >= '0' && currentChar <= '9') {
    				System.out.println("Digit found");
    				foundNumericDigit = true;
    			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
    				System.out.println("Special character found");
    				foundSpecialChar = true;
    			} else {
    				passwordIndexofError = currentCharNdx;
    				return "*** Error *** An invalid character has been found!";
    			}
    		
    			
    			if (currentCharNdx >= 7) {
    				System.out.println("At least 8 characters found");
    				foundLongEnough = true;
    				if (currentCharNdx > 15) {
    					//Password is longer than 16 characters
    					foundLongEnough = false;
    				}
    			}
    			
    			
    			// Go to the next character if there is one
    			currentCharNdx++;
    			if (currentCharNdx >= inputLine.length())
    				running = false;
    			else
    				currentChar = input.charAt(currentCharNdx);
    			
    			System.out.println();
    		}
    		
    		String errMessage = "";
    		if (!foundUpperCase)
    			errMessage += "Has at least one upper case letter;\n ";
    		
    		if (!foundLowerCase)
    			errMessage += "Has at least one lower case letter;\n ";
    		
    		if (!foundNumericDigit)
    			errMessage += "Has at least one number;\n ";
    			
    		if (!foundSpecialChar)
    			errMessage += "Has at least one special character;\n ";
    			
    		if (!foundLongEnough)
    			errMessage += "Is between 8-16 characters;\n ";
    		
    		
    		if (errMessage == "")
    			return "";
    		
    		passwordIndexofError = currentCharNdx;
    		return "Please create a password that\n " + errMessage;

    	}
    }
    class UserNameRecognizer {
    	/**
    	 * <p> Title: FSM-translated UserNameRecognizer. </p>
    	 * 
    	 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
    	 * diagram into an executable Java program using the UserName Recognizer. The code 
    	 * detailed design is based on a while loop with a select list</p>
    	 * 
    	 * <p> Copyright: Lynn Robert Carter © 2024 </p>
    	 * 
    	 * @author Lynn Robert Carter
    	 * 
    	 * @version 1.00		2024-09-13	Initial baseline derived from the Even Recognizer
    	 * @version 1.01		2024-09-17	Correction to address UNChar coding error, improper error
    	 * 									message, and improve internal documentation
    	 * 
    	 */

    	/**********************************************************************************************
    	 * 
    	 * Result attributes to be used for GUI applications where a detailed error message and a 
    	 * pointer to the character of the error will enhance the user experience.
    	 * 
    	 */

    	public static String userNameRecognizerErrorMessage = "";	// The error message text
    	public static String userNameRecognizerInput = "";			// The input being processed
    	public static int userNameRecognizerIndexofError = -1;		// The index of error location
    	private static int state = 0;						// The current state value
    	private static int nextState = 0;					// The next state value
    	private static boolean finalState = false;			// Is this state a final state?
    	private static String inputLine = "";				// The input line
    	private static char currentChar;					// The current character in the line
    	private static int currentCharNdx;					// The index of the current character
    	private static boolean running;						// The flag that specifies if the FSM is 
    														// running
    	private static int userNameSize = 0;			// A numeric value may not exceed 16 characters

    	// Private method to display debugging data
    	private static void displayDebuggingInfo() {
    		// Display the current state of the FSM as part of an execution trace
    		if (currentCharNdx >= inputLine.length())
    			// display the line with the current state numbers aligned
    			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
    					((finalState) ? "       F   " : "           ") + "None");
    		else
    			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
    				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
    				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
    				nextState + "     " + userNameSize);
    	}
    	
    	// Private method to move to the next character within the limits of the input line
    	private static void moveToNextCharacter() {
    		currentCharNdx++;
    		if (currentCharNdx < inputLine.length())
    			currentChar = inputLine.charAt(currentCharNdx);
    		else {
    			currentChar = ' ';
    			running = false;
    		}
    	}

    	/**********
    	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
    	 * method.
    	 * 
    	 * @param input		The input string for the Finite State Machine
    	 * @return			An output string that is empty if every things is okay or it is a String
    	 * 						with a helpful description of the error
    	 */
    	public static String checkForValidUserName(String input) {
    		// Check to ensure that there is input to process
    		if(input.length() <= 0) {
    			userNameRecognizerIndexofError = 0;	// Error at first character;
    			return "\n*** ERROR *** The input is empty";
    		}
    		
    		// The local variables used to perform the Finite State Machine simulation
    		state = 0;							// This is the FSM state number
    		inputLine = input;					// Save the reference to the input line as a global
    		currentCharNdx = 0;					// The index of the current character
    		currentChar = input.charAt(0);		// The current character from above indexed position

    		// The Finite State Machines continues until the end of the input is reached or at some 
    		// state the current character does not match any valid transition to a next state

    		userNameRecognizerInput = input;	// Save a copy of the input
    		running = true;						// Start the loop
    		nextState = -1;						// There is no next state
    		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
    		
    		// This is the place where semantic actions for a transition to the initial state occur
    		
    		userNameSize = 0;					// Initialize the UserName size

    		// The Finite State Machines continues until the end of the input is reached or at some 
    		// state the current character does not match any valid transition to a next state
    		while (running) {
    			// The switch statement takes the execution to the code for the current state, where
    			// that code sees whether or not the current character is valid to transition to a
    			// next state
    			switch (state) {
    			case 0: 
    				// State 0 has 1 valid transition that is addressed by an if statement.
    				
    				// The current character is checked against A-Z, a-z. If any are matched
    				// the FSM goes to state 1
    				
    				// A-Z, a-z -> State 1
    				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
    						(currentChar >= 'a' && currentChar <= 'z' )) {	
    					nextState = 1;
    					
    					// Count the character 
    					userNameSize++;
    					
    					// This only occurs once, so there is no need to check for the size getting
    					// too large.
    				}
    				// If it is none of those characters, the FSM halts
    				else 
    					running = false;
    				
    				// The execution of this state is finished
    				break;
    			
    			case 1: 
    				// State 1 has one valid transition, 
    				//	1: a A-Z, a-z, 0-9 that transitions to state 2

    				
    				// A-Z, a-z, 0-9 -> State 2
    				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
    						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
    						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
    					nextState = 2;
    					
    					// Count the character
    					userNameSize++;
    				}
    						
    				// If it is none of those characters, the FSM halts
    				else
    					running = false;
    				
    				// The execution of this state is finished
    				// If the size is larger than 16, the loop must stop
    				if (userNameSize > 16)
    					running = false;
    				break;			
    				
    			case 2: 
    				// State 2 has 2 transitions
    				
    				// A-Z, a-z, 0-9 -> State 2
    				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
    						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
    						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
    					nextState = 2;
    					
    					// Count the odd digit
    					userNameSize++;
    					
    				}
    				// _ or - or . -> State 3
    				else if (currentChar == '_' || currentChar == '-' || currentChar == '.') {
    					nextState = 3;
    					userNameSize++;
    					
    				}
    				// If it is none of those characters, the FSM halts
    				else 
    					running = false;

    				// The execution of this state is finished
    				// If the size is larger than 16, the loop must stop
    				if (userNameSize > 16)
    					running = false;
    				break;
    			case 3:
    				//State 3 has 1 transition
    				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
    						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
    						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
    					nextState = 2;
    					userNameSize++;
    				}
    				// If it's not an alphanumeric character, the FSM halts
    				else
    					running = false;

    				//If the user name is larger than 16 characters, the loop halts
    				if (userNameSize > 16)
    					running = false;
    				break;
    			}
    			
    			if (running) {
    				displayDebuggingInfo();
    				// When the processing of a state has finished, the FSM proceeds to the next
    				// character in the input and if there is one, it fetches that character and
    				// updates the currentChar.  If there is no next character the currentChar is
    				// set to a blank.
    				moveToNextCharacter();

    				// Move to the next state
    				state = nextState;
    				
    				// Is the new state a final state?  If so, signal this fact.
    				if (state == 1) finalState = true;

    				// Ensure that one of the cases sets this to a valid value
    				nextState = -1;
    			}
    			// Should the FSM get here, the loop starts again
    	
    		}
    		displayDebuggingInfo();
    		
    		System.out.println("The loop has ended.");
    		
    		// When the FSM halts, we must determine if the situation is an error or not.  That depends
    		// of the current state of the FSM and whether or not the whole string has been consumed.
    		// This switch directs the execution to separate code for each of the FSM states and that
    		// makes it possible for this code to display a very specific error message to improve the
    		// user experience.
    		userNameRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
    		userNameRecognizerErrorMessage = "\n*** ERROR *** ";
    		
    		// The following code is a slight variation to support just console output.
    		switch (state) {
    		case 0:
    			// State 0 is not a final state, so we can return a very specific error message
    			userNameRecognizerErrorMessage += "A UserName must start with A-Z, a-z.\n";
    			return userNameRecognizerErrorMessage;

    		case 1:
    			// State 1 is not a final state, so we can return a very specific error message
    			userNameRecognizerErrorMessage += "A UserName must have A-Z, a-z, or 0-9.\n";
    			return userNameRecognizerErrorMessage;
    			
    			

    		case 2:
    			// State 2 is a final state. Check to see if the UserName length is valid.  If so we
    			// we must ensure the whole string has been consumed.
    			if (userNameSize < 5) {
    				// UserName is too small
    				userNameRecognizerErrorMessage += "A UserName must have at least 5 characters.\n";
    				return userNameRecognizerErrorMessage;
    			}
    			else if (userNameSize > 16) {
    				// UserName is too long
    				userNameRecognizerErrorMessage += 
    					"A UserName must have no more than 16 character.\n";
    				return userNameRecognizerErrorMessage;
    			}
    			else if (currentCharNdx < input.length()) {
    				// There are characters remaining in the input, so the input is not valid
    				userNameRecognizerErrorMessage += 
    					"A UserName may only contain the characters A-Z, a-z, or a _, -, or . in between alphanumeric characters\n";
    				return userNameRecognizerErrorMessage;
    			} 
    			else {
    					// UserName is valid
    					userNameRecognizerIndexofError = -1;
    					userNameRecognizerErrorMessage = "";
    					return userNameRecognizerErrorMessage;
    			}
    		case 3:
    			userNameRecognizerErrorMessage +=
    			"A UserName character after an underscore, minus, or period, must be A-Z, a-z, 0-9.\n";
    		return userNameRecognizerErrorMessage;
    			
    		default:
    			// This is for the case where we have a state that is outside of the valid range.
    			// This should not happen
    			return "";
    		}
    	}
    }
}
