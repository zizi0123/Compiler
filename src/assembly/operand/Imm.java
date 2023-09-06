package assembly.operand;

import IR.Entity.literal.IntLiteral;
import IR.Entity.literal.Literal;

public class Imm extends Val{
    public int val;

    public Imm(int a){
        val = a;
    }

    public Imm(IntLiteral i){
        val = i.value;
    }

    @Override
    public String toString() {
        return Integer.toString(val);
    }
}
