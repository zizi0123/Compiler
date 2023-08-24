package IR.type;

import IR.IRFunction;
import ast.SingleVarDefNode;

import java.util.HashMap;


public class IRClassType extends IRType {

    public int bitSize;


    HashMap<String, Integer> memberNums = new HashMap<>();

    HashMap<String, IRType> memberTypes = new HashMap<>();

    HashMap<String, Integer> getPtrTimes = new HashMap<>();

    public IRFunction constructor;

    public IRClassType(String typeName, int bitSize) {
        super(typeName, bitSize);
    }

    public void addType(SingleVarDefNode member) {
        memberNums.put(member.name, memberNums.size());
        memberTypes.put(member.name, toIRType(member.type));
        getPtrTimes.put(member.name, 0);
    }

    public int getMemberNum(String memberName) {
        return memberNums.get(memberName);
    }

    public IRType getMemberType(String memberName) {
        return memberTypes.get(memberName);
    }

    public int getGEPtime(String memberName) {
        int a = getPtrTimes.get(memberName);
        getPtrTimes.put(memberName, a + 1);
        return a;
    }


}
