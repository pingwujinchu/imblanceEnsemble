package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import bean.EvaluationInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import thread.SaveFileThread;

public class SelectShowMethod extends Stage {
		@FXML ChoiceBox project;
		@FXML ChoiceBox method;
		@FXML ChoiceBox base;
		Stage stage;
		static Log log;
		CategoryAxis xlabel;
		BarChart chart;
		ListView textShow;
		TabPane tp;
		
		public SelectShowMethod(Stage parent,Log log,CategoryAxis xlabel,BarChart chart,ListView textShow,TabPane tp) throws MalformedURLException  {
			this.log = log;
			this.xlabel = xlabel;
			this.chart = chart;
			this.textShow = textShow;
			this.tp = tp;
			this.initOwner(parent);
			this.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("selectShowMethod.fxml"),ResourceBundle.getBundle("selectShowMethod"));
			 fXMLLoader.setController(this);
			 Parent root;
				try {  
		        	root = fXMLLoader.load();  
		        } catch (IOException exception) {  
		            throw new RuntimeException(exception);  
		        }  
				 Scene s = new Scene(root);
				 this.setScene(s);
				 stage = this;
				 stage.getIcons().add(new Image(new File(("resource/images/unbalance.png")).toURL().toString()));
				 
		}

		@FXML
		public void initialize() {
			// TODO Auto-generated method stub
		    List projectList = new ArrayList();
		    for(int i = 0 ; i < log.logList.size() ; i++){
		    	Log l = (Log)((List)((List)log.logList.get(i)).get(0)).get(0);
		    	projectList.add(l.getDataset().getDataSetName());
		    }
		    if(projectList.size() > 1){
		       projectList.add("All");
		    }
		    project.getItems().addAll(projectList);
		    
		    List methodList = new ArrayList();
		    List curr = ((List)log.logList.get(0));
		    for(int i = 0 ; i < curr.size() ; i++){
		    	Log l = (Log) ((List)curr.get(i)).get(0);
		    	methodList.add(l.getMethod().getEnsamble()+" "+l.getMethod().getIn()+" "+l.getMethod().getSample());
		    }
		    if(methodList.size() > 1){
		    	methodList.add("All");
		    }
		    method.getItems().addAll(methodList);
		    
		    List baseList = new ArrayList();
		    List currBase = (List) ((List)log.logList.get(0)).get(0);
		    for(int i = 0 ; i < currBase.size() ; i++){
		    	Log l = (Log) currBase.get(i);
		    	baseList.add(l.getMethod().getBase());
		    }
		    if(baseList.size() > 1){
		    	baseList.add("All");
		    }
		    base.getItems().addAll(baseList);
		    
		}
		
		@FXML
		public void okAction(ActionEvent event){
			String projectName = (String) project.getSelectionModel().getSelectedItem();
			String methodName = (String) method.getSelectionModel().getSelectedItem();
			String baseName = (String) base.getSelectionModel().getSelectedItem();
			
			textShow.getItems().removeAll(textShow.getItems());
			if((!projectName.equals("All"))&&(!methodName.equals("All"))&& (!baseName.equals("All"))){
				List curr = null;
				for(int i = 0 ; i < log.logList.size();i++){
					Log l = (Log) ((List)((List)log.logList.get(i)).get(0)).get(0);
					if(l.getDataset().getDataSetName().equals(projectName)){
						curr = (List) log.logList.get(i);
						break;
					}
				}
				
				List baseList = null;
				for(int j = 0 ; j < curr.size() ; j++){
					Log ml = (Log)((List)curr.get(j)).get(0);
					if(methodName.equals(ml.getMethod().getEnsamble()+" "+ml.getMethod().getIn()+" "+ml.getMethod().getSample())){
						baseList = (List) curr.get(j);
						break;
					}
				}
				
				Log findLog = null;
				for(int k = 0 ; k < baseList.size() ; k++){
					Log temL = (Log) baseList.get(k);
					if(temL.getMethod().getBase().equals(baseName)){
						findLog = temL;
					}
				}
				drawByLog(findLog);
			    stage.close();
			}else{
				//数据集选所有
				if(projectName.equals("All")&&(!methodName.equals("All"))&& (!baseName.equals("All"))){
					List projectList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						for(int j = 0 ; j < ((List)log.logList.get(i)).size() ; j++){
							Method m = ((Log)((List)((List)log.logList.get(i)).get(j)).get(0)).getMethod();
							if(methodName.equals(m.getEnsamble()+" "+m.getIn()+" "+m.getSample())){
								for(int k = 0 ; k < ((List)((List)log.logList.get(i)).get(j)).size() ; k ++){
									Log l = (Log) ((List)((List)log.logList.get(i)).get(j)).get(k);
									if(l.getMethod().getBase().equals(baseName)){
										projectList.add(l);
									}
								}
							}
						}
					}
					drawByProjectList(projectList);
				}
				//使用采样+集成方法选所有ALL+""+""
				if((!projectName.equals("All"))&&(methodName.equals("All"))&& (!baseName.equals("All"))){
					List methodList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						List ml = (List) log.logList.get(i);
						if(((Log)((List)ml.get(0)).get(0)).getDataset().getDataSetName().equals(projectName)){
							for(int j = 0 ; j < ml.size() ; j++){
								for(int k = 0 ; k < ((List)ml.get(j)).size() ; k++){
									Method m = ((Log)((List)ml.get(j)).get(k)).getMethod();
									if(baseName.equals(m.getBase())){
										methodList.addAll((List)ml.get(j));
									}
								}
							}
						}
					}
					drawByMethodList(methodList);
				}
				//基分类器选所有""+""+All
				if((projectName.equals("All"))&&(!methodName.equals("All"))&& (baseName.equals("All"))){
					List all = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						List newList = new ArrayList();
						List ml = (List)log.logList.get(i);
						for(int j = 0 ; j < ml.size() ; j++){
							String m = ((Log)((List)ml.get(j)).get(0)).getMethodString();
							if(methodName.equals(m)){
								newList.add(ml.get(j));
							}
						}
						all.add(newList);
					}
					analyseBaseOnDataSet(all);
				}
				//集成方法对比All+All+""
				//该方法用于统计某个基分类器在所有方法，所有数据集上的信息
				if((projectName.equals("All"))&&(methodName.equals("All"))&&(!baseName.equals("All"))){
					List ml = new ArrayList();//ml第一维是数据集，每个数据集后面是一个列表，列表的每一维是一个
					// log.logList保存的是所有程序在所有方法上在所有基分类器上的运行结果，第一维是数据集，第二维是方法，第三维是基分类器
					for(int i = 0 ; i < log.logList.size();i++){
						List pl = (List)log.logList.get(i);
						List npl = new ArrayList();
						for(int j = 0 ; j < pl.size() ; j++){
							List bl = (List) pl.get(j);
							for(int k = 0 ; k < bl.size() ; k++){
								if(((Log)bl.get(k)).getMethod().getBase().equals(baseName)){
									npl.add((Log)bl.get(k));
								}
							}
						}
						ml.add(npl);//ml是不同数据集+不同方法的二维列表
					}
					analyseMethodOnDataSet(ml);
				}
				
				//数据集选一个，方法选一个，基分类器选所有
				if((!projectName.equals("All"))&&(!methodName.equals("All"))&&(baseName.equals("All"))){
					List newList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						List prList = (List) log.logList.get(i);
						if(((Log)((List)prList.get(0)).get(0)).getDataset().getDataSetName().equals(projectName)){
							for(int j = 0 ; j < prList.size() ; j++){
								List base = (List) prList.get(j);
								if(((Log)base.get(0)).getMethodString().equals(methodName)){
									newList.add(base);
								}
							}
						}
					}
					analyseBaseOnDataSet(newList);
				}
			}
		}
		
		@FXML
		public void cancelAction(ActionEvent event){
			stage.close();
		}
		
		public void drawByLog(Log log){
			ObservableList<String> names = FXCollections.observableArrayList();
			ObservableList<String> yint = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("数据集: "+log.getDataset().getDataSetName());
			 textShow.getItems().add("实例数: "+log.getDataset().getInstancesNum());
			 textShow.getItems().add("属性数: "+log.getDataset().getAttributesNum());
			 
			 textShow.getItems().add("采样方法: "+log.getMethod().getSample());
			 textShow.getItems().add("方式: "+log.getMethod().getIn());
			 textShow.getItems().add("集成方法: "+log.getMethod().getEnsamble());
			 textShow.getItems().add("基分类方法: "+log.getMethod().getBase());
			 
			 textShow.getItems().add("");
		     DecimalFormat    df   = new DecimalFormat("######0.00");
			 
			 xlabel.setCategories(names);
			//每一类为一个系列
			for(int i = 0 ; i < log.getEi().get(0).classNum ; i++){
				textShow.getItems().add("类: "+i);
			 XYChart.Series<String, Double> series = new XYChart.Series<>();
			 for(int j = 0 ; j < names.size() ; j++){
				 series.getData().add(new XYChart.Data<>(names.get(j),log.get(j,i)));
				 textShow.getItems().add(names.get(j)+": "+df.format(log.get(j,i)));
			 }
			 series.setName(log.toString()+"-"+i);
			 chart.getData().add(series);
			}
		}
		
		public void drawByProjectList(List arrList){
			 ObservableList<String> names = FXCollections.observableArrayList();
			
			 names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("集成方法: "+((Log)arrList.get(0)).getMethod().getEnsamble());
			 textShow.getItems().add("方式: "+((Log)arrList.get(0)).getMethod().getIn());
			 textShow.getItems().add("采样方法: "+((Log)arrList.get(0)).getMethod().getSample());
			 textShow.getItems().add("基分类方法: "+((Log)arrList.get(0)).getMethod().getBase());
			 for(int m = 0 ; m < arrList.size() ; m++){
				 textShow.getItems().add("数据集: "+((Log)arrList.get(m)).getDataset().getDataSetName());
				 textShow.getItems().add("实例数: "+((Log)arrList.get(m)).getDataset().getInstancesNum());
				 textShow.getItems().add("属性数: "+((Log)arrList.get(m)).getDataset().getAttributesNum());
				 
				 textShow.getItems().add("");
			     DecimalFormat    df   = new DecimalFormat("######0.00");
				 
				 xlabel.setCategories(names);
				//每一类为一个系列
				
				for(int i = 0 ; i < ((Log)arrList.get(m)).getEi().get(0).classNum ; i++){
					textShow.getItems().add("类: "+i);
					
					 for(int j = 0 ; j < names.size() ; j++){
						 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,i)));
//						 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,i)));
					 }
