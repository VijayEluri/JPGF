package se.chalmers.cs.gf.Core.Absyn; // Java Package generated by the BNF Converter.

public abstract class Decl implements java.io.Serializable {
  public abstract <R,A> R accept(Decl.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(se.chalmers.cs.gf.Core.Absyn.DataDecl p, A arg);
    public R visit(se.chalmers.cs.gf.Core.Absyn.TypeDecl p, A arg);
    public R visit(se.chalmers.cs.gf.Core.Absyn.ValueDecl p, A arg);

  }

}