package thread;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import application.Log;
import application.Main;
import application.Method;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import main.Start;

/**
 * @author fan
 *
 */
public class EnsembleThread implements Runnable {
	List<String> project;          //所有数据集列表
    List<Method> methodList;       //所有选择的方法列表
	List<String> baseSelected;     //所有选择的基分类器列表
	TextArea resultView;
	Button start;
	ProgressBar pb;
	ListView listView;
	ListView selectShow;
	List result;                  //结果返回的是三个嵌套的List
	String logInfo;


	public static boolean stop = false;

//	public EnsembleThread(String project, String simpleSelected, String ensembleSelected, String baseSelected,
//			boolean isIn, TextArea resultView, Button start, ProgressBar pb, ListView listView,ListView selectShow) {
//		super();
//		this.project = project;
//		this.simpleSelected = simpleSelected;
//		this.ensembleSelected = ensembleSelected;
//		this.baseSelected = baseSelected;
//		this.isIn = isIn;
//		this.resultView = resultView;
//		this.start = start;
//		this.pb = pb;
//		this.listView = listView;
//		this.selectShow = selectShow;
//	}
	
	/**
	 * @param project
	 * @param methodList
	 * @param baseSelected
	 * @param resultView
	 * @param start
	 * @param pb
	 * @param listView
	 * @param selectShow
	 */
	public EnsembleThread(List<String> project,List<Method> methodList , List<String> baseSelected, TextArea resultView, Button start, ProgressBar pb, ListView listView,ListView selectShow,String logInfo) {
		super();
		this.project = project;
		this.methodList = methodList;
		this.baseSelected = baseSelected;
		this.resultView = resultView;
		this.start = start;
		this.pb = pb;
		this.listView = listView;
		this.selectShow = selectShow;
		this.logInfo = logInfo;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			result = Start.runClassification(project, methodList, baseSelected,pb,resultView);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					start.setText("开始");
					
				}
			});
			e.printStackTrace();
		}

//		resultView.setText(
//				"project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc ");
//		for(int i = 0 ; i < result.size() ; i++){
////		   resultView.setText(result.get(i).getResultInfo());
//		}
		
		Log log = new Log(logInfo);
		log.logList = result;
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				start.setText("开始");
				pb.setProgress(1);
				
				listView.getItems().add(log);
				selectShow.getItems().add(log);
				resultView.setText(log.getAllInfo());
			}
		});
	}
}
