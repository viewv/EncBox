package top.viewv.api;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.Collections;

public class PublishGithubGist {
    public static String publish(String oauthtoken, String content, String description, String gistname, boolean ifpublic) throws IOException {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(oauthtoken);

        GistFile file = new GistFile();
        file.setContent(content);
        Gist gist = new Gist();

        gist.setDescription(description);

        gist.setFiles(Collections.singletonMap(gistname, file));
        gist.setPublic(ifpublic);

        gist = new GistService(client).createGist(gist);

        return gist.getHtmlUrl();
    }
}
