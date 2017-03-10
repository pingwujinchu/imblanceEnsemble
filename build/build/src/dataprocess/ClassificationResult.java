package dataprocess;

public class ClassificationResult {
	
	double accuracy = 0;
	double auc = 0;
	double recall1 = 0;
	double recall2 = 0;
	
	public ClassificationResult(double a, double b, double c, double d){
		accuracy = a;
		auc = b;
		recall1 = c;
		recall2 = d;
	}

	public ClassificationResult() {
		accuracy = 0;
		auc = 0;
		recall1 = 0;
		recall2 = 0;
	}
	
	public void setAccuracy(double a){
		accuracy = a;
	}
	public void setAuc(double a){
		auc = a;
	}
	public void setRecall1(double r1){
		recall1 = r1;
	}
	public void setRecall2(double r2){
		recall2 = r2;
	}
	
}