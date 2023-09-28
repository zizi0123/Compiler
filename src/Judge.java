import ast.util.error.SemanticError;
import ast.util.error.SyntaxError;

import java.io.*;

public class Judge {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) throws Exception {
        judge(args);
    }

    public static void judge(String [] args) {
        String folderPath = "testcases/sema";
        if (traverseFolder(new File(folderPath),args)) {
            System.out.println(GREEN + "All tests passed!");
        } else {
            System.out.println(RED + "Some tests failed!");
        }
        System.out.print(RESET);
    }

    public static boolean traverseFolder(File folder,String []args) {
        if (folder.isDirectory()) {
            boolean success = true;
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!traverseFolder(file,args)) success = false;
                }
            }
            return success;
        } else {
            System.out.println(YELLOW + folder.getAbsolutePath().substring(folder.getAbsolutePath().indexOf("sema\\") + 5) + ": ");
            if (semanticCheck(folder.getAbsolutePath(),args)) {
                System.out.println(GREEN + "Pass!");
                return true;
            } else {
                System.out.println(RED + "Fail!");
                System.exit(0);
                return false;
            }
        }
    }

    public static boolean semanticCheck(String fileName,String []args) {
        boolean success = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineNumber == 6) {
                    String result = line.substring(line.indexOf(":") + 2);
                    success = result.equals("Success");
                    break; // find the answer in line 6 and exit the loop
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream input = new FileInputStream(fileName);
            Compiler.compile(input,args);
        } catch (SyntaxError syntaxError) {
            System.err.println(syntaxError.message + "in line" + syntaxError.pos.getRow() + ", column" + syntaxError.pos.getCol());
            return !success;
        } catch (SemanticError semanticError) {
            System.err.println(semanticError.message);
            return !success;
        } catch (Exception error) {
            System.err.println("COMPILER FAIL!!");
            return !success;
        }
        return success;
    }
}
