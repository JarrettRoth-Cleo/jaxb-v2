package xjcTests;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class XJCRestrictionTests extends AbstractXJCTest{

	//  @Ignore
  @Test
  public void restrictionIssue() throws Throwable{
  	runTest(new Logic(){

			@Override
			public File getXsd() {
				return new File(resourceDir,"JustRestrictionIssue.xsd");
			}  		
  	});
  }
  
  @Ignore
  @Test
  public void fullRestrictionIssue() throws Throwable{
  	runTest(new Logic(){

			@Override
			public File getXsd() {
				return new File("C:/code/xjcFork/xsds/RestrictionIssue","Party.xsd");
			}  		
  	});
  }  
}
