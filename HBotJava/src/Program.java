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
    public static void main(String[] args) {
        DataSource source = null;
        try {
            source = new DataSource("C:/Users/tomdo/Documents/Weka datasets/reddithelp.arff");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.classIndex() == -1)
        {
            data.setClassIndex(data.numAttributes() - 1);
            System.out.println("[WARNING] Could not find class index info, automatically set class to attribute with name '" + data.classAttribute().name() + "'! (= last attribute)");
        }

        System.out.println("Training J48 tree...");

        MultilayerPerceptron tree = new MultilayerPerceptron();
        try {
            tree.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("J48 classifier ready!");

        Evaluation eval = null;
        try {
            eval = new Evaluation(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            eval.evaluateModel(tree, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(eval.toSummaryString("\nResults\n======\n", false));

        System.out.println("WI " + data.attribute("a").index());
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

            System.out.println(tree.toString());

            //di.setValue();
            double cl = 0;
            try {
                di.setClassValue(tree.classifyInstance(di));
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
