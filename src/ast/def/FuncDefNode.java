package ast.def;

import ast.ASTVisitor;
import ast.FuncParameterListNode;
import ast.SingleParameter;
import ast.stmt.BlockStmtNode;
import util.position.Position;
import util.type.Type;

public class FuncDefNode extends DefinitionNode {
    public Type returnType;
    public String funcName;
    public FuncParameterListNode functionParameterList;
    public BlockStmtNode blockStmt;

    public FuncDefNode(Position pos, String name) {
        super(pos);
        this.funcName = name;
    }

    public FuncDefNode(Position pos, String name, Type returnType, int paraNum, SingleParameter para1, SingleParameter para2) {
        super(pos);
        this.funcName = name;
        this.returnType = returnType;
        this.functionParameterList = new FuncParameterListNode(null);
        if (paraNum != 0) {
            this.functionParameterList.parameters.add(para1);
            if(paraNum == 2){
                this.functionParameterList.parameters.add(para2);
            }
        }
        blockStmt = new BlockStmtNode(null);
    }



    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }


}