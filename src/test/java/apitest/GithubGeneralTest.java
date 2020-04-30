package apitest;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.Collections;

public class GithubGeneralTest {
    public static void main(String[] args) throws IOException {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token("");

        GistFile file = new GistFile();
        file.setContent("System.out.println(\"Hello World\");");
        Gist gist = new Gist();

        gist.setDescription("Prints a string to standard out");

        gist.setFiles(Collections.singletonMap("sout.java", file));
        gist.setPublic(true);

        gist = new GistService(client).createGist(gist);

        System.out.println(gist.getDescription());
    }
}
