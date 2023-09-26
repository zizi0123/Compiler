package frontend.ast;

import frontend.ast.stmt.BlockStmtNode;
import frontend.ast.util.position.Position;

public class ClassConstructorNode extends ASTNode{
    public String className;
    public BlockStmtNode blockStmt;

    public String irFuncName;

    ClassConstructorNode(Position pos,String name){
        super(pos);
        className = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
