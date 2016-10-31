package org.christiangalsterer.bitbucket.server;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.ui.ContextualFormFragment;
import com.atlassian.bitbucket.ui.ValidationErrors;
import com.atlassian.bitbucket.view.TemplateRenderingException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Component
public class PullRequestSquashFormFragment implements ContextualFormFragment {

    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String FIELD_KEY = "prSquash";
    private static final String FRAGMENT_TEMPLATE = "org.christiangalsterer.bitbucket.server.prsquash.fragment";

    private final PullRequestSquashSettings pullRequestSquashSettings;
    private final SoyTemplateRenderer soyTemplateRenderer;

    @Autowired
    public PullRequestSquashFormFragment(PullRequestSquashSettings pullRequestSquashSettings, @ComponentImport SoyTemplateRenderer soyTemplateRenderer) {
        this.pullRequestSquashSettings = pullRequestSquashSettings;
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    @Override
    public void doError(Appendable appendable, Map<String, String[]> requestParams, Map<String, Collection<String>> fieldErrors, Map<String, Object> context) throws IOException {
        context.put(FIELD_KEY, isEnabled(requestParams.get(FIELD_KEY)));
        context.put(FIELD_ERRORS, fieldErrors);
        renderView(appendable, context);
    }

    @Override
    public void doView(Appendable appendable, Map<String, Object> context) throws IOException {
        Repository repository = (Repository) context.get("repository");
        context.put(FIELD_KEY, pullRequestSquashSettings.isEnabled(repository));
        renderView(appendable, context);
    }

    @Override
    public void execute(Map<String, String[]> requestParams, Map<String, Object> context) {
        Repository repository = (Repository) context.get("repository");

        if (isEnabled(requestParams.get(FIELD_KEY))) {
            pullRequestSquashSettings.enableFor(repository);
        } else {
            pullRequestSquashSettings.disableFor(repository);
        }
    }

    @Override
    public void validate(Map<String, String[]> requestParams, ValidationErrors errors, Map<String, Object> context) {
        // nothing to validate
    }

    private boolean isEnabled(String[] values) {
        return values != null && values.length > 0 && Boolean.valueOf(values[0]);
    }

    private void renderView(Appendable appendable, Map<String, Object> context) {
        try {
            soyTemplateRenderer.render(appendable, PullRequestSquashSettings.PLUGIN_KEY + ":prsquash-soy-templates",
                    FRAGMENT_TEMPLATE, context);
        } catch (SoyException e) {
            throw new TemplateRenderingException("Failed to render " + FRAGMENT_TEMPLATE, e);
        }
    }
}
