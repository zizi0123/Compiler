package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import ast.util.position.Position;

public class IfStmtNode extends StmtNode {
    public ExprNode condition;
    public StmtNode trueStmts;
    public StmtNode falseStmts;

    public IfStmtNode(Position pos){
        super(pos);
        falseStmts = null;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
