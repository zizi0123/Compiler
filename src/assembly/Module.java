package assembly;

import assembly.operand.GlobalString;
import assembly.operand.GlobalVal;
import assembly.operand.StackVal;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {

    public HashMap<String, GlobalString> globalStrings = new HashMap<>();

    public ArrayList<ASMFunction> functions = new ArrayList<>();

    public HashMap<String, GlobalVal> irName2GbVal = new HashMap<>();

    public void print() {
        for (var func : functions) {
            func.print();
        }
        for (var gv : irName2GbVal.values()) {
            gv.print();
        }
        for (var gs : globalStrings.values()) {
            gs.print();
        }
    }


}
