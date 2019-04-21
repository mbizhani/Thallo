package x.y.z;

import org.devocative.thallo.core.annotation.ELogMode;
import org.devocative.thallo.core.annotation.ELogPlace;
import org.devocative.thallo.core.annotation.LogIt;
import org.springframework.stereotype.Service;

import java.util.List;

@LogIt
@Service
public class TestService implements ITestService {

	@Override
	public void noResult() {
	}

	@Override
	public String simpleMethod(Integer no) {
		return "no = " + no;
	}

	@Override
	public double throwsError(int no) {
		return div(no);
	}

	@LogIt(mode = ELogMode.Disabled)
	@Override
	public String ignored(List<Integer> list) {
		return String.valueOf(list);
	}

	@LogIt(mode = ELogMode.Info, logParams = false, logResult = false)
	@Override
	public Boolean auth(String password) {
		return true;
	}

	@LogIt(mode = ELogMode.Info, place = ELogPlace.Both)
	@Override
	public double ignoreError(int no) {
		return throwsError(no);
	}

	// ------------------------------

	private double div(int no) {
		return no / 0;
	}
}
