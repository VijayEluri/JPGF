package se.chalmers.cs.gf.GFC.Absyn; // Java Package generated by the BNF Converter.

public class Decl {
  public final String ident_;
  public final Exp exp_;

  public Decl(String p1, Exp p2) { ident_ = p1; exp_ = p2; }

  public <R,A> R accept(se.chalmers.cs.gf.GFC.Absyn.Decl.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof se.chalmers.cs.gf.GFC.Absyn.Decl) {
      se.chalmers.cs.gf.GFC.Absyn.Decl x = (se.chalmers.cs.gf.GFC.Absyn.Decl)o;
      return this.ident_.equals(x.ident_) && this.exp_.equals(x.exp_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.ident_.hashCode())+this.exp_.hashCode();
  }

  public interface Visitor <R,A> {
    public R visit(se.chalmers.cs.gf.GFC.Absyn.Decl p, A arg);

  }

}