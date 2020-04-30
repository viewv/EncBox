package apitest;


import top.viewv.api.PublishGithubGist;

import java.io.IOException;

public class GithubGeneralTest {
    public static void main(String[] args) {
        try {
            String url =  PublishGithubGist.publish("",
                    "Test","Test Gist","test",true);
            System.out.println(url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Publish Filed");
        }
    }
}