//				 XYChart.Series<String, Double> series = new XYChart.Series<>();
//				 for(int j = 0 ; j < names.size() ; j++){
//					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,i)));
//					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,i)));
//				 }
//				 String dtName = ((Log)arrList.get(m)).getDataset().getDataSetName();
//				 series.setName(dtName.substring(0, dtName.lastIndexOf(".")) +"-"+i);
//				 chart.getData().add(series);
				}
				 XYChart.Series<String, Double> series = new XYChart.Series<>();
				 for(int j = 0 ; j < names.size() ; j++){
					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,0)));
					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,0)));
				 }
				 String dtName = ((Log)arrList.get(m)).getDataset().getDataSetName();
				 series.setName(dtName.substring(0, dtName.lastIndexOf(".")));
				chart.getData().add(series);
			 }
			 
			 textShow.getItems().add("");
			 textShow.getItems().add("");
			 for(int i = 0 ; i < names.size() ; i++){
			    textShow.getItems().add("*********");
			    textShow.getItems().add("	Max "+names.get(i)+"	");
			    textShow.getItems().add("---------");
			    Log l = (Log) arrList.get(findMax(arrList,0));
			    textShow.getItems().add(l.getDataset().getDataSetName());
			 }
			 
			 stage.close();
		}
		
		public int findMax(List arrList,int m){
			double max = 0;
			int index = 0;
			for(int i= 0 ; i < arrList.size() ; i++){
				Log l = (Log) arrList.get(i);
				if(l.get(m, 0) > max){
					max = l.get(m, 0);
					index = i;
				}
			}
			return index;
		}
		
		public void drawByMethodList(List ml){
			 ObservableList<String> names = FXCollections.observableArrayList();
				
			 names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			 
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("数据集: "+((Log)ml.get(0)).getDataset().getDataSetName());
			 textShow.getItems().add("实例数: "+((Log)ml.get(0)).getDataset().getInstancesNum());
			 textShow.getItems().add("属性数: "+((Log)ml.get(0)).getDataset().getAttributesNum());
			 textShow.getItems().add("基分类方法: "+((Log)ml.get(0)).getMethod().getBase());
			 
			 textShow.getItems().add("");
			 DecimalFormat df = new DecimalFormat("######0.00");
			 
			 xlabel.setCategories(names);
			 for(int i = 0 ; i < ml.size() ; i++){
			    // int num[] = new int [];
				 XYChart.Series<String, Double> series = new XYChart.Series<>();
				 for(int j = 0 ; j < names.size() ; j++){
					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)ml.get(i)).get(j,0)));
					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)ml.get(i)).get(j,0)));
				 }
				 series.setName(((Log)ml.get(i)).getMethod().getEnsamble()+" "+((Log)ml.get(i)).getMethod().getIn()+" "+((Log)ml.get(i)).getMethod().getSample());
				 chart.getData().add(series);
			 }
			 
			 textShow.getItems().add("************");
			 for(int j = 0 ; j < names.size() ; j++){
				 textShow.getItems().add("*****Max"+names.get(j)+"****");
				 Method m = ((Log)ml.get(findMax(ml,j))).getMethod();
				 textShow.getItems().add(m.getEnsamble()+" "+m.getIn()+" "+m.getSample());
			 }
			 
			 stage.close();
		}
		
		//All+""+All and ""+""+All
		public void analyseBaseOnDataSet(List mlList){
			Tab tb = new Tab("基分类方法对比");
			
			tb.setClosable(true);
			TextArea tx = new TextArea();
			BorderPane bp = new BorderPane();
			bp.setCenter(tx);
			GridPane gp = new GridPane();
			Button outputLog = new Button("导出日志");
			gp.add(outputLog, 0, 0);
			gp.setAlignment(Pos.BASELINE_RIGHT);
			bp.setBottom(gp);
//			bp.setBottom(outputLog);
			
			tb.setContent(bp);
			
			outputLog.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					String result = tx.getText();
					FileChooser fc = new FileChooser();
					fc.setInitialDirectory(new File("/"));
					File f = fc.showSaveDialog(stage);
					if(f != null){
						SaveFileThread sft = new SaveFileThread(f, result);
						Thread t = new Thread(sft);
						t.start();
					}
				}
			});
			tp.getTabs().add(tb);
			if(mlList.size() == 1){
				tx.appendText("====================================\n");
				tx.appendText("数据集: "+((Log)(((List)mlList.get(0)).get(0))).getDataset().getDataSetName()+"\n");
				tx.appendText("====================================\n");
				tx.appendText("实例数: "+((Log)(((List)mlList.get(0)).get(0))).getDataset().getInstancesNum()+"\n");
				tx.appendText("属性数: "+((Log)(((List)mlList.get(0)).get(0))).getDataset().getAttributesNum()+"\n");
				tx.appendText("采用方法："+((Log)(((List)mlList.get(0)).get(0))).getMethodString()+"\n");
				tx.appendText("\n\n");
				tx.setEditable(false);
				tx.appendText("							");
				
				textShow.getItems().add("数据集:"+((Log)(((List)mlList.get(0)).get(0))).getDataset().getDataSetName());
				textShow.getItems().add("实例数: "+((Log)(((List)mlList.get(0)).get(0))).getDataset().getInstancesNum());
				textShow.getItems().add("属性数: "+((Log)(((List)mlList.get(0)).get(0))).getDataset().getAttributesNum());
				textShow.getItems().add("采用方法："+((Log)(((List)mlList.get(0)).get(0))).getMethodString());
				textShow.getItems().add("\n\n");
				int numClass = ((Log)(((List)mlList.get(0)).get(0))).getEi().get(0).getClassNum();
				for(int i = 0 ; i < numClass ; i++){
				    tx.appendText("TPR-C"+i+"		");
				}
				for(int i = 0 ; i < numClass ; i++){
					tx.appendText("FPR-C"+i+"		");
				}
				for(int i = 0 ; i < numClass ; i++){
					tx.appendText("Precision-C"+i+"		");
				}
				for(int i = 0 ; i < numClass ; i++){
					tx.appendText("Recall-C"+i+"		");
				}
				for(int i = 0 ; i < numClass ; i++){
					tx.appendText("Fmeasure-C"+i+"		");
				}
				tx.appendText("gmeans"+"		");
				tx.appendText("acc"+"		");
				tx.appendText("AUC"+"		\n");
				List ml = (List) mlList.get(0);
				
				ObservableList<String> names = FXCollections.observableArrayList();
				names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
				DecimalFormat    df   = new DecimalFormat("######0.00");
				
				xlabel.setCategories(names);
				for(int i = 0 ; i < ml.size() ; i++){
					Log l = (Log)ml.get(i);
					tx.appendText(l.getMethod().getBase());
					if(l.getMethod().getBase().length() < 26){
						tx.appendText("			");
					}else{
						tx.appendText("	");
					}
					textShow.getItems().add(l.getMethod().getBase());
					XYChart.Series<String, Double> series = new XYChart.Series<>();//每个基分类方法就是一个系列
					for(int m = 0 ; m < 8 ; m++){
						for(int j = 0 ; j < numClass ; j++){
					    	tx.appendText(df.format(l.get(m, j))+"		");
					    	textShow.getItems().add(l.getStr(m)+"-c-"+j+":"+df.format(l.get(m, j)));
						}
						series.getData().add(new XYChart.Data<>(names.get(m),l.get(m, 0)));
					}
					series.setName(l.getMethod().getBase());
					chart.getData().add(series);
					tx.appendText("\n");
					textShow.getItems().add("\n");
				}
			}else{
				tx.appendText("====================================\n");
				tx.appendText("采样+集成学习："+((Log)((List)((List)mlList.get(0)).get(0)).get(0)).getMethodString()+"\n");
				tx.appendText("====================================\n");
				int baseLength = ((List)((List)mlList.get(0)).get(0)).size();
				double average[][]= new double[baseLength][8];
				for(int i = 0 ; i < mlList.size() ; i++){   //数据集
					for(int j = 0 ; j < ((List)((List)(List)mlList.get(0)).get(0)).size() ; j++){ //
						Log l = (Log) ((List)((List)mlList.get(i)).get(0)).get(j);
						for(int k =0 ; k < 8 ; k++){
							average[j][k] += l.get(k, 0)/mlList.size();
						}
					}
				}
				ObservableList<String> names = FXCollections.observableArrayList();
				names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
				DecimalFormat    df   = new DecimalFormat("######0.00");
				DecimalFormat    df2   = new DecimalFormat("######0");
				
				xlabel.setCategories(names);
				List l0 = (List)((List)mlList.get(0)).get(0);
				int numClass = ((Log)l0.get(0)).getEi().get(0).classNum;
				
				textShow.getItems().add("==============");
				textShow.getItems().add("采样+集成学习："+((Log)(l0).get(0)).getMethodString());
				textShow.getItems().add("==============");
				for(int i = 0 ; i < ((List)((List)mlList.get(0)).get(0)).size();i++){//方法
					XYChart.Series<String, Double> series = new XYChart.Series<>();
					
					textShow.getItems().add(((Log)((List)((List)mlList.get(0)).get(0)).get(i)).getMethod().getBase());
					for(int j = 0 ; j < 8 ; j ++){
						series.getData().add(new XYChart.Data<>(names.get(j),average[i][j]));
						textShow.getItems().add("Average "+names.get(j)+":"+df.format(average[i][j]));
					}
					chart.getData().add(series);
					series.setName(((Log)((List)((List)mlList.get(0)).get(0)).get(i)).getMethod().getBase());
				}
				int finRes [][] = new int[l0.size()][5*2 + 3];//存储最后结果
				int matrix[][][] = new int[mlList.size()][l0.size()][5*2+3];  //第一维为数据集，第二维为方法，第三维为指标
				double [][] max = new double[mlList.size()][5*2 + 3];
				for(int i = 0 ; i < mlList.size() ; i++){//数据集
					List ml = (List) ((List)mlList.get(i)).get(0);
					for(int j = 0 ; j < ml.size() ; j++){//方法
						Log l = (Log) ml.get(j);
						for(int k = 0 ; k < 5 ; k ++){
							for(int c = 0 ; c < 2 ; c++){
								if(l.get(k, c) > max[i][k*2 + c]){
									max[i][k*2+c] = l.get(k, c);
								}
							}
						}
						for(int k = 5 ; k < 8 ; k++){
							if(l.get(k, 0)>max[i][5*2+k - 5]){
								max[i][5*2+k - 5] = l.get(k, 0);
							}
						}
					}
				}
				
				for(int i = 0 ; i < mlList.size() ; i++){//数据集
					List ml = (List) ((List)mlList.get(i)).get(0);
					for(int j = 0 ; j < ml.size() ; j++){//方法
						Log l = (Log) ml.get(j);
						for(int k = 0 ; k < 5 ; k ++){
							for(int c = 0 ; c < 2 ; c++){
								if(l.get(k, c) == max[i][k*2 + c]){
									matrix[i][j][k*2 + c] += 1;
								}
							}
						}
						for(int k = 5 ; k < 8 ; k++){
							if(l.get(k, 0)==max[i][5*2+k - 5]){
								matrix[i][j][5*2+k - 5] += 1;
							}
						}
					}
				}
				
				for(int i = 0 ; i < l0.size() ; i++){
					for(int j = 0 ; j < mlList.size() ; j++){
						 for(int k = 0 ; k < 5*2+3 ; k++){
							 finRes[i][k] += matrix[j][i][k];
						 }
					}
				}
				
				tx.appendText("							TPR-0		TPR-1		FPR-0		FPR-1		Presicion-0		Precision-1		Recall-0		Recall-1		FMeasure-0		FMeasure-1		GMeans		ACC		AUC\n");
				for(int i = 0 ; i < l0.size() ; i++){
					tx.appendText(((Log)l0.get(i)).getMethod().getBase());
					if(((Log)l0.get(i)).getMethod().getBase().length() < 26){
						tx.appendText("			");
					}else{
						tx.appendText("	");
					}
					for(int k = 0 ; k < 5*2+3 ; k++){
					   tx.appendText(df2.format(finRes[i][k])+"			");
					   if(k == 5){
						   tx.appendText(df2.format(finRes[i][k])+"		");
					   }
					}
					tx.appendText("\n");
				}
				
				for(int j = 0 ; j < mlList.size() ; j++){
					List ml = (List) ((List)mlList.get(j)).get(0);
					tx.appendText("========================\n");
					tx.appendText("数据集："+((Log)ml.get(0)).getDataset().getDataSetName()+"\n");
					tx.appendText("========================\n");
					tx.appendText("							TPR-0		TPR-1		FPR-0		FPR-1		Presicion-0		Precision-1		Recall-0		Recall-1		FMeasure-0		FMeasure-1		GMeans		ACC		AUC\n");
					for(int k = 0 ; k < l0.size() ; k++){
						Log l = (Log) ml.get(k);
						tx.appendText(l.getMethod().getBase());
						if(l.getMethod().getBase().length() < 26){
							tx.appendText("			");
						}else{
							tx.appendText("	");
						}
						for(int m = 0 ; m < 5 ; m++){
							for(int n = 0 ; n < 2 ; n ++){
						       tx.appendText(df.format(l.get(m, n))+"		");
							}
							for(int n = 5;n < 8 ; n ++){
								tx.appendText(df.format(l.get(n, 0))+"		");
							}
						}
						tx.appendText("\n");
					}
				}
			}
			stage.close();
		}
		
		//All+All+""
		public void analyseMethodOnDataSet(List ll){
			Tab tb = new Tab("采样集成方法对比");
			tb.setClosable(true);
			TextArea tx = new TextArea();
			
			BorderPane bp = new BorderPane();
			bp.setCenter(tx);
			GridPane gp = new GridPane();
			Button outputLog = new Button("导出日志");
			gp.add(outputLog, 0, 0);
			gp.setAlignment(Pos.BASELINE_RIGHT);
			bp.setBottom(gp);
			
			tb.setContent(bp);
			
			outputLog.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					String result = tx.getText();
					FileChooser fc = new FileChooser();
					fc.setInitialDirectory(new File("/"));
					File f = fc.showSaveDialog(stage);
					if(f != null){
						SaveFileThread sft = new SaveFileThread(f, result);
						Thread t = new Thread(sft);
						t.start();
					}
				}
			});
			
			tp.getTabs().add(tb);
			textShow.getItems().add("基分类方法: "+((Log)(((List)ll.get(0)).get(0))).getMethod().getBase());
			tx.appendText("====================================\n");
			tx.appendText("基分类方法: "+((Log)(((List)ll.get(0)).get(0))).getMethod().getBase()+"\n");
			tx.appendText("====================================\n");
			tx.appendText("\n\n");
			tx.setEditable(false);
			
			ObservableList<String> names = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			DecimalFormat    df   = new DecimalFormat("#####00.00");
			DecimalFormat    df2   = new DecimalFormat("######0");
			xlabel.setCategories(names);
			//获取所选组合方法的名称
			List<String> methodNameList = new ArrayList();

		    for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){
				Log l = ((Log)((List)ll.get(0)).get(j));
				methodNameList.add(l.getMethodString());	
			}
			
		    Log temL = (Log)((List)ll.get(0)).get(0);
			int [][][] matrix = new int[ll.size()][((List)ll.get(0)).size()][8*((EvaluationInfo)temL.getEi().get(0)).classNum];
			
			//首先统计所有方法在各个数据集上的均值
			
			double [][] average1 = new double[8][((List)ll.get(0)).size()];
			double [][] average2 = new double[8][((List)ll.get(0)).size()];
			for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){//方法
				for(int i = 0 ; i < ll.size() ; i++){//数据集
					Log l = (Log)((List)ll.get(i)).get(j);
					String m = l.getMethodString();
					for(int k= 0 ; k < 8 ; k++ ){//指标
				  	   average1[k][methodNameList.indexOf(m)] += l.get(k, 0)/ll.size();
				  	   average2[k][methodNameList.indexOf(m)] += l.get(k, 1)/ll.size();
					}
				}
			}
			
			   textShow.getItems().add("-------------");
