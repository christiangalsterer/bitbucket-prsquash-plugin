package org.christiangalsterer.bitbucket.server;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.scm.pull.MergeRequest;
import com.atlassian.bitbucket.scm.pull.MergeRequestCheck;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

/**
 * Merge check which checks if the all changes in a pull request have been squashed into a single commit.
 */
@Scanned
public class PullRequestSquashMergeRequestCheck implements MergeRequestCheck {

    private PullRequestService pullRequestService;
    private PullRequestSquashSettings pullRequestSquashSettings;
    private I18nService i18Service;

    @Autowired
    public PullRequestSquashMergeRequestCheck(@ComponentImport PullRequestService pullRequestService, PullRequestSquashSettings pullRequestSquashSettings, @ComponentImport I18nService i18Service){
        this.pullRequestService = pullRequestService;
        this.pullRequestSquashSettings = pullRequestSquashSettings;
        this.i18Service = i18Service;
    }

    public void check(@Nonnull MergeRequest mergeRequest) {
        PullRequest pullRequest = mergeRequest.getPullRequest();
        long commits = pullRequestService.countCommits(pullRequest.getToRef().getRepository().getId(), pullRequest.getId());
        if (pullRequestSquashSettings.isEnabled(pullRequest.getToRef().getRepository()) && commits > 1) {
            mergeRequest.veto(i18Service.getMessage("org.christiangalsterer.bitbucket.server.prsquash.prsquashmergecheck.summary"), i18Service.getMessage("org.christiangalsterer.bitbucket.server.prsquash.prsquashmergecheck.detailed"));
        }
    }
}
