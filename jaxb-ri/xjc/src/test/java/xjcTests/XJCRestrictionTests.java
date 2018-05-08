package xjcTests;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.tools.xjc.Plugin;

public class XJCRestrictionTests extends AbstractXJCTest{

	//  @Ignore
  @Test
  public void restrictionIssue() throws Throwable{
  	runTest(new Logic(){

			@Override
			public File getXsd() {
				return new File(resourceDir,"JustRestrictionIssue.xsd");
			}


			@Override
			public void loadBindings(List<File> files) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void loadPlugins(List<Plugin> plugins) {
				// TODO Auto-generated method stub
				
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


			@Override
			public void loadBindings(List<File> files) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void loadPlugins(List<Plugin> plugins) {
				// TODO Auto-generated method stub
				
			}
  		
  	});
  }  
}
