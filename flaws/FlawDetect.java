package flaws;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;




public class FlawDetect {
    /*
    * classDataList stores data about each class in the form of ClassData Objects*/
    public static ArrayList<ClassData> classdatalist=new ArrayList<ClassData>();
    public static ArrayList<Object> metrics=new ArrayList<>();
    public static ArrayList<String> dupList=new ArrayList<>();
    /*
 * detectFlaws() method intiates Parsing of class and stores the extracted data in ClassData objects.
  * These objects are then added to classdatalist
  *
  * */
    public static ArrayList<ClassData> detectFlaws(String mainPath,String filePath) throws IOException, ParseException {
        //String mainPath="C:\\Users\\Vedant\\workspace\\Lect8\\src";
        metrics.clear();
        classdatalist.clear();
        dupList.clear();

        File projectDir = new File(mainPath);
        for(ClassOrInterfaceDeclaration cd: ListClassesExample.listClasses(projectDir)){

            ArrayList<FieldDeclaration> fd= FieldDeclarationExample.listFields(projectDir,cd);
            int number_of_fields=0;

            for(FieldDeclaration field:fd){
                List<VariableDeclarator> var=field.getVariables();

                number_of_fields+=var.size();
            }
            // System.out.println("Number of fields = "+number_of_fields);

            int number_of_methods=0;
            ArrayList<MethodDeclaration> md= MethodDeclarationExample.listMethods(projectDir,cd);
            number_of_methods+=md.size();
            //System.out.println("Number of Methods = "+number_of_methods);
            ClassData classdata=new ClassData(cd,number_of_fields,number_of_methods,fd,md);
            classdata.packageName=filePath;
            classdatalist.add(classdata);

            //FieldAccessExample.listFieldCalls(projectDir,cd);
        }
        for(ClassData classData:classdatalist){
            ArrayList<MethodDeclaration> mdlist=classData.methodDeclarations;
            int count=0;
            for(MethodDeclaration md:mdlist){
                FileInputStream in = new FileInputStream( filePath);
                CompilationUnit cu;
                try {
                    // parse the file
                    cu = JavaParser.parse(in);
                    ArrayList<String> list_st=FieldAccessList.getAccessedField(md,classData.fieldDeclarations,cu,null);
                    count+=list_st.size();
                } finally {
                    in.close();
                }
            }
            classData.distinct_field_accesses=count;
        }




        PathGiver pathGiver=new PathGiver();


        ArrayList<ClassDataTest> classes= FieldAccessDemo.returnMemberData(pathGiver.givePath(mainPath));
        for (ClassDataTest cdt:classes){
            for (ClassData classData:classdatalist){
                if(cdt.ClassName.equals(classData.cd.getName())){
                    classData.incoming_calls=cdt.incomingMethodCalls;
                    classData.outgoing_calls=cdt.outgoingMethodCalls;
                }
            }
        }
        metrics.add("Classes");

        FlawDetect flawDetect=new FlawDetect();
        for(ClassData classData:classdatalist){
            metrics.add(classData.cd.getName());
        }


        flawDetect.isLargeClass();
        flawDetect.checkCohesion();
        flawDetect.checkControllerClass();
        flawDetect.checkDataClass();
        //flawDetect.checkCoupling(filePath);
        flawDetect.duplicateMethod(filePath);
        //flawDetect.generateReport();
        storePrev(filePath);
        return classdatalist;
    }
    /*
    *
    *Duplicate Method Signature
    * */
    public  ArrayList<ClassData> duplicateMethod(String filePath){
        ArrayList<ClassData> duplicate=new ArrayList<ClassData>();
        metrics.add("Duplicate Method");
        for(ClassData cd:classdatalist){
            ArrayList<MethodDeclaration> mdList=cd.methodDeclarations;
            for(MethodDeclaration md:mdList) {

                for (ClassData cd1 : classdatalist) {

                    if (!cd.equals(cd1)) {
                        ArrayList<MethodDeclaration> mdList1 = cd1.methodDeclarations;


                        for (MethodDeclaration md1 : mdList1) {
                            if (md1.getName().equals(md.getName())) {
                                //System.out.println(md.getParameters());
                                List<Parameter> param=md.getParameters();
                                List<Parameter> param1=md1.getParameters();

                                boolean status=true;
                                if(param.size()==param1.size()) {
                                    for(int i=0;i<param.size();i++){
                                        if(!(param.get(i).getType().toString().equals(param1.get(i).getType().toString()))){
                                            //System.out.println(param.get(i).getType().toString()+"----"+param1.get(i).getType().toString());
                                            status=false;
                                            break;
                                        }
                                    }

                                } else
                                    status=false;

                                if(status){
                                    cd.reportFlag[0] = 1;
                                    // metrics.add("Duplicate Method" + md.getName()+"\n");
                                    metrics.add( md.getName());
                                    dupList.add(md.getName());
                                    //metrics.add("----------------"+"\n");
                                    //System.out.println("Duplicate Method = " + md.getName());
                                    duplicate.add(cd);
                                    duplicate.add(cd1);
                                }

                            }
                        }
                    }
                }
            }
        }

        return duplicate;
    }
    /*
    * Duplicate Method Name
    *
    * */
    public static String  getDuplicateMethods(){
        return dupList.get(0);
    }

