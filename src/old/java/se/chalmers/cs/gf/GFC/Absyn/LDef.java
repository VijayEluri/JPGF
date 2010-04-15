package se.chalmers.cs.gf.GFC.Absyn; // Java Package generated by the BNF Converter.

public class LDef extends Line {
  public final Def def_;

  public LDef(Def p1) { def_ = p1; }

  public <R,A> R accept(se.chalmers.cs.gf.GFC.Absyn.Line.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof se.chalmers.cs.gf.GFC.Absyn.LDef) {
      se.chalmers.cs.gf.GFC.Absyn.LDef x = (se.chalmers.cs.gf.GFC.Absyn.LDef)o;
      return this.def_.equals(x.def_);
    }
    return false;
  }

  public int hashCode() {
    return this.def_.hashCode();
  }


}