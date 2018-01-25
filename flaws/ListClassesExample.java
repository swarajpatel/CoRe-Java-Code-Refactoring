package flaws; /**
 * Created by Vedant on 01-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListClassesExample {

    public static ArrayList<ClassOrInterfaceDeclaration> listClasses(File projectDir) {
        ArrayList<ClassOrInterfaceDeclaration> list=new ArrayList<ClassOrInterfaceDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println(path);
            //System.out.println(Strings.repeat("=", path.length()));
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);

                        list.add(n);
                    }
                }.visit(JavaParser.parse(file), null);
             //   System.out.println(); // empty line
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return list;
    }
    public static String listParentClass(ClassOrInterfaceDeclaration cd){
        System.out.println(cd.getName());
        System.out.println(cd.getExtends());
        return cd.getExtends().get(0).getName().toString();
    }

}
