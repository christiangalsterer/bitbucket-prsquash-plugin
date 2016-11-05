package org.christiangalsterer.bitbucket.server;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.rest.RestErrorMessage;
import com.atlassian.bitbucket.rest.fragment.RestFragment;
import com.atlassian.bitbucket.rest.fragment.RestFragmentContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

@Component
public class PullRequestSquashRestFragment implements RestFragment {

    private static final String PR_SQUASH_KEY = "requiredPullRequestSquash";

    private final I18nService i18nService;
    private final PullRequestSquashSettings settings;

    @Autowired
    public PullRequestSquashRestFragment(PullRequestSquashSettings settings, @ComponentImport I18nService i18nService) {
        this.settings = settings;
        this.i18nService = i18nService;
    }

    @Nonnull
    @Override
    public Map<String, Object> execute(@Nonnull RestFragmentContext fragmentContext,
                                       @Nonnull Map<String, Object> requestContext) {
        Repository repository = (Repository) requestContext.get("repository");

        if (fragmentContext.getMethod().equals("GET")) {
            return doGet(repository);
        } else if (fragmentContext.getMethod().equals("POST")) {
            return doPost(fragmentContext, repository);
        }

        return ImmutableMap.of(PR_SQUASH_KEY, i18nService.getMessage("org.christiangalsterer.bitbucket.server.prsquash.prsquashmergecheck.rest.method.unsupported",
                fragmentContext.getMethod()));
    }

    @Nonnull
    @Override
    public Map<String, Object> validate(@Nonnull RestFragmentContext fragmentContext,
                                        @Nonnull Map<String, Object> requestContext) {
        if (fragmentContext.getMethod().equals("POST")) {
            Optional<Object> property = fragmentContext.getBodyProperty(PR_SQUASH_KEY);

            if (property.isPresent() && !(property.get() instanceof Boolean)) {
                return new RestErrorMessage(PR_SQUASH_KEY,
                        i18nService.getMessage("org.christiangalsterer.bitbucket.server.prsquash.prsquashmergecheck.rest.invalid"));
            }
        }

        return ImmutableMap.of();
    }

    private Map<String, Object> doGet(Repository repository) {
        return ImmutableMap.of(PR_SQUASH_KEY, settings.isEnabled(repository));
    }

    private Map<String, Object> doPost(RestFragmentContext fragmentContext, Repository repository) {
        fragmentContext.getBodyProperty(PR_SQUASH_KEY).ifPresent(b -> {
            if ((Boolean) b) {
                settings.enableFor(repository);
            } else {
                settings.disableFor(repository);
            }
        });

        return doGet(repository);
    }
}