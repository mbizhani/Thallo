package x.y.z;

import org.devocative.thallo.core.annotation.LogIt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@LogIt
@RestController
public class TestController {

	private final ITestService testService;

	public TestController(ITestService testService) {
		this.testService = testService;
	}

	@GetMapping("/ignore")
	public String ignore() {
		return testService.ignored(Collections.emptyList());
	}
}
