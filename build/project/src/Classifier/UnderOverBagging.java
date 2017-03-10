package Classifier;

/**
 * 将多数类随机划分为多个子类，每个子类中含有与少数类相同的实例数量，然后将每个子类与
 * 少数类实例结合起来构成新的训练集，用这些训练集进行学习，构造多个分类器，最后
 * 将这些分类器的分类结果通过vote技术整合起来
 */

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;


public class UnderOverBagging
extends RandomizableMultipleClassifiersCombiner
implements TechnicalInformationHandler {

	/** for serialization */
	static final long serialVersionUID = -637891196294399624L;

	protected Classifier Base_Classifier = new ZeroR();
	protected Instances[] m_Instances;

	/** imbalanced method(ensembleType) */
	public String ensembleType = "UNDEROVERBAGGING";
	/** the number of classifiers */
	public int n_classifiers= 10;

	/** combination rule: Average of Probabilities */
	public static final int AVERAGE_RULE = 1;
	/** combination rule: Product of Probabilities (only nominal classes) */
	public static final int PRODUCT_RULE = 2;
	/** combination rule: Majority Voting (only nominal classes) */
	public static final int MAJORITY_VOTING_RULE = 3;
	/** combination rule: Minimum Probability */
	public static final int MIN_RULE = 4;
	/** combination rule: Maximum Probability */
	public static final int MAX_RULE = 5;
	/**Our combination rule**/
	public static final int Average_Distance=6;
	public static final int Product_Distance=7;
	public static final int Majority_Distance=8;
	public static final int Min_Distance=9;
	public static final int Max_Distance=10;
	/**combination rule from keel imbalance ensemble */
	public static final int WEIGHTED_VOTING=11;

	/** combination rules */
	public static final Tag[] TAGS_RULES = {
		new Tag(AVERAGE_RULE, "AVG", "Average of Probabilities"),
		new Tag(PRODUCT_RULE, "PRO", "Product of Probabilities"),
		new Tag(MAJORITY_VOTING_RULE, "MAJ", "Majority Voting"),
		new Tag(MIN_RULE, "MIN", "Minimum Probability"),
		new Tag(MAX_RULE, "MAX", "Maximum Probability"),
		new Tag(Average_Distance,"AvgD","Average Distance"),
		new Tag(Product_Distance,"ProD","Product Distance"),
		new Tag(Majority_Distance,"MajD","Majority Distance"),
		new Tag(Min_Distance,"MinD","Min Distance"),
		new Tag(Max_Distance,"MaxD","Max Distance"),
		new Tag(WEIGHTED_VOTING,"WVote","Weighted Voting Rule")
	};

	/** Combination Rule variable */
	protected int m_CombinationRule = AVERAGE_RULE;

	/** the random number generator used for breaking ties in majority voting
	 * @see #distributionForInstanceMajorityVoting(Instance) */
	protected Random m_Random;

	/** 属性选择开关 */
	protected boolean m_attribSelection = false;

	/**
	 * 设定属性选择开关
	 */
	public void setAttribSelection(boolean flag){
		m_attribSelection=flag;
	}

	/**
	 * Returns a string describing classifier
	 * @return a description suitable for
	 * displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return 
				"Class for combining classifiers. Different combinations of "
				+ "probability estimates for classification are available.\n\n"
				+ "For more information see:\n\n"
				+ getTechnicalInformation().toString();
	}

	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options.
	 */
	public Enumeration listOptions() {
		Enumeration 	enm;
		Vector		result;

		result = new Vector();

		enm = super.listOptions();
		while (enm.hasMoreElements())
			result.addElement(enm.nextElement());

		result.addElement(new Option(
				"\tThe combination rule to use\n"
						+ "\t(default: AVG)",
						"R", 1, "-R " + Tag.toOptionList(TAGS_RULES)));

		return result.elements();
	}

	/**
	 * Gets the current settings of Vote.
	 *
	 * @return an array of strings suitable for passing to setOptions()
	 */
	public String [] getOptions() {
		int       	i;
		Vector    	result;
		String[]  	options;

		result = new Vector();

		options = super.getOptions();
		for (i = 0; i < options.length; i++)
			result.add(options[i]);

		result.add("-R");
		result.add("" + getCombinationRule());

		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Parses a given list of options. <p/>
	 *
   <!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -S &lt;num&gt;
	 *  Random number seed.
	 *  (default 1)</pre>
	 * 
	 * <pre> -B &lt;classifier specification&gt;
	 *  Full class name of classifier to include, followed
	 *  by scheme options. May be specified multiple times.
	 *  (default: "weka.classifiers.rules.ZeroR")</pre>
	 * 
	 * <pre> -D
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console</pre>
	 * 
	 * <pre> -R &lt;AVG|PROD|MAJ|MIN|MAX|MED&gt;
	 *  The combination rule to use
	 *  (default: AVG)</pre>
	 * 
   <!-- options-end -->
	 *
	 * @param options the list of options as an array of strings
	 * @throws Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {
		String 	tmpStr;

		tmpStr = Utils.getOption('R', options);
		if (tmpStr.length() != 0) 
			setCombinationRule(new SelectedTag(tmpStr, TAGS_RULES));
		else
			setCombinationRule(new SelectedTag(AVERAGE_RULE, TAGS_RULES));

		super.setOptions(options);
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing 
	 * detailed information about the technical background of this class,
	 * e.g., paper reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation 	result;
		TechnicalInformation 	additional;

		result = new TechnicalInformation(Type.BOOK);
		result.setValue(Field.AUTHOR, "Ludmila I. Kuncheva");
		result.setValue(Field.TITLE, "Combining Pattern Classifiers: Methods and Algorithms");
		result.setValue(Field.YEAR, "2004");
		result.setValue(Field.PUBLISHER, "John Wiley and Sons, Inc.");

		additional = result.add(Type.ARTICLE);
		additional.setValue(Field.AUTHOR, "J. Kittler and M. Hatef and Robert P.W. Duin and J. Matas");
		additional.setValue(Field.YEAR, "1998");
		additional.setValue(Field.TITLE, "On combining classifiers");
		additional.setValue(Field.JOURNAL, "IEEE Transactions on Pattern Analysis and Machine Intelligence");
		additional.setValue(Field.VOLUME, "20");
		additional.setValue(Field.NUMBER, "3");
		additional.setValue(Field.PAGES, "226-239");

		return result;
	}

	/**
	 * Returns default capabilities of the classifier.
	 *
	 * @return      the capabilities of this classifier
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();

		result.disableAllClasses();
		result.disableAllClassDependencies();
		result.enable(Capability.NOMINAL_CLASS);
		result.enableDependency(Capability.NOMINAL_CLASS);

		return result;
	}

	/**
	 * Set the base learner.
	 *
	 * @param newClassifier the classifier to use.
	 */
	public void setBaseClassifier(Classifier newClassifier) {

		Base_Classifier = newClassifier;
	}

	/**
	 * Get the classifier used as the base learner.
	 *
	 * @return the classifier used as the classifier
	 */
	public Classifier getBaseClassifier() {

		return Base_Classifier;
	}

	/**
	 * Buildclassifier selects a classifier from the set of classifiers
	 * by minimising error on the training data.
	 *
	 * @param data the training data to be used for generating the
	 * boosted classifier.
	 * @throws Exception if the classifier could not be built successfully
	 */
	public void buildClassifier(Instances data) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		Instances newData = new Instances(data);
		newData.deleteWithMissingClass();

		//统计类别实例个数，默认第一个类为少数类，第二个类为多数类
		int classNum[]=newData.attributeStats(newData.classIndex()).nominalCounts;
		int minC, nMin=classNum[0];
		int majC, nMaj=classNum[1];
		if (nMin<nMaj){
			minC=0;
			majC=1;
		}else {
			minC=1;
			majC=0;
			nMin=classNum[1];
			nMaj=classNum[0];
		}

		m_Classifiers=Classifier.makeCopies(Base_Classifier,n_classifiers);

		/*    if (m_attribSelection){
    	for (int i=0; i< m_Classifiers.length;i++){
			AttributeSelectedClassifier as = new AttributeSelectedClassifier();
			ASEvaluation evaluator = new CfsSubsetEval();
			ASSearch search = new BestFirst();

			as.setClassifier(getClassifier(i));
			as.setEvaluator(evaluator);
			as.setSearch(search);
			m_Classifiers[i]=as;
    	}
    }*/

		m_Instances=new Instances[m_Classifiers.length];
		m_Random = new Random(getSeed());
		int b = 0;
		int incre = 100/n_classifiers;

		//newData.randomize(m_Random);
		//newData.sort(newData.classIndex());

		for (int i = 0; i < n_classifiers; i++) {        
			b += incre;
			/* (b% * Nmaj) instances are taken from each class */
			Instances sampleData = randomSampling(newData, majC, minC, b);
			m_Instances[i]=new Instances(sampleData);
			getClassifier(i).buildClassifier(sampleData);
		}
	}

	/**
	 * 对原始数据进行采样
	 * 选择所有少数类实例以及和少数类实例个数相同的多数类实例
	 * @param data
	 * @param i
	 * @return
	 */
	protected Instances randomSampling(Instances copia, int majC, int minC, int a)
	{
		int[] majExamples = new int[copia.numInstances()];
		int[] minExamples = new int[copia.numInstances()];
		int majCount = 0, minCount = 0;
		// First, we copy the examples from the minority class and save the indexes of the majority
		// the new data-set contains samples_min + samples_min * N / 100
		int size = copia.attributeStats(copia.classIndex()).nominalCounts[majC] * a / 100 * 2;
		// class name
		String majClassName = copia.attribute(copia.classIndex()).value(majC);

		for (int i = 0; i < copia.numInstances(); i++){
			if (copia.instance(i).stringValue(copia.classIndex()).equalsIgnoreCase(majClassName))
			{
				// save index
				majExamples[majCount] = i;
				majCount++;
			}
			else
			{
				minExamples[minCount] = i;
				minCount++;
			}
		}

		/* random undersampling of the majority */
		Instances myDataset= new Instances(copia, 0);
		int r;
		for (int i = 0; i < size / 2; i++){
			r = m_Random.nextInt(majCount);
			myDataset.add(copia.instance(majExamples[r]));

			if (minCount>0){
				r = m_Random.nextInt(minCount);
				myDataset.add(copia.instance(minExamples[r]));
			}
		}    

		myDataset.randomize(m_Random);
		return myDataset;
	}

	/**
	 * Classifies the given test instance.
	 *
	 * @param instance the instance to be classified
	 * @return the predicted most likely class for the instance or 
	 * Instance.missingValue() if no prediction is made
	 * @throws Exception if an error occurred during the prediction
	 */
	public double classifyInstance(Instance instance) throws Exception {
		double result;
		double[] dist;
		int index;

		switch (m_CombinationRule) {
		case AVERAGE_RULE:
		case PRODUCT_RULE:
		case MAJORITY_VOTING_RULE:
		case MIN_RULE:
		case MAX_RULE:
		case Average_Distance:
		case Product_Distance:
		case Majority_Distance:
		case Min_Distance:
		case Max_Distance:
		case WEIGHTED_VOTING:
			dist = distributionForInstance(instance);
			if (instance.classAttribute().isNominal()) {
				index = Utils.maxIndex(dist);
				if (dist[index] == 0)
					result = Instance.missingValue();
				else
					result = index;
			}
			else if (instance.classAttribute().isNumeric()){
				result = dist[0];
			}
			else {
				result = Instance.missingValue();
			}
			break;
		default:
			throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
		}

		return result;
	}

	/**
	 * Classifies the given test instance, returning the median from all
	 * classifiers.
	 *
	 * @param instance the instance to be classified
	 * @return the predicted most likely class for the instance or 
	 * Instance.missingValue() if no prediction is made
	 * @throws Exception if an error occurred during the prediction
	 */
	protected double classifyInstanceMedian(Instance instance) throws Exception {
		double[] results = new double[m_Classifiers.length];
		double result;

		for (int i = 0; i < results.length; i++)
			results[i] = m_Classifiers[i].classifyInstance(instance);

		if (results.length == 0)
			result = 0;
		else if (results.length == 1)
			result = results[0];
		else
			result = Utils.kthSmallestValue(results, results.length / 2);

		return result;
	}

	/**
	 * Classifies a given instance using the selected combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] result = new double[instance.numClasses()];

		switch (m_CombinationRule) {
		case AVERAGE_RULE:
			result = distributionForInstanceAverage(instance);
			break;
		case PRODUCT_RULE:
			result = distributionForInstanceProduct(instance);
			break;
		case MAJORITY_VOTING_RULE:
			result = distributionForInstanceMajorityVoting(instance);
			break;
		case MIN_RULE:
			result = distributionForInstanceMin(instance);
			break;
		case MAX_RULE:
			result = distributionForInstanceMax(instance);
			break;
		case Average_Distance:
			result = distributionForAvgD(instance);	  
			break;
		case Product_Distance:
			result = distributionForProD(instance);
			break;
		case Majority_Distance:
			result = distributionForMajD(instance);
			break;
		case Min_Distance:
			result = distributionForMinD(instance);
			break;
		case Max_Distance:
			result = distributionForMaxD(instance);
			break;
		case WEIGHTED_VOTING:
			result = distributionForInstanceWeightedVoting(instance);
			break;
		default:
			throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
		}

		if (!instance.classAttribute().isNumeric() && (Utils.sum(result) > 0))
			Utils.normalize(result);

		return result;
	}

	/**
	 * Classifies a given instance using the Average of Probabilities 
	 * combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceAverage(Instance instance) throws Exception {

		double[] probs = getClassifier(0).distributionForInstance(instance);
		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			for (int j = 0; j < dist.length; j++) {
				probs[j] += dist[j];
			}
		}
		for (int j = 0; j < probs.length; j++) {
			probs[j] /= (double)m_Classifiers.length;
		}
		return probs;
	}

	/**
	 * Classifies a given instance using the Product of Probabilities 
	 * combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceProduct(Instance instance) throws Exception {

		double[] probs = getClassifier(0).distributionForInstance(instance);
		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			for (int j = 0; j < dist.length; j++) {
				probs[j] *= dist[j];
			}
		}

		return probs;
	}

	/**
	 * Classifies a given instance using the Majority Voting combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceMajorityVoting(Instance instance) throws Exception {

		double[] probs = new double[instance.classAttribute().numValues()];
		double[] votes = new double[probs.length];

		for (int i = 0; i < m_Classifiers.length; i++) {
			probs = getClassifier(i).distributionForInstance(instance);
			int maxIndex = 0;
			for(int j = 0; j<probs.length; j++) {
				if(probs[j] > probs[maxIndex])
					maxIndex = j;
			}

			// Consider the cases when multiple classes happen to have the same probability
			for (int j=0; j<probs.length; j++) {
				if (probs[j] == probs[maxIndex])
					votes[j]++;
			}
		}

		int tmpMajorityIndex = 0;
		for (int k = 1; k < votes.length; k++) {
			if (votes[k] > votes[tmpMajorityIndex])
				tmpMajorityIndex = k;
		}

		// Consider the cases when multiple classes receive the same amount of votes
		Vector<Integer> majorityIndexes = new Vector<Integer>();
		for (int k = 0; k < votes.length; k++) {
			if (votes[k] == votes[tmpMajorityIndex])
				majorityIndexes.add(k);
		}
		// Resolve the ties according to a uniform random distribution
		int majorityIndex = majorityIndexes.get(m_Random.nextInt(majorityIndexes.size()));

		//set probs to 0
		for (int k = 0; k<probs.length; k++)
			probs[k] = 0;
		probs[majorityIndex] = 1; //the class that have been voted the most receives 1

		return probs;
	}

	/**
	 * Classifies a given instance using the Weighted Voting combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceWeightedVoting(Instance instance) throws Exception {
		double sum = 0;       // Weighted voting sum

		double[] probs = new double[instance.classAttribute().numValues()];
		double[] votes = new double[probs.length];

		for (int i = 0; i < m_Classifiers.length; i++) {
			probs = getClassifier(i).distributionForInstance(instance);
			int maxIndex = probs[0]>probs[1]?0:1;
			if (maxIndex==0){
				sum+=probs[0];
			}else {
				sum-=probs[1];
			}
		}

		int majorityIndex= sum>=0?0:1;

		//set probs to 0
		for (int k = 0; k<probs.length; k++)
			probs[k] = 0;
		probs[majorityIndex] = 1; //the class that have been voted the most receives 1

		return probs;
	}

	/**
	 * Classifies a given instance using the Maximum Probability combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceMax(Instance instance) throws Exception {

		double[] max = getClassifier(0).distributionForInstance(instance);
		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			for (int j = 0; j < dist.length; j++) {
				if(max[j]<dist[j])
					max[j]=dist[j];
			}
		}

		return max;
	}

	/**
	 * Classifies a given instance using the Minimum Probability combination rule.
	 *
	 * @param instance the instance to be classified
	 * @return the distribution
	 * @throws Exception if instance could not be classified
	 * successfully
	 */
	protected double[] distributionForInstanceMin(Instance instance) throws Exception {

		double[] min = getClassifier(0).distributionForInstance(instance);
		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			for (int j = 0; j < dist.length; j++) {
				if(dist[j]<min[j])
					min[j]=dist[j];
			}
		}

		return min;
	} 

	/**
	 * 使用(1)平均距离 的倒数或者(2)平均距离的核函数 作为权值，对
	 * 分类器的预测概率进行加权
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	protected double[] distributionForAvgD(Instance instance) 
			throws Exception{

		double[] probs = getClassifier(0).distributionForInstance(instance);
		double[] weights= computeInverseDistance(instance,m_Instances[0]);
		//double[] weights= computeKernelDistance(instance,m_Instances[0]);
		for(int i=0;i<probs.length;i++){
			probs[i]=probs[i]*weights[i];
		}
		Utils.normalize(probs);

		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			weights=computeInverseDistance(instance,m_Instances[i]);
			//weights=computeKernelDistance(instance,m_Instances[i]);
			double[] temp=new double[dist.length];

			for (int j = 0; j < dist.length; j++) {
				temp[j]=dist[j]*weights[j];
			}
			if(Utils.sum(temp)!=0)
			{
				Utils.normalize(temp);  
			}

			for(int k=0;k<temp.length;k++){
				probs[k]+=temp[k];
			}  
		}

		Utils.normalize(probs);
		return probs;  
	}


	protected double[] distributionForProD(Instance instance) throws Exception{

		double[] probs = getClassifier(0).distributionForInstance(instance);
		double sim[]=computeInverseDistance(instance,m_Instances[0]);
		for(int i=0;i<probs.length;i++){
			probs[i]=probs[i]*sim[i];
		}
		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			sim=computeInverseDistance(instance,m_Instances[i]);
			for (int j = 0; j < dist.length; j++) {
				probs[j] *= dist[j]*sim[j];
			}
		}

		return probs;

	}

	/**
	 * 使用(1)平均距离 的倒数或者(2)平均距离的核函数作为权值进行投票
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	protected double[] distributionForMajD(Instance instance) throws Exception{

		double sim[]=computeInverseDistance(instance,m_Instances[0]);
		//double sim[]=computeKernelDistance(instance,m_Instances[0]);
		double[] probs = getClassifier(0).distributionForInstance(instance);
		int index=Utils.maxIndex(probs);
		double[] vote=new double[probs.length];
		for(int i=0;i<probs.length;i++){
			if(i==index)
				vote[index]=sim[index];
		}

		for(int i=1;i<m_Classifiers.length;i++){
			sim=computeInverseDistance(instance,m_Instances[i]);
			//sim=computeKernelDistance(instance,m_Instances[i]);
			probs=getClassifier(i).distributionForInstance(instance);
			index=Utils.maxIndex(probs);
			for(int j=0;j<probs.length;j++){
				if(j==index)
					vote[index]+=sim[index];
			}
		}

		Utils.normalize(vote);
		return vote;
	}

	protected double[] distributionForMinD(Instance instance) throws Exception{

		double sim[]=computeInverseDistance(instance,m_Instances[0]);
		double[] min = getClassifier(0).distributionForInstance(instance);
		for(int i=0;i<min.length;i++){
			min[i]=min[i]*sim[i];
		}

		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			sim=computeInverseDistance(instance,m_Instances[i]);
			for (int j = 0; j < dist.length; j++) {
				if(dist[j]*sim[j]<min[j])
					min[j]=dist[j]*sim[j];
			}
		}

		return min;

	}

	protected double[] distributionForMaxD(Instance instance) throws Exception{

		//double sim[]=computeInverseDistance(instance,m_Instances[0]);
		double sim[]=computeKernelDistance(instance,m_Instances[0]);
		double[] max = getClassifier(0).distributionForInstance(instance);
		for(int i=0;i<max.length;i++){
			max[i]=max[i]*sim[i];
		}

		for (int i = 1; i < m_Classifiers.length; i++) {
			double[] dist = getClassifier(i).distributionForInstance(instance);
			//sim=computeInverseDistance(instance,m_Instances[i]);
			sim=computeKernelDistance(instance,m_Instances[i]);
			for (int j = 0; j < dist.length; j++) {
				if(max[j]<dist[j]*sim[j])
					max[j]=dist[j]*sim[j];
			}
		}

		return max;

	}

	/**
	 * 平均距离的倒数 1/(d+1)
	 * @param instance
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	protected double[] computeInverseDistance(Instance instance,Instances instances) throws Exception{

		double[] distance=new double[instances.numClasses()];
		double[] weights=new double[distance.length];
		EuclideanDistance ed= new EuclideanDistance(instances);
		int numMin=0;
		int numMaj=0;
		for(int i=0;i<instances.numInstances();i++){
			Instance ins=instances.instance(i);
			if(ins.classValue()==0){
				distance[0]=distance[0]+ed.distance(instance, ins);
				numMin++;
			}	  
			else{
				distance[1]=distance[1]+ed.distance(instance, ins);
				numMaj++;
			}	  
		}

		weights[0]=numMin/(1+distance[0]);
		weights[1]=numMaj/(1+distance[1]);

		Utils.normalize(weights);
		return weights;  
	}


	/**
	 * 平均距离的核函数e^(-d^2)
	 * @param instance
	 * @param instances
	 * @return
	 */
	protected double[] computeKernelDistance(Instance instance,Instances instances)throws Exception{

		double[] distance=new double[instances.numClasses()];
		double[] weights=new double[distance.length];
		EuclideanDistance ed= new EuclideanDistance(instances);
		int numMin=0;
		int numMaj=0;
		for(int i=0;i<instances.numInstances();i++){
			Instance ins=instances.instance(i);
			if(ins.classValue()==0){
				distance[0]=distance[0]+ed.distance(instance, ins);
				numMin++;
			}	  
			else{
				distance[1]=distance[1]+ed.distance(instance, ins);
				numMaj++;
			}	  
		}

		Utils.normalize(distance);

		for(int i=0;i<weights.length;i++){
			weights[i]=Math.exp(-distance[i]*distance[i]);
		}

		Utils.normalize(weights);
		return weights;
	}


	/**
	 * Returns the tip text for this property
	 * 
	 * @return 		tip text for this property suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String combinationRuleTipText() {
		return "The combination rule used.";
	}

	/**
	 * Gets the combination rule used
	 *
	 * @return 		the combination rule used
	 */
	public SelectedTag getCombinationRule() {
		return new SelectedTag(m_CombinationRule, TAGS_RULES);
	}

	/**
	 * Sets the combination rule to use. Values other than
	 *
	 * @param newRule 	the combination rule method to use
	 */
	public void setCombinationRule(SelectedTag newRule) {
		if (newRule.getTags() == TAGS_RULES)
			m_CombinationRule = newRule.getSelectedTag().getID();
	}

	/**
	 * Output a representation of this classifier
	 * 
	 * @return a string representation of the classifier
	 */
	public String toString() {

		if (m_Classifiers == null) {
			return "Vote: No model built yet.";
		}

		String result = "Vote combines";
		result += " the probability distributions of these base learners:\n";
		for (int i = 0; i < m_Classifiers.length; i++) {
			result += '\t' + getClassifierSpec(i) + '\n';
		}
		result += "using the '";

		switch (m_CombinationRule) {
		case AVERAGE_RULE:
			result += "Average of Probabilities";
			break;

		case PRODUCT_RULE:
			result += "Product of Probabilities";
			break;

		case MAJORITY_VOTING_RULE:
			result += "Majority Voting";
			break;

		case MIN_RULE:
			result += "Minimum Probability";
			break;

		case MAX_RULE:
			result += "Maximum Probability";
			break;

		default:
			throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
		}

		result += "' combination rule \n";

		return result;
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return		the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.19 $");
	}

	/**
	 * Main method for testing this class.
	 *
	 * @param argv should contain the following arguments:
	 * -t training file [-T test file] [-c class index]
	 */
	public static void main(String [] argv) {
		runClassifier(new UnderOverBagging(), argv);
	}
}
