package se.chalmers.cs.gf.GFC.Absyn; // Java Package generated by the BNF Converter.

public abstract class ArgVar implements java.io.Serializable {
  public abstract <R,A> R accept(ArgVar.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(se.chalmers.cs.gf.GFC.Absyn.A p, A arg);
    public R visit(se.chalmers.cs.gf.GFC.Absyn.AB p, A arg);

  }

}