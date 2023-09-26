package backend.IR.type;

import backend.IR.IRFunction;
import frontend.ast.SingleVarDefNode;

import java.util.ArrayList;
import java.util.HashMap;


public class IRClassType extends IRType {

    HashMap<String, Integer> memberNums = new HashMap<>();

    ArrayList<IRType> memberTypes = new ArrayList<>();

    HashMap<String, Integer> getPtrTimes = new HashMap<>();

    public IRFunction constructor;

    int newNum = 0;

    public IRClassType(String typeName, int bitSize) {
        super(typeName, bitSize);
    }

    public void addType(SingleVarDefNode member) {
        memberNums.put(member.name, memberNums.size());
        memberTypes.add(toIRType(member.type));
        getPtrTimes.put(member.name, 0);
    }

    public int getMemberNum(String memberName) {
        return memberNums.get(memberName);
    }

    public int getGEPtime(String memberName) {
        int a = getPtrTimes.get(memberName);
        getPtrTimes.put(memberName, a + 1);
        return a;
    }

    public int gerNewTime() {
        return newNum++;
    }

    public String toDefineString() {
        String a = typeName;
        a += " = type {";
        for (int i = 0; i < memberTypes.size(); ++i) {
            a += memberTypes.get(i).toString();
            if (i != memberTypes.size() - 1) {
                a += ", ";
            } else {
                a += "}\n";
            }
        }
        return a;
    }

    public String toString() {
        return typeName;
    }


}
