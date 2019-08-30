package sample;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BackgroundTask extends Task {
    private int value;

    public BackgroundTask(int value) {
        this.value = value;
    }

    @Override
    protected Object call() throws Exception {
        try {
            String cmd = "for (( i = "+value+" ; $i > 0; i=i-1)) ; do echo $i ; sleep 1; done";
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);

            Process process = builder.start();

            InputStream out = process.getInputStream();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(out));

            String line = null;
            while ((line = stdout.readLine()) != null ) {
                if(isCancelled()) {
                    process.destroy();
                    return null;
                }
                updateMessage(line);
//                txtOutput.appendText(line + System.lineSeparator());
            }

//            txtOutput.appendText("Finished! ");

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
