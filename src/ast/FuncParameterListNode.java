package ast;

import util.position.Position;

import java.util.ArrayList;

public class FuncParameterListNode extends ASTNode{
    public ArrayList<SingleParameter> parameters;

    public FuncParameterListNode(Position pos) {
        super(pos);
        parameters = new ArrayList<>();
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isEmpty(){
        return parameters.isEmpty();
    }
}