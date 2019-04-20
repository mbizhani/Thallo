package x.y.z;

import java.util.List;

public interface ITestService {
	void noResult();

	String simpleMethod(Integer no);

	double throwsError(int no);

	String ignored(List<Integer> list);

	Boolean auth(String password);

	double ignoreError(int no);
}
