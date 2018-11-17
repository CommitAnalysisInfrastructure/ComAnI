package core;

import java.io.File;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.data.IExtractionQueue;
import net.ssehub.comani.extraction.AbstractCommitExtractor;
import net.ssehub.comani.extraction.ExtractionSetupException;

public class CommitExtractor extends AbstractCommitExtractor {
	
  private static final String ID = "MyCommitExtractor";

  public CommitExtractor(Properties extractionProperties,
      IExtractionQueue commitQueue)
      throws ExtractionSetupException {
    super(extractionProperties, commitQueue);
    this.logger.log(ID, "Created", null, Logger.MessageType.INFO);
    // TODO Further setup actions go here
  }

  @Override
  public boolean extract(File repository) {
    // TODO Extraction of all commits from given repository
    return false;
  }

  @Override
  public boolean extract(File repository,List<String> commitList) {
    /* TODO Extraction of all commits of given commit list from
     * given repository */
    return false;
  }

  @Override
  public boolean extract(String commit) {
    // TODO Extraction of given commit (convert to data model)
    return false;
  }

  @Override
  public boolean operatingSystemSupported(String os) {
    // TODO Check if extractor supports given operating system
    return false;
  }

  @Override
  public boolean versionControlSystemSupported(String vcs) {
    /* TODO Check if extractor supports given version control
     * system */
    return false;
  }

}
