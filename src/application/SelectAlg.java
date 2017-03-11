package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class SelectAlg extends Stage {
	
	    Stage stage;
	    ChoiceBox choiceBox;
	    Properties pro;
	    @FXML Button fileSelect;
	    @FXML TextField showSelectedFile;
	    @FXML ChoiceBox selectClass;
	    @FXML CheckBox cb;
	    boolean select = false;
	    File selectedFile;
	    
		public SelectAlg(Stage parent,Properties pro,ChoiceBox choiceBox) throws MalformedURLException{
			this.initOwner(parent);
			this.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("selectAlg.fxml"),ResourceBundle.getBundle("selAlg"));
	        fXMLLoader.setController(this);
			Parent root;
			try {  
	        	root = fXMLLoader.load();  
	        } catch (IOException exception) {  
	            throw new RuntimeException(exception);  
	        }  
			 Scene s = new Scene(root);
			 this.setScene(s);
			 stage = this;
			 stage.getIcons().add(new Image(new File(("resource/images/unbalance.png")).toURL().toString()));
			 this.pro = pro;
			 this.choiceBox = choiceBox;
		}

		
		/**
		 * 选择类库
		 * @param event
		 * @throws IOException
		 */
		@FXML
		public void selectJar(ActionEvent event) throws IOException {
			 FileChooser fc = new FileChooser();
			 fc.setTitle("选择jar包");
			 fc.setInitialDirectory(new File("lib"));
			 ExtensionFilter ef=new ExtensionFilter("jar","*.jar");
			 fc.getExtensionFilters().add(ef);
			 File f = fc.showOpenDialog(stage);
			 selectedFile = f;
			 
			 if(f != null && f.exists()){
				 
				 showSelectedFile.setText(f.getAbsolutePath());
				 JarFile jarFile = new JarFile(f);
				 List<String> classList = new ArrayList();
				 Enumeration enums = jarFile.entries();
				 while(enums.hasMoreElements()){
					 JarEntry je = (JarEntry) enums.nextElement();
					 String name = je.getName();
						try {
							if(name.endsWith(".class")){
								 URL url1 = f.toURL();  
					             URLClassLoader myClassLoader1 = new URLClassLoader(new URL[] { url1 }, Thread.currentThread()  
					                        .getContextClassLoader());  
					             Class<?> myClass1 = myClassLoader1.loadClass(name.substring(0, name.length()-6).replaceAll("/", "\\."));   
						
					             if(weka.classifiers.Classifier.class.isAssignableFrom(myClass1)){
								     classList.add(name.substring(0, name.length()-6));
								     File lf = new File("lib");
								     if(!f.getAbsolutePath().startsWith(lf.getAbsolutePath())){
								    	 Main.classMap.put(name.substring(0, name.length()-6), myClass1);
								     }
					             }
							 }
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }
				 
				 selectClass.getItems().removeAll(selectClass.getItems());
				 selectClass.getItems().addAll(classList);
//				 opfile.setDisable(true);
			 }
		}
		
		@FXML 
		public void addAll(ActionEvent event){
			if(select){
				select = false;

				selectClass.setDisable(false);
			}else{
				select = true;

				selectClass.setDisable(true);
			}
		}
		
		/**
		 * 点击确认按钮
		 * @param event
		 */
		@FXML
		public void okAction(ActionEvent event){
			List<String> l = selectClass.getItems();
			Properties prow = new Properties();
			
			if(selectedFile == null){
				 Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				 _alert.setTitle("提示");
				 _alert.setHeaderText(null);
				 _alert.setContentText("请选择jar包");
			     _alert.initOwner(stage);
			     _alert.show();
				 return;
			}
			
			if(!select && selectClass.getValue() == null){
				 Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				 _alert.setTitle("提示");
				 _alert.setHeaderText(null);
				 _alert.setContentText("请选择分类器");
			     _alert.initOwner(stage);
			     _alert.show();
				 return;
			}
			
			FileOutputStream oFile;
			boolean isInPath = false;
			if(showSelectedFile.getText().startsWith(new File("lib").getAbsolutePath())){
				isInPath = true;
			}
			try {
				oFile = new FileOutputStream(new File("setting/baseClassification.properties"),true);
			if(select){
				for(String str : l){
					str = str.replaceAll("/", "\\.");
					if(!isInPath){
						str = "file:"+showSelectedFile.getText()+":"+str;
					}
					String name = str.substring(str.lastIndexOf(".")+1, str.length());
					if(!pro.containsKey(name)){
					   choiceBox.getItems().add(name);
					   prow.put(name, str);
					   prow.store(oFile,null);
					}
				}
				
				if(!choiceBox.getItems().contains("All")&&choiceBox.getItems().size() > 1){
					   choiceBox.getItems().add("All");
				}else{
					choiceBox.getItems().remove("All");
					choiceBox.getItems().add("All");
				}
				oFile.flush();
				oFile.close();
			  }else{
				String str = (String) selectClass.getValue();
				str = str.replaceAll("/","\\.");
				if(!isInPath){
					str = "file:"+showSelectedFile.getText()+":"+str;
				}
				String name = str.substring(str.lastIndexOf(".")+1, str.length());
				if(!pro.containsKey(name)){
					   choiceBox.getItems().add(name);
					   prow.put(name, str);
					   prow.store(oFile,null);
					}
				if(choiceBox.getItems().size() > 1 &&!choiceBox.getItems().contains("All")){
					   choiceBox.getItems().add("All");
				}else if(choiceBox.getItems().size() > 1 && choiceBox.getItems().contains("All")){
					choiceBox.getItems().remove("All");
					choiceBox.getItems().add("All");
				}
				oFile.flush();
				oFile.close();
			  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stage.hide();
		}
		
		@FXML
		public void cancelAction(ActionEvent event){
			stage.hide();
		}
		
}
