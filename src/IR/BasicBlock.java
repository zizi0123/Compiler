package IR;

import IR.instruction.Instruction;

import java.util.ArrayList;

public class BasicBlock {
    public String label;
    public ArrayList<Instruction> instructions = new ArrayList<>();
    public Instruction exitInstruction; //todo 检查设置exitinstruction的位置是否正确。如果还未给exitins赋值就开始访问Block中的stmts，可能导致下一block的exitins继承本块而丢失exitins.

    public BasicBlock(String name) {
        this.label = name;
    }

    public void addIns(Instruction ins) {
        instructions.add(ins);
    }

    public void print() {
        if(instructions.isEmpty() && exitInstruction == null) return;
        System.out.println(label + ":");
        for (var ins : instructions) {
            System.out.print("  ");
            ins.Print();
        }
        if (exitInstruction != null) {
            System.out.print("  ");
            exitInstruction.Print();
        }
    }
}
