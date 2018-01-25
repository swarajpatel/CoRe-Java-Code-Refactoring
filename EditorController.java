import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import flaws.ClassData;
import flaws.FlawDetect;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class EditorController {
    public GridPane gridreport;
    public GridPane gridmetrics;
    public Button button1=new Button();
    @FXML

    private TextArea areaText1;
    private TextFile currentTextFile;
    private EditorModel model;
    private String mainPath;
    private String filePath;

    public EditorController(EditorModel model) {

        this.model = model;

    }


    @FXML

    private void onSave() {

        TextFile textFile = new TextFile(currentTextFile.getFile(), Arrays.asList(areaText1.getText().split("\n")));

        model.save(textFile);

    }



    @FXML

    private void onLoad() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);
        areaText1.setFont(new Font("verdana",14));
        if (file != null) {

            IOResult<TextFile> io = model.load(file.toPath());
            mainPath = file.toPath().toString();
            filePath = file.toPath().toString();
            mainPath = mainPath.substring(0, mainPath.lastIndexOf("\\"));
            if (io.isOk() && io.hasData()) {

                currentTextFile = io.getData();
                areaText1.clear();

                currentTextFile.getContent().forEach(line -> areaText1.appendText(line + "\n"));
                FlawDetect.storeOriginal(filePath);
            } else {

                System.out.println("Failed");

            }

        }

        ContextMenu contextMenu = new ContextMenu();
        //MenuItem renameField = new MenuItem("Rename Field");
        // MenuItem renameMethod = new MenuItem("Rename Method");
        MenuItem moveMethod = new MenuItem("Move Method");
        MenuItem moveField = new MenuItem("Move Field");
        MenuItem extractClass = new MenuItem("Extract Class");
        MenuItem extractSuperClass = new MenuItem("Extract Super Class");
        MenuItem removeDuplicate = new MenuItem("Remove Duplicate");
        MenuItem pullUpMethod = new MenuItem("pullUpMethod");
        MenuItem encapsulate = new MenuItem("Encapsulation");

        contextMenu.getItems().addAll(moveField, moveMethod, extractClass, extractSuperClass, removeDuplicate,pullUpMethod,encapsulate);
        areaText1.setContextMenu(contextMenu);
        /*
        * Encapsulation of data class Members
        *
        * */
        encapsulate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String className=areaText1.getSelectedText();
                FlawDetect.classdatalist.clear();
                try {
                    FlawDetect.detectFlaws(mainPath,filePath);
                    String content = FlawDetect.encapsulate( className,filePath);

                    areaText1.clear();
                    areaText1.setText(content);
                    FlawDetect.classdatalist.clear();
                } catch (Exception e) {
                    System.out.println( className+"==============");
                }

            }
        });
        /*
        * Extract Class
        *
        * */
        extractClass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                VBox comp = new VBox();
                Label destClass = new Label("Destination Class");
                String methodName = areaText1.getSelectedText();

                TextField destClassName = new TextField();

                Button refactor = new Button("Refactor");
                refactor.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println(methodName);
                        String className = destClassName.getText();
                        System.out.println(className);
                        try {
                            FlawDetect.classdatalist.clear();
                            FlawDetect flawDetect = new FlawDetect();
                            flawDetect.detectFlaws(mainPath, filePath);
                            String content = FlawDetect.extractClass(mainPath, filePath, methodName, className);
                            areaText1.clear();
                            areaText1.setText(content);
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (ParseException e) {
                            System.out.println(e);
                        }
                        newStage.close();
                    }
                });
                comp.getChildren().addAll(destClass, destClassName, refactor);

                Scene stageScene = new Scene(comp, 200, 200);

                newStage.setScene(stageScene);
                newStage.show();
            }
        });


        extractSuperClass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                VBox comp = new VBox();
                Label destClass = new Label("Destination Class");
                String methodName = areaText1.getSelectedText();

                TextField destClassName = new TextField();

                Button refactor = new Button("Refactor");
                refactor.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println(methodName);
                        String className = destClassName.getText();
                        System.out.println(className);
                        try {
                            FlawDetect.classdatalist.clear();
                            FlawDetect flawDetect = new FlawDetect();
                            flawDetect.detectFlaws(mainPath, filePath);
                            String content = FlawDetect.extractSuperClass(mainPath, filePath, methodName, className);
                            areaText1.clear();
                            areaText1.setText(content);
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (ParseException e) {
                            System.out.println(e);
                        }
                        newStage.close();
                    }
                });
                comp.getChildren().addAll(destClass, destClassName, refactor);

                Scene stageScene = new Scene(comp, 200, 200);

                newStage.setScene(stageScene);
                newStage.show();
            }
        });

        removeDuplicate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                VBox comp = new VBox();
                Label destClass = new Label("Destination Class");
                String methodName = areaText1.getSelectedText();

                TextField destClassName = new TextField();

                Button refactor = new Button("Refactor");
                refactor.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        String className = destClassName.getText();
                        System.out.println(className);
                        try {
                            FlawDetect.classdatalist.clear();
                            FlawDetect flawDetect = new FlawDetect();
                            flawDetect.detectFlaws(mainPath, filePath);
                            String content = FlawDetect.removeDuplicate(mainPath, filePath, methodName, className);
                            areaText1.clear();
                            areaText1.setText(content);
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (ParseException e) {
                            System.out.println(e);
                        }
                        newStage.close();
                    }
                });
                comp.getChildren().addAll(destClass, destClassName, refactor);

                Scene stageScene = new Scene(comp, 200, 200);

                newStage.setScene(stageScene);
                newStage.show();
            }
        });

        moveMethod.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                VBox comp = new VBox();
                Label destClass = new Label("Destination Class");
                String methodName = areaText1.getSelectedText();

                TextField destClassName = new TextField();

                Button refactor = new Button("Refactor");
                refactor.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //System.out.println(methodName);
                        String className = destClassName.getText();
                        // System.out.println(className);
                        try {
                            FlawDetect.classdatalist.clear();
                            FlawDetect flawDetect = new FlawDetect();
                            flawDetect.detectFlaws(mainPath, filePath);
                            String content = FlawDetect.moveMethod(mainPath, filePath, methodName, className);
                            areaText1.clear();
                            areaText1.setText(content);
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (ParseException e) {
                            System.out.println(e);
                        }
                        newStage.close();
                    }
                });


                comp.getChildren().addAll(destClass, destClassName, refactor);

                Scene stageScene = new Scene(comp, 200, 200);

                newStage.setScene(stageScene);
                newStage.show();
            }
        });

        moveField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage newStage = new Stage();
                VBox comp = new VBox();
                Label destClass = new Label("Destination Class");
                String fieldName = areaText1.getSelectedText();

                TextField destClassName = new TextField();

                Button refactor = new Button("Refactor");
                refactor.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println(fieldName);
                        String className = destClassName.getText();
                        System.out.println(className);
                        newStage.close();
                    }
                });
                comp.getChildren().addAll(destClass, destClassName, refactor);

                Scene stageScene = new Scene(comp, 200, 200);

                newStage.setScene(stageScene);
                newStage.show();
            }
        });

        pullUpMethod.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String methodName=areaText1.getSelectedText();
                FlawDetect.classdatalist.clear();

                try {
                    FlawDetect.detectFlaws(mainPath,filePath);
                    String content = FlawDetect.pullUpMethod( methodName, mainPath, filePath);
                    areaText1.clear();
                    areaText1.setText(content);
                    FlawDetect.classdatalist.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @FXML

    private void onClose() {

        model.close();

    }

    @FXML



    public void onClick(ActionEvent actionEvent) {


        gridmetrics.getChildren().clear();
        gridreport.getChildren().clear();
        int i=0,j=0,c=0,r=0;
        FlawDetect flawDetect=new FlawDetect();

        gridreport.setPadding(new Insets(10,10,10,10));
        gridreport.setVgap(8);
        gridreport.setHgap(10);
        gridmetrics.setPadding(new Insets(0,0,0,0));
        //gridmetrics.setVgap(8);
       // gridmetrics.setHgap(30);

        // Text reportString=new Text();
        Label l=new Label("Report for current project");
        l.setFont(new Font("verdana",14));
        GridPane.setConstraints(l,i,j);
        j++;
        gridreport.getChildren().addAll(l);
        //gridreport.setStyle("-fx-background-color: linear-gradient(#99ccff, #ffff);-fx-border-radius: 2;-fx-border-color: grey;-fx-border-style:inset;");
        //gridmetrics.setStyle("-fx-background-color: linear-gradient(#99ccff, #ffff);-fx-border-radius: 2;-fx-border-color: grey;-fx-border-style:inset;");
        l=new Label("Metrics");
        GridPane.setConstraints(l,c,r);
        r++;
        gridmetrics.getChildren().addAll(l);




        try {
            FlawDetect.classdatalist.clear();
            ArrayList<ClassData> classdatalist=flawDetect.detectFlaws(mainPath,filePath);

            for (ClassData classData:classdatalist)
            {
                if(classData.reportFlag[0]==1){

                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("Duplicate Method");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);

                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);



                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            Stage newStage = new Stage();
                            newStage.setAlwaysOnTop(true);
                            newStage.getIcons().add(new Image("file:C:\\Users\\Vedant\\IdeaProjects\\code_refactoring\\src\\Image\\logo.PNG"));

                            newStage.setTitle("Extract Super Class");
                            //VBox comp = new VBox();
                            GridPane grid1 = new GridPane();
                            grid1.setPadding(new Insets(10,10,10,10));
                            grid1.setHgap(10);
                            grid1.setVgap(5);
                            Label destClass = new Label("Destination Class : ");
                            destClass.setFont(new Font("verdana",12));

                            TextField destClassName = new TextField();


                            GridPane.setConstraints(destClass,0,2);
                            GridPane.setConstraints(destClassName,1,2);

                            Button refactor = new Button("Refactor");

                            GridPane.setConstraints(refactor,1,4);

                            grid1.getChildren().addAll(destClass, destClassName, refactor);

                            refactor.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    String className = destClassName.getText();
                                    String methodName=FlawDetect.getDuplicateMethods();

                                    try {
                                        FlawDetect.classdatalist.clear();
                                        FlawDetect flawDetect = new FlawDetect();
                                        flawDetect.detectFlaws(mainPath, filePath);
                                        String content = FlawDetect.removeDuplicate(mainPath, filePath, methodName, className);
                                        areaText1.clear();
                                        areaText1.setText(content);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    newStage.close();
                                }
                            });


                            Scene stageScene = new Scene(grid1, 300, 200);

                            newStage.setScene(stageScene);
                            newStage.show();



                        }
                    });


                }
                if(classData.reportFlag[1]==1){

                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("Low Cohesion");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);

                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            Stage newStage = new Stage();
                            newStage.setAlwaysOnTop(true);
                            newStage.getIcons().add(new Image("file:C:\\Users\\Vedant\\IdeaProjects\\code_refactoring\\src\\Image\\logo.PNG"));

                            newStage.setTitle("Extract Class");
                            //VBox comp = new VBox();
                            GridPane grid1 = new GridPane();
                            grid1.setPadding(new Insets(10,10,10,10));
                            grid1.setHgap(10);
                            grid1.setVgap(5);
                            Label destClass = new Label("Destination Class : ");
                            destClass.setFont(new Font("verdana",12));
                            Label methodlistview = new Label("Method List : ");

                            TextField destClassName = new TextField();
                            final ComboBox methodlist = new ComboBox();
                            ArrayList<String> methods=new ArrayList<String>();
                            for(MethodDeclaration md: classData.methodDeclarations){
                                methods.add(md.getName());
                            }
                            methodlist.setPromptText("--Select Method--");
                            methodlist.getItems().addAll(
                                    methods
                            );
                            GridPane.setConstraints(methodlistview,0,0);
                            GridPane.setConstraints(methodlist,1,0);
                            GridPane.setConstraints(destClass,0,2);
                            GridPane.setConstraints(destClassName,1,2);

                            Button refactor = new Button("Refactor");

                            GridPane.setConstraints(refactor,1,4);

                            grid1.getChildren().addAll(methodlistview,methodlist,destClass, destClassName, refactor);

                            refactor.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    String className = destClassName.getText();
                                    String methodName=methodlist.getSelectionModel().getSelectedItem().toString();

                                    try {
                                        FlawDetect.classdatalist.clear();
                                        FlawDetect flawDetect = new FlawDetect();
                                        flawDetect.detectFlaws(mainPath, filePath);
                                        String content = FlawDetect.extractClass(mainPath, filePath, methodName, className);
                                        areaText1.clear();
                                        areaText1.setText(content);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    newStage.close();
                                }
                            });


                            Scene stageScene = new Scene(grid1, 300, 200);

                            newStage.setScene(stageScene);
                            newStage.show();



                        }
                    });


                }
                if(classData.reportFlag[2]==1){
                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("Controller class");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);

                }
                if(classData.reportFlag[3]==1){
                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("High Coupling");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);
                }
                if(classData.reportFlag[4]==1){
                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("Data class");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);
                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {


                            try {
                                FlawDetect.detectFlaws(mainPath,filePath);
                                String content = FlawDetect.encapsulate( classData.cd.getName(),filePath);



                                areaText1.clear();
                                areaText1.setText(content);
                                FlawDetect.classdatalist.clear();
                            } catch (Exception e) {
                                System.out.print(e);

                            }

                        }
                    });
                }
                if(classData.reportFlag[5]==1){

                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("God Class");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);
                }
                if(classData.reportFlag[6]==1){
                    Label l1=new Label(classData.cd.getName());
                    l1.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l1,i,j);
                    i++;
                    Label l2=new Label("Large class");
                    l2.setFont(new Font("verdana",12));
                    GridPane.setConstraints(l2,i,j);
                    i++;
                    Button b=new Button("Refactor");
                    GridPane.setConstraints(b,i,j);
                    j++;
                    i=0;
                    gridreport.getChildren().addAll(l1,l2,b);

                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            Stage newStage = new Stage();
                            newStage.setAlwaysOnTop(true);
                            newStage.getIcons().add(new Image("file:C:\\Users\\Vedant\\IdeaProjects\\code_refactoring\\src\\Image\\logo.PNG"));

                            newStage.setTitle("Extract Class");
                            //VBox comp = new VBox();
                            GridPane grid1 = new GridPane();

                            grid1.setPadding(new Insets(10,10,10,10));
                            grid1.setHgap(10);
                            grid1.setVgap(5);
                            Label destClass = new Label("Destination Class : ");
                            Label methodlistview = new Label("Method List : ");

                            TextField destClassName = new TextField();
                            final ComboBox methodlist = new ComboBox();
                            ArrayList<String> methods=new ArrayList<String>();
                            for(MethodDeclaration md: classData.methodDeclarations){
                                methods.add(md.getName());
                            }
                            methodlist.setPromptText("--Select Method--");
                            methodlist.getItems().addAll(
                                    methods
                            );
                            GridPane.setConstraints(methodlistview,0,0);
                            GridPane.setConstraints(methodlist,1,0);
                            GridPane.setConstraints(destClass,0,2);
                            GridPane.setConstraints(destClassName,1,2);

                            Button refactor = new Button("Refactor");

                            GridPane.setConstraints(refactor,1,4);

                            grid1.getChildren().addAll(methodlistview,methodlist,destClass, destClassName, refactor);

                            refactor.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    String className = destClassName.getText();
                                    String methodName=methodlist.getSelectionModel().getSelectedItem().toString();

                                    try {
                                        FlawDetect.classdatalist.clear();
                                        FlawDetect flawDetect = new FlawDetect();
                                        flawDetect.detectFlaws(mainPath, filePath);
                                        String content = FlawDetect.extractClass(mainPath, filePath, methodName, className);
                                        areaText1.clear();
                                        areaText1.setText(content);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    newStage.close();
                                }
                            });


                            Scene stageScene = new Scene(grid1, 300, 200);

                            newStage.setScene(stageScene);
                            newStage.show();



                        }
                    });


                }
                //gridreport.getChildren().addAll(reportString);



            }
            //areaText2.setText(" ");

            int size=FlawDetect.classdatalist.size();
            int k=1;
            c=0;r=1;
            size+=1;
            for(i=0;i<FlawDetect.metrics.size();i++)
            {
                l=new Label();
                l.setText(FlawDetect.metrics.get(i).toString());
                //l.setFont(new Font("verdana",12));
                if(k<=size)
                {
                    GridPane.setConstraints(l,c,r);
                    gridmetrics.getChildren().addAll(l);
                    //l.setStyle("-fx-background-color: #e7f5fe;");
                    k++;
                }
                else
                {
                    r++;
                    c=0;
                    GridPane.setConstraints(l,c,r);
                    gridmetrics.getChildren().addAll(l);
                    k=2;
                }

                c++;



                //((FlawDetect.metrics.get(i)).toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void undo(ActionEvent actionEvent) {
        areaText1.clear();;
        String originalCode=FlawDetect.undo(filePath);
        areaText1.setText(originalCode);
    }

    public void revert(ActionEvent actionEvent) {
        areaText1.clear();;
        String originalCode=FlawDetect.revertChanges(filePath);
        areaText1.setText(originalCode);
    }
}