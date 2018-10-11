import java.lang.Math.*;

class ExpressionTree {
    private String value;
    private ExpressionTree leftChild, rightChild, parent;
    
    ExpressionTree() {
        value = null; 
        leftChild = rightChild = parent = null;
    }
    
    // Constructor
    /* Arguments: String s: Value to be stored in the node
                  ExpressionTree l, r, p: the left child, right child, and parent of the node to created      
       Returns: the newly created ExpressionTree               
    */
    ExpressionTree(String s, ExpressionTree l, ExpressionTree r, ExpressionTree p) {
        value = s; 
        leftChild = l; 
        rightChild = r;
        parent = p;
    }
    
    /* Basic access methods */
    String getValue() { return value; }

    ExpressionTree getLeftChild() { return leftChild; }

    ExpressionTree getRightChild() { return rightChild; }

    ExpressionTree getParent() { return parent; }


    /* Basic setting methods */ 
    void setValue(String o) { value = o; }
    
    // sets the left child of this node to n
    void setLeftChild(ExpressionTree n) { 
        leftChild = n; 
        n.parent = this; 
    }
    
    // sets the right child of this node to n
    void setRightChild(ExpressionTree n) { 
        rightChild = n; 
        n.parent=this; 
    }
    

    // Returns the root of the tree describing the expression s
    // Watch out: it makes no validity checks whatsoever!
    ExpressionTree(String s) {
        // check if s contains parentheses. If it doesn't, then it's a leaf
        if (s.indexOf("(")==-1) setValue(s);
        else {  // it's not a leaf

            /* break the string into three parts: the operator, the left operand,
               and the right operand. ***/
            setValue( s.substring( 0 , s.indexOf( "(" ) ) );
            // delimit the left operand 2008
            int left = s.indexOf("(")+1;
            int i = left;
            int parCount = 0;
            // find the comma separating the two operands
            while (parCount>=0 && !(s.charAt(i)==',' && parCount==0)) {
                if ( s.charAt(i) == '(' ) parCount++;
                if ( s.charAt(i) == ')' ) parCount--;
                i++;
            }
            int mid=i;
            if (parCount<0) mid--;

        // recursively build the left subtree
            setLeftChild(new ExpressionTree(s.substring(left,mid)));
    
            if (parCount==0) {
                // it is a binary operator
                // find the end of the second operand.F13
                while ( ! (s.charAt(i) == ')' && parCount == 0 ) )  {
                    if ( s.charAt(i) == '(' ) parCount++;
                    if ( s.charAt(i) == ')' ) parCount--;
                    i++;
                }
                int right=i;
                setRightChild( new ExpressionTree( s.substring( mid + 1, right)));
        }
    }
    }


    // Returns a copy of the subtree rooted at this node... 2014
    ExpressionTree deepCopy() {
        ExpressionTree n = new ExpressionTree();
        n.setValue( getValue() );
        if ( getLeftChild()!=null ) n.setLeftChild( getLeftChild().deepCopy() );
        if ( getRightChild()!=null ) n.setRightChild( getRightChild().deepCopy() );
        return n;
    }
    
    // Returns a String describing the subtree rooted at a certain node.
    public String toString() {
        String ret = value;
        if ( getLeftChild() == null ) return ret;
        else ret = ret + "(" + getLeftChild().toString();
        if ( getRightChild() == null ) return ret + ")";
        else ret = ret + "," + getRightChild().toString();
        ret = ret + ")";
        return ret;
    } 


    // Returns the value of the expression rooted at a given node
    // when x has a certain value
    static public double surgery( String tool, double surgeon, double patient){
    
    	if ( tool.equals("add")){return(surgeon + patient);}// checks to find the tool used and accordingly returns the patient and surgeon after surgery
    	if ( tool.equals("minus")){return(surgeon - patient);}
    	if ( tool.equals("mult")){return(surgeon * patient);}
    	if ( tool.equals("sin")){return Math.sin(surgeon);}
    	if ( tool.equals("cos")){return Math.cos(surgeon);}
    	if ( tool.equals("exp")){return Math.exp(surgeon);}
    		else return 0;
    	
    }
    double evaluate(double x) {
    	if (getLeftChild() == null){// checks if the left child is null in order to kill recursive calls
    		if (getValue().equals("x")){return x;}// check if the value is x and returns it without doing anything to it
    		else{
    			return Double.parseDouble(getValue());// else parses the function into a double
    		}
    	}
    		else {
    			if (getRightChild() != null){// checks if the right child is not equal to null 
    				return surgery(getValue(), getLeftChild().evaluate(x), getRightChild().evaluate(x));// performs surgery on the whole node in order to find the answer and returns it.
    			}
    			else {
    				return surgery(getValue(),getLeftChild().evaluate(x),0);// if it is null it then performs surgery on the left child alone considering sin cos and exp.
    			}
    		}
    	}                                                 

    /* returns the root of a new expression tree representing the derivative of the
       original expression */
    ExpressionTree differentiate() {
    	
    	 if (getLeftChild()== null){ // checks if the left child is null termination for recursion
    		if( getValue().equals("x")){ return new ExpressionTree("1");}// checks if the value is x and makes it 1 if it is
    			else { return new ExpressionTree("0");// else creates a node with the value zero
    			}
    		}
    
    		if (getValue().equals("add") || getValue().equals("minus")) {// checks if the tool is addition or subtraction
    			return new ExpressionTree(getValue(),getLeftChild().differentiate(),getRightChild().differentiate(),null );// returns a new expression tree that derives the right and the left child recursively calling it
    			}
    		
    		if (getValue().equals("mult")) {// checks if the tool is multiply
    				ExpressionTree l,r;	// initialization of 2 new nodes
    			l=new ExpressionTree("mult", getLeftChild().differentiate(),getRightChild().deepCopy(),null );
    			r=new ExpressionTree("mult", getLeftChild().deepCopy(),getRightChild().differentiate(), null );   			
    			return new ExpressionTree("add", l, r,null);}// returns the derivative of the patient multiplied by the surgeon plus the the derivative of the surgeon multiplied by the patient
    		// basically chain rule
    		
    		if (getValue().equals("sin")) {// checks if the tool is sin
    				ExpressionTree l,r;// initialization of 2 new nodes
    			l = new ExpressionTree("cos", getLeftChild().deepCopy(),null,null);
    			r = getLeftChild().differentiate();
    			return new ExpressionTree("mult",l,r,null);}// another instance of the chain rule so that the correct derivative is returned
    		
    		if (getValue().equals("cos")) {// pretty much the same as sin but i found a way to not make 2 nodes just 1 by making the node make a newer node inside it again chain rule
				ExpressionTree r;
    			r = new ExpressionTree("mult", new ExpressionTree("sin",getLeftChild().deepCopy(), null, null), getLeftChild().differentiate(), null);
    			return new ExpressionTree("minus", new ExpressionTree("0",null,null,null), r, null);}
    		
    		if (getValue().equals("exp")) {// checks if the tool is e^x 
    			return new ExpressionTree("mult", deepCopy(), getLeftChild().differentiate(),null);}// returns a copy of the whole function multiplied by a differentiated version of the left child 
    		
    	
        return null;
    }
    
    
    public static void main(String args[]) {
        ExpressionTree e = new ExpressionTree("mult(x,2)");
        System.out.println(e);
        System.out.println(e.evaluate(1));
        System.out.println(e.differentiate());
   
 }
}
