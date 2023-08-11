package ast;

import ast.stmt.BlockStmtNode;
import util.position.Position;

public class ClassConstructorNode extends ASTNode{
    public String className;

    public BlockStmtNode blockStmt;

    ClassConstructorNode(Position pos,String name){
        super(pos);
        className = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