    /*
    * Finds out whether class is LargeCLass
    *
    * */
    void isLargeClass(){

        double average=0;
        metrics.add("Number of Members");

        for(ClassData classdata:classdatalist){
            int number_of_members=classdata.number_of_fields+classdata.number_of_methods;
            //System.out.println(classdata.cd.getName()+" has "+number_of_members+" members");
            average+=number_of_members;
        }
        average/=classdatalist.size();
        //System.out.println("Avergae is "+average);
        for(ClassData classdata:classdatalist){

            int number_of_members=classdata.number_of_fields+classdata.number_of_methods;

            metrics.add(number_of_members);
            if(number_of_members>average&&number_of_members>6){

                classdata.reportFlag[6]=1;
            }
        }
    }
    /*
    * Finds out whether class has Low Cohesion or not
    *
    * */
    void checkCohesion(){
        metrics.add("LCOM5");

        for(ClassData classdata:classdatalist){
            double lcom5=0;
            double a=classdata.distinct_field_accesses;
            double l=classdata.number_of_fields;
            double k=classdata.number_of_methods;
            /* where,
                a is number of  distinct_field_accesses made by  method
                l is number of field in class
                k is number_of_methods in class
                 */
            if(a==0&&k>1) {

                classdata.reportFlag[1]=1;
            }else {
                lcom5 = (a - k * l) / (l - k * l);


                if (lcom5 > 0.65)
                    classdata.reportFlag[1]=1;

            }
            // metrics.add("Lcom5 : " + lcom5+"\n");
            if(!Double.isNaN(lcom5))
                metrics.add(lcom5);
            else
                metrics.add("--");


        }
    }

    /*
    * Finds out whether class is Contoller CLass or not
    *
    * */
    void checkControllerClass(){
        metrics.add("Call Ratio");

        /*
        System.out.println("-------------------");
        System.out.println("Checking Contoller Class");*/
        double averageRatio=0;
        double classListLength=classdatalist.size();
        for(ClassData classData:classdatalist){

            //System.out.println("-------------------");
            String className=classData.cd.getName();
            //System.out.println(className);
            //System.out.println();
            double incomingCalls=classData.incoming_calls;
            double outgoingCalls=classData.outgoing_calls;
            double ratio=outgoingCalls/incomingCalls;

            averageRatio+=ratio;
        }
        averageRatio/=classListLength;
        //System.out.println("Average Ratio is : "+averageRatio);
        //System.out.println();
        for (ClassData classData:classdatalist){
            String className=classData.cd.getName();
            double incomingCalls=classData.incoming_calls;
            double outgoingCalls=classData.outgoing_calls;
            double ratio=outgoingCalls/incomingCalls;
            //metrics.add("Call Ratio" + ratio+"\n");
            if(Double.isNaN(ratio)||Double.isInfinite(ratio))
                metrics.add("--");
            else
                metrics.add(ratio);
            if(ratio>=averageRatio){
                classData.reportFlag[2]=1;
                //System.out.println("Class "+className+" is Controller Class");
            }
        }
    }



