package flaws; /**
 * Created by Vedant on 01-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FieldAccessExample {

    public static void listFieldCalls(File projectDir, ClassOrInterfaceDeclaration cd) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            List<Node> list=cd.getChildrenNodes();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override

                    public void visit(FieldAccessExpr n, Object arg) {
                        super.visit(n, arg);

                                System.out.println(" [L " + n.getBeginLine() + "] " + n.getFieldExpr());



                    }
                }.visit(JavaParser.parse(file), null);
                System.out.println(); // empty line
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
    }


}
