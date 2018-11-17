package core;

import java.util.Properties;

import net.ssehub.comani.analysis.AbstractCommitAnalyzer;
import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.core.Logger;
import net.ssehub.comani.data.Commit;
import net.ssehub.comani.data.IAnalysisQueue;

public class CommitAnalyzer extends AbstractCommitAnalyzer {
	
  private static final String ID = "MyCommitAnalyzer";

  public CommitAnalyzer(Properties analysisProperties,
      IAnalysisQueue commitQueue)
      throws AnalysisSetupException {
    super(analysisProperties, commitQueue);
    this.logger.log(ID, "Created", null, Logger.MessageType.INFO);
    // TODO Further setup actions go here
  }

  @Override
  public boolean analyze() {
    while (this.commitQueue.isOpen()) {
      Commit commit = this.commitQueue.getCommit();
      if (commit != null) {
        // TODO Analyze commit
      }
    }
    return false;
  }

  @Override
  public boolean operatingSystemSupported(String os) {
    // TODO Check if analyzer supports given operating system
    return false;
  }

  @Override
  public boolean versionControlSystemSupported(String vcs) {
    /* TODO Check if analyzer supports given version control
     * system */
    return false;
  }

}