package core;

import java.util.Properties;

import net.ssehub.comani.analysis.AbstractCommitAnalyzer;
import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.data.IAnalysisQueue;

public class CommitAnalyzer extends AbstractCommitAnalyzer {

    public CommitAnalyzer(Properties arg0, IAnalysisQueue arg1) throws AnalysisSetupException {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean analyze() {
        System.out.println("Yeeeeeeeeeeeeeeeeeeaaaaaaaaaaaaaaaaahhhhh, commits, yum yum");
        return false;
    }

    @Override
    public boolean operatingSystemSupported(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean versionControlSystemSupported(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

}
