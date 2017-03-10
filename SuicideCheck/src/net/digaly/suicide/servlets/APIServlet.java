package net.digaly.suicide.servlets;

import net.digaly.suicide.model.SuicideClassifier;
import org.json.JSONObject;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils;

import java.io.IOException;

/**
 * Created by tomdo on 10/03/2017.
 */
public class APIServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        JSONObject responseJson = new JSONObject();

        SuicideClassifier suicideClassifier = null;

        if (!SuicideClassifier.hasInstance()) {
            try {
                Classifier classifier = (Classifier) weka.core.SerializationHelper.read(
                        getServletContext().getResourceAsStream("/WEB-INF/res/suicide.classifier"));
                ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(
                        getServletContext().getResourceAsStream("/WEB-INF/res/reddithelp.arff"));
                suicideClassifier = SuicideClassifier.getInstance(classifier, dataSource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (SuicideClassifier.hasInstance()) {
            String result = suicideClassifier.getInstance(null, null).classifiyString(request.getParameter("q")) > 0.5 ? "suicidal" : "not suicidal";

            responseJson.put("result", result);
        }

        response.getWriter().write(responseJson.toString());
    }
}
