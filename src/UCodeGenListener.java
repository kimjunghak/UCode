import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kjh on 16. 11. 26.
 */
public class UCodeGenListener extends MiniCBaseListener{

    ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
    int local_var_num = 0;
    int var_num = 0;
    int local_array_num = 0;
    int array_num = 0;
    int depth = 0;
    ArrayList<Node> local_var_list = new ArrayList<>();
    ArrayList<Node> var_list = new ArrayList<>();
    ArrayList<String> literal_list = new ArrayList<>();

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) throws IOException {
        FileWriter writer = new FileWriter("/home/kimjunghak/Downloads/hw04/test.txt");
        String str = "";
        for(int i=0 ; i<ctx.decl().size() ; i++) {
            str += newTexts.get(ctx.decl(i));
            if(newTexts.get(ctx.decl(i)) == newTexts.get(ctx.decl(i).fun_decl())) {
                if (str.contains("           retv") || str.contains("           ret"))
                    str += "           end\n";
                else
                    str += "           ret\n" + "           end\n";
            }
        }

        str += "           bgn " + var_num + "\n";
        for(int i=0 ; i<literal_list.size() ; i++)
            str += literal_list.get(i);
        str += "           ldp\n" + "           call main\n"   + "           end\n";

        System.out.print(str);
        writer.write(str);

        writer.close();
    }

    @Override
    public void exitDecl(MiniCParser.DeclContext ctx) {
        String str;

        if(ctx.getChild(0) == ctx.var_decl())
            str = newTexts.get(ctx.var_decl());
        else
            str = newTexts.get(ctx.fun_decl());

        newTexts.put(ctx, str);
    }

    @Override
    public void exitVar_decl(MiniCParser.Var_declContext ctx) {
        String str = "";
        if(ctx.getChildCount() == 3) {
            var_list.add(new Node(ctx.getChild(1).getText(), "1 " + (++var_num), false));
            str += "           sym 1 " + var_num + " 1\n";
        }

        else if(ctx.getChildCount() == 6){
            var_list.add(new Node(ctx.getChild(1).getText(), "1 " + (++var_num), true));
            array_num = Integer.parseInt(ctx.LITERAL().getText());
            str += "           sym 1 " + var_num + " " + array_num + "\n";
        }

        else{
            var_list.add(new Node(ctx.getChild(1).getText(), "1 " + (++var_num), false));
            str += "           sym 1 " + var_num + " 1\n";
            literal_list.add("           ldc " + ctx.LITERAL() + "\n"
                    + "           str " + findNode(ctx.getChild(1).getText()).num + "\n");
        }


        newTexts.put(ctx, str);
    }

    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
        String str = "";
        String space = "";
        String compound = newTexts.get(ctx.compound_stmt());
        for(int i=0 ; i<11 - ctx.getChild(1).getText().length() ; i++)
            space += " ";

        str += ctx.getChild(1).getText() + space + "proc " + local_var_num + " 2 2\n";

        for(int i = 0; i< local_var_num; i++) {
            if(local_var_list.get(i).isArray == true)
                str += "           sym 2 " + (i + 1) + " " + local_array_num + "\n";
            else
                str += "           sym 2 " + (i + 1) + " 1\n";
        }

        str += compound;

        local_var_num = 0;
        local_array_num = 0;

        newTexts.put(ctx, str);
    }

    @Override
    public void exitParams(MiniCParser.ParamsContext ctx) {
        for(int i=0 ; i<ctx.param().size() ; i++)
            newTexts.get(ctx.param(i));
    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) {
        local_var_list.add(new Node(ctx.getChild(1).getText(), "2 " + (++local_var_num), false));
    }

    @Override
    public void exitStmt(MiniCParser.StmtContext ctx) {
        stmtCheck(ctx);
    }

    @Override
    public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
        String str = "";

        if(ctx.expr().getChild(1).getText().equals("=")){
            Node var;
            var = findNode(ctx.expr().getChild(0).getText());

            str += newTexts.get(ctx.expr().expr(0));
            str += "           str " + var.num + "\n";
        }

        str += newTexts.get(ctx.expr());

        newTexts.put(ctx, str);
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
        String str = "";

        for(int i=0 ; i<ctx.local_decl().size() ; i++)
            str += newTexts.get(ctx.local_decl(i));

        for(int i=0 ; i<ctx.stmt().size() ; i++)
            str += newTexts.get(ctx.stmt(i));


        newTexts.put(ctx, str);
    }


    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
        String str = "";
        if(ctx.getChildCount() == 3)
            local_var_list.add(new Node(ctx.getChild(1).getText(), "2 " + (++local_var_num), false));

        else if(ctx.getChildCount() == 6){
            local_var_list.add(new Node(ctx.getChild(1).getText(), "2 " + (++local_var_num), true));
            local_array_num = Integer.parseInt(ctx.LITERAL().getText());
        }

        else{
            local_var_list.add(new Node(ctx.getChild(1).getText(), "2 " + (++local_var_num), false));
            str += "           ldc " + ctx.LITERAL().getText() + "\n"
                    + "           str " + findNode(ctx.getChild(1).getText()).num + "\n";
        }

        newTexts.put(ctx, str);
    }

    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
        String str = "";
        str += newTexts.get(ctx.expr());
        str += "           fjp $$" + depth + "\n";

        if(ctx.getChildCount() == 5) {
            depth++;
            str += newTexts.get(ctx.stmt(0));
        }

        else {
            depth++;
            str += newTexts.get(ctx.stmt(1));
        }

        str += "$$" + (depth - 1) + "        nop\n";

        newTexts.put(ctx, str);
    }

    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
        String str = "";
        if(ctx.getChildCount() == 1)
            str += "           ret\n";
        else {
            str += newTexts.get(ctx.expr());
            str += "           retv\n";
        }

        newTexts.put(ctx, str);
    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String str = "";
        String space = "           ";

        if(isLiteral(ctx))
            str += space + "ldc " + ctx.LITERAL().getText() + "\n";

        else if(isIdent(ctx)){
            Node var = findNode(ctx.IDENT().getText());

            str += space + "lod " + var.num + "\n";
        }

        else if(ctx.getChildCount() == 2){
            str += newTexts.get(ctx.expr(0));

            Node var = findNode(ctx.getChild(1).getText());

            if(ctx.getChild(0).getText().equals("-"))
                str += space + "neg\n";
            else if(ctx.getChild(0).getText().equals("--")) {
                str += space + "dec\n";
                str += space + "str " + var.num + "\n";
            }

            else if(ctx.getChild(0).getText().equals("++")) {
                str += space + "inc\n";
                str += space + "str " + var.num + "\n";
            }

            else if(ctx.getChild(0).getText().equals("!"))
                str += space + "notop\n";
        }

        else if(ctx.getChildCount() == 3 && !ctx.getChild(1).getText().equals("=")){
            str += newTexts.get(ctx.expr(0)) + newTexts.get(ctx.expr(1));

            if(ctx.getChild(1).getText().equals(">"))
                str += space + "gt\n";

            else if(ctx.getChild(1).getText().equals("<"))
                str += space + "lt\n";

            else if(ctx.getChild(1).getText().equals("+"))
                str += space + "add\n";

            else if(ctx.getChild(1).getText().equals("/"))
                str += space + "div\n";

            else if(ctx.getChild(1).getText().equals("-"))
                str += space + "sub\n";

            else if(ctx.getChild(1).getText().equals("*"))
                str += space + "mult\n";

            else if(ctx.getChild(1).getText().equals("%"))
                str += space + "mod\n";

            else if(ctx.getChild(1).getText().equals("!="))
                str += space + "ne\n";

            else if(ctx.getChild(1).getText().equals("<="))
                str += space + "le\n";

            else if(ctx.getChild(1).getText().equals(">="))
                str += space + "ge\n";

            else if(ctx.getChild(1).getText().equals("=="))
                str += space + "eq\n";
        }

        else if(ctx.getChildCount() == 4) {
            if (ctx.args() != null) {
                str += space + "ldp\n"
                        + newTexts.get(ctx.args())
                        + space + "call " + ctx.getChild(0).getText() + "\n";
            }
            else{
                Node var = findNode(ctx.IDENT().getText());
                str += newTexts.get(ctx.expr(0)) + space + "lda " + var.num + "\n"
                        + space + "add\n";
            }
        }

        else if(ctx.getChildCount() == 6){
            Node var = findNode(ctx.IDENT().getText());
            str += newTexts.get(ctx.expr(0))
                    + space + "lda " + var.num + "\n"
                    + space + "add\n" + newTexts.get(ctx.expr(1))
                    + space + "sti\n";
        }

        newTexts.put(ctx, str);
    }

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {
        String str = "";
        for(int i=0 ; i<ctx.expr().size() ; i++)
            str += newTexts.get(ctx.expr(i));

        newTexts.put(ctx, str);
    }

    private boolean isIdent(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 1 && ctx.IDENT() != null;
    }

    private boolean isLiteral(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 1 && ctx.LITERAL() != null;
    }


    private Node findNode(String text) {
        Node var;
        if (find_local_var(text) != null)
            var = find_local_var(text);
        else
            var = find_var(text);
        return var;
    }

    private Node find_local_var(String str){
        Node temp = null;
        for(int i = 0; i< local_var_list.size() ; i++){
            if(local_var_list.get(i).id.equals(str))
                temp = local_var_list.get(i);
        }
        return temp;
    }

    private Node find_var(String str){
        Node temp = null;
        for(int i = 0; i< var_list.size() ; i++){
            if(var_list.get(i).id.equals(str))
                temp = var_list.get(i);
        }
        return temp;
    }

    class Node{
        String id;
        String num;
        boolean isArray;

        public Node(String id, String num, boolean isArray) {
            this.id = id;
            this.num = num;
            this.isArray = isArray;
        }
    }

    private void stmtCheck(MiniCParser.StmtContext ctx) {
        String str = "";

        if(ctx.getChild(0) == ctx.expr_stmt())
            str += newTexts.get(ctx.expr_stmt());

        else if(ctx.getChild(0) == ctx.if_stmt())
            str += newTexts.get(ctx.if_stmt());

        else if(ctx.getChild(0) == ctx.while_stmt())
            str += newTexts.get(ctx.while_stmt());

        else if(ctx.getChild(0) == ctx.compound_stmt())
            str += newTexts.get(ctx.compound_stmt());

        else
            str += newTexts.get(ctx.return_stmt());

        newTexts.put(ctx, str);
    }
}
