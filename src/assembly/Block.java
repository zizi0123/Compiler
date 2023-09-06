package assembly;

import assembly.Instruction.ASMIns;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;

public class Block {
    public String name;

    public String comment;

    public ArrayList<ASMIns> instructions = new ArrayList<>();

    public ArrayList<ASMIns> exitInses;

    public Block(String n, String c) {
        name = n;
        comment = c;
    }

    public void addIns(ASMIns i) {
        instructions.add(i);
    }

    public void print() {
        System.out.print(name + ":\t" + comment + "\n");
        for (var ins : instructions) {
            System.out.print("\t" + ins.toString());
        }
        if (exitInses != null) {
            System.out.print("\t" + exitInses.toString());
        }
    }

}
