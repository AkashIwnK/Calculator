/**** ECE309 - Lab 09 Expression Calculator ****/
/**** Instructor: Paul Bowman ****/
/**** Author: Akash Kothari ****/

import java.io.*;

public class StringTest {
	String x; /* String in the textfield that contains the value of x entered by the user */
/****************** TWO GLOBAL BOOLEANS REMOVED REMOVED *************/

	public StringTest(String expression) {
		expression = expression.trim();
		
	/* Error Checking */
		if(lexicalErrorCheck(expression) == null)
			return;
		
	/* Replace all x's in the expression with the x value if specified */
		if(x != null) {
			double x_value;
			try {
				x_value = Double.parseDouble(x.trim());
			} catch (Exception e) {
				System.out.println("Invalid x");
				return;
			}
			while(expression.contains("x"))
				expression = expression.replace("x", String.valueOf(x_value));
		} else {
			if(expression.contains("x")) {
				System.out.println("No value of x specified");
				return;
			}
		}
				
	/* Replace all 'pi's and 'e's in the string with the actual values */
		while(expression.contains("pi"))
			expression = expression.replace("pi", String.valueOf(Math.PI));
		while(expression.contains("e"))
			expression = expression.replace("e", String.valueOf(Math.E));	
			
	/* Solve the expression now */	
		String result = computeExpression(expression);
		if(result == null) {
			System.out.println("Erroneous expression");
			return;
		} else {
			System.out.println("Result: " + result);
		}
	}
	
	private String lexicalErrorCheck(String expression) {	
	/* Check if expression starts with '(' and/or ')' */
		if(expression.charAt(0) == ')') {
			System.out.println("Expression cannot start with ')'");
			return null;
		}
		if(expression.endsWith("(") == true) {
			System.out.println("Expression cannot end with '('");
			return null;
		}
	
	/* If there is an imbalance in brackets in the expression, an error needs returning */	
		if(checkBracketsBalance(expression) == null) {
			System.out.println("Erroneous expression since brackets are not balanced");
			return null;
		}
		
	/* Check if the expression contains with "()" */
		if(expression.replaceAll("\\s","").contains("()") == true) {
			System.out.println("Cannot have empty brackets in the expression");
			return null;
		}

/************************************** NEWLY ADDED *****************************************/				
	/* Check if expression contains ")(" */
		if(expression.replaceAll("\\s","").contains(")(") == true) {
			System.out.println("Cannot have ')(' in the expression");
			return null;
		}	
/************************************** NEWLY ADDED *****************************************/				
		
	/* Check if the numbers do not have operators between them */
		String[] splitExp = expression.split("[ ()]+");
		int index = 0;
		while(index != splitExp.length - 1) {
		/* Skip if the string is empty */
			if(splitExp[index].isEmpty() == true) {
				index++;
				continue;
			}
		System.out.println(splitExp[index+1]);
		System.out.println(index);
		/* Check if the numbers have no operators between them */	
			if(stringEndsWithAnOperator(splitExp[index]) == false			 /************* CHECK REMOVED *************/
			&& stringStartsWithAnOperator(splitExp[index + 1]) == false) {   /********* NO NEED FOR THAT CHECK ******/
			/* There is no operator between the operands */	
				System.out.println("There is no operator between two numbers");
				return null;
			}
			index++;
		}
	
	/* Check if there is an operator right after '(' */
		splitExp = expression.split("[(]+");
		index = 0;
		while(index != splitExp.length) {
		/* Skip if the string is empty */
			if(splitExp[index].isEmpty() == true) {
				index++;
				continue;
			}	
			
		/* Check if the string starts with an operator */	
			if(stringStartsWithAnOperator(splitExp[index].trim()) == true) {
				if(splitExp[index].trim().charAt(0) != '-') {
					System.out.println("operator used after '(' or at the beginning of expression");
					return null;
				}
			}
			
/************************************** NEWLY ADDED *****************************************/					
		/* Check if there is an operator right before the first '(' between the strings */
			if(index != 0) {
				if(stringEndsWithAnOperator(splitExp[index - 1].trim()) == false) {
					System.out.println("no operator used before'('");
					return null;
				}
			}	
/************************************** NEWLY ADDED *****************************************/					
			index++;
		}
		
	/* Check if there is an operator right before ')' */
		splitExp = expression.split("[)]+");
		index = 0;
		while(index != splitExp.length) {
		/* Skip if the string is empty */
			if(splitExp[index].isEmpty() == true) {
				index++;
				continue;
			}	
			
		/* Check if the string starts with an operator */	
			if(stringEndsWithAnOperator(splitExp[index].trim()) == true) {
				System.out.println("operator used before ')' or at the end of expression");
				return null;
			}
			
/************************************** NEWLY ADDED *****************************************/			
		/* Check if there is an operator right after the last ')' between the strings */
			if(index != splitExp.length - 1) {
				if(stringStartsWithAnOperator(splitExp[index + 1].trim()) == false) {
					System.out.println("no operator used after ')'");
					return null;
				}
			}
/************************************** NEWLY ADDED *****************************************/		
			index++;
		}	
	
		return expression;
	}
	
