// ECE309 Lab10 - Akash Kothari & Alex White
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GraphingCalculator implements ActionListener {
	JFrame 		calcWindow = new JFrame();
	JPanel 		northPanel = new JPanel();
	JPanel      southPanel = new JPanel();
	JTextField  enterExp   = new JTextField(20);
	JTextField  error	   = new JTextField(30);
	JTextField  xText	   = new JTextField(10);
	JTextField  xIncText   = new JTextField(10);
	JLabel 		xLabel	   = new JLabel("for X=");
	JLabel      xIncLabel  = new JLabel("with X increments of");
	JTextArea   outputText = new JTextArea();
	JScrollPane outputPane = new JScrollPane(outputText);
	JButton     clearBut   = new JButton("Clear");
	JButton		recallBut  = new JButton("Recall");
	String		lastExp;
	String      printExp;
	
	GraphingCalculator gc;
		
	/* Information about the axes values to be plotted */	
	
	
	public static void main(String[] args) 
	{
		System.out.println("ECE309 Lab10 - Akash Kothari & Alex White");
		new GraphingCalculator();
	}
	public GraphingCalculator() 
	{
		calcWindow.setTitle("Expression Calculator Window");
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setSize(850,500);
		northPanel.add(enterExp);
		northPanel.add(recallBut);
		northPanel.add(xLabel);
		northPanel.add(xText);
		calcWindow.getContentPane().add(northPanel, "North");
		calcWindow.add(outputPane, BorderLayout.CENTER);
		southPanel.add(xIncLabel);
		southPanel.add(xIncText);
		southPanel.add(error);
		southPanel.add(clearBut);
		calcWindow.getContentPane().add(southPanel, "South");
		
		calcWindow.setVisible(true);
		outputText.setEditable(false); 
		error.setBackground(Color.pink);
		error.setForeground(Color.black);
		outputText.setFont(new Font("default",Font.BOLD,20));
		xText.setFont(new Font("default",Font.PLAIN,20));
		xLabel.setFont(new Font("default",Font.PLAIN,20));
		enterExp.setFont(new Font("default",Font.PLAIN,20));
		recallBut.setFont(new Font("default",Font.PLAIN,20));
		clearBut.setFont(new Font("default",Font.PLAIN,20));
		error.setFont(new Font("default",Font.PLAIN,15));
		xIncText.setFont(new Font("default",Font.PLAIN,20));
		xIncLabel.setFont(new Font("default",Font.PLAIN,20));
		
		recallBut.addActionListener(this);
		clearBut.addActionListener(this);
		enterExp.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) 
	{
		if (ae.getSource() == enterExp)
			{
			lastExp = enterExp.getText();
			if(!xIncText.getText().isEmpty() && !xText.getText().isEmpty() && enterExp.getText().contains("x")) 
				{
				double incCheck;
				try {
					incCheck = Double.parseDouble(xIncText.getText().trim());
				} catch (Exception e) {
					error.setText("Invalid x-increment, only enter an integer or decimal value");
					return;
				}
				
				if(incCheck <= 0) {error.setText("x-increment must be a positive value"); return;}
				error.setText("");
				StartGraph(enterExp.getText());
				return;
				}
			if(!xIncText.getText().isEmpty() && !xText.getText().isEmpty() && !enterExp.getText().contains("x")) 
			    {
				error.setText("x is not contained in expression");
				return;
			    }
			if(!xIncText.getText().isEmpty() && xText.getText().isEmpty())
				{
				error.setText("x-increment given but no initial x is given");
				return;
				}
			expressionCalculator(enterExp.getText());
			}
		if (ae.getSource() == clearBut) // this function is handled "locally"
	     {
			 enterExp.setText("");
		     xText.setText("");
			 error.setText("");
			 xIncText.setText("");
			 return;
	     }	
		if (ae.getSource() == recallBut)
		{
			enterExp.setText(lastExp);
		}
	}

	public void StartGraph(String expression) 
	{
		graphExpression(expression);
		
		JFrame            graphWindow= new JFrame(expression + " Press and hold mouse to show x and y values on the graph");
		RefreshGraphPanel graphPanel = new RefreshGraphPanel(expression, xAxis, yAxis, yPrint);
		graphWindow.getContentPane().add(graphPanel, "Center");
		graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		graphWindow.setSize(750,500); 
		graphWindow.setVisible(true);
	}
	
	public void StringTest(String expression) {
		graphExpression(expression);
		//expressionCalculator(expression);
	}
	
/************ SIMPLE EXPRESSION CALCULATOR METHODS ***************/
/* String in the textfield that contains the value of x entered by the user */	
	private String x; 	
	
	private void expressionCalculator(String expression) {
		expression = expression.trim();
		printExp   = expression;
		x 		   = xText.getText();
	
		/* Error Checking */
		if((expression = lexicalErrorCheck(expression)) == null)
			return;
    
	/* Replace all x's in the expression with the x value if specified */
	boolean xCheck = false;
	if(!(xText.getText().isEmpty())) {
		if(!expression.contains("x")) {
			error.setText("x value specified, but expression contains no x");
			return;
		}
	}
	if(expression.contains("x") && xText.getText().isEmpty()) {
		//System.out.println("No value of x specified");
		error.setText("No value of x specified");
		return;
	}
		if(!xText.getText().isEmpty()) {
			double x_value;
			try {
				x_value = Double.parseDouble(x.trim());
			} catch (Exception e) {
				//System.out.println("Invalid x");
				error.setText("Invalid x, only enter an integer or decimal value");
				return;
			}
				
		/* But brackets around x value if x is negative */	
			if(x_value < 0) {
				while(expression.contains("x"))
					expression = expression.replace("x", "(" + String.valueOf(x_value) + ")");
				xCheck = true;
			} else {
				while(expression.contains("x"))
					expression = expression.replace("x", String.valueOf(x_value));
				xCheck = true;
			}			
		} else {
			if(expression.contains("x") && xText.getText().isEmpty()) {
				//System.out.println("No value of x specified");
				error.setText("No value of x specified");
				return;
			}
		}
				
	/* Replace all 'pi's and 'e's in the string with the actual values */
		while(expression.contains("pi"))
			expression = expression.replace("pi", String.valueOf(Math.PI));
		while(expression.contains("e"))
			expression = expression.replace("e", String.valueOf(Math.E));	
			
		// Check if any other illegal characters are still in expression   */
			expression = expression.replaceAll("\\s","");
	        if(!expression.matches("[0-9+=*/^()-.r]+")){
	        	//System.out.println("Expression contains illegal characters");
	        	error.setText("Expression contains illegal characters");
	        	return;
	        }	
		
	/* Solve the expression now */	
		String result = computeExpression(expression);
		if(result == null) {
			if(error.getText().isEmpty()) {
				error.setText("Erroneous expression");
				return;
				}
		} else {
			//System.out.println("Result: " + result);
			if(xCheck == true) {
				outputText.append(printExp + " = " + result + " for x = " + x + '\n');
				error.setText("");
			}
			else {
			outputText.append(printExp + " = " + result + '\n');
			error.setText("");
			}
		}	
	}
	
	private String lexicalErrorCheck(String expression) {	
	/* Check if expression starts with '(' and/or ')' */
		if(expression.charAt(0) == ')') {
			//System.out.println("Expression cannot start with ')'");
			error.setText("Expression cannot start with ')'");
			return null;
		}
		if(expression.endsWith("(") == true) {
			//System.out.println("Expression cannot end with '('");
			error.setText("Expression cannot end with '('");
			return null;
		}
	
	/* If there is an imbalance in brackets in the expression, an error needs returning */	
		if(checkBracketsBalance(expression) == null) {
			//System.out.println("Erroneous expression since brackets are not balanced");
			error.setText("Erroneous expression since brackets are not balanced");
			return null;
		}
		
	/* Check if the expression contains with "()" */
		if(expression.replaceAll("\\s","").contains("()") == true) {
			//System.out.println("Cannot have empty brackets in the expression");
			error.setText("Cannot have empty brackets in the expression");
			return null;
		}
		
	/* Check if expression contains ")(" */
		if(expression.replaceAll("\\s","").contains(")(") == true) {
			//System.out.println("Cannot have ')(' in the expression");
			error.setText("Cannot have ')(' in the expression");
			return null;
		}				
		
	/* Check if the numbers do not have operators between them */
		String[] splitExp = expression.split("[ ()]+");
		int index = 0;
		while(index != splitExp.length - 1) {
		/* Skip if the string is empty */
			if(splitExp[index].isEmpty() == true) {
				index++;
				continue;
			}
		//System.out.println(splitExp[index+1]);
		//System.out.println(index);
		/* Check if the numbers have no operators between them */	
			if(stringEndsWithAnOperator(splitExp[index]) == false			 
			&& stringStartsWithAnOperator(splitExp[index + 1]) == false) {  
			/* There is no operator between the operands */	
				//System.out.println("There is no operator between two numbers");
				error.setText("There is no operator between two numbers");
				return null;
			}
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
				//System.out.println("operator used before ')' or at the end of expression");
				error.setText("operator used before ')' or at the end of expression");
				return null;
			}
				
		/* Check if there is an operator right after the last ')' between the strings */
			if(index != splitExp.length - 1) {
				if(stringStartsWithAnOperator(splitExp[index + 1].trim()) == false) {
					//System.out.println("no operator used after ')'");
					error.setText("no operator used after ')'");
					return null;
				}
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
					//System.out.println("operator used after '(' or at the beginning of expression");
					error.setText("operator used after '(' or at the beginning of expression");
					return null;
				}
			}
						
		/* Check if there is an operator right before the first '(' between the strings */
			if(index > 1) {
				if(stringEndsWithAnOperator(splitExp[index - 1].trim()) == false) {
					//System.out.println("no operator used before'('");
					error.setText("no operator used before'('");
					return null;
				}
			} 	

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
	
	/* Iterate over the array of strings */
		int index = 0;
		while(index != stringArray.length) {	
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
					
	/* Now we can compute the expression */
		String finalResult = computeExpression_WithoutBrackets(simplified_exp);
		if(finalResult == null)
			return null;
		
	/* Get rid of the brackets if any */
		if(finalResult.contains("(") == true 
		&& finalResult.contains(")") == true) {
			finalResult = finalResult.substring(1, finalResult.length() - 1);
		}
		
		return finalResult;		
	}
	
	private boolean stringEndsWithAnOperator(String givenString) {
		if(givenString.endsWith("+") == false
		&& givenString.endsWith("-") == false  
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
		&& givenString.charAt(0) != '-'		
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
	}
		
	private String solveExponents(String expression) {
	/* Solve the operands on either side of ^ */
		while(expression.contains("^") == true) {
			int operatorIndex = expression.indexOf("^");
							
		/* Since exponent and root have the same precedence, all the roots before 
		 * this expoent need to be solved first.
		 */
			String newReplacement = solveRoots(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */	
		/*********************** NEWLY ADDED START **************************/	
			String partString = expression.substring(operatorIndex);
			expression = newReplacement + partString;
		/*********************** NEWLY ADDED END **************************/	
			//expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("^");		
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
					
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
					
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			
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
				//System.out.println("Exponent could not be computed");
				error.setText("Exponent could not be computed");
				return  null;
			}
								
		/* If the result is negative, enclose it within brackets */	
			String result_string = String.valueOf(result);
			if(result < 0)
				result_string = "(" + result_string + ")";					
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "^" + rightOperand;
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveRoots(String expression) {
	/* Solve the operands on either side of r */
		while(expression.contains("r") == true) {
			int operatorIndex = expression.indexOf("r");
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}			
			
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
				
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
					
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
				
			double result;
			try {
				result = Math.pow(left_operand, 1/right_operand);
			} catch (Exception e) {
				//System.out.println("Root could not be computed");
				error.setText("Root could not be computed");
				return null;
			}
	
		/* If the result is negative, enclose it within brackets */	
			String result_string = String.valueOf(result);
			if(result < 0)
				result_string = "(" + result_string + ")";					
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "r" + rightOperand;
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveMultiplications(String expression) {
	/* Solve the operands on either side of * */
		while(expression.contains("*") == true) {
			int operatorIndex = expression.indexOf("*");
				
		/* Since division and multiplication have the same precedence, solve all 
		 * divisions before this multiplication.
		 */	
			String newReplacement = solveDivisions(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */	
			/*********************** NEWLY ADDED START **************************/	
			String partString = expression.substring(operatorIndex);
			expression = newReplacement + partString;
			/*********************** NEWLY ADDED END **************************/	
			//expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("*");				
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
				
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
	
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
				
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand * right_operand;
					
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "*" + rightOperand;
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveDivisions(String expression) {
	/* Solve the operands on either side of / */
		while(expression.contains("/") == true) {
			int operatorIndex = expression.indexOf("/");
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
			
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
			
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
			
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
			
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
							
			double result = left_operand / right_operand;
					
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";		
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "/" + rightOperand;
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}
	
	private String solveAdditions (String expression) {
	/* Solve the operands on either side of + */
		while(expression.contains("+") == true) {
			int operatorIndex = expression.indexOf("+");
							
		/* Since additionand subtraction have the same precedence, solve all 
		 * subtractions before this addition.
		 */	
			String newReplacement = solveSubtractions(expression.substring(0, operatorIndex));
			if(newReplacement == null)
				return null;
			
		/* Update the expression and operator index */
			/*********************** NEWLY ADDED START **************************/		
			String partString = expression.substring(operatorIndex);
			expression = newReplacement + partString;
			/*********************** NEWLY ADDED END **************************/	
			//expression = expression.replace(expression.substring(0, operatorIndex), newReplacement);
			operatorIndex = expression.indexOf("+");				
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
				
		/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}		
					
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
			
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
		
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand + right_operand;
				
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";				
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "+" + rightOperand;
			expression = expression.replace(regex, result_string);		
		}	
		return expression.trim();
	}
	
	private String solveSubtractions(String expression) {
	/* Solve the operands on either side of - */
		while(expression.contains("-") == true) {
			int operatorIndex = expression.indexOf("-");
			
		/* Test if expression is just a negative number */
			String mainString;
			if(expression.charAt(0) == '(' && expression.endsWith(")")) 
				mainString = expression.substring(1, expression.length() - 1);
			else 
				mainString = expression;
		
			try{	
				Double.parseDouble(mainString.trim());
				
			/* Check of expression has brackets */	
				if(expression.charAt(0) != '(' && expression.endsWith(")") == false) {
					expression = "(" + expression + ")";
				}
				return expression.trim();
			} catch (Exception e) {
			/* The negative sign being referred to as an operator is a minus sign infront of
			 * a number, then we change the operator index to the next minus if we find one.
			 */
				while(expression.charAt(operatorIndex - 1) == '(') {
					if(expression.substring(operatorIndex + 1).contains("-") == true) {
						int index = operatorIndex + 1;
						while(expression.charAt(index) != '-')
							index++;
						operatorIndex = index;
					} else {
						return expression.trim();
					}
				}
			}
			
		/* Get the left operand */
			String leftOperand = getLeftOperand(expression, operatorIndex);
			if(leftOperand == null)
				return null;
				
			/* Get rid of the brackets if the substring has any */	
			String left_mainSubstring = leftOperand;
			if(left_mainSubstring.contains(")") == true 
			&& left_mainSubstring.contains("(") == true) {
				left_mainSubstring = 
					left_mainSubstring.substring(1, left_mainSubstring.length() - 1);
			}
			
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
			
		/* Get rid of the brackets if the substring has any */	
			String right_mainSubstring = rightOperand;
			if(right_mainSubstring.contains(")") == true 
			&& right_mainSubstring.contains("(") == true) {
				right_mainSubstring = 
						right_mainSubstring.substring(1, right_mainSubstring.length() - 1);
			}
		
			double right_operand;
			try{
				right_operand = Double.parseDouble(right_mainSubstring.trim());
			} catch (Exception e) {
				return null;
			}
			
			double result = left_operand - right_operand;
					
		/* If result is negative, enclose the epression in brackets */	
			String result_string = String.valueOf(result);
			if(result < 0) 
				result_string = "(" + result_string + ")";					
			
		/* Replace the regex with the computed result */	
			String regex = leftOperand + "-" + rightOperand;
			expression = expression.replace(regex, result_string);
		}	
		return expression.trim();
	}

	private String getLeftOperand(String expression, int operatorIndex) {
	/* Check if there is any valid left operand */	
		if(operatorIndex == 0) {
			//System.out.println("Invalid expression");
			error.setText("Invalid expression");
			return null;
		}
		
	/* Walk the string backwards to get the left operand */
		int index = operatorIndex - 1;
		while(index != -1
		   && expression.charAt(index) != '+'
		   && expression.charAt(index) != '/'
		   && expression.charAt(index) != '*'
		   && expression.charAt(index) != '^'
		   && expression.charAt(index) != 'r') {		
			if(expression.charAt(index) == '-') {
			/* If two operators are adjacent to each other, it is an error */		
				if(index == operatorIndex - 1) {
					//System.out.println("Two operators are adjacent to each other");
					error.setText("Two operators are adjacent to each other");
					return null;
				}
							
			/* Check whether the operand is negative */
				if(expression.substring(index, operatorIndex).contains(")") == true) {
				/* Check the negative number is actually enclosed in brackets */
					if(expression.charAt(index - 1) == '(') {
						index -= 2;
						break;
					} 
					//System.out.println("Left Operand: Erroneous expression");
					error.setText("Left Operand: Erroneous expression");
					return null;
				}
				
			/* This means that the minus sign is an operator */	
				break;
			}	
			index--;
		}
	
	/* If two operators are adjacent to each other, it is an error */	
		if(index == operatorIndex - 1) {
			//System.out.println("Two operators are adjacent to each other");
			error.setText("Two operators are adjacent to each other");
			return null;
		}
		
		return expression.substring(index + 1, operatorIndex);
	}
	
	private String getRightOperand(String expression, int operatorIndex) {
	/* Check if there is any valid right operand */	
		if(operatorIndex == expression.length() - 1) {
			//System.out.println("Invalid expression");
			error.setText("Invalid expression");
			return null;
		}
		
	/* Walk the string forward to get the right operand */
		int index = operatorIndex + 1;
		while(index != expression.length()
		   && expression.charAt(index) != '+'
		   && expression.charAt(index) != '/'
		   && expression.charAt(index) != '*'
		   && expression.charAt(index) != '^'
		   && expression.charAt(index) != 'r') {		
			if(expression.charAt(index) == '-') {	
			/* If two operators are adjacent to each other, it is an error */		
				if(index == operatorIndex + 1) {
					//System.out.println("Two operators are adjacent to each other");
					error.setText("Two operators are adjacent to each other");
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
					//System.out.println("GetRightOperand: Erroneous expression");
					error.setText("GetRightOperand: Erroneous expression");
					return null;
				}
				
			/* This means that the minus sign is an operator */	
				break;
			}
			index++;
		}
	
	/* If two operators are adjacent to each other, it is an error */	
		if(index == operatorIndex + 1) {
			//System.out.println("Two operators are adjacent to each other");
			error.setText("Two operators are adjacent to each other");
			return null;
		}
		
		return expression.substring(operatorIndex + 1, index);
	}

/***************************** GRAPHING METHODS ********************************/
/* Information about the axes values to be plotted */	
	private double[] xAxis;
	private double[] yAxis;
	private double[] yPrint;
	private double numPrintyVal;
	
	/* Substitute values for Inifinity and NaN */
	private double infinity = 1000000000;  /******** BE CAREFUL IF YOU PLOT THIS VALUE *********/
	private double nan      = -1000000000;	/******** BE CAREFUL IF YOU PLOT THIS VALUE *********/
	
/* The rest of the code assumes that these variables are given */	
 	private String numPoints;  /* Optionally provided */
	private String increment;
	private String initialX;
			
	private void graphExpression(String expression) {
		increment = xIncText.getText();
		initialX  = xText.getText();
		expression = expression.trim();
		
	/* Error Checking */
		if((expression = lexicalErrorCheck(expression)) == null)
			return;
		
	/* Check if increment is given */
		if(increment == null) {
			System.out.println("No increment provided");
			return;
		}
		
		
	/* Check if intial x is given */	
		if(initialX == null) {
			System.out.println("No intial x provided");
			return;
		}
		
	/* Replace all 'pi's and 'e's in the string with the actual values */
		while(expression.contains("pi"))
			expression = expression.replace("pi", String.valueOf(Math.PI));
		while(expression.contains("e"))
			expression = expression.replace("e", String.valueOf(Math.E));		
		
	/* Now we check if the expression graph is symmetric about the y-axis */
		double[] negxYVal = new double[50];
		double[] posxYVal = new double[50];
		
	/* Fill in the array for y values for negative x values */	
		int index = 0;
		while(index != 50) {
			String tryExpression = expression.substring(0);
			while(tryExpression.contains("x"))
				tryExpression = tryExpression.replace("x", "(" + String.valueOf(-50 + index) + ")");
		
		//System.out.println("Try expression: " + tryExpression);	
		/* Compute the expression */
			String result = computeExpression(tryExpression);
			if(result == null) {
				//System.out.println("Errorneous expression");
				error.setText("Errorneous expression");
				return;
			}
			
			negxYVal[index] = Double.parseDouble(result);
			index++;
		}
		
	/* Fill in the array for y values for negative x values */	
		index = 0;
		while(index != 50) {
			String tryExpression = expression.substring(0);
			while(tryExpression.contains("x"))
				tryExpression = tryExpression.replace("x", String.valueOf(index + 1));
		
		/* Compute the expression */
			String result = computeExpression(tryExpression);
			if(result == null) {
				System.out.println("Errorneous expression");
				return;
			}
			
			posxYVal[index] = Double.parseDouble(result);
			index++;
		}	
		
	/* Get increment */
		double givenIncrement = Double.parseDouble(increment);
		
	/* Make sure that the increment is positive just for the sake simplicity */
		if(givenIncrement < 0)
			givenIncrement *= -1;
		
	/* Get xStart */
		double givenStart = Double.parseDouble(initialX);
		
	/* Check if the graph is symmetric about the y-axis */
		if(arraysAreEqual(posxYVal, negxYVal, 50) == true) {
			plotGraph(expression,givenStart, givenIncrement);
			return;
		}
		
	/* Check if the graph is symmetric about the origin */
		if(arraysAreOfOppositeSigns(posxYVal, negxYVal, 50) == true) {
			plotGraph(expression, givenStart, givenIncrement);
			return;
		}	
		
	/* Plot the asymmetric graph */
		plotGraph(expression, givenStart, givenIncrement);		
	}
		
	private void plotGraph (String expression, double xStart, double increment) {
	/* Allocate space to store plotting values */	
		int numValues;
		if(numPoints == null) {
			xAxis = new double[11];
			yAxis = new double[11];
			numValues = 11;
		} else {
			try {
				numValues = Integer.parseInt(numPoints);
			} catch(Exception e) {
				//System.out.println("Could not parse number of points to plot");
				return;
			}
			xAxis = new double[numValues];
			yAxis = new double[numValues];
		}
		
	/* Fill the arrays with the values of x and corresponding values of y */	
		int index = 0;
		while(index != numValues) {
			xAxis[index] = xStart + index*increment;
			index++;
		}
		index = 0;
		while(index != numValues) {
			double x_value = xAxis[index];
			String tryExpression = expression.substring(0);	
			if(x_value < 0) {
				while(tryExpression.contains("x"))
					tryExpression = tryExpression.replace("x", "(" + String.valueOf(x_value) + ")");
			} else {
				while(tryExpression.contains("x"))
					tryExpression = tryExpression.replace("x", String.valueOf(x_value));
			}			
			
			String computedExpression = computeExpression(tryExpression);
			if(computedExpression ==  null) 
				return;
			
			if(computedExpression.contains("Infinity")) {
				yAxis[index] = infinity;
			} else {
				if(computedExpression.contains("NaN")) {
					yAxis[index] = nan;
				} else {
					yAxis[index] = Double.parseDouble(computedExpression);
				}
			}
			index++;
		}
		
	/* Graph the expression */	
		printAxes();  /***** REPLACE WITH PLOTTING ROUTINE ******/	
		
		double yMax = Double.parseDouble(getMaxY());
		double yMin = Double.parseDouble(getMinY());
		getPrintYVal(yMin, yMax);
	}
	
	/* Methods that can be used to get the maximum and minimum values of y and x that have been used to plot */	
	private String getMaxY() {
		if(yAxis == null)
			return null; 
		
		double max = yAxis[0];
		int index = 0;
		int num;
		if(numPoints != null)
			num = Integer.parseInt(numPoints);
		else 
			num = 11;
		while(index != num) {
			if(yAxis[index] > max)
				max = yAxis[index];
			index++;
		}
		return String.valueOf(max);
	}
	
	private String getMinY() {
		if(yAxis == null)
			return null; 
		
		double min = yAxis[0];
		int index = 0;
		int num;
		if(numPoints != null)
			num = Integer.parseInt(numPoints);
		else 
			num = 11;
		while(index != num) {
			if(yAxis[index] < min)
				min = yAxis[index];
			index++;
		}
		return String.valueOf(min);
	}
	
	private void getPrintYVal(double  yMin, double yMax) {
		  int plotRange, initialIncrement, upperIncrement, 
		     lowerIncrement, selectedIncrement, numberOfYscaleValues,
		     lowestYscaleValue, highestYscaleValue;
		  String zeros = "0000000000";
		  
	  // 1) Determine the RANGE to be plotted.
	  double dPlotRange = yMax - yMin;
	  //System.out.println("Plot range (Ymax-Ymin) = " + dPlotRange);

	  // 2) Determine an initial increment value.
	  if (dPlotRange > 10)
	     {
		 plotRange = (int)dPlotRange;
		 //System.out.println("Rounded plot range = " + plotRange);
	     }
	  else
	     {
		 //System.out.println("Add handling of small plot range!");
		 return;
	     }
	/*ASSUME*/ // 10 scale values as a starting assumption.
	  initialIncrement = plotRange/10;
	  //System.out.println("Initial increment value = " + initialIncrement);
	  // Please excuse this clumsy "math"!
	  String initialIncrementString = String.valueOf(initialIncrement);
	  //System.out.println("InitialIncrementString = " + initialIncrementString + " (length = " + initialIncrementString.length() + ")");

	  // 3) Find even numbers above and below the initial increment. 
	  String leadingDigit = initialIncrementString.substring(0,1);
	  int leadingNumber = Integer.parseInt(leadingDigit);
	  int bumpedLeadingNumber = leadingNumber + 1;
	  String bumpedLeadingDigit = String.valueOf(bumpedLeadingNumber);
	  String upperIncrementString = bumpedLeadingDigit + zeros.substring(0,initialIncrementString.length()-1);
	  String lowerIncrementString = leadingDigit       + zeros.substring(0,initialIncrementString.length()-1);
	  upperIncrement = Integer.parseInt(upperIncrementString);
	  lowerIncrement = Integer.parseInt(lowerIncrementString);
	  //System.out.println("Upper increment alternative = " + upperIncrement);
	  //System.out.println("Lower increment alternative = " + lowerIncrement);

	  // 4) Pick the upper or lower even increment depending on which is closest.
	  int distanceToUpper = upperIncrement - initialIncrement;
	  int distanceToLower = initialIncrement - lowerIncrement;
	  if (distanceToUpper > distanceToLower)
		  selectedIncrement = lowerIncrement;
	    else
	      selectedIncrement = upperIncrement;
	  //
	  //System.out.println("The closest even increment (and therefore the one chosen) = " + selectedIncrement);

	  // 5) Determine lowest Y scale value
	  numberOfYscaleValues = 0;
	  lowestYscaleValue    = 0;
	  if (yMin < 0)
	     {
	     for (; lowestYscaleValue > yMin; lowestYscaleValue-=selectedIncrement)
	          numberOfYscaleValues++;
	     }
	  if (yMin > 0)
	     {
		 for (; lowestYscaleValue < yMin; lowestYscaleValue+=selectedIncrement)
		      numberOfYscaleValues++;
	     numberOfYscaleValues--;
	     lowestYscaleValue -= selectedIncrement;
	     }
	  //System.out.println("The lowest Y scale value will be " + lowestYscaleValue + ")");
	  
	  
	  // 6) Determine upper Y scale value
	  numberOfYscaleValues = 1;
	  for (highestYscaleValue = lowestYscaleValue; highestYscaleValue < yMax; highestYscaleValue+=selectedIncrement)
		  numberOfYscaleValues++;
	  //System.out.println("The highest Y scale value will be " + highestYscaleValue);
	  //System.out.println("The number of Y scale click marks will be " + numberOfYscaleValues);
	  if ((numberOfYscaleValues < 5) || (numberOfYscaleValues > 20))
	     {
		 //System.out.println("Number of Y scale click marks is too few or too many!");
		 return;
	     }
	  
	 /* Allocate the print y array */
	  yPrint = new double[numberOfYscaleValues];
	  
	  // 7) Determine if Y scale will be extended to include the 0 point.
	 /* if ((lowestYscaleValue < 0) && (highestYscaleValue > 0))
	       System.out.println("The Y scale includes the 0 point.");
	    else // Y scale does not include 0.
	     {   //	Should it be extended to include the 0 point?
	     if ((lowestYscaleValue > 0) && (lowestYscaleValue/selectedIncrement <= 3))
	        {
	    	lowestYscaleValue = 0;
	    	System.out.println("Lower Y scale value adjusted down to 0 to include 0 point. (Additional click marks added.)");
	        }
	     if ((highestYscaleValue < 0) && (highestYscaleValue/selectedIncrement <= 3))
	        {
	     	highestYscaleValue = 0;
	    	System.out.println("Upper Y scale value adjusted up to 0 to include 0 point. (Additional click marks added.)");
	        }
	     }*/
	  int yScaleValue = lowestYscaleValue;
	  int index = 0;
	  while(index < numberOfYscaleValues)
	       {
		   //System.out.print(yScaleValue + ",");
		   yPrint[index] = yScaleValue;
		   yScaleValue += selectedIncrement;
		   index++;
	       }
	  
	  numPrintyVal = numberOfYscaleValues;
	 // System.out.println(yScaleValue);
	  
	 /* Print the array values */ 
	  print_printY_values();
	}
	
	private void printAxes() {
		int index = 0;
		int num;
		if(numPoints != null)
			num = Integer.parseInt(numPoints);
		else 
			num = 11;
		while(index != num) {
			//System.out.println("X: " + xAxis[index] + "  Y: " + yAxis[index]);
			index++;
		}
	}
	
	private void print_printY_values() {
		int index = 0;
		while(index != numPrintyVal) {
			//System.out.println("Print y value: " + yPrint[index]);
			index++;
		}
	}
	
	private boolean arraysAreEqual(double[] array1, double[] array2, int arrayLength) {
		int index = 0;
		while(index != arrayLength) {
			if(array1[index] != array2[index])
				return false;
			index++;
		}
		return true;
	}
	
	private boolean arraysAreOfOppositeSigns(double[] array1, double[] array2, int arrayLength) {
		int index = 0;
		while(index != arrayLength) {
			if(array1[index] + array2[index] != 0)
				return false;
			index++;
		}
		return true;
	}
}
