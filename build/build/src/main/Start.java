package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.Log;
import application.Method;
import bean.DataSet;
import bean.EvaluationInfo;
import classification.Classification;
import dataprocess.CmdLineParser;
import dataprocess.Util;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import weka.core.AttributeStats;
import weka.core.Instances;

public class Start {
	public static volatile boolean finish = false;

	public static void main(String argv[]) throws Exception {
		/**
		 * Command line parsing
		 */
		CmdLineParser cmdparser = new CmdLineParser();
		CmdLineParser.Option project_opt = cmdparser.addStringOption('p', "project");
		CmdLineParser.Option filepath_opt = cmdparser.addStringOption('f', "filepath");
		CmdLineParser.Option classifier_opt = cmdparser.addStringOption('c', "classifier");

		try {
			cmdparser.parse(argv);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			// printUsage();
			System.exit(2);
		}

		String project = (String) cmdparser.getOptionValue(project_opt, "weather");
		String filepath = (String) cmdparser.getOptionValue(filepath_opt, "");
		String classifier_name = (String) cmdparser.getOptionValue(classifier_opt, "j48");

		// change classification projects
		// String projects[] = {"ant",
		// "camel","itext","jedit","lucene","synapse","tomcat","voldemort"};

		// reopen bug projects
		// String projects[] = {"eclipse","apache", "openoffice"};

		// test projects
		String projects[] = { "weather" };
		Util util = new Util();
		String output_file = filepath + "result.txt";
		String measure_name = "project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc \n";
		util.saveResult(measure_name, output_file);
		String predict_result = "";
		for (int i = 0; i < projects.length; i++) {
			project = projects[i];
			System.out.println(project);
			// read in the input arff file
			String inputfile = filepath + project + ".arff";

			FileReader fr = new FileReader(inputfile);
			BufferedReader br = new BufferedReader(fr);
			Instances data = new Instances(br);
			data.setClassIndex(data.numAttributes() - 1);
			// print out number of instances
			System.out.println("Total number of instances: " + data.numInstances());
			AttributeStats as = data.attributeStats(data.numAttributes() - 1);
			int count[] = as.nominalCounts;
			System.out.println("Number of buggy instances: " + count[1]);
			Classification classification = new Classification(data);
			// predict_result = classification.predict(classifier_name,
			// filepath, project, 1);
			util.appendResult(predict_result, output_file);
		}
	}

	// 重写方法,该方法可以使用多种方法在多种基分类器上
	public static List runClassification(List<String> project,List<Method> methodList,List<String> base,ProgressBar pb,TextArea resultView) throws IOException{
		List lll = new ArrayList();
		int totle = 0;
		outer : for(int k = 0 ; k < project.size() ; k++){
			List ll = new ArrayList();
			for(int j = 0 ; j < methodList.size() ; j++){
				List result = new ArrayList();
				for(int i = 0 ; i < base.size() ; i++){
					final int temK = k;
					final int temJ = j;
					final int temI = i;

				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				Log log;
				String bs = base.get(i);
				log = new Log(sdf.format(d)+"-"+bs.substring(bs.lastIndexOf("."), bs.length()));
				
				Util util = new Util();
				String measure_name = "project, method, classifier, accuracy, recall-0, recall-1, precision-0, precision-1, fMeasure-0, fMeasure-1, gmean, auc \n";
		//		util.saveResult(measure_name, output_file);
				String predict_result = "";
				
				FileReader fr = new FileReader(new File(project.get(k)));
				BufferedReader br = new BufferedReader(fr);
				Instances data = new Instances(br);
				data.setClassIndex(data.numAttributes()-1);
				DataSet ds = new DataSet();
				ds.setDataSetName(new File(project.get(k)).getName());
				ds.setAttributesNum(data.numAttributes());
				ds.setInstancesNum(data.numInstances());
				ds.setRelation(ds.getRelation());
				ds.setTestMode("10-fold cross-validation");
				EvaluationInfo ei = new EvaluationInfo(data.attribute(data.numAttributes()-1).numValues());
				Method method = new Method(methodList.get(j));
				method.setBase(base.get(i));
				
				Classification classification = new Classification(data);
				List<EvaluationInfo> eiList = null;
				try {
					eiList = classification.predict(methodList.get(j),base.get(i), 1,ei);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.setDataset(ds);
				log.setEi(eiList);
				log.setMethod(method);
				result.add(log);
				totle ++;
				final int curr = totle;
	    		Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								pb.setProgress((double)curr/((project.size())*(methodList.size())*(base.size())));
								resultView.appendText(log.getLogShow());
							}
						});
				
				if(finish){
					
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							pb.setProgress(1);
						}
					});
					break outer;
					
				}
				}
				ll.add(result);
			}
			lll.add(ll);
		}
		return lll;
	}
}