//			textShow.getItems().add("采样+集成方法");
//			textShow.getItems().add(l.getMethodString());
			//添加右侧输出信息
			    textShow.getItems().add("								"+"TPR0"+"		"+"TPR1"+"		"+"FPR0		"+"FPR1		"+"Precision0		"+"Precision1		"+"Recall0		"+"Recall1		"+"FMeasure0		"+"FMeasure1		"+"GMeans0		"+"GMeans1		"+"ACC0		"+"ACC1		"+"AUC0		"+"AUC1		");
				for(int i = 0 ; i < ((List)ll.get(0)).size() ; i++){
					StringBuilder strBuilder = new StringBuilder();
					Log l = (Log)((List)ll.get(0)).get(i);
					strBuilder.append(l.getMethodString()+"		");
					if(l.getMethodString().length()<=10){
						strBuilder.append("				");
						if(l.getMethodString().length() == 9){
							strBuilder.append("	");
						}
					}else if(l.getMethodString().length() < 25){
						strBuilder.append("	");
					}
					
					XYChart.Series<String, Double> series = new XYChart.Series<>();//每个采样+集成学习方法就是一个系列
					for(int j = 0 ; j < names.size() ; j++){
//						  textShow.getItems().add("Class 0 Average "+names.get(j)+":"+df.format(average1[j][i]));
//						  textShow.getItems().add("Class 1 Average "+names.get(j)+":"+df.format(average2[j][i]));
						  strBuilder.append(df.format(average1[j][i])+"		"+df.format(average2[j][i])+"		");
						  series.getData().add(new XYChart.Data<>(names.get(j),average1[j][i]));
					}
					textShow.getItems().add(strBuilder.toString());
					series.setName(l.getMethodString());
					chart.getData().add(series);
				}
				
				
				for(int i =0 ; i < ll.size() ; i++){
					List curr = (List) ll.get(i);
					textShow.getItems().add("-------------");
					textShow.getItems().add(((Log)curr.get(0)).getDataset().getDataSetName());
					textShow.getItems().add("								"+"TPR0"+"		"+"TPR1"+"		"+"FPR0		"+"FPR1		"+"Precision0		"+"Precision1		"+"Recall0		"+"Recall1		"+"FMeasure0		"+"FMeasure1		"+"GMeans0		"+"GMeans1		"+"ACC0		"+"ACC1		"+"AUC0		"+"AUC1		");
					for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){
						StringBuilder strBuilder = new StringBuilder();
						Log l = (Log) ((List)ll.get(i)).get(j);
						strBuilder.append(l.getMethodString());
						if(l.getMethodString().length() <= 9){
							strBuilder.append("	");
						}
						if(l.getMethodString().length() <= 10){
							strBuilder.append("				");
						}else if(l.getMethodString().length() < 25){
							strBuilder.append("	");
						}
						strBuilder.append("		");
						for(int k = 0 ; k < 8 ; k++){
							for(int c = 0 ; c < 2 ; c++){
							   strBuilder.append(df.format(l.get(k, c))+"		");
							}
						}
						textShow.getItems().add(strBuilder.toString());
					}
				}
			
				int [][]finRes = new int[methodNameList.size()][8*((EvaluationInfo)temL.getEi().get(0)).classNum ];
				//在不同数据集上进行统计。
				for(int i = 0 ; i < ll.size() ; i++){//数据集
					double [] currRes = new double[8*((EvaluationInfo)temL.getEi().get(0)).classNum];
					for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){//采样+集成学习
						Log l = (Log) ((List)ll.get(i)).get(j);
						
						for(int ind = 0 ; ind < 8 ; ind ++){
						     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
							   if(currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] < l.get(ind,k)){ 
								   currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] = l.get(ind,k);
							   }
							}
						}
					}
				
				//统计
					for(int j = 0 ; j <((List)ll.get(0)).size() ; j++ ){
						Log l = (Log) ((List)ll.get(i)).get(j);
						for(int ind = 0 ; ind < 8 ; ind ++){
						     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
						    	 if(currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] == l.get(ind,k)){
						    		 matrix[i][j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] += 1;
						    	 }
						     }
						}
					}
					
				}
			
				for(int j = 0 ; j < ((List)ll.get(0)).size() ; j ++){  //方法
					for(int i = 0 ; i < ll.size() ; i++){    //数据集
						for(int ind = 0 ; ind < 8 ; ind ++){   
						     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
						         finRes[j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] += matrix[i][j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k];
						     }
						}
					}
				}
			
			    tx.appendText("								");
			    for(int j = 0 ; j < 8 ; j ++){
					    for(int i = 0 ; i < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; i ++){
					    	switch(j){
					    	case 0:
					    		tx.appendText("TP-C"+i+"		");
					    		break;
					    	case 1:
					    		tx.appendText("FP-C"+i+"		");
					    		break;
					    	case 2:
					    		tx.appendText("Precision-C"+i+"		");
					    		break;
					    	case 3:
					    		tx.appendText("Recall-C"+i+"		");
					    		break;
					    	case 4:
					    		tx.appendText("FMeasure-C"+i+"		");
					    		break;
					    	case 5:
					    		tx.appendText("Gmean-C"+i+"		");
					    		break;
					    	case 6:
					    		tx.appendText("Acc-C"+i+"		");
					    		break;
					    	case 7:
					    		tx.appendText("AUC-C"+i+"		");
					    		break;
					    	}
					    }
			    }
			    
			tx.appendText("\n");
			for(int j = 0 ; j < finRes.length ; j ++){
				tx.appendText(methodNameList.get(j)+"		");
				if(methodNameList.get(j).length() <= 9){
					tx.appendText("	");
				}
				if(methodNameList.get(j).length() <= 10){
					tx.appendText("				");
				}
				else if(methodNameList.get(j).length() < 26){
					tx.appendText("	");
				}
				for(int k = 0 ; k < finRes[0].length ; k++){
					tx.appendText(df2.format(finRes[j][k])+"			");
				}
				tx.appendText("\n");
			}
			
			tx.appendText("\n\n");
			List<String> result = textShow.getItems();
			StringBuilder strBuild = new StringBuilder();
			
			for(String str : result){
				strBuild.append(str+"\n");
			}
			tx.appendText(strBuild.toString());
			stage.close();
		}
		
}
