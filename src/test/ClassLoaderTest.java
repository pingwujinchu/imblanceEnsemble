package test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import application.Main;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;

public class ClassLoaderTest {
		public static void main(String []args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
			 URL url1 = (new File("C:\\Users\\fan\\Desktop\\cmar.jar")).toURL();  
             URLClassLoader myClassLoader1 = new URLClassLoader(new URL[] { url1 }, Thread.currentThread()  
                        .getContextClassLoader());  
             Class<?> myClass1 = myClassLoader1.loadClass("mine.CMAR_App");   
	   
             Classifier cl = (Classifier) myClass1.newInstance();
		}
}
