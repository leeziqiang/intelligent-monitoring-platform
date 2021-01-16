package com.vsst.common.video;


import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;


public class FaceDetect {
    /**
     * //人脸检测
     *
     * @param image 【Mat】
     * @return 【Mat】
     */
    public static Mat detectFaces(Mat image) {

        //人脸检测的配置文件
        String xml = "G:\\javaCode\\intelligent-monitoring-platform\\src\\main\\resources\\faceDetect\\haarcascade_frontalface_alt.xml";
        CvPoint cvPoint = new CvPoint();

        CascadeClassifier cascadeClassifier = new CascadeClassifier(xml);
        //在矩形框中检测人脸
        RectVector faces = new RectVector();
        cascadeClassifier.detectMultiScale(image, faces);
        rectangle(image, new Point(22,22),new Point(100,100),new Scalar(255, 0, 255, 1));//在原图上画出人脸的区域
        for (int i = 0; i < faces.size(); i++) {
            Rect face_i = faces.get(i);

//            rectangle(image, new Point(22,22),new Point(100,100),new Scalar(255, 255, 255, 6));//在原图上画出人脸的区域

        }
        return image;
    }
    public synchronized static Mat toMat(BufferedImage src){
        OpenCVFrameConverter.ToMat  matConv = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter biConv  = new Java2DFrameConverter();
        return matConv.convertToMat(biConv.convert(src)).clone();
    }
    public synchronized static BufferedImage toBufferedImage(Mat src) {
        Java2DFrameConverter  biConv  = new Java2DFrameConverter();
        OpenCVFrameConverter.ToMat  matConv = new OpenCVFrameConverter.ToMat();
        return deepCopy(biConv.getBufferedImage(matConv.convert(src).clone()));
    }
    public static BufferedImage deepCopy(BufferedImage source) {
        return Java2DFrameConverter.cloneBufferedImage(source);
    }
}