	private String checkBracketsBalance(String expression) {
	/* Check if the number of opening and closing brackets is the same */	
		int numBrackets = 0;
		int index = 0;
		while(index != expression.trim().length()) {
			if(expression.charAt(index) == '(')
				numBrackets++;
			else if(expression.charAt(index) == ')')
					numBrackets--;
			index++;
		}
		if(numBrackets != 0)
			return null;
		
		return expression;
	}
	
	private String computeExpression(String expression) {
	/* Get rid of all the empty space in the string */
		expression = expression.replaceAll("\\s","");
		
	/* Split the entire string first into tokens in an array of string since we need to separate out the 
	 * segments of the expression that are enclosed in brackets since brackets have the highest precedence.
	 */
		String delim = "[()]+";
		String[] stringArray = expression.trim().split(delim);
		System.out.println("--------------stringArray length: " + stringArray.length);
		
	/* Iterate over the array of strings */
		int index = 0;
		while(index != stringArray.length) {
			System.out.println("String in the array: " + stringArray[index]);
		/* Skip if the string is empty */
			if(stringArray[index].isEmpty() == true) {
				index++;
				continue;
			}
			
		/* If a token in the array does not start and/or end with an operator, then we can
		 * the string by solving that segment of the expression.
		 */	
			if(stringEndsWithAnOperator(stringArray[index]) == false) {
				if(stringStartsWithAnOperator(stringArray[index]) == false) {
					stringArray[index] = computeExpression_WithoutBrackets(stringArray[index]);
					if(stringArray[index] == null)
						return null;
				} else {
				/* Check if the string starts with a negative sign */
					if(stringArray[index].trim().charAt(0) == '-') {
						stringArray[index] = computeExpression_WithoutBrackets(stringArray[index]);
						if(stringArray[index] == null)
							return null;
					}
				}
			}
			index++;
		}
		
	/* Now we add the simplified segments of the expression (strings in the array) into a single string */
		String simplified_exp = stringArray[0];
		index = 1;
		while(index != stringArray.length) {
			simplified_exp += stringArray[index];
			index++;
		}
		
/************************************** NEWLY ADDED *****************************************/				
	/* Now we can compute the expression */
		System.out.println("FINAL COMPUTATION");
		String finalResult = computeExpression_WithoutBrackets(simplified_exp);
		if(finalResult == null)
			return null;
		
	/* Get rid of the brackets if any */
		if(finalResult.contains("(") == true 
		&& finalResult.contains(")") == true) {
			finalResult = finalResult.substring(1, finalResult.length() - 1);
		}
		
		return finalResult;
/************************************** NEWLY ADDED *****************************************/				
	}
	
	private boolean stringEndsWithAnOperator(String givenString) {
		if(givenString.endsWith("+") == false
		&& givenString.endsWith("-") == false  /******** ADDED BACK  (DO NOT REMOVE) **********/
		&& givenString.endsWith("*") == false
		&& givenString.endsWith("/") == false
		&& givenString.endsWith("^") == false
		&& givenString.endsWith("r") == false) {
			return false;
		}
		return true;
	}
	