    /*
    * Finds out whether class is DataClass
    *
    * */
    void checkDataClass() {
        //System.out.println("--------------------");
        //System.out.println("Checking Data Class");
        metrics.add("Data Class Ratio");

        ArrayList<String> dataCLassList =new ArrayList<String>();
        for (ClassData classData : classdatalist) {
            double getterCount=0,setterCount=0;
            for (FieldDeclaration fieldList:classData.fieldDeclarations){
                Type type = fieldList.getType();
                for (VariableDeclarator var:fieldList.getVariables()){
                    String varName=var.toString();
                    varName=varName.substring(0,1).toUpperCase()+varName.substring(1);
                    String getterName="get"+varName;
                    //System.out.println(getterName);
                    String setterName="set"+varName;
                    for (MethodDeclaration method:classData.methodDeclarations){
                        String method_name=method.getName();
                        if(method_name.equals(getterName)&&type.equals(method.getType())&&method.getParameters().size()==0){

                            getterCount++;
                        }

                        if(method_name.equals(setterName)&&method.getType().toString().equals("void")&&method.getParameters().size()==1){
                            if(method.getParameters().get(0).getType().equals(type)){
                                setterCount++;
                            }
                        }
                    }
                }
            }


            double ratio=(getterCount+setterCount)/classData.methodDeclarations.size();
            //metrics.add("Data Class Ratio" + ratio+"\n");
            metrics.add(Math.round(ratio));
            if(ratio<=1&&ratio>0.6){

                for(FieldDeclaration fd: classData.fieldDeclarations){
                    //System.out.println(fd.getType()+"=======");
                    if(fd.getModifiers()!=2){

                        classData.reportFlag[4]=1;
                        break;
                    }
                }

            }
        }


    }
    private void checkCoupling(String filepath) {
        double classListLength=classdatalist.size();
        System.out.println(classListLength);
        // for(ClassData classData:classdatalist){
        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(new File(filepath));

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //System.out.println(classListLength+"====");

        //System.out.println("-------------------");

        // Test.oc();
        double cbo=ObjectCalls.giveLinks(cu,null)/classListLength;
        //System.out.println("CBO="+cbo);
        if(cbo>4)
            System.out.println("tightly coupled");

        // double objectCalls=classData.


        // }

    }



    /*
    * Generate final list of flaws in a class
    * */
    void generateReport(){
        for (ClassData classData:classdatalist){

            if(classData.reportFlag[0]==1){
                System.out.println("Class "+classData.cd.getName()+" has Duplicate code");
            } else if(classData.reportFlag[1]==1){
                System.out.println("Class "+classData.cd.getName()+" has low Cohesion");
            } else if(classData.reportFlag[2]==1){
                System.out.println("Class "+classData.cd.getName()+" is Controller Class");
            } else if(classData.reportFlag[3]==1){
                System.out.println("Class "+classData.cd.getName()+" has high Coupling");
            } else if(classData.reportFlag[4]==1){
                System.out.println("Class "+classData.cd.getName()+" is Data Class");
            } else if(classData.reportFlag[5]==1){
                System.out.println("Class "+classData.cd.getName()+" is God Class");

            } else if(classData.reportFlag[6]==1){
                System.out.println("Class "+classData.cd.getName()+" is Large Class");
            }

        }
    }


    /*
    * Get CLassData object on the basis of
    * CLass Name
    *
    * */
    static ClassData getClassInfo(String className){
        for(ClassData classData:classdatalist){
            if(classData.cd.getName().equals(className)){
                return classData;
            }
        }
        return null;
    }

   /*
    * Get MethodDeclaration object on the basis of
    * given Method Name
    *
    * */

    static MethodDeclaration getMethodInfo(String methodName,ClassData classData){
        for(MethodDeclaration md:classData.methodDeclarations){
            if(md.getName().equals(methodName)){
                //System.out.println(md.getName());
                return md;
            }
        }
        return null;
    }

