package unitTests;

import static org.mockito.Mockito.*;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import refaco.StreamReader;

public class StreamReaderTests {

	@Test
	public void test1(){	
		InputStream inputStream = IOUtils.toInputStream("foo1 foo2 foo3 foo4 foo5");
		StreamReader streamReader = new StreamReader(inputStream, "INPUT");
		streamReader.run();
	}
	
	@Test
	public void test2(){	
		InputStream inputStream = IOUtils.toInputStream("foo1 foo2 foo3 foo4 foo5");
		IProgressMonitor monitor = new NullProgressMonitor();
		StreamReader streamReader = new StreamReader(inputStream, "INPUT", monitor);
		streamReader.run();
	}
	
	@Test
	public void testFail(){	
		InputStream inputStream = mock(InputStream.class);
		StreamReader streamReader = new StreamReader(inputStream, "INPUT");
		streamReader.run();
	}
}
