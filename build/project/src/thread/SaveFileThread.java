package thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SaveFileThread implements Runnable{
	File file;
	String content;
	
	
	public SaveFileThread(File file, String content) {
		this.file = file;
		this.content = content;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
