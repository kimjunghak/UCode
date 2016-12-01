import org.antlr.v4.runtime.tree.ParseTreeProperty;
import sun.rmi.runtime.NewThreadAction;

import java.util.ArrayList;

/**
 * Created by kjh on 16. 11. 26.
 */
public class UCodeGenListener extends MiniCBaseListener {

    ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
    int var_num = 0;
    int depth = 0;
    ArrayList<Node> var_list = new ArrayList<>();

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) {
        String str = "";
        for(int i=0 ; i<ctx.decl().size() ; i++)
            str += newTexts.get(ctx.decl(i));

        str += "           ret\n" + "           end\n" + "           bgn 0\n" + "           ldp\n"
                + "           call main\n"   + "           end\n";

        System.out.print(str);
    }

    @Override
    public void exitDecl(MiniCParser.DeclContext ctx) {
        newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
    }

    @Override
    public void exitVar_decl(MiniCParser.Var_declContext ctx) {

    }

    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
        String str = "";
        String compound = newTexts.get(ctx.compound_stmt());
        if(ctx.getChild(1).getText().equals("main"))
            str = "main       proc " + var_num + " 2 2\n" + str + compound;

        newTexts.put(ctx, str);
    }

    @Override
    public void exitParams(MiniCParser.ParamsContext ctx) {

    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) {

    }

    @Override
    public void exitStmt(MiniCParser.StmtContext ctx) {
        if(ctx.getChild(0) == ctx.expr_stmt())
            newTexts.put(ctx, newTexts.get(ctx.expr_stmt()));

        else if(ctx.getChild(0) == ctx.if_stmt())
            newTexts.put(ctx, newTexts.get(ctx.if_stmt()));

        else if(ctx.getChild(0) == ctx.while_stmt())
            newTexts.put(ctx, newTexts.get(ctx.while_stmt()));

        else
            newTexts.put(ctx, newTexts.get(ctx.return_stmt()));
    }

    @Override
    public void enterStmt(MiniCParser.StmtContext ctx){
        String str = var_num + " 2 2\n";
        for(int i=0 ; i<var_num ; i++)
            str += "           sym 2 " + i + " 1\n";

        newTexts.put(ctx, str);
    }

    @Override
    public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
        String expr = newTexts.get(ctx.expr());
        newTexts.put(ctx, expr);
    }

    @Override
    public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
        String str = "$$" + depth + "        nop\n";
        depth++;

        str += newTexts.get(ctx.expr());
        str += "           fjp $$" + depth + "\n";

        str += newTexts.get(ctx.stmt());
        str += "           ujp $$" + (depth-1) + "\n";

        str += "$$" + depth + "        nop\n";
        depth++;

        newTexts.put(ctx, str);

    }

    @Override
    public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {

    }

    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
        if(ctx.getChildCount() == 3) {
            var_num++;
            var_list.add(new Node(ctx.getChild(1).getText(), var_num));
        }
        newTexts.put(ctx, "proc ");
        if(ctx.getChild(2).getText().equals("="))
            newTexts.put(ctx, "           ldc " + ctx.getChild(3).getText() + "\n" + "           str 2 " + findNode(ctx.getChild(1).getText()).num + "\n");
    }

    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {

    }

    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {

    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String str = "";
        String expr = newTexts.get(ctx.expr(0));
        if(ctx.getChildCount() == 3 && ctx.getChild(1).getText().equals("=")){
            Node var = findNode(ctx.getChild(0).getText());
            str += "           str 2 " + var.num + "\n";
        }

        else if(ctx.getChildCount() == 4)
            str += "           call " + ctx.getChild(0).getText() + "\n";

        else if(ctx.getChildCount() == 1)
            str += "           ldc " + expr + "\n";

        newTexts.put(ctx, str);
    }

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {

    }

    private Node findNode(String str){
        Node temp = null;
        for(int i=0 ; i<var_list.size() ; i++){
            if(var_list.get(i).id.equals(str))
                temp = var_list.get(i);
        }
        return temp;
    }

    class Node{
        String id;
        int num;

        public Node(String id, int num) {
            this.id = id;
            this.num = num;
        }
    }
}
