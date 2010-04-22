package reader;

public abstract class Production {
    private int sel;
    private int fId;
    
    public Production(int selector, int fId) {
	this.sel = selector;
	this.fId = fId;
    }
    
    public abstract String toString();
}
