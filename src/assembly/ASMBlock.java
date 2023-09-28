package assembly;

import assembly.Instruction.ASMIns;
import assembly.operand.Reg;

import java.util.ArrayList;
import java.util.HashSet;

public class ASMBlock {
    public String name;

    public String comment;

    public int succSize; //在未将所有块确定前，就需要确定后继节点的数目，以便phi指令的分析
    public ArrayList<ASMBlock> succs = new ArrayList<>();
    public ArrayList<ASMBlock> preds = new ArrayList<>();

    public HashSet<Reg> def = new HashSet<>();
    public HashSet<Reg> use = new HashSet<>();
    public HashSet<Reg> in = new HashSet<>();
    public HashSet<Reg> out = new HashSet<>();


    public ArrayList<ASMIns> instructions = new ArrayList<>();

    public ArrayList<ASMIns> exitInses = new ArrayList<>();

    public ASMBlock(String n, String c,int s) {
        name = n;
        comment = c;
        succSize = s;
    }

    public void addIns(ASMIns i) {
        instructions.add(i);
    }

    public void addFirst(ASMIns i) {
        instructions.add(0,i);
    }

    public void print() {
        if(instructions.isEmpty() && exitInses.isEmpty()) return;
        System.out.print(name + ":\t#" + comment + "\n");
        for (var ins : instructions) {
            System.out.print("\t" + ins.toString());
        }
        if (!exitInses.isEmpty()) {
            for(var ins:exitInses){
                System.out.print("\t" + ins.toString());
            }
        }
    }

}
