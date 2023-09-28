package ast;

import ast.stmt.BlockStmtNode;
import ast.util.position.Position;

public class ClassConstructorNode extends ASTNode{
    public String className;
    public BlockStmtNode blockStmt;

    public String irFuncName;

    public ClassConstructorNode(Position pos, String name){
        super(pos);
        className = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
