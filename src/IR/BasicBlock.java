package IR;

import IR.instruction.Instruction;
import IR.instruction.PhiIns;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class BasicBlock {
    public String label;
    public ArrayList<Instruction> instructions = new ArrayList<>();
    public Instruction exitInstruction;

    //opt
    public LinkedList<BasicBlock> preds = new LinkedList<>();
    public LinkedList<BasicBlock> succs = new LinkedList<>();
    public HashSet<BasicBlock> dom = new HashSet<>();
    public BasicBlock domTreeDad;
    public HashSet<BasicBlock> domFrontier = new HashSet<>();
    public HashSet<BasicBlock> domTreeSons = new HashSet<>();
    public ArrayList<PhiIns> phis = new ArrayList<>();


    public BasicBlock(String name) {
        this.label = name;
    }

    public void addIns(Instruction ins) {
        if(ins instanceof PhiIns phiIns){
            phis.add(phiIns);
        }else {
            instructions.add(ins);
        }
    }

    public void print(PrintWriter pw) {
        if (instructions.isEmpty()) return;
        pw.println(label + ":");
        for (var ins : phis) {
            pw.print("  " + ins.toString());
        }
        for (var ins : instructions) {
            pw.print("  " + ins.toString());
        }
    }

    public void print() {
        if (instructions.isEmpty() && phis.isEmpty()) return;
        System.out.println(label + ":");
        for (var phi : phis) {
            System.out.print("  " + phi.toString());
        }
        for (var ins : instructions) {
            System.out.print("  " + ins.toString());
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
