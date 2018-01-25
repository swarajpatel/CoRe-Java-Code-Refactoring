package flaws; /**
 * Created by Vedant on 01-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
//import com.google.common.base.Charsets;
//import com.google.common.io.Files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MethodDeclarationExample {

    public static ArrayList<MethodDeclaration> listMethods(File projectDir, ClassOrInterfaceDeclaration cd) {
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

            List<Node> list=cd.getChildrenNodes();

            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        String name=n.toString();
                        for(Node n1:list){
                            if(n1.toString().equals(name)){

                                md.add(n);
                                break;

                            }
                        }


                    }
                }.visit(JavaParser.parse(file), null);


            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return md;
    }


    public static void renameMethodExample(File projectDir, String methodName, String newName) {
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        String name=n.getName();
                        if(methodName.equals(name)) {
                            String oldText=n.getName();
                            /*
                            n.setName(newName);
                            String newText=n.toString();
                            */
                            //saveToFile(oldText, newName, PathGiver.givePath(projectDir.getAbsolutePath()).get(0));

                        }
                    }
                }.visit(JavaParser.parse(file), null);


            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);

    }
    /*
    private static void saveToFile(String oldText,String newText,String path){
        try {
            String data = Files.toString(new File(path), Charsets.UTF_8);
            System.out.println(data);
            System.out.println(oldText);
            System.out.println(newText);
            data=data.replace(oldText,newText);
            System.out.println(data);


            FileWriter fileWriter=new FileWriter(new File(path));
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    */
    public static ArrayList<MethodDeclaration> getMethods(File projectDir) {
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);

                        String name=n.getName();
                        //System.out.println(name);
                        md.add(n);
                    }
                }.visit(JavaParser.parse(file), null);


            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return md;
    }


}
