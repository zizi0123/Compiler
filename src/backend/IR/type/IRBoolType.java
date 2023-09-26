package backend.IR.type;

public class IRBoolType extends IRType{
    IRBoolType(){
        super("i1",1);
    }

    @Override
    public String toString() {
        return "i1";
    }
}
