package IR;

import IR.instruction.Instruction;

import java.io.PrintWriter;
import java.util.ArrayList;

public class BasicBlock {
    public String label;
    public ArrayList<Instruction> instructions = new ArrayList<>();
    public Instruction exitInstruction;

    public BasicBlock(String name) {
        this.label = name;
    }

    public void addIns(Instruction ins) {
        instructions.add(ins);
    }

    public void print(PrintWriter pw) {
        if (instructions.isEmpty()) return;
        pw.println(label + ":");
        for (var ins : instructions) {
            pw.print("  " + ins.toString());
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
