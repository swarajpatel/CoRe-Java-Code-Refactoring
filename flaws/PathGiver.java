package flaws; /**
 * Created by Vedant on 03-Dec-16.
 */
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Swaraj on 12/1/2016.
 */
public class PathGiver {
    public static ArrayList<String> listClasses(File projectDir){
        ArrayList<String> paths = new ArrayList<String>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

            paths.add(path);

        }).explore(projectDir);

        return paths;
    }



    public static ArrayList<String> givePath(String mainPath) {
        File projectDir = new File(mainPath);
        ArrayList<String> pathList = listClasses(projectDir);
        for(int i=0;i<pathList.size();i++){
            String st=pathList.get(i);
           // st=st.replaceAll("/","\\\\");
            //System.out.println(st);
            pathList.remove(i);
            pathList.add(i,mainPath+st);

            //System.out.println(mainPath+st);

        }
        return  pathList;
    }


}

