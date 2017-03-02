package thread;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	String baseSelected;
	boolean isIn;
	TextArea resultView;
	Button start;
	ProgressBar pb;
	ListView listView;
	String result;

	public static boolean stop = false;

	public EnsembleThread(String project, String simpleSelected, String ensembleSelected, String baseSelected,
			boolean isIn, TextArea resultView, Button start, ProgressBar pb, ListView listView) {
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

		resultView.setText(
				"project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc ");
		resultView.appendText("\n" + result);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				start.setText("开始");
				pb.setProgress(1);
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				Log log = new Log(sdf.format(d) + "-" + baseSelected,result);
				listView.getItems().add(log);
			}
		});
	}

}
