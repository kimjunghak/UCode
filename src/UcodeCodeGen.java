import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Created by kjh on 16. 11. 26.
 */
public class UcodeCodeGen {
    public static void main(String[] args) throws IOException {
        MiniCLexer lexer = new MiniCLexer(new ANTLRFileStream("src/test2.c"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCParser parser = new MiniCParser(tokens);
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new UCodeGenListener(), tree);
    }
}
