package org.christiangalsterer.bitbucket.server;

import com.atlassian.bitbucket.event.repository.RepositoryDeletedEvent;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PullRequestSquashSettings {

    static final String PLUGIN_KEY = "org.christiangalsterer.bitbucket.server.bitbucket-prsquash-plugin";

    private final PluginSettings pluginSettings;

    @Autowired
    public PullRequestSquashSettings(@ComponentImport PluginSettingsFactory factory) {
        pluginSettings = factory.createSettingsForKey(PLUGIN_KEY);
    }

    public void disableFor(Repository repository) {
        pluginSettings.remove(createKey(repository));
    }

    public void enableFor(Repository repository) {
        pluginSettings.put(createKey(repository), "1");
    }

    public boolean isEnabled(Repository repository) {
        return pluginSettings.get(createKey(repository)) != null;
    }

    @EventListener
    public void onRepositoryDeleted(RepositoryDeletedEvent event) {
        pluginSettings.remove(createKey(event.getRepository()));
    }

    private String createKey(Repository repository) {
        return "repo." + repository.getId() + "." + PLUGIN_KEY + "prsquash";
    }
}
