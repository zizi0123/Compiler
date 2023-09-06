package assembly.Instruction;

import assembly.operand.*;

public class Lw extends ASMIns{
    Reg rd;
    Reg rs;
    Imm offset;

    GlobalVal symbol;

    public Lw(Reg rd, Reg rs, Imm offset,String c){
        this.rd = rd;
        this.rs = rs;
        this.offset = offset;
        comment = c;
    }

    public Lw(Reg rd, GlobalVal symbol, String c) {
        this.rd = rd;
        this.symbol = symbol;
        comment = c;
    }

    @Override
    public String toString() {
        String result = "lw  " + rd.toString() + ", ";
        if (symbol != null) {
            result += symbol.toString() + '\n';
        } else {
            result += offset.toString() + "(" + rs.toString() + ")\n";
        }
        return result;
    }
}
