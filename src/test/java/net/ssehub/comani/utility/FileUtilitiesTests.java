package net.ssehub.comani.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test class for {@link FileUtilities}.
 *
 * @author Marcel Spark
 */
public class FileUtilitiesTests {
    
    /**
     * Specify the test file which will be written in test cases.
     * Be sure the file does not exist.
     */
    private static final String TEST_FILE = "test.txt";
    
    /**
     * Specifies a simple test content which will written into a file.
     */
    private static final String TEST_FILE_CONTENT = "Test \n }{ \n #öüßö";
    
    /**
     * TemporaryFolder JUnit rule. JUnit does automatic cleanup after tests.
     */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    /**
     * An instance of FileUtilities which will set before each test case.
     */
    private FileUtilities fileUtils;
    
    /**
     * Initialize an instance of fileUtils before each test case.
     */
    @Before
    public void setUp() {
        fileUtils = FileUtilities.getInstance();
    }
    
    /**
     * Writes a file with content to a existing parent-path and validate its content. 
     * 
     * @throws IOException thrown if something was falsely written to disk or file not found
     */
    @Test
    public void testWriteFileWithExistingPath() throws IOException {
        fileUtils.writeFile(folder.getRoot().toString(), TEST_FILE, TEST_FILE_CONTENT, false);
        File fileToTest = new File(folder.getRoot().toString(), TEST_FILE); // system independent
        boolean success = verifyDiskContent(fileToTest, TEST_FILE_CONTENT.split("\n"));
        
        assertTrue("File content was not correctly written to disk", success);
    }
    
    /**
     * Tests the {@link FileUtilities#writeFile(String, String, String, boolean)} method and tries to write a file
     * to a non existing path. 
     * The method should create the parent and write content successfully.
     * 
     * @throws IOException thrown if something was falsely written to disk or file not found
     */
    @Test
    public void testWriteFileInvalidPath() throws IOException {
        Path filePath = Paths.get(folder.getRoot().toString(), "subdir", TEST_FILE);
        boolean writeSuccess = fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, 
                TEST_FILE_CONTENT, false);
        
        assertTrue("The sub folder should be created.  ", writeSuccess);
    }
    
    /**
     * Tests the {@link FileUtilities#writeFile(String, String, String, boolean)} method and tries to write a file
     * to a existing path and an existing file. 
     * The override flag is set and the file should be replaced.
     * 
     * @throws IOException Unwanted. If anything during write/read process fails
     */
    @Test
    public void testWriteFileWithOverride() throws IOException {
        Path filePath = Paths.get(folder.getRoot().toString(), TEST_FILE);
        fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, "test", false); // create empty file
        
        // create file with content
        fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, TEST_FILE_CONTENT, true); 
        
        boolean success = verifyDiskContent(filePath.toFile(), TEST_FILE_CONTENT.split("\n"));
        assertTrue("Override of file fails. ", success);
    }
    
    /**
     * Tests the {@link FileUtilities#writeFile(String, String, String, boolean)} method and tries to write a file
     * to a existing path and an existing file. 
     * The override flag is not set and the file should not be replaced.
     * 
     * @throws IOException Unwanted. If anything during write/read process fails
     */
    @Test
    public void testWriteFileWithoutOverride() throws IOException {
        Path filePath = Paths.get(folder.getRoot().toString(), TEST_FILE);
        fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, "test", false); // create empty file
        
        fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, TEST_FILE_CONTENT, false);
        boolean success = verifyDiskContent(filePath.toFile(), TEST_FILE_CONTENT.split("\n"));
        
        assertFalse("The override flag was not set but the file was overriden. ",  success);
    }
    
    /**
     * Test for {@link FileUtilities#readFile(File)}. It reads an existing file content and compares it with the written
     * content. 
     */
    @Test
    public void testReadExistingFile() {
        Path filePath = Paths.get(folder.getRoot().toString(), TEST_FILE);
        fileUtils.writeFile(filePath.getParent().toString(), TEST_FILE, TEST_FILE_CONTENT, true);
        List<String> extractedContent = fileUtils.readFile(filePath.toFile());
        String[] expectedContent = TEST_FILE_CONTENT.split("\n");
        
        assertEquals("The line number have to be equal to the written file.", 
                expectedContent.length, extractedContent.size());
        
        for (int i = 0; i < extractedContent.size(); i++) {
            boolean equal = extractedContent.get(i).equals(expectedContent[i]);
             assertTrue("Content have to be the same." , equal);
        }
    }
    
    /**
     * Test for {@link FileUtilities#readFile(File)}. It tries to read a non existing file. 
     * This should fail. 
     */
    @Test
    public void testReadNonExistingFile() {
        Path filePath = Paths.get(folder.getRoot().toString(), TEST_FILE);
        List<String> fileContent = fileUtils.readFile(filePath.toFile());
        //assertNull(fileContent); // maybe the function should return null ? TODO
        assertTrue(fileContent.isEmpty());
    }
    
    /**
     * Helper method to verify the content inside a file with given content. 
     * 
     * @param fileToTest the existing {@link File} which content will be checked.
     * @param content the content to verify the file-content against
     * @return <code> true </code> content of the given file is equal to the given content, <code> false </code> 
     * otherwise
     * @throws IOException thrown if something was falsely written to disk or file not found
     */
    private boolean verifyDiskContent(File fileToTest, String[] content) throws IOException {
        boolean success = true;
        try (BufferedReader br = new BufferedReader(new FileReader(fileToTest))) {
            int i = 0;
            String line = br.readLine();
            while (line != null) {
                if (!content[i].equals(line)) {
                    success = false;
                    line = null;
                    break;
                }
                i++;
                line = br.readLine();
            }
        }
        return success;
    }
}
