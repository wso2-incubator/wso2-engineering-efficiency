package tk.neverstopcoding;

import tk.neverstopcoding.GitRepoChecker.GitRepoCheck;

public class Main {

    public static void main(String[] args) {
        try {
            GitRepoCheck.validateRepository("https://github.com/dimuthnc/test-dependency-manager",null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
