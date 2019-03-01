package net.ssehub.comani;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.comani.utility.FileUtilitiesTests;

/**
 * Class starts all test cases and defines global variable for the test classes. 
 * 
 * @author Marcel Spark
 */
@RunWith(Suite.class)
@SuiteClasses({
    FileUtilitiesTests.class,
    })
public class AllTests {

}
