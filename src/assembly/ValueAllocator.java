package assembly;

import assembly.operand.PhysicalReg;
import assembly.operand.StackVal;
import assembly.operand.VirtualReg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class ValueAllocator {

    HashMap<PhysicalReg, VirtualReg> calleeSaveTo = new HashMap<>(); //被保存的callee save reg
    PhysicalReg[] regs = new PhysicalReg[32];
    HashMap<String, PhysicalReg> ASMName2Preg = new HashMap<>();
    ArrayList<VirtualReg> virtualRegs = new ArrayList<>();
    public static ArrayList<PhysicalReg> callerSaveRegs, calleeSaveRegs;
    public HashMap<String,VirtualReg> irReg2asmReg = new HashMap<>();
    public HashMap<String, StackVal> irVar2Stack = new HashMap<>();

    ValueAllocator() {
        for (int i = 0; i < 32; ++i) {
            regs[i] = new PhysicalReg(i);
        }
        regs[0].name = "zero";
        regs[1].name = "ra";   //return address
        regs[2].name = "sp";   //stack pointer
        regs[3].name = "gp";   //global pointer
        regs[4].name = "tp";   //thread pointer
        regs[5].name = "t0";   //temporaries
        regs[5].name = "t1";   //temporaries
        regs[6].name = "t2";   //temporaries
        regs[7].name = "s0";   //saved regs
        regs[8].name = "s1";   //saved regs
        for (int i = 0; i < 8; ++i) {
            regs[i + 10].name = "a" + i; //arguments(a0,a1 :return values)
        }
        for (int i = 2; i < 12; ++i) {
            regs[i + 16].name = "s" + i; //saved regs
        }
        for (int i = 3; i < 7; ++i) {
            regs[i + 25].name = "t" + i; //temporaries
        }
        for (int i = 0; i < 32; ++i) {
            ASMName2Preg.put(regs[i].name, regs[i]);
        }
        calleeSaveRegs.add(regs[8]);
        calleeSaveRegs.add(regs[9]);
        calleeSaveRegs.addAll(Arrays.asList(regs).subList(18, 28));

        callerSaveRegs.add(regs[1]);
        callerSaveRegs.addAll(Arrays.asList(regs).subList(5,8));
        callerSaveRegs.addAll(Arrays.asList(regs).subList(10,18));
        callerSaveRegs.addAll(Arrays.asList(regs).subList(28,32));
    }

    public VirtualReg getNewVirtualReg(){
        VirtualReg r = new VirtualReg(virtualRegs.size());
        virtualRegs.add(r);
        return r;
    }

    public VirtualReg getAsmReg(String irRegName){
        if(irReg2asmReg.containsKey(irRegName)){
            return irReg2asmReg.get(irRegName);
        }
        var vreg = getNewVirtualReg();
        irReg2asmReg.put(irRegName,vreg);
        return vreg;
    }

    public StackVal getStackVal(String irVarName){
        if(irVar2Stack.containsKey(irVarName)) {
            return irVar2Stack.get(irVarName);
        }
        StackVal stackVal = new StackVal();
        irVar2Stack.put(irVarName,stackVal);
        return stackVal;
    }



    public PhysicalReg getPReg(String name){
        return ASMName2Preg.get(name);
    }

}
