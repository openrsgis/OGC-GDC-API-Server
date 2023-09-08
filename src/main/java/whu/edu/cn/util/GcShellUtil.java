package whu.edu.cn.util;

import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author czp
 * @version 1.0
 * @date 2021/3/19 17:21
 */
@Component
public class GcShellUtil {

    /**
     * Executes a command in the system shell and returns the exit code.
     *
     * @param command The command to execute.
     * @return The exit code of the command. Returns -1 if an exception occurs.
     */
    public Integer ExecCommand(String command) {
        int retCode = 0;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, null);
            retCode = process.waitFor();
            ExecOutput(process);
        } catch (Exception e) {
            retCode = -1;
        }
        return retCode;
    }

    /**
     * Reads and processes the output of a given Process.
     *
     * @param process The Process whose output is being read and processed.
     * @return True if the process output was successfully read and processed, false otherwise.
     * @throws Exception If an error occurs while reading the process output.
     */
    public boolean ExecOutput(Process process) throws Exception {
        if (process == null) {
            return false;
        } else {
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            String output = "";
            while ((line = input.readLine()) != null) {
                output += line + "\n";
            }
            input.close();
            ir.close();
        }
        return true;
    }
}