	private boolean stringStartsWithAnOperator(String givenString) {
		if(givenString.charAt(0) != '+'
		&& givenString.charAt(0) != '-'		/******** ADDED BACK (DO NOT REMOVE) *********/
		&& givenString.charAt(0) != '*'
		&& givenString.charAt(0) != '/'
		&& givenString.charAt(0) != '^'
		&& givenString.charAt(0) != 'r') {
			return false;
		}
		return true;
	}
	
/* This method solves an expression with no brackets */	
	private String computeExpression_WithoutBrackets(String expression) {		
/************************************** NEWLY ADDED *****************************************/						
		expression = expression.trim();
		
	/* Solve all exponents in the expression */	
		expression = solveExponents(expression);
		if(expression == null)
			return null;
		
	/* Solve all the roots in the expression */
		expression = solveRoots(expression);
		if(expression == null)
			return null;
		
	/* Solve all the multiplications in the expression */
		expression = solveMultiplications(expression);
		if(expression == null)
			return null;	
		
	/* Solve all the divisions in the expression */
		expression = solveDivisions(expression);
		if(expression == null)
			return null;		
		
	/* Solve all the additions in the expression */
		expression = solveAdditions(expression);
		if(expression == null)
			return null;		
		
	/* Solve all the subtractions in the expression */
		expression = solveSubtractions(expression);
		if(expression == null)
			return null;	
		
		return expression.trim();
/************************************** NEWLY ADDED *****************************************/						
	}
		