   /*
    * Get MethodDeclaration object on the basis of
    * given Method Name and FilePath
    *
    * */

    static MethodDeclaration getMethodInfoFromPath(String methodName,String filePath){
        for(ClassData classData:classdatalist){
            if(classData.packageName.equals(filePath)){
                for(MethodDeclaration md:classData.methodDeclarations){
                    if(md.getName().equals(methodName)){
                        //System.out.println(md.getName());
                        return md;
                    }
                }
            }
        }
        return null;
    }

    /*
    * Get CLassData object on the basis of
    * given Method Name
    *
    * */
    static ClassData getClassInfoFromMethod(String methodName,String filePath){
        for(ClassData classData:classdatalist){
            if(classData.packageName.equals(filePath)){
                for(MethodDeclaration md:classData.methodDeclarations){
                    if(md.getName().equals(methodName)){
                        //System.out.println(md.getName());
                        return classData;
                    }
                }
            }
        }
        return null;
    }


    /*
    * Remove Field From class
    * */
    static void removeField(ClassData classData,String filePath){
        List<BodyDeclaration> memberList=classData.cd.getMembers();

        ArrayList<FieldDeclaration> fieldDeclarations=classData.fieldDeclarations;
        ArrayList<String> toNotRemove=new ArrayList<String>();
        try {
            // parse the file
            FileInputStream in = new FileInputStream( filePath);
            CompilationUnit cu;
            cu = JavaParser.parse(in);

            for (MethodDeclaration md : classData.methodDeclarations) {

                toNotRemove.addAll(FieldAccessList.getAccessedField(md, fieldDeclarations, cu,null));
            }
            System.out.println(toNotRemove);
            ArrayList<String> toRemove=new ArrayList<String>();
            for(FieldDeclaration fd:classData.fieldDeclarations){
                for (VariableDeclarator vd:fd.getVariables()){
                    toRemove.add(vd.toString());

                }
            }
            System.out.println(toRemove);

            toRemove.removeAll(toNotRemove);
            System.out.println(toRemove);
            List<BodyDeclaration> bdlist=new ArrayList<BodyDeclaration>();
            for (int j=0;j<memberList.size();j++){
                BodyDeclaration bd=memberList.get(j);
                List<Node> nodeList=bd.getChildrenNodes();
                List<Node> toRemoveNode=new ArrayList<Node>();

                //System.out.println(nodeList+"============+");
                for(int i=0;i<nodeList.size();i++){
                    Node node=nodeList.get(i);

                    if(toRemove.contains(node.toString())) {
                        toRemoveNode.add(node);
                        bdlist.add(bd);
                    }
                }
            }
            memberList.removeAll(bdlist);
            if(!bdlist.isEmpty()){
                FieldDeclaration fd=(FieldDeclaration) bdlist.get(0);

                for(VariableDeclarator vd1:fd.getVariables()) {
                    if(toNotRemove.contains(vd1.toString())) {
                        FieldDeclaration fieldDeclaration = new FieldDeclaration(fd.getModifiers(), fd.getType(), vd1);

                        memberList.add(0, fieldDeclaration);
                    }

                }


            }

        }
        catch (ParseException e){

        } catch (FileNotFoundException e) {

        }
    }

    /*
    * Remove Method From class
    * */
    static void removeMethod(ClassData classData,MethodDeclaration md){
        List<BodyDeclaration> memberList=classData.cd.getMembers();
        int index=-1;
        for (int i=0;i<memberList.size();i++){
            BodyDeclaration mdTemp=memberList.get(i);
            if(mdTemp.toString().equals(md.toString()))
                index=i;
        }
        memberList.remove(index);
        index=-1;
        ArrayList<MethodDeclaration> mdList=classData.methodDeclarations;
        for(int i=0;i<mdList.size();i++){
            MethodDeclaration mdTemp=mdList.get(i);
            if(mdTemp.getNameExpr().toString().equals(md.getNameExpr().toString())){
                mdList.remove(i);
            }
        }
    }
    /*
    Perform Encapsulation of field

    */

