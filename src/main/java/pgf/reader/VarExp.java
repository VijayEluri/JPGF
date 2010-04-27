package reader;

public class VarExp extends Expr{
   private int ind;

 public VarExp(int _ind) 
  {ind = _ind;}

 public String toString()
 {return "Variable Expression : [Index : "+ind+"]";}
 
 public int getVarInd() {return ind;}
}
