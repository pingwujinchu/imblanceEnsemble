package application;
	

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Start;
import thread.EnsembleThread;
import thread.SaveFileThread;
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
import javafx.scene.control.TabPane;
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

	@FXML TextArea resultView;
	@FXML ProgressBar pb;
	@FXML ListView selectShow;
	@FXML BarChart chart;
	@FXML CategoryAxis xlabel;
	@FXML NumberAxis ylabel;
	@FXML ListView textShow;
	@FXML TabPane tp;

	Stage stage = null;
	public static Map classMap = new HashMap();
	static Map methodMap = new HashMap();
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
			primaryStage.setTitle("imEnsemble");
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
					resultView.setText(newValue.getAllInfo());
				}
			});  
			
			primaryStage.show();
			methodMap.put("Bagging embedded SMOTE", 1);
			methodMap.put("Bagging over SMOTE", 2);
			methodMap.put("Boosting embedded SMOTE", 3);
			methodMap.put("Boosting over SMOTE", 4);
			methodMap.put("Bagging embedded OverSample", 5);
			methodMap.put("Bagging over OverSample", 6);
			methodMap.put("Boosting embedded OverSample", 7);
			methodMap.put("Boosting over OverSample", 8);
			methodMap.put("Bagging embedded UnderSample", 9);
			methodMap.put("Bagging over UnderSample", 10);
			methodMap.put("Boosting embedded UnderSample", 11);
			methodMap.put("Boosting over UnderSample", 12);
			methodMap.put("Bagging", 13);
			methodMap.put("Boosting", 14);
			methodMap.put("Over", 15);
			methodMap.put("Under", 16);
			methodMap.put("Simple", 17);
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
		
	   stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		
		@Override
		public void handle(WindowEvent event) {
			// TODO Auto-generated method stub
			Start.finish = true;
		}
	});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	 @FXML
	 protected void chooseFileAction(ActionEvent event){
		 
		 final DirectoryChooser fc = new DirectoryChooser();
		 fc.setTitle("选择数据集");
		 fc.setInitialDirectory(new File("/"));

		 File f  = fc.showDialog(stage);
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
			 
              List<String> dataSetList = new ArrayList();
			  
			  if(simple.getValue() == null){
				  Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				  _alert.setTitle("提示");
				  _alert.setHeaderText(null);
				  _alert.setContentText("请选择采样+集成方法");
				  _alert.initOwner(stage);
				  _alert.show();
				  start.setText("开始");
				  return;
			  }
			  
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
			  
			  File allF = new File(fileName.getText());
			  File[] files = allF.listFiles();
			  for(int i = 0 ; i < files.length ; i++){
				  if(files[i].getName().endsWith(".arff")||files[i].getName().endsWith(".csv")){
					  dataSetList.add(files[i].getAbsolutePath());
				  }
			  }
			  if(dataSetList.size() == 0){
				  Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				  _alert.setTitle("提示");
				  _alert.setHeaderText(null);
				  _alert.setContentText("所选择的文件夹中没有数据集，请重新选择。");
				  _alert.initOwner(stage);
				  _alert.show();
				  start.setText("开始");
				  return;
			  }
			  Start.finish = false;
			  String simpleSelected = (String) simple.getValue();
			  String[] method = simpleSelected.split("\\s");
			  try {
				pro.load(new FileInputStream("setting/baseClassification.properties"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				String logInfo = fileName.getText().substring( fileName.getText().lastIndexOf("\\")+1,  fileName.getText().length())+" & ";
			  List<String> alList = new ArrayList();    //添加所有基分类器
			  if(base.getValue().equals("All")){
				  logInfo += "All";
				  List<String> all = base.getItems();
				  for(String key : all){
					  if(!key.equals("All")){
					     alList.add(pro.getProperty(key));
					  }
				  }
			  }else{
				  String baseSelected = pro.getProperty((String) base.getValue());
				  alList.add(baseSelected);
				  logInfo += baseSelected;
			  }
			  
			  ///=========================================================================
			  //1:Bagging embedded SMOTE;2:Bagging over SMOTE;3:Boosting embedded SMOTE;4:Boosting over SMOTE
			  //5:Bagging embedded OverSample;6:Bagging over OverSample;7:Boosting embedded OverSample;8:Boosting over OverSample
			  //9:Bagging embedded UnderSample;10:Bagging over UnderSample;11:Boosting embedded UnderSample;12:Boosting over UnderSample
			  //13:Bagging;14:Boosting
			  //============================================================================
			  List<Method> methodList = new ArrayList();  //添加所有方法
			  
			  if(simple.getValue().equals("All")){
				   logInfo += " & All";
				   List allItem = simple.getItems();
				   for(int i = 0 ; i < allItem.size() ; i++){
					      String value = (String) allItem.get(i);
						  Method currMd = null;
						  if(value.equals("All")){
						     continue;
						  }
						  if(value.contains("_")){
							  String allStr [] = value.split("_");
							  String currStr = allStr[0]+" "+allStr[1]+" "+allStr[2];
							  int currIndex = (Integer)methodMap.get(currStr);
							  currMd = new Method(currIndex, allStr[2], allStr[1], allStr[0]);
						  }else{
							     int currIndex = (Integer)methodMap.get(value);
							     currMd = new Method(i + 1, "", "", value);
							  }
						  methodList.add(currMd);
				   }
			  }else{
				  String value = (String) simple.getValue();
				  logInfo += " & "+value;
				  Method currMd = null;
				  if(value.contains("_")){
					  String allStr [] = value.split("_");
					  int index = (Integer)methodMap.get(allStr[0]+" "+allStr[1]+" "+allStr[2]);
					  currMd = new Method(index , allStr[2], allStr[1], allStr[0]);
				  }else{
					  int index = (Integer)methodMap.get(value);
					  currMd = new Method(index , "", "", value);
				  }
				  methodList.add(currMd);
			  }
			  logInfo = sdf.format(d)+"-" + logInfo;
				  try {
				    	EnsembleThread enThread = new EnsembleThread(dataSetList,methodList, alList, resultView,start,pb,listView,selectShow,logInfo);
					    Thread t = new Thread(enThread);
					    t.start();
				       } catch (Exception e) {
					// TODO Auto-generated catch block
				      	e.printStackTrace();
				      }
		  }
		  if(str.equals("结束")){
			  start.setText("开始");
			  Start.finish = true;
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
				pro.load(new FileInputStream("setting/baseClassification.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@FXML
		public void initialize() throws MalformedURLException{
	
			pb.setOnMouseEntered(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					DecimalFormat    df   = new DecimalFormat("######0.0000");
					pb.setTooltip(new Tooltip(df.format(pb.getProgress()*100)+"%"));
				}
			});
			Properties mpro = new Properties();
			List mList = new ArrayList();
			try {
				mpro.load(new FileInputStream("setting/method.properties"));
				Iterator enumMethod = mpro.keySet().iterator();
				while(enumMethod.hasNext()){
					mList.add(enumMethod.next());
				}
				if(mList.size() > 1){
					mList.add("All");
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			simple.getItems().addAll(mList);
			ObservableList<String> names = FXCollections.observableArrayList();
			ObservableList<String> yint = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			xlabel.setCategories(names);
			ylabel.setUpperBound(1);
//			selectShow.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			selectShow.setOnMouseClicked(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					
					 Log selectedItems =  (Log) selectShow.getSelectionModel().getSelectedItem();
					 if(selectedItems == null){
						 return;
					 }
					 try {
						SelectShowMethod  ssm = new SelectShowMethod(stage,selectedItems,xlabel,chart,textShow,tp);
						ssm.show();
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 chart.getData().removeAll(chart.getData());
//					 for(int i = 0 ; i < selectedItems.size() ; i++){
//						 XYChart.Series<String, Double> series = new XYChart.Series<>();
//						 for(int j = 0 ; j < names.size() ; j++){
//							 series.getData().add(new XYChart.Data<>(names.get(j),selectedItems.get(i).get(j)));
//						 }
//						 series.setName(selectedItems.get(i).getLog());
//						 chart.getData().add(series);
//						 chart.setCategoryGap(20);
//						
//					 }
				}
			});
			
		}
		
		@FXML
		public void outLog(ActionEvent event){
			String result = resultView.getText();
			FileChooser fc = new FileChooser();
			fc.setInitialDirectory(new File("/"));
			File f = fc.showSaveDialog(stage);
			if(f != null){
				SaveFileThread sft = new SaveFileThread(f, result);
				Thread t = new Thread(sft);
				t.start();
			}
		}
		
		@FXML
		public void outLAction(ActionEvent event){
			List<String> result = textShow.getItems();
			StringBuilder strBuild = new StringBuilder();
			
			for(String str : result){
				strBuild.append(str+"\n");
			}
			FileChooser fc = new FileChooser();
			
			fc.setInitialDirectory(new File("/"));
			File f = fc.showSaveDialog(stage);
			if(f != null){
				SaveFileThread sft = new SaveFileThread(f, strBuild.toString());
				Thread t = new Thread(sft);
				t.start();
			}
		}
}