    public static boolean isMethod(ClassData cd,String member){
        for(MethodDeclaration md:cd.methodDeclarations){
            if(member.toString().equals(md.getName()))
                return true;
        }
        return false;
    }

    public static String encapsulate(String className,String filePath){
        className=className.trim();
        ClassData classData=getClassInfo(className);
        ArrayList<FieldDeclaration> oldList=new ArrayList<FieldDeclaration>();
        ArrayList<FieldDeclaration> newList=new ArrayList<FieldDeclaration>();
        List<BodyDeclaration> memberList=classData.cd.getMembers();
        for(int i=0;i<memberList.size();i++){
            try {

                FieldDeclaration fd =(FieldDeclaration) memberList.get(i);
                if(fd.getModifiers()!=3){
                    oldList.add(fd);

                    for(VariableDeclarator vd1:fd.getVariables()) {


                        FieldDeclaration fd1 = new FieldDeclaration(2 ,fd.getType(), vd1);

                        newList.add(fd1);

                    }
                }

            } catch(Exception e){

                continue;
            }

        }
        memberList.removeAll(oldList);
        memberList.addAll(newList);


        String content=sendChanges(filePath);

        return content;



    }
    /*
    * Perform Extract Class Refctoring
    * */
    public static String extractClass(String mainPath,String filePath,String methodName,String newClassName) throws IOException, ParseException {

        ClassOrInterfaceDeclaration cid=new ClassOrInterfaceDeclaration();
        cid.setName(newClassName);
        ClassData classData=new ClassData();
        classData.cd=cid;
        classData.packageName=filePath;
        classdatalist.add(classData);
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();
        classData.methodDeclarations=md;
        classData.fieldDeclarations=fd;
        //System.out.println(cid.toString());
        String content=moveMethod(mainPath,filePath,methodName,newClassName);
        return content;
    }

    /*
    * Perform Extract Class Refctoring
    * */
    public static String extractSuperClass(String mainPath,String filePath,String methodName,String newClassName) throws IOException, ParseException {

        ClassOrInterfaceDeclaration cid=new ClassOrInterfaceDeclaration();
        cid.setName(newClassName);
        ClassData classData=new ClassData();
        classData.cd=cid;
        classData.packageName=filePath;
        classdatalist.add(classData);
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();
        classData.methodDeclarations=md;
        classData.fieldDeclarations=fd;
        //System.out.println(cid.toString());
        ClassData classDataOriginal=getClassInfoFromMethod(methodName,filePath);
        ClassOrInterfaceType classOrInterfaceType=new ClassOrInterfaceType(cid.getName());
        List<ClassOrInterfaceType> list=new ArrayList<ClassOrInterfaceType>();
        list.add(classOrInterfaceType);

        classDataOriginal.cd.setExtends(list);

        String content=moveMethod(mainPath,filePath,methodName,newClassName);

        return content;
    }

    public static String removeDuplicate(String mainPath,String filePath,String methodName,String newClassName) throws IOException, ParseException {

        ClassOrInterfaceDeclaration cid=new ClassOrInterfaceDeclaration();
        cid.setName(newClassName);
        ClassData classData=new ClassData();
        classData.cd=cid;
        classData.packageName=filePath;
        classdatalist.add(classData);
        ArrayList<MethodDeclaration> md=new ArrayList<MethodDeclaration>();
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();
        classData.methodDeclarations=md;
        classData.fieldDeclarations=fd;
        //System.out.println(cid.toString());
        ClassData classDataOriginal=getClassInfoFromMethod(methodName,filePath);
        ClassOrInterfaceType classOrInterfaceType=new ClassOrInterfaceType(cid.getName());
        List<ClassOrInterfaceType> list=new ArrayList<ClassOrInterfaceType>();
        list.add(classOrInterfaceType);
        FlawDetect flawDetect=new FlawDetect();
        ArrayList<ClassData> classList=flawDetect.duplicateMethod(filePath);
        for(ClassData classData1:classList){
            if(!classData1.cd.getName().equals(classDataOriginal.cd.getName())) {
                MethodDeclaration methodDeclaration = getMethodInfo(methodName, classData1);
                try {
                    classData1.cd.setExtends(list);
                    removeMethod(classData1, methodDeclaration);
                } catch (Exception e){}

            }
        }

        classDataOriginal.cd.setExtends(list);

        String content=moveMethod(mainPath,filePath,methodName,newClassName);

        return content;
    }



