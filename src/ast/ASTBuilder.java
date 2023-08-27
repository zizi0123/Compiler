package ast;

import ast.def.ClassDefNode;
import ast.def.DefinitionNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import grammar.MxParser;
import grammar.MxParserBaseVisitor;
import ast.util.constValue.*;
import ast.util.error.SemanticError;
import ast.util.error.SyntaxError;
import ast.util.position.Position;
import ast.util.type.ASTType;
import ast.util.type.TypeName;

public class ASTBuilder extends MxParserBaseVisitor<ASTNode> {
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        Position pos = new Position(ctx);
        ProgramNode programNode = new ProgramNode(pos);

        for (var defCtx : ctx.definition()) {
            DefinitionNode defNode = (DefinitionNode) visitDefinition(defCtx);
            if (defNode != null) { //defCtx";" -> null
                programNode.defNodes.add(defNode);
            }
        }
        return programNode;
    }

    @Override
    public ASTNode visitDefinition(MxParser.DefinitionContext ctx) {
        if (ctx.classDef() != null) {
            return visitClassDef(ctx.classDef());
        } else if (ctx.funcDef() != null) {
            return visitFuncDef(ctx.funcDef());
        } else if (ctx.varDef() != null) {
            return ((VarDefStmtNode) visitVarDef(ctx.varDef())).varDefNode;
        }
        return null;
    }

    @Override
    public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        Position pos = new Position(ctx);
        FuncDefNode funcDefNode = new FuncDefNode(pos, ctx.Identifier().getText());
        funcDefNode.returnType = new ASTType(ctx.returnType());
        funcDefNode.functionParameterList = (FuncParameterListNode) visitFunctionParameterList(ctx.functionParameterList());
        funcDefNode.blockStmt = (BlockStmtNode) visitBlockStmt(ctx.blockStmt());
        return funcDefNode;
    }


    @Override
    public ASTNode visitFunctionParameterList(MxParser.FunctionParameterListContext ctx) {
        Position pos = new Position(ctx);
        FuncParameterListNode funcParameterListNode = new FuncParameterListNode(pos);
        for (int i = 0; i < ctx.type().size(); ++i) {
            ASTType type = new ASTType(ctx.type(i));
            SingleParameter singlePara = new SingleParameter(type, ctx.Identifier(i).getText());
            funcParameterListNode.parameters.add(singlePara);
        }
        return funcParameterListNode;
    }


    @Override
    public ASTNode visitBlockStmt(MxParser.BlockStmtContext ctx) {
        Position pos = new Position(ctx);
        BlockStmtNode blockStmtNode = new BlockStmtNode(pos);
        for (var stmtCtx : ctx.statement()) {
            StmtNode node = (StmtNode) visitStatement(stmtCtx);
            blockStmtNode.stmts.add(node);
        }
        return blockStmtNode;
    }


    @Override
    public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        Position pos = new Position(ctx);
        ClassDefNode classDefNode = new ClassDefNode(pos, ctx.Identifier().getText());
        for (var varDefCtx : ctx.varDef()) {
            VarDefNode node = ((VarDefStmtNode) visitVarDef(varDefCtx)).varDefNode;
//            classDefNode.varDefs.add(node);
            for (var var : node.vars) {
                if (classDefNode.members.containsKey(var.name)) {
                    throw new SemanticError("class " + classDefNode.className + "already has member " + var.name, var.pos);
                }
                classDefNode.members.put(var.name, var);
            }
        }
        for (var funcDefCtx : ctx.funcDef()) {
            FuncDefNode node = (FuncDefNode) visitFuncDef(funcDefCtx);
            if (classDefNode.functions.containsKey(node.funcName)) {
                throw new SemanticError("class " + classDefNode.className + "already has function " + node.funcName, node.pos);
            }
            classDefNode.functions.put(node.funcName, node);
        }
        if (!ctx.constructorDef().isEmpty()) {
            if (ctx.constructorDef().size() > 1) {
                throw new SemanticError("class" + classDefNode.className + " has more than one constructor ", new Position(ctx.constructorDef(0)));
            }
            if (ctx.constructorDef().isEmpty()) {
                classDefNode.constructor = null;
            } else {
                classDefNode.constructor = (ClassConstructorNode) visitConstructorDef(ctx.constructorDef(0));
            }
        } else {
            classDefNode.constructor = null;
        }
        return classDefNode;
    }


    @Override
    public ASTNode visitConstructorDef(MxParser.ConstructorDefContext ctx) {
        Position pos = new Position(ctx);
        ClassConstructorNode classConstructorNode = new ClassConstructorNode(pos, ctx.Identifier().getText());
        classConstructorNode.blockStmt = (BlockStmtNode) visitBlockStmt(ctx.blockStmt());
        return classConstructorNode;
    }

    @Override
    public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        Position pos = new Position(ctx);
        ASTType type = new ASTType(ctx.type());
        VarDefNode varDefNode = new VarDefNode(pos, type);
        for (var defAssign : ctx.defAndAssign()) {
            SingleVarDefNode node = (SingleVarDefNode) visitDefAndAssign(defAssign);
            node.type = type;
            varDefNode.vars.add(node);
        }
        return new VarDefStmtNode(varDefNode, pos);
    }


    @Override
    public ASTNode visitDefAndAssign(MxParser.DefAndAssignContext ctx) {
        Position pos = new Position(ctx);
        SingleVarDefNode singleVarDefNode = new SingleVarDefNode(pos, ctx.Identifier().getText());
        if (ctx.expression() != null) {
            singleVarDefNode.expr = (ExprNode) visit(ctx.expression());
        }
        return singleVarDefNode;
    }


    @Override
    public ASTNode visitStatement(MxParser.StatementContext ctx) {
        if (ctx.blockStmt() != null) {
            return visitBlockStmt(ctx.blockStmt());
        } else if (ctx.varDef() != null) {
            return visitVarDef(ctx.varDef());
        } else if (ctx.ifStmt() != null) {
            return visitIfStmt(ctx.ifStmt());
        } else if (ctx.whileStmt() != null) {
            return visitWhileStmt(ctx.whileStmt());
        } else if (ctx.forStmt() != null) {
            return visitForStmt(ctx.forStmt());
        } else if (ctx.returnStmt() != null) {
            return visitReturnStmt(ctx.returnStmt());
        } else if (ctx.breakStmt() != null) {
            return visitBreakStmt(ctx.breakStmt());
        } else if (ctx.continueStmt() != null) {
            return visitContinueStmt(ctx.continueStmt());
        } else if (ctx.exprStmt() != null) {
            return visitExprStmt(ctx.exprStmt());
        }
        return null;
    }


    @Override
    public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        Position pos = new Position(ctx);
        IfStmtNode ifStmtNode = new IfStmtNode(pos);
        ifStmtNode.condition = (ExprNode) visit(ctx.condition);
        ifStmtNode.trueStmts = (StmtNode) visitStatement(ctx.trueStmt);
        if (ctx.falseStmt != null) {
            ifStmtNode.falseStmts = (StmtNode) visitStatement(ctx.falseStmt);
        }
        return ifStmtNode;
    }


    @Override
    public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        Position pos = new Position(ctx);
        WhileStmtNode whileStmtNode = new WhileStmtNode(pos);
        whileStmtNode.condition = (ExprNode) visit(ctx.condition);
        whileStmtNode.stmts = (StmtNode) visitStatement(ctx.statement());
        return whileStmtNode;
    }


    @Override
    public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        Position pos = new Position(ctx);
        ForStmtNode forStmtNode = new ForStmtNode(pos);
        if (ctx.init != null) {
            forStmtNode.initVarDef = ((VarDefStmtNode) visitVarDef(ctx.init)).varDefNode;
        } else if (ctx.initt != null) {
            forStmtNode.initExpr = (ExprNode) visit(ctx.initt);
        }
        if (ctx.condition != null) {
            forStmtNode.condition = (ExprNode) visit(ctx.condition);
        }
        if (ctx.step != null) {
            forStmtNode.step = (ExprNode) visit(ctx.step);
        }
        forStmtNode.stmts = (StmtNode) visitStatement(ctx.statement());
        return forStmtNode;
    }


    @Override
    public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        Position pos = new Position(ctx);
        ReturnStmtNode returnStmtNode = new ReturnStmtNode(pos);
        if (ctx.expression() != null) {
            returnStmtNode.expr = (ExprNode) visit(ctx.expression());
        }
        return returnStmtNode;
    }

    @Override
    public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        Position pos = new Position(ctx);
        return new BreakStmtNode(pos);
    }


    @Override
    public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        Position pos = new Position(ctx);
        return new ContinueStmtNode(pos);
    }


    @Override
    public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
        Position pos = new Position(ctx);
        if (ctx.expression() == null) {
            return new ExprStmtNode(pos, null);
        }
        ExprNode exprNode = (ExprNode) visit(ctx.expression());
        return new ExprStmtNode(pos, exprNode);
    }


    @Override
    public ASTNode visitParenExpr(MxParser.ParenExprContext ctx) {
        Position pos = new Position(ctx);
        return new ParenExprNode(pos, (ExprNode) visit(ctx.expression()));
    }

    @Override
    public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        if (ctx.newType() instanceof MxParser.NewClassContext) {
            MxParser.NewClassContext newClassContext = (MxParser.NewClassContext) ctx.newType();
            return visitNewClass(newClassContext);
        } else {
            MxParser.NewArrayContext newArrayContext = (MxParser.NewArrayContext) ctx.newType();
            return visitNewArray(newArrayContext);
        }
    }

    @Override
    public ASTNode visitVarExpr(MxParser.VarExprContext ctx) {
        Position pos = new Position(ctx);
        VarExprNode varExprNode = new VarExprNode(pos, ctx.Identifier().getText());
        return varExprNode;
    }

    @Override
    public ASTNode visitUnaryExpr(MxParser.UnaryExprContext ctx) {
        Position pos = new Position(ctx);
        UnaryExprNode unaryExprNode = new UnaryExprNode(pos);
        unaryExprNode.op = ctx.op.getText();
        unaryExprNode.exprNode = (ExprNode) visit(ctx.expression());
        return unaryExprNode;
    }

    @Override
    public ASTNode visitPreOpExpr(MxParser.PreOpExprContext ctx) {
        Position pos = new Position(ctx);
        PreOpExprNode preOpExprNode = new PreOpExprNode(pos);
        preOpExprNode.op = ctx.op.getText();
        preOpExprNode.expr = (ExprNode) visit(ctx.expression());
        return preOpExprNode;
    }


    @Override
    public ASTNode visitTernaryExpr(MxParser.TernaryExprContext ctx) {
        Position pos = new Position(ctx);
        TernaryExprNode ternaryExprNode = new TernaryExprNode(pos);
        ternaryExprNode.lExpr = (ExprNode) visit(ctx.lexpr);
        ternaryExprNode.mExpr = (ExprNode) visit(ctx.mexpr);
        ternaryExprNode.rExpr = (ExprNode) visit(ctx.rexpr);
        return ternaryExprNode;
    }


    @Override
    public ASTNode visitArrayExpr(MxParser.ArrayExprContext ctx) {
        Position pos = new Position(ctx);
        ArrayExprNode arrayExprNode = new ArrayExprNode(pos);
        arrayExprNode.array = (ExprNode) visit(ctx.expression(0));
        arrayExprNode.index = (ExprNode) visit(ctx.expression(1));
        return arrayExprNode;
    }


    @Override
    public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        Position pos = new Position(ctx);
        MemberExprNode memberExprNode = new MemberExprNode(pos, ctx.Identifier().getText());
        memberExprNode.obj = (ExprNode) visit(ctx.expression());
        return memberExprNode;
    }


    @Override
    public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        Position pos = new Position(ctx);
        BinaryExprNode binaryExprNode = new BinaryExprNode(pos);
        binaryExprNode.op = ctx.op.getText();
        binaryExprNode.lhs = (ExprNode) visit(ctx.lhs);
        binaryExprNode.rhs = (ExprNode) visit(ctx.rhs);
        return binaryExprNode;
    }


    @Override
    public ASTNode visitFuncCallExpr(MxParser.FuncCallExprContext ctx) {
        Position pos = new Position(ctx);
        FuncCallExprNode funcCallExprNode = new FuncCallExprNode(pos);
        funcCallExprNode.func = (ExprNode) visit(ctx.expression(0));
        if (funcCallExprNode.func instanceof VarExprNode function) {
            function.isFunction = true;
        }
        for (int i = 1; i < ctx.expression().size(); ++i) {
            funcCallExprNode.args.add((ExprNode) visit(ctx.expression(i)));
        }
        return funcCallExprNode;
    }


    @Override
    public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
        Position pos = new Position(ctx);
        AssignExprNode assignExprNode = new AssignExprNode(pos);
        assignExprNode.lhs = (ExprNode) visit(ctx.expression(0));
        assignExprNode.rhs = (ExprNode) visit(ctx.expression(1));
        return assignExprNode;
    }

    @Override
    public ASTNode visitNewClass(MxParser.NewClassContext ctx) {
        Position pos = new Position(ctx);
        NewExprNode newExprNode = new NewExprNode(pos);
        newExprNode.isArray = false;
        newExprNode.typeName = new TypeName(ctx.Identifier().getText(), true, false);
        return newExprNode;
    }

    @Override
    public ASTNode visitNewArray(MxParser.NewArrayContext ctx) {
        Position pos = new Position(ctx);
        NewExprNode newExprNode = new NewExprNode(pos);
        newExprNode.isArray = true;
        newExprNode.typeName = new TypeName(ctx.typeName());
        newExprNode.arrDim = ctx.arrayIndex().size();
        boolean nullAppeared = false, sizeAppeared = false;
        for (var arrayIndexCtx : ctx.arrayIndex()) {
            if (arrayIndexCtx.expression() == null) {
                nullAppeared = true;
            } else {
                sizeAppeared = true;
                if (!nullAppeared) {
                    newExprNode.lengths.add((ExprNode) visit(arrayIndexCtx.expression()));
                } else {
                    throw new SyntaxError("invalid new array expression", pos);
                }
            }
        }
        if (!sizeAppeared) {
            throw new SyntaxError("invalid new array expression", pos);
        }
        return newExprNode;
    }

    @Override
    public ASTNode visitConstExpr(MxParser.ConstExprContext ctx) {
        Position pos = new Position(ctx);
        ConstValue constValue;
        if (ctx.Null() != null) {
            constValue = new Null();
        } else if (ctx.This() != null) {
            constValue = new This();
        } else if (ctx.True() != null) {
            constValue = new BoolConst(true);
        } else if (ctx.False() != null) {
            constValue = new BoolConst(false);
        } else if (ctx.IntConst() != null) {
            constValue = new IntConst(Integer.parseInt(ctx.IntConst().getText()));
        } else {
            constValue = new StringConst(ctx.StringConst().getText());
        }
        ConstExprNode constExprNode = new ConstExprNode(pos, constValue);
        return constExprNode;
    }
}