	private String solveExponents(String expression) {
	/* Solve the operands on either side of ^ */
		while(expression.contains("^") == true) {
			System.out.println("^ OPERATOR");
			System.out.println("expression: " + expression);
			int operatorIndex = expression.indexOf("^");
			
	/************************************** NEWLY ADDED *****************************************/						
		/* Since exponent and root have the same precedence, all the roots before 
		 * this expoent need to be solved first.
		 */
			String newReplacement = solveRoots(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */	
			expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("^");
	/************************************** NEWLY ADDED *****************************************/						
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
	/************************************** NEWLY ADDED *****************************************/		
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			System.out.println("left_mainSubstring: " + left_mainSubstring);
	/************************************** NEWLY ADDED *****************************************/			
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
			
		/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);
		/****************************** NEWLY ADDED ***********************************/				
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result;
			try {
				result = Math.pow(left_operand, right_operand);
			} catch (Exception e) {
				System.out.println("Exponent could not be computed");
				return  null;
			}
			
	/************************************** NEWLY ADDED *****************************************/						
		/* If the result is negative, enclose it within brackets */	
			String result_string = String.valueOf(result);
			if(result < 0)
				result_string = "(" + result_string + ")";
	/************************************** NEWLY ADDED *****************************************/						
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "^" + rightOperand;
			System.out.println("Regex: "+ regex);
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveRoots(String expression) {
	/* Solve the operands on either side of r */
		while(expression.contains("r") == true) {
			System.out.println("r OPERATOR");
			System.out.println("expression: " + expression);
			int operatorIndex = expression.indexOf("r");
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
		
	/************************************** NEWLY ADDED *****************************************/		
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			System.out.println("left_mainSubstring: " + left_mainSubstring);
		/************************************** NEWLY ADDED *****************************************/			
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
			
		/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);
		/****************************** NEWLY ADDED ***********************************/					
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
		
/************************************** NEWLY ADDED *****************************************/					
			double result;
			try {
				result = Math.pow(left_operand, 1/right_operand);
			} catch (Exception e) {
				System.out.println("Root could not be computed");
				return null;
			}
	
		/* If the result is negative, enclose it within brackets */	
			String result_string = String.valueOf(result);
			if(result < 0)
				result_string = "(" + result_string + ")";
	/************************************** NEWLY ADDED *****************************************/							
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "r" + rightOperand;
			System.out.println("Regex: "+ regex);
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveMultiplications(String expression) {
	/* Solve the operands on either side of * */
		while(expression.contains("*") == true) {
			System.out.println("* OPERATOR");
			System.out.println("expression: " + expression);
			int operatorIndex = expression.indexOf("*");
			
	/************************************** NEWLY ADDED *****************************************/				
		/* Since division and multiplication have the same precedence, solve all 
		 * divisions before this multiplication.
		 */	
			String newReplacement = solveDivisions(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */	
			expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("*");
	/************************************** NEWLY ADDED *****************************************/						
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
	/************************************** NEWLY ADDED *****************************************/		
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			System.out.println("left_mainSubstring: " + left_mainSubstring);
	/************************************** NEWLY ADDED *****************************************/			
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
			
	/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);	
	/****************************** NEWLY ADDED ***********************************/					
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand * right_operand;
			
/************************************** NEWLY ADDED *****************************************/				
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";
/************************************** NEWLY ADDED *****************************************/				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "*" + rightOperand;
			System.out.println("Regex: "+ regex);
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveDivisions(String expression) {
	/* Solve the operands on either side of / */
		while(expression.contains("/") == true) {
			System.out.println("/ OPERATOR");
			System.out.println("expression: " + expression);
			int operatorIndex = expression.indexOf("/");
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
		/************************************** NEWLY ADDED *****************************************/		
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			System.out.println("left_mainSubstring: " + left_mainSubstring);
		/************************************** NEWLY ADDED *****************************************/			
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
			
		/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);			
		/****************************** NEWLY ADDED ***********************************/					
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
/************************************** NEWLY ADDED *****************************************/				
			double result;
			try {
				result = left_operand / right_operand;
			} catch (Exception e) {
				System.out.println("Could not divide expression");
				return null;
			}
					
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";
/************************************** NEWLY ADDED *****************************************/				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "/" + rightOperand;
			System.out.println("Regex: "+ regex);
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveAdditions (String expression) {
	/* Solve the operands on either side of + */
		while(expression.contains("+") == true) {
			System.out.println("+ OPERATOR");
			System.out.println("expression for addition: " + expression);
			int operatorIndex = expression.indexOf("+");
			
	/************************************** NEWLY ADDED *****************************************/					
		/* Since additionand subtraction have the same precedence, solve all 
		 * subtractions before this addition.
		 */	
			String newReplacement = solveSubtractions(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */	
			expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("+");		
			
			System.out.println("new expression for addtion: " + expression);
	/************************************** NEWLY ADDED *****************************************/						
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
	/************************************** NEWLY ADDED *****************************************/		
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			System.out.println("left_mainSubstring: " + left_mainSubstring);
	/************************************** NEWLY ADDED *****************************************/	
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
			
		/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);		
		/****************************** NEWLY ADDED ***********************************/					
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand + right_operand;
			
	/************************************** NEWLY ADDED *****************************************/				
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";	
	/************************************** NEWLY ADDED *****************************************/				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "+" + rightOperand;
			System.out.println("Regex: "+ regex);
			System.out.println("result: " + result);
			System.out.println("String after replacement: " + result_string);
			expression = expression.replace(regex, result_string);		
			System.out.println("new expression: " + expression);
		}	
		return expression.trim();
	}
	
	private String solveSubtractions(String expression) {
	/* Solve the operands on either side of - */
		while(expression.contains("-") == true) {
			System.out.println("- OPERATOR");
			System.out.println("expression for subtraction: " + expression);
			int operatorIndex = expression.indexOf("-");
			
/************************************** NEWLY ADDED *****************************************/				
	/***** SOME UNNECESSARY VARIABLES REMOVED *****/	
		/* Test if expression is just a negative number */
			String mainString;
			if(expression.charAt(0) == '(' && expression.endsWith(")")) 
				mainString = expression.substring(1, expression.length() - 1);
			else 
				mainString = expression;
			
			System.out.println("subtraction mainString: " + mainString);
			try{	
				Double.parseDouble(mainString.trim());
				System.out.println("is a negative value");
				
			/* Check of expression has brackets */	
				if(expression.charAt(0) != '(' && expression.endsWith(")") == false) {
					expression = "(" + expression + ")";
				}
				System.out.println("expression: " + expression);
				return expression.trim();
			} catch (Exception e) {
				System.out.println("Not a negative value");
			}
/************************************** NEWLY ADDED *****************************************/	
		
	/*********** REMOVED SOME CODE -- NOT NEEDED: CHECK OUT GETLEFT AND GETRIGHT OPERANDS FUNCTTION **********/		
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
		/************************************** NEWLY ADDED *****************************************/		
			/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}
			System.out.println("left_mainSubstring: " + left_mainSubstring);
		/************************************** NEWLY ADDED *****************************************/			
			
			double left_operand;
			try{
				left_operand = Double.parseDouble(left_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
		/* Get the right operand */
			String rightOperand = getRightOperand(expression, operatorIndex);
			if(rightOperand == null)
				return null;
		
	/****************************** NEWLY ADDED ***********************************/		
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			System.out.println("right_mainSubstring: " + right_mainSubstring);
	/****************************** NEWLY ADDED ***********************************/			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand - right_operand;
			
	/************************************** NEWLY ADDED *****************************************/				
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";	
	/************************************** NEWLY ADDED *****************************************/				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "-" + rightOperand;
			System.out.println("Regex: "+ regex);
			System.out.println("result_string: " + result_string);
			expression = expression.replace(regex, result_string);
			System.out.println("expression to be looped: " + expression);
		}	
		return expression.trim();
	}

	private String getLeftOperand(String expression, int operatorIndex) {
		System.out.println("leftOperand expression: " + expression);
	/* Check if there is any valid left operand */	
		if(operatorIndex == 0) {
			System.out.println("Invalid expression");
			return null;
		}
		
	/* Walk the string backwards to get the left operand */
		System.out.println("operator: "+ expression.charAt(operatorIndex));
		int index = operatorIndex - 1;
		while(index != -1
		   && expression.charAt(index) != '+'
		   && expression.charAt(index) != '/'
		   && expression.charAt(index) != '*'
		   && expression.charAt(index) != '^'
		   && expression.charAt(index) != 'r') {
			
/*********************** PREVIOUS CODE REMOVED. NEW CODE ADDED ***************************/			
			if(expression.charAt(index) == '-') {
			/* If two operators are adjacent to each other, it is an error */		
				if(index == operatorIndex - 1) {
					System.out.println("Two operators are adjacent to each other");
					return null;
				}
							
			/* Check whether the operand is negative */
				if(expression.substring(index, operatorIndex).contains(")") == true) {
				/* Check the negative number is actually enclosed in brackets */
					if(expression.charAt(index - 1) == '(') {
						index -= 2;
						break;
					} 
					System.out.println("Erroneous expression");
					return null;
				}
				
			/* This means that the minus sign is an operator */	
				System.out.println("minus sign is an operator");
				break;
			}
/*********************** PREVIOUS CODE REMOVED. NEW CODE ADDED ***************************/			
			System.out.println("character: " + expression.charAt(index));
			index--;
		}
	
	/* If two operators are adjacent to each other, it is an error */	
		if(index == operatorIndex - 1) {
			System.out.println("Two operators are adjacent to each other");
			return null;
		}
		
		System.out.println("LeftOperand: "+ expression.substring(index + 1, operatorIndex));
		return expression.substring(index + 1, operatorIndex);
	}
	
	private String getRightOperand(String expression, int operatorIndex) {
		System.out.println("rightOperand expression: " + expression);
	/* Check if there is any valid right operand */	
		if(operatorIndex == expression.length() - 1) {
			System.out.println("Invalid expression");
			return null;
		}
		
	/* Walk the string forward to get the right operand */
		System.out.println("operator: "+ expression.charAt(operatorIndex));
		int index = operatorIndex + 1;
		while(index != expression.length()
		   && expression.charAt(index) != '+'
		   && expression.charAt(index) != '/'
		   && expression.charAt(index) != '*'
		   && expression.charAt(index) != '^'
		   && expression.charAt(index) != 'r') {
		
/*********************** PREVIOUS CODE REMOVED. NEW CODE ADDED ***************************/			
			if(expression.charAt(index) == '-') {	
			/* If two operators are adjacent to each other, it is an error */		
				if(index == operatorIndex + 1) {
					System.out.println("Two operators are adjacent to each other");
					return null;
				}
							
			/* Check whether the operand is negative */
				if(expression.substring(operatorIndex + 1, index).contains("(") == true) {
				/* Check the negative number is actually enclosed in brackets */
					if(expression.substring(index).contains(")") == true) {
						while(expression.charAt(index) != ')')
							index++;
						index++;
						break;
					} 
					System.out.println("GetRightOperand: Erroneous expression");
					return null;
				}
				
			/* This means that the minus sign is an operator */	
				System.out.println("minus sign is an operator");
				break;
			}
/*********************** PREVIOUS CODE REMOVED. NEW CODE ADDED ***************************/	
			System.out.println("character: "+expression.charAt(index));
			index++;
		}
	
	/* If two operators are adjacent to each other, it is an error */	
		if(index == operatorIndex + 1) {
			System.out.println("Two operators are adjacent to each other");
			return null;
		}
		
		System.out.println("RightOperand: "+ expression.substring(operatorIndex + 1, index));
		return expression.substring(operatorIndex + 1, index);
	}
	
	public static void main(String[] args) throws IOException {
		InputStreamReader isr  = new InputStreamReader(System.in);
		BufferedReader    br   = new BufferedReader(isr);
		String input = br.readLine(); 
		new StringTest(input);
	}
}
