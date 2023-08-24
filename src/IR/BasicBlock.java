package IR;

import IR.instruction.Instruction;

import java.util.ArrayList;

public class BasicBlock {
    public String label;
    public ArrayList<Instruction> instructions = new ArrayList<>();
    public Instruction exitInstruction;

    public BasicBlock(String name){
        this.label = name;
    }

    public void addIns(Instruction ins){
        instructions.add(ins);
    }
}
