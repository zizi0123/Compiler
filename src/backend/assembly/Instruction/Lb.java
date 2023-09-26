package backend.assembly.Instruction;

import backend.assembly.ASMVisitor;
import backend.assembly.operand.GlobalVal;
import backend.assembly.operand.Imm;
import backend.assembly.operand.Reg;

public class Lb extends ASMIns {
    public Reg rd;
    public Reg rs;
    public Imm offset;

    public GlobalVal symbol;

    public Lb(Reg rd, Reg rs, Imm offset, String c) {
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
        rd.size = 1;
    }

    public Lb(Reg rd, GlobalVal symbol, String c) {
        this.rd = rd;
        this.symbol = symbol;
        comment = c;
        rd.size = 1;
    }

    @Override
    public String toString() {
        String result = "lb  " + rd.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString();
        } else {
            result += offset.toString() + "(" + rs.toString()+")";
        }
        result += "       #" + comment;
        return result;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}