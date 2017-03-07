import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Created by tomdo on 7/03/2017.
 */
public class Program {
    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("C:/Users/tomdo/Documents/Weka datasets/reddithelp.arff");
        Instances data = source.getDataSet();

        if (data.classIndex() == -1)
        {
            data.setClassIndex(data.numAttributes() - 1);
            System.out.println("[WARNING] Could not find class index info, automatically set class to attribute with name '" + data.classAttribute().name() + "'! (= last attribute)");
        }

        System.out.println("Training J48 tree...");

        J48 tree = new J48();
        tree.buildClassifier(data);

        System.out.println("J48 classifier ready!");

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(tree, data);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }
}
