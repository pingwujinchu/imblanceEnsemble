package application;
	

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import javafx.event.Event;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
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
	@FXML ListView selectShow;
	@FXML BarChart chart;
	@FXML CategoryAxis xlabel;
	@FXML NumberAxis ylabel;
	Stage stage = null;
	static Stage dialog = null;
	static volatile Properties pro;
	@Override
	public void start(Stage primaryStage) {
		try {
			super.init();
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("main.fxml"),ResourceBundle.getBundle("main"));
			Scene scene = new Scene(root,1000,600);
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
//		    chbase.setValue(str[0]);
		    if(chbase.getItems().size()>1){
		    	chbase.getItems().add("All");
		    }
			stage.getIcons().add(new Image(new File(("resource/images/unbalance.png")).toURL().toString()));
			
			ListView lv = (ListView) root.lookup("#listView");
			resultView = (TextArea) root.lookup("#resultView");
			lv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Log>() {

				@Override
				public void changed(ObservableValue<? extends Log> observable, Log oldValue, Log newValue) {
					// TODO Auto-generated method stub
					resultView.setText(newValue.getResultInfo());
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
	 protected void addAlgAction() throws MalformedURLException{
		 if(dialog == null){
			 dialog = new SelectAlg(stage,pro,base);
			 dialog.setTitle("选择算法");
			 dialog.show();
		 }else{
			 dialog.show();
		 }
		 try {
			pro.load(new FileInputStream("setting/baseClassification.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			  if(base.getValue() == null){
				  Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				  _alert.setTitle("提示");
				  _alert.setHeaderText(null);
				  _alert.setContentText("请选择基分类器");
				  _alert.initOwner(stage);
				  _alert.show();
				  start.setText("开始");
				  return;
			  }
			  try {
				pro.load(new FileInputStream("setting/baseClassification.properties"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  if(base.getValue().equals("All")){
				  List alList = new ArrayList();
				  List<String> all = base.getItems();
				  for(String key : all){
					  if(!key.equals("All")){
					     alList.add(pro.getProperty(key));
					  }
				  }
				  boolean isIn = in.isSelected();
				  try {
				    	EnsembleThread enThread = new EnsembleThread(project, simpleSelected, ensembleSelected, alList, isIn, resultView,start,pb,listView,selectShow);
					    Thread t = new Thread(enThread);
					    t.start();
				       } catch (Exception e) {
					// TODO Auto-generated catch block
				      	e.printStackTrace();
				       }
			  }else{
			      String baseSelected = pro.getProperty((String) base.getValue());
			      List alList = new ArrayList();
			      alList.add(baseSelected);
			      boolean isIn = in.isSelected();
			      try {
			    	EnsembleThread enThread = new EnsembleThread(project, simpleSelected, ensembleSelected, alList, isIn, resultView,start,pb,listView,selectShow);
				    Thread t = new Thread(enThread);
				    t.start();
			       } catch (Exception e) {
				// TODO Auto-generated catch block
			      	e.printStackTrace();
			       }
			  }
		  }
		  if(str.equals("结束")){
			  start.setText("开始");
			  EnsembleThread.stop = true;
		  }
	 }
	 
		@FXML
		public void clearAction(ActionEvent event){
			base.getItems().removeAll(base.getItems());
			try {
				FileOutputStream oFile = new FileOutputStream(new File("setting/baseClassification.properties"));
				oFile.write("#=====clear====".getBytes());
				oFile.flush();
				oFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@FXML
		public void initialize(){
			ObservableList<String> names = FXCollections.observableArrayList();
			ObservableList<String> yint = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			xlabel.setCategories(names);
			ylabel.setUpperBound(1);
			selectShow.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			selectShow.setOnMouseClicked(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					 ObservableList<Log> selectedItems =  selectShow.getSelectionModel().getSelectedItems();
					 chart.getData().removeAll(chart.getData());
					 for(int i = 0 ; i < selectedItems.size() ; i++){
						 XYChart.Series<String, Double> series = new XYChart.Series<>();
						 for(int j = 0 ; j < names.size() ; j++){
							 series.getData().add(new XYChart.Data<>(names.get(j),selectedItems.get(i).get(j)));
						 }
						 series.setName(selectedItems.get(i).getLog());
						 chart.getData().add(series);
					 }
				}
			});
			
		}
}
