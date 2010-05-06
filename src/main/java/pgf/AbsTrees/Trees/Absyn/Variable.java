package Trees.Absyn; // Java Package generated by the BNF Converter.

public class Variable extends Tree {
  public final Integer integer_;

  public Variable(Integer p1) { integer_ = p1; }

  public <R,A> R accept(Trees.Absyn.Tree.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Trees.Absyn.Variable) {
      Trees.Absyn.Variable x = (Trees.Absyn.Variable)o;
      return this.integer_.equals(x.integer_);
    }
    return false;
  }

  public int hashCode() {
    return this.integer_.hashCode();
  }


}