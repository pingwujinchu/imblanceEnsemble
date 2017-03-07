package dataprocess;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.SymmetricalUncertAttributeEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class DataPreprocessor {
	Instances data;

	public DataPreprocessor(Instances data) {
		this.data = data;
	}

	public Instances preprocessData() throws Exception{
		System.out.println("Total number of attributes: "+data.numAttributes());
		deleteUselessAttributes();
		//selectAttributesCFS();
		System.out.println("Use number of attributes: "+data.numAttributes());
		return data;
	}

	public void deleteUselessAttributes() throws Exception {
		data.deleteAttributeAt(0); //remove the attribute commit_id
		data.deleteAttributeAt(0); //remove the attribute file_id
		RemoveUseless ru = new RemoveUseless();
		ru.setMaximumVariancePercentageAllowed(99.0);//remove attributes that have more than 90% same values
		ru.setInputFormat(data);
		data = Filter.useFilter(data, ru);

	}

	public void selectAttributesCFS() throws Exception {
		AttributeSelection fs = new AttributeSelection();
		SymmetricalUncertAttributeEval evaluator = new SymmetricalUncertAttributeEval();
		fs.setEvaluator(evaluator);
		Ranker rank = new Ranker();
		rank.setNumToSelect(200);
		fs.setSearch(rank);
		fs.setSeed(2);
		fs.SelectAttributes(data);
		data = fs.reduceDimensionality(data);
	}

}
