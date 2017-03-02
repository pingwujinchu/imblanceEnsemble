package application;
	

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Start;
import thread.EnsembleThread;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application{
	
	@FXML Button folder;
	@FXML Button opfile;
	@FXML TextField fileName;
	@FXML ChoiceBox simple;
	@FXML ChoiceBox base;
	@FXML
	public Button start;
	@FXML ListView listView;
	@FXML ChoiceBox ensemble;
	@FXML CheckBox in;
	@FXML TextArea resultView;
	@FXML ProgressBar pb;
	Stage stage = null;
	static Properties pro;
	@Override
	public void start(Stage primaryStage) {
		try {
			super.init();
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("main.fxml"),ResourceBundle.getBundle("main"));
			Scene scene = new Scene(root,700,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("imEnsamble");
			stage = primaryStage;
		    
		    ChoiceBox chbase = (ChoiceBox) root.lookup("#base");
		    pro = new Properties();
		    pro.load(new FileInputStream("setting/baseClassification.properties"));
		    Iterator it = pro.keySet().iterator();
		    String []str = new String[pro.size()];
		    int i = 0;
		    while(it.hasNext()){
		    	str[i ++] = (String) it.next();
		    }
		    chbase.setItems(FXCollections.observableArrayList(str));
		    chbase.setValue(str[0]);
			stage.getIcons().add(new Image(new File(("resource/images/unbalance.png")).toURL().toString()));
			
			ListView lv = (ListView) root.lookup("#listView");
			resultView = (TextArea) root.lookup("#resultView");
			lv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Log>() {

				@Override
				public void changed(ObservableValue<? extends Log> observable, Log oldValue, Log newValue) {
					// TODO Auto-generated method stub
					String measure_name = "project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc \n";
					resultView.setText(measure_name+newValue.getInfo());
				}
			});  
			primaryStage.show();
			
//			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//				
//				@Override
//				public void handle(WindowEvent event) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	 @FXML
	 protected void chooseFileAction(ActionEvent event){
		 
		 FileChooser fc = new FileChooser();
		 fc.setTitle("选择数据集");
		 fc.setInitialDirectory(new File("/"));
		 ExtensionFilter ef=new ExtensionFilter("arff","*.arff");
		 fc.getExtensionFilters().add(ef);
		 File f = fc.showOpenDialog(stage);
		 if(f != null && f.exists()){
			 fileName.setText(f.getAbsolutePath());
//			 opfile.setDisable(true);
		 }
	 }
	 
	 @FXML
	 protected void startAction(ActionEvent event){
		     String str = start.getText();

		  if(str.equals("开始")){
			  if(fileName.getText() == null || fileName.getText().equals("")){
				  chooseFileAction(event);
                  return;
			  }
			  start.setText("结束");
			  String project = fileName.getText();
			  String simpleSelected = (String) simple.getValue();
			  String ensembleSelected = (String) ensemble.getValue();
			  String baseSelected = pro.getProperty((String) base.getValue());
			  boolean isIn = in.isSelected();
			  try {
				EnsembleThread enThread = new EnsembleThread(project, simpleSelected, ensembleSelected, baseSelected, isIn, resultView,start,pb,listView);
				Thread t = new Thread(enThread);
				t.start();
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		  }
		  if(str.equals("结束")){
			  start.setText("开始");
			  EnsembleThread.stop = true;
		  }
	 }
}
