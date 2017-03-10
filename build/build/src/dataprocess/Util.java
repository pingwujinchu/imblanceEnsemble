package dataprocess;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Util {
	
	public void saveResult(String result, String file) throws IOException{
		FileWriter fw=new FileWriter(file, false);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(result);
		bw.flush();
		bw.close();
	}
	
	public void appendResult(String result, String file) throws IOException{
		FileWriter fa=new FileWriter(file, true);
		BufferedWriter ba = new BufferedWriter(fa);
		ba.write(result);
		ba.flush();
		ba.close();
	}
	
}
