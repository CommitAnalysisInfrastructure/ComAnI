package core;

import java.io.File;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.data.IExtractionQueue;
import net.ssehub.comani.extraction.AbstractCommitExtractor;
import net.ssehub.comani.extraction.ExtractionSetupException;

public class CommitExtractor extends AbstractCommitExtractor {

    public CommitExtractor(Properties arg0, IExtractionQueue arg1) throws ExtractionSetupException {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean extract(File arg0) {
        System.out.println("I am a new commit extractor doing nothing!");
        return false;
    }

    @Override
    public boolean extract(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean extract(File arg0, List<String> arg1) {
        // TODO Auto-generated method stub
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
