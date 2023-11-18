package com.example.python.python;

import java.util.*;
import java.io.*;
//import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.FastMath;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class PushupEnsembleModel {
//    private Object model;
//    private Object scaler;

    public PushupEnsembleModel() {
//        try {
//            FileInputStream fileIn = new FileInputStream("./model/ensemble_model.pkl");
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            model = in.readObject();
//            in.close();
//            fileIn.close();
//
//            fileIn = new FileInputStream("./scaler/scaler1020.pkl");
//            in = new ObjectInputStream(fileIn);
//            scaler = in.readObject();
//            in.close();
//            fileIn.close();
//        } catch (IOException i) {
//            i.printStackTrace();
//        } catch (ClassNotFoundException c) {
//            System.out.println("Class not found");
//            c.printStackTrace();
//        }
    }

    public Object predict(ArrayList<HashMap<String, Double>> keypoint3d) {
        double[] preprocessedData = preprocess(keypoint3d);
        PythonInterpreter pyInterpreter = new PythonInterpreter();
        pyInterpreter.execfile("src/main/java/com/example/python/python/predict.py");

        PyFunction pyFunction = pyInterpreter.get("predict", PyFunction.class);

        System.out.println(Arrays.toString(preprocessedData));
        PyObject obj = pyFunction.__call__(new PyString(Arrays.toString(preprocessedData)));
        return obj.toString(); // model.predict() should be implemented
    }

    private double[] preprocess(ArrayList<HashMap<String, Double>> keypoint3d) {
//        if (scaler == null) {
//            throw new IllegalArgumentException("Scaler not loaded.");
//        }
        double[][] data = new double[keypoint3d.size()][3];
        for (int i=0; i<keypoint3d.size(); i++) {
            data[i][0] = keypoint3d.get(i).get("x");
            data[i][1] = keypoint3d.get(i).get("y");
            data[i][2] = keypoint3d.get(i).get("z");
        }

        double[] hipCoords = data[23];
        for (int i=0; i<data.length; i++) {
            data[i] = subtract(data[i], hipCoords);
        }

        // Right
        double[] shoulderRight = data[11];
        double[] elbowRight = data[13];
        double[] wristRight = data[15];
        double[] hipRight = data[23];
        double[] kneeRight = data[25];
        double[] ankleRight = data[27];

        // Left
        double[] shoulderLeft = data[12];
        double[] elbowLeft = data[14];
        double[] wristLeft = data[16];
        double[] hipLeft = data[24];
        double[] kneeLeft = data[26];
        double[] ankleLeft = data[28];

        double angleRightArm = calculateAngle(shoulderRight, elbowRight, wristRight);
        double angleLeftArm = calculateAngle(shoulderLeft, elbowLeft, wristLeft);
        double angleRightLeg = calculateAngle(hipRight, kneeRight, ankleRight);
        double angleLeftLeg = calculateAngle(hipLeft, kneeLeft, ankleLeft);

        double xyAngleRightArm = calculateXYAngle(
                Arrays.copyOfRange(shoulderRight, 0, 2),
                Arrays.copyOfRange(elbowRight, 0, 2),
                Arrays.copyOfRange(wristRight, 0, 2)
        );
        double xyAngleLeftArm = calculateXYAngle(
                Arrays.copyOfRange(shoulderLeft, 0, 2),
                Arrays.copyOfRange(elbowLeft, 0, 2),
                Arrays.copyOfRange(wristLeft, 0, 2)
        );
        double xyAngleRightLeg = calculateXYAngle(
                Arrays.copyOfRange(hipRight, 0, 2),
                Arrays.copyOfRange(kneeRight, 0, 2),
                Arrays.copyOfRange(ankleRight, 0, 2)
        );
        double xyAngleLeftLeg = calculateXYAngle(
                Arrays.copyOfRange(hipLeft, 0, 2),
                Arrays.copyOfRange(kneeLeft, 0, 2),
                Arrays.copyOfRange(ankleLeft, 0, 2)
        );

        ArrayList<Double> dataList = new ArrayList<>();
        for (double[] arr : data) {
            for (double val : arr) {
                dataList.add(val);
            }
        }
        roundAndAppend(angleRightArm, angleLeftArm, angleRightLeg, angleLeftLeg, dataList);
        roundAndAppend(xyAngleRightArm, xyAngleLeftArm, xyAngleRightLeg, xyAngleLeftLeg, dataList);

        double[] dataArray = new double[dataList.size()];
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = dataList.get(i);
        }

        PythonInterpreter pyInterpreter = new PythonInterpreter();
        pyInterpreter.execfile("src/main/java/com/example/python/python/transform.py");

        PyFunction pyFunction = pyInterpreter.get("transform", PyFunction.class);

        System.out.println(Arrays.toString(dataArray));
        PyObject obj = pyFunction.__call__(new PyString(Arrays.toString(dataArray)));
        PyList pyList = (PyList) obj;
        double[] normalizedData = new double[pyList.size()];
        for (int i = 0; i < pyList.size(); i++) {
            normalizedData[i] = (Double) pyList.get(i);
        }

        double[] preprocessedData = Arrays.copyOfRange(normalizedData, normalizedData.length - 8, normalizedData.length);

        preprocessedData[0] = Math.pow(preprocessedData[0] * 4, 2) / 2;
        preprocessedData[1] = Math.pow(preprocessedData[1] * 4, 2) / 2;
        preprocessedData[4] = Math.pow(preprocessedData[4] * 4, 2) / 2;
        preprocessedData[5] = Math.pow(preprocessedData[5] * 4, 2) / 2;
        preprocessedData[2] *= 4;
        preprocessedData[3] *= 4;
        preprocessedData[6] *= 4;
        preprocessedData[7] *= 4;

        return preprocessedData;
    }

    private void roundAndAppend(double angleRightArm, double angleLeftArm, double angleRightLeg, double angleLeftLeg, ArrayList<Double> dataList) {
        dataList.add(Math.round(angleRightArm * 100) / 100.0);
        dataList.add(Math.round(angleLeftArm * 100) / 100.0);
        dataList.add(Math.round(angleRightLeg * 100) / 100.0);
        dataList.add(Math.round(angleLeftLeg * 100) / 100.0);
    }

    private double calculateAngle(double[] a, double[] b, double[] c) {
        double[] ba = subtract(a, b);
        double[] bc = subtract(c, b);
        double cosineAngle = dotProduct(ba, bc) / (norm(ba) * norm(bc));
        double angle = FastMath.acos(cosineAngle);
        return FastMath.toDegrees(angle);
    }

    private double calculateXYAngle(double[] a, double[] b, double[] c) {
        double[] ba = subtract(a, b);
        double[] bc = subtract(c, b);
        double dotProduct = ba[0]*bc[0] + ba[1]*bc[1];
        double magnitudeBA = FastMath.sqrt(ba[0]*ba[0] + ba[1]*ba[1]);
        double magnitudeBC = FastMath.sqrt(bc[0]*bc[0] + bc[1]*bc[1]);
        double cosTheta = dotProduct / (magnitudeBA * magnitudeBC);
        return FastMath.acos(cosTheta) * (180 / FastMath.PI);
    }

    private double[] subtract(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i=0; i<a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    private double norm(double[] a) {
        double sum = 0;
        for (int i=0; i<a.length; i++) {
            sum += a[i]*a[i];
        }
        return FastMath.sqrt(sum);
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i=0; i<a.length; i++) {
            sum += a[i]*b[i];
        }
        return sum;
    }
}