    /*
    * Perform Move Method Refctoring
    * */
    public static String moveMethod(String mainPath,String filePath,String methodName,String newClassName) throws IOException, ParseException {

        ClassData classData=getClassInfoFromMethod(methodName,filePath);
        MethodDeclaration md=getMethodInfoFromPath(methodName,filePath);
        ClassData newClassData=getClassInfo(newClassName);
        List<BodyDeclaration> memberList=newClassData.cd.getMembers();
        memberList.add(md);
        // ArrayList<String> pathLists=PathGiver.givePath(mainPath);
        FileInputStream in = new FileInputStream( filePath);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
            ArrayList<String> list_st=FieldAccessList.getAccessedField(md,classData.fieldDeclarations,cu,null);
            for (String s:list_st){
                //System.out.println(s);
                for (FieldDeclaration fd:classData.fieldDeclarations){
                    if(fd.toString().contains(" "+s)){
                        for(VariableDeclarator vd1:fd.getVariables()) {

                            if(vd1.toString().contains(s)) {
                                // System.out.println(fd.getType()+" "+s+";");

                                FieldDeclaration fieldDeclaration = new FieldDeclaration(2, fd.getType(), vd1);

                                memberList.add(0,fieldDeclaration);

                            }
                        }
                    }
                }
            }
        } finally {
            in.close();
        }
        removeMethod(classData,md);
        removeField(classData,filePath);

        String content=sendChanges(filePath);

        return content;

    }
    public static String pullUpMethod(String methodName,String mainPath,String filePath) throws IOException, ParseException {
        //refactoring();
        ClassData classData=getClassInfoFromMethod(methodName,filePath);
        System.out.println(classData.cd.getName()+"==============");
        File projectDir=new File(mainPath);
        String newClass=ListClassesExample.listParentClass(classData.cd);
        String content= moveMethod(mainPath,filePath,methodName,newClass);
        return content;
    }
    /*
    * Combine Refactored code
    * */
    static String sendChanges(String filePath){
        String content="";
        content+=SaveChanges.readImports(filePath);

        for(ClassData classData:classdatalist){

            if(classData.packageName.equals(filePath)) {
                content += "\n"+classData.cd.toString()+"\n";

            }
        }

        SaveChanges.write(filePath,content);
        return content;
    }
    static String prevCode="",originalCode="";
    public static String storeOriginal(String filePath){

        CompilationUnit cu;
        try {
            FileInputStream in = new FileInputStream( filePath);
            cu = JavaParser.parse(in);
            originalCode=cu.toString();

        } catch(Exception e){
            System.out.println(e);
        }
        return originalCode;
    }
    public static String storePrev(String filePath){

        CompilationUnit cu;
        try {
            FileInputStream in = new FileInputStream( filePath);
            cu = JavaParser.parse(in);
            prevCode=cu.toString();

        } catch(Exception e){
            System.out.println(e);
        }
        return originalCode;
    }

    public static String undo(String filePath) {
        SaveChanges.write(filePath,prevCode);
        return prevCode;
    }
    public static String revertChanges(String filePath) {
        SaveChanges.write(filePath,originalCode);
        return originalCode;
    }

}
/*
* This class saves changes to a file
*
* */
class SaveChanges{
    static String originalCode="";
    /*
    * Read all imports from a file
    *
    * */
    public static String readImports(String FILENAME) {

        BufferedReader br = null;
        FileReader fr = null;
        String importsList="";
        try {

            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(FILENAME));

            while ((sCurrentLine = br.readLine()) != null) {
                if(!sCurrentLine.contains("class "))
                    importsList+=sCurrentLine+"\n";
                else
                    return importsList;
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return importsList;
    }



    /*
    * Write Code to a file
    *
    * */
    public static void write(String FILENAME,String content) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME,false))) {



            bw.write(content);

            // no need to close it.
            //bw.close();

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}