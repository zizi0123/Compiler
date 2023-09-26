package backend.assembly;

import backend.IR.Entity.variable.GlobalVar;
import backend.assembly.operand.GlobalString;
import backend.assembly.operand.GlobalVal;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {

    public HashMap<String, GlobalString> globalStrings = new HashMap<>();

    public ArrayList<ASMFunction> functions = new ArrayList<>();

    public HashMap<GlobalVar, GlobalVal> gbIr2Asm = new HashMap<>();

    public void print() {
        for (var func : functions) {
            func.print();
        }
        for (var gv : gbIr2Asm.values()) {
            gv.print();
        }
        for (var gs : globalStrings.values()) {
            gs.print();
        }
    }


}
