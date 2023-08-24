package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import ast.util.position.Position;

public class WhileStmtNode extends StmtNode {
    public ExprNode condition;
    public StmtNode stmts; //todo is an arraylist better?

    public WhileStmtNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
