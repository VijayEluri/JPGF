package org.grammaticalframework.Trees.Absyn; // Java Package generated by the BNF Converter.

public class Lambda extends Tree {
  public final String ident_;
  public final Tree tree_;

  public Lambda(String p1, Tree p2) { ident_ = p1; tree_ = p2; }

  public <R,A> R accept(org.grammaticalframework.Trees.Absyn.Tree.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof org.grammaticalframework.Trees.Absyn.Lambda) {
      org.grammaticalframework.Trees.Absyn.Lambda x = (org.grammaticalframework.Trees.Absyn.Lambda)o;
      return this.ident_.equals(x.ident_) && this.tree_.equals(x.tree_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.ident_.hashCode())+this.tree_.hashCode();
  }


}