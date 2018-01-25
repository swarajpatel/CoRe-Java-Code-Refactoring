package flaws; /**
 * Created by Vedant on 01-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
//import com.google.common.base.Charsets;
//import com.google.common.io.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FieldDeclarationExample {

    public static ArrayList<FieldDeclaration> listFields(File projectDir, ClassOrInterfaceDeclaration cd) {
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println(path);
            //System.out.println(Strings.repeat("=", path.length()));
            List<Node> list=cd.getChildrenNodes();

            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(FieldDeclaration n, Object arg) {
                        super.visit(n, arg);
                        String name=n.toString();
                        for(Node n1:list){
                            if(n1.toString().equals(name)){
                                //System.out.println(" [L " + n.getBeginLine() + "] " + n.getVariables());
                                fd.add(n);
                                break;

                            }
                        }


                    }
                }.visit(JavaParser.parse(file), null);

                //System.out.println(); // empty line
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return fd;
    }
    public static void changeFieldDecl(File projectDir,String field, String newField,  Object arg){

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>(){
                    @Override
                    public void visit(NameExpr n, Object arg){
                        super.visit(n,arg);
                        //System.out.println(n.toString());
                        if(n.toString().equals(field)) {
                            //System.out.println(n);
                            //List<VariableDeclarator> a = n.getVariables();
                            //saveToFile(field, newField, PathGiver.givePath(projectDir.getAbsolutePath()).get(0));
                        }
                    }
                }.visit(JavaParser.parse(file), arg);
            } catch (ParseException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }

        }).explore(projectDir);
    }
    public static ArrayList<FieldDeclaration> listAllFields(File projectDir,CompilationUnit cu) {
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {



                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(FieldDeclaration n, Object arg) {
                        super.visit(n, arg);
                        String name=n.toString();


                                //System.out.println(" [L " + n.getBeginLine() + "] " + n.getVariables());
                                fd.add(n);
                    }
                }.visit(cu, null);

                //System.out.println(); // empty line

        }).explore(projectDir);
        return fd;
    }

    public static ArrayList<VariableDeclarator> listAllVar(File projectDir) {
        ArrayList<VariableDeclarator> vd=new ArrayList<VariableDeclarator>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {


            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(VariableDeclarator n, Object arg) {
                        super.visit(n, arg);
                        vd.add(n);
                    }
                }.visit(JavaParser.parse(file), null);

                //System.out.println(); // empty line
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return vd;
    }

    /*
    private static void saveToFile(String oldText,String newText,String path){
        try {
            String data = Files.toString(new File(path), Charsets.UTF_8);
            System.out.println(data);
            System.out.println(oldText);
            System.out.println(newText);
            data=data.replace(" "+oldText," "+newText);
            data=data.replace("."+oldText,"."+newText);
            data=data.replace(oldText+".",newText+".");
            data=data.replace("+"+oldText,"+"+newText);
            data=data.replace("-"+oldText,"-"+newText);
            data=data.replace("*"+oldText,"*"+newText);
            data=data.replace("/"+oldText,"/"+newText);


            System.out.println(data);


            FileWriter fileWriter=new FileWriter(new File(path));
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    */
}
