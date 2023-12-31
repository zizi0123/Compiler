package assembly;

import assembly.operand.StackVal;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;

public class ASMFunction {
    public String funcName;

    public ArrayList<ASMBlock> blocks = new ArrayList<>();

    public ASMBlock initBlock;
    public ASMBlock exitBlock;

    public StackVal raStack;  //栈上存放ra的位置。如果本函数不是叶函数，则需要把返回地址储存在栈上的此位置。

    public HashMap<String, ASMBlock> irName2Block = new HashMap<>();

    public ArrayList<StackVal> params = new ArrayList<>(); //上一个栈帧的底部

    public ArrayList<StackVal> stack = new ArrayList<>();


    public int maxArgNum = -1;

    public ASMFunction(String funcName) {
        this.funcName = funcName;
    }


    public void print() {
        System.out.print("\t.text\n" +
                "\t.globl " + funcName + "\n" +
                funcName + ":\n");
        for (var block : blocks) {
            block.print();
        }
        System.out.println();
    }

}
