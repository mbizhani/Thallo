package x.y.z;

public class DummyExceptionGen {
	public static void excGen() {
		test1();
	}

	private static void test1() {
		test2();
	}

	private static void test2() {
		test3();
	}

	private static void test3() {
		test4();
	}

	private static void test4() {
		test5();
	}

	private static void test5() {
		throw new Level1Exception();
	}
}
