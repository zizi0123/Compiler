package backend.assembly.Instruction;

import backend.assembly.ASMVisitor;
import backend.assembly.operand.*;

public class Lw extends ASMIns{
    public Reg rd;
    public Reg rs;
    public Imm offset;
    public GlobalVal symbol;

    public Lw(Reg rd, Reg rs, Imm offset,String c){
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
        rd.size = 4;
    }

    public Lw(Reg rd, GlobalVal symbol, String c) {
        this.rd = rd;
        this.symbol = symbol;
        comment = c;
        rd.size = 4;
    }

    @Override
    public String toString() {
        String result = "lw  " + rd.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString();
        } else {
            result += offset.toString() + "(" + rs.toString() + ")";
        }
        result += "       #" + comment;
        return result;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}