package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.*;

import java.util.HashSet;

public class Lw extends ASMIns {
    public Reg rd;
    public Reg rs;
    public Imm offset;
    public GlobalVal symbol;

    public Lw(Reg rd, Reg rs, Imm offset, String c) {
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

    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        if (rs != null) {
            result.add(rs);
        }
        return result;
    }

    @Override
    public Reg getDef() {
        return rd;
    }

    @Override
    public void replace(Reg olde, Reg newe) {
        rd = (rd == olde) ? newe : rd;
        if (rs != null) {
            rs = (rs == olde) ? newe : rs;
        }
    }
}
