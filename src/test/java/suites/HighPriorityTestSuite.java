package suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("defaultHttp.tests")
@IncludeTags("High")
public class HighPriorityTestSuite {
}
