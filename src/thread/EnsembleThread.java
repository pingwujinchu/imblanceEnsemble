package thread;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import application.Log;
import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import main.Start;

public class EnsembleThread implements Runnable {
	String project;
	String simpleSelected;
	String ensembleSelected;
	List<String> baseSelected;
	boolean isIn;
	TextArea resultView;
	Button start;
	ProgressBar pb;
	ListView listView;
	ListView selectShow;
	List<Log> result;

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
	
	public EnsembleThread(String project, String simpleSelected, String ensembleSelected, List<String> baseSelected,
			boolean isIn, TextArea resultView, Button start, ProgressBar pb, ListView listView,ListView selectShow) {
		super();
		this.project = project;
		this.simpleSelected = simpleSelected;
		this.ensembleSelected = ensembleSelected;
		this.baseSelected = baseSelected;
		this.isIn = isIn;
		this.resultView = resultView;
		this.start = start;
		this.pb = pb;
		this.listView = listView;
		this.selectShow = selectShow;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				pb.setProgress(0.5);

			}
		});

		try {
			result = Start.runClassification(project, simpleSelected, isIn, ensembleSelected, baseSelected);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					start.setText("开始");
					pb.setProgress(1);
				}
			});
			e.printStackTrace();
		}

//		resultView.setText(
//				"project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc ");
		for(int i = 0 ; i < result.size() ; i++){
		   resultView.setText(result.get(i).getResultInfo());
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				start.setText("开始");
				pb.setProgress(1);
				for(int i = 0 ; i < result.size() ; i++){
				  Log log = result.get(i);
				  listView.getItems().add(log);
				  selectShow.getItems().add(log);
				}
			}
		});
	}
}
