package assembly;

import assembly.Instruction.ASMIns;

import java.util.ArrayList;

public class ASMBlock {
    public String name;

    public String comment;

    public ArrayList<ASMIns> instructions = new ArrayList<>();

    public ArrayList<ASMIns> exitInses = new ArrayList<>();

    public ASMBlock(String n, String c) {
        name = n;
        comment = c;
    }

    public void addIns(ASMIns i) {
        instructions.add(i);
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
