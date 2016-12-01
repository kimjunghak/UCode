
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class MiniCPrintListener extends MiniCBaseListener{
	
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
	int tab = 0;

	// program	: decl+
	@Override
	public void exitProgram(MiniCParser.ProgramContext ctx) {
		// TODO Auto-generated method stub
		super.exitProgram(ctx);
		String decl = "";
		if(ctx.getChildCount() > 0)						// decl이 하나 이상 존재할 때
			for(int i=0; i<ctx.getChildCount(); i++)
				decl += newTexts.get(ctx.decl(i));
		newTexts.put(ctx, decl);
		System.out.println(newTexts.get(ctx));
	}
	
	// decl	: var_decl | fun_decl
	@Override
	public void exitDecl(MiniCParser.DeclContext ctx) {
		// TODO Auto-generated method stub
		super.exitDecl(ctx);
		String decl = "";
		if(ctx.getChildCount() == 1)
		{
			if(ctx.var_decl() != null)				// decl이 var_decl인 경우
				decl += newTexts.get(ctx.var_decl());
			else									// decl이 fun_decl인 경우
				decl += newTexts.get(ctx.fun_decl());
		}
		newTexts.put(ctx, decl);
	}
	
	
	// var_decl	: type_spec IDENT ';' | type_spec IDENT '=' LITERAL ';'|type_spec IDENT '[' LITERAL ']' ';'
	@Override
	public void exitVar_decl(MiniCParser.Var_declContext ctx) {
		// TODO Auto-generated method stub
		super.exitVar_decl(ctx);
		String decl = "";
		if(ctx.getChildCount() >= 3)
		{
			decl += newTexts.get(ctx.type_spec());					// type_spec
			decl += " ";				
			decl += ctx.getChild(1).getText();						// IDENT
			if(ctx.getChildCount() == 5)					
			{
				decl += " ";	
				decl += ctx.getChild(2).getText();		
				decl += " ";	
				decl += ctx.getChild(3).getText();					
			}else if(ctx.getChildCount() == 6){
				decl += ctx.getChild(2).getText();					
				decl += ctx.getChild(3).getText();	
				decl += ctx.getChild(4).getText();	
			}
			decl += ctx.getChild(ctx.getChildCount()-1).getText();	// ;
			decl += "\n";
		}
		newTexts.put(ctx, decl);
	}
	
	// type_spec	: VOID | INT
	@Override
	public void exitType_spec(MiniCParser.Type_specContext ctx) {
		// TODO Auto-generated method stub
		super.exitType_spec(ctx);
		newTexts.put(ctx, ctx.getChild(0).getText());
	}
	
	// fun_decl : type_spec IDENT '(' params ')' compound_stmt
	@Override
	public void enterFun_decl(MiniCParser.Fun_declContext ctx) {
		// TODO Auto-generated method stub
		super.enterFun_decl(ctx);
		tab++;
	}

	@Override
	public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
		// TODO Auto-generated method stub
		super.exitFun_decl(ctx);
		String stmt="";
		if(ctx.getChildCount() == 6)
		{
			stmt += newTexts.get(ctx.type_spec());		// type_spec
			stmt += " ";
			stmt += ctx.getChild(1).getText();			// IDENT
			stmt += ctx.getChild(2).getText();			// (
			stmt += newTexts.get(ctx.params());			// params
			stmt += ctx.getChild(4).getText();			// )
			stmt += "\n";
			stmt += newTexts.get(ctx.compound_stmt());	// compound_stmt
			stmt += "\n";
			newTexts.put(ctx, stmt);
		}
		tab--;
	}
	
	// params	: param (',' param)* | VOID
	@Override
	public void exitParams(MiniCParser.ParamsContext ctx) {
		// TODO Auto-generated method stub
		super.exitParams(ctx);
		String params = "";
		if(ctx.getChildCount() > 0)
		{
			if(ctx.getChild(0) == ctx.VOID())					// VOID
				params = ctx.getChild(0).getText();
			else												// param (',' param)*
			{
				for(int i=0; i<ctx.getChildCount(); i++)
				{
					if(i % 2 == 0)
						params += newTexts.get(ctx.param(i/2));	// param
					else
					{
						params += ctx.getChild(i).getText();	// ,
						params += " ";
					}
				}
			}
		
		}
		newTexts.put(ctx, params);
	}
	
	// param	: type_spec IDENT | type_spec IDENT '[' ']'
	@Override
	public void exitParam(MiniCParser.ParamContext ctx) {
		// TODO Auto-generated method stub
		super.exitParam(ctx);
		String param = "";
		if(ctx.getChildCount() >= 2)
		{
			param += newTexts.get(ctx.type_spec());	// type_spec
			param += " ";
			param += ctx.getChild(1).getText();		// IDENT
			if(ctx.getChildCount() == 4)
			{
				param += ctx.getChild(2).getText();	// [
				param += ctx.getChild(3).getText();	// ]
			}
			newTexts.put(ctx, param);
		}
	}
	
	// stmt	: expr_stmt | compound_stmt | if_stmt | while_stmt | return_stmt
	@Override
	public void exitStmt(MiniCParser.StmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitStmt(ctx);
		String stmt = "";
		if(ctx.getChildCount() > 0)
		{
			if(ctx.expr_stmt() != null)				// expr_stmt일 때
				stmt += newTexts.get(ctx.expr_stmt());
			else if(ctx.compound_stmt() != null)	// compound_stmt일 때
				stmt += newTexts.get(ctx.compound_stmt());
			else if(ctx.if_stmt() != null)			// if_stmt일 때
				stmt += newTexts.get(ctx.if_stmt());
			else if(ctx.while_stmt() != null)		// while_stmt일 때
				stmt += newTexts.get(ctx.while_stmt());
			else									// return_stmt일 때
				stmt += newTexts.get(ctx.return_stmt());
		}
		newTexts.put(ctx, stmt);
	}
	
	// expr_stmt	: expr ';'
	@Override
	public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitExpr_stmt(ctx);
		String stmt = "";
		if(ctx.getChildCount() == 2)
		{
			stmt += printTab();
			stmt += newTexts.get(ctx.expr());	// expr
			stmt += ctx.getChild(1).getText();	// ;
			stmt += "\n";
		}
		newTexts.put(ctx, stmt);
	}
	
	// while_stmt	: WHILE '(' expr ')' stmt
	@Override
	public void enterWhile_stmt(MiniCParser.While_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.enterWhile_stmt(ctx);
		tab++;
	}

	@Override
	public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitWhile_stmt(ctx);
		String stmt = "";
		if(ctx.getChildCount() == 5)
		{
			tab--;
			stmt += printTab();
			tab++;
			stmt += ctx.getChild(0).getText();	// while
			stmt += " ";
			stmt += ctx.getChild(1).getText();	// (
			stmt += newTexts.get(ctx.expr());	// expr
			stmt += ctx.getChild(3).getText();	// )
			stmt += "\n";
			stmt += newTexts.get(ctx.stmt());	// stmt
		}
		newTexts.put(ctx, stmt);
		tab--;
	}
	
	// compound_stmt	: '{' local_decl* stmt* '}'
	@Override
	public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitCompound_stmt(ctx);
		String stmt = "";
		int local_i = 0, stmt_i = 0;
		if(ctx.getChildCount() >= 2)
		{
			tab--;
			stmt += printTab();
			tab++;
			stmt += ctx.getChild(0).getText();						// {
			stmt += "\n";
			for(int i=1; i<ctx.getChildCount()-1; i++)
			{
				//stmt += printTab();
				if(ctx.local_decl().contains(ctx.getChild(i)))		// local_decl인 경우
					stmt += newTexts.get(ctx.local_decl(local_i++));
				else												// stmt인 경우
					stmt += newTexts.get(ctx.stmt(stmt_i++));
			}
			tab--;
			stmt += printTab();
			tab++;
			stmt += ctx.getChild(ctx.getChildCount()-1).getText();	// }
			stmt += "\n";
		}
		newTexts.put(ctx, stmt);
	}
	
	// local_decl	: type_spec IDENT ';' | type_spec IDENT '[' ']' ';'
	@Override
	public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
		// TODO Auto-generated method stub
		super.exitLocal_decl(ctx);
		String decl = "";
		if(ctx.getChildCount() >= 3)
		{
			decl += printTab();
			decl += newTexts.get(ctx.type_spec());					// type_spec
			decl += " ";
			decl += ctx.getChild(1).getText();						// IDENT
			if(ctx.getChildCount() == 5)
			{
				decl += ctx.getChild(2).getText();					// [
				decl += ctx.getChild(3).getText();					// ]
			}
			decl += ctx.getChild(ctx.getChildCount()-1).getText();	// ;
			decl += "\n";
		}
		newTexts.put(ctx, decl);
	}
	
	// if_stmt	: IF '(' expr ')' stmt | IF '(' expr ')' stmt ELSE stmt;
	@Override
	public void enterIf_stmt(MiniCParser.If_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.enterIf_stmt(ctx);
		tab++;
	}

	@Override
	public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitIf_stmt(ctx);
		String stmt = "";
		if(ctx.getChildCount() >= 5)
		{
			tab--;
			stmt += printTab();
			tab++;
			stmt += ctx.getChild(0).getText();	// if
			stmt += " ";
			stmt += ctx.getChild(1).getText();	// (
			stmt += newTexts.get(ctx.expr());	// expr
			stmt += ctx.getChild(3).getText();	// )
			stmt += "\n";
			stmt += newTexts.get(ctx.stmt(0));	// stmt
			if(ctx.getChildCount() == 7)
			{
				tab--;
				stmt += printTab();
				tab++;
				stmt += ctx.getChild(5).getText();	// else
				stmt += "\n";
				stmt += newTexts.get(ctx.stmt(1));	// stmt
			}
			newTexts.put(ctx, stmt);
		}
		tab--;
	}
	
	// return_stmt	: RETURN ';' | RETURN expr ';'
	@Override
	public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
		// TODO Auto-generated method stub
		super.exitReturn_stmt(ctx);
		String stmt = "";
		if(ctx.getChildCount() >= 2)
		{
			stmt += printTab();
			stmt += ctx.getChild(0).getText();						// RETURN
			
			if(ctx.getChildCount() ==3){
				stmt += " ";
				stmt += newTexts.get(ctx.expr());		
			}// expr
			stmt += ctx.getChild(ctx.getChildCount()-1).getText();	// ;
			stmt += "\n";
		}
		newTexts.put(ctx, stmt);
	}
	
	// expr
	@Override
	public void exitExpr(MiniCParser.ExprContext ctx) {
		// TODO Auto-generated method stub
		super.exitExpr(ctx);
		String expr = "";
		if(ctx.getChildCount() > 0)
		{
			// IDENT | LITERAL일 경우
			if(ctx.getChildCount() == 1)
				expr+= ctx.getChild(0).getText();
			// '!' expr | '-' expr | '+' expr일 경우
			else if(ctx.getChildCount() == 2)
			{
				expr += ctx.getChild(0).getText();
				expr += newTexts.get(ctx.expr(0));
			}
			else if(ctx.getChildCount() == 3)
			{
				// '(' expr ')'
				if(ctx.getChild(0).getText().equals("("))
				{
					expr += ctx.getChild(0).getText();
					expr += newTexts.get(ctx.expr(0));
					expr += ctx.getChild(2).getText();
				}
				// IDENT '=' expr
				else if(ctx.getChild(1).getText().equals("="))
				{
					expr += ctx.getChild(0).getText();
					expr += " ";
					expr += ctx.getChild(1).getText();
					expr += " ";
					expr += newTexts.get(ctx.expr(0));
				}
				// binary operation
				else
				{
					expr += newTexts.get(ctx.expr(0));
					expr += " ";
					expr += ctx.getChild(1).getText();
					expr += " ";
					expr += newTexts.get(ctx.expr(1));
				}
			}
			// IDENT '(' args ')' |  IDENT '[' expr ']'일 경우
			else if(ctx.getChildCount() == 4)
			{
				expr += ctx.getChild(0).getText();	// IDENT
				if(ctx.args() != null)				// args
				{
					expr += ctx.getChild(1).getText();	// (
					expr += newTexts.get(ctx.args());
				}
				else								// expr
				{
					expr += ctx.getChild(1).getText();	// [
					expr += newTexts.get(ctx.expr(0));
				}
				expr += ctx.getChild(3).getText();	// ) | ]
				
			}
			// IDENT '[' expr ']' '=' expr
			else
			{
				expr += ctx.getChild(0).getText();	// IDENT
				expr += ctx.getChild(1).getText(); 	// [
				expr += newTexts.get(ctx.expr(0));	// expr
				expr += ctx.getChild(3).getText();	// ]
				expr += " ";
				expr += ctx.getChild(4).getText();	// =
				expr += " ";
				expr += newTexts.get(ctx.expr(1));	// expr
			}
			newTexts.put(ctx, expr);
		}
	}

	
	// args	: expr (',' expr)* | ;
	@Override
	public void exitArgs(MiniCParser.ArgsContext ctx) {
		// TODO Auto-generated method stub
		super.exitArgs(ctx);
		String args = "";
		if(ctx.getChildCount() >= 0)
		{
			for(int i=0; i<ctx.getChildCount(); i++)
			{
				if(i % 2 == 0)
					args += newTexts.get(ctx.expr(i/2));	// expr
				else
				{
					args += ctx.getChild(i).getText();		// ,
					args += " ";
				}
			}
		}
		newTexts.put(ctx, args);
	}
	
	private String printTab()
	{
		String blank = "";
		for(int i=0; i<tab; i++)
			blank += "    ";
		return blank;
	}
}
