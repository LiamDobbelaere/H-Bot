import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Enumeration;
import java.util.Scanner;

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

        System.out.println(data.numAttributes() + " attributes in dataset");

        Classifier classifier = new J48();
        classifier.buildClassifier(data);

        System.out.println("Training classifier (" + classifier.getClass() + ")");
        System.out.println("Classifier ready!");

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(classifier, data);

        System.out.println(eval.toSummaryString("\nResults\n======\n", false));

        System.out.println("WI " + data.attribute("a").index());

        System.out.print("Saving classifier...");
        weka.core.SerializationHelper.write("suicide.classifier", classifier);
        System.out.println("Done!");

        System.out.println("Interactive mode starting..");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            DenseInstance di = new DenseInstance(data.numAttributes());
            di.setDataset(data);
            String test = scanner.nextLine();

            Enumeration<Attribute> ea = data.enumerateAttributes();

            while (ea.hasMoreElements()) {
                Attribute next = ea.nextElement();
                System.out.println(next.name());
                di.setValue(next.index(), 0);
            }

            test = test.toLowerCase();
            for (String s : test.split(" ")) {
                if (data.attribute(s) != null) {
                    di.setValue(data.attribute(s), 1);
                    System.out.println("Set value 1 for " + s);
                }
            }

            System.out.println(classifier.toString());

            //di.setValue();
            double cl = 0;
            try {
                di.setClassValue(classifier.classifyInstance(di));
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Instance class is: " + di.classValue());

            if (di.classValue() > 0.5) {
                System.out.println("Result for '" + test + "' is suicidal");
            } else {
                System.out.println("Result for '" + test + "' is not suicidal");
            }
        }
    }
}
