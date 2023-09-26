package frontend.ast;

import frontend.ast.util.position.Position;

import java.util.ArrayList;

public class FuncParameterListNode extends ASTNode {
    public ArrayList<SingleParameter> parameters = new ArrayList<>();

    public FuncParameterListNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }
}