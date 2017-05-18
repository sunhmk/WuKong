package org.base.hbase.sizeestimate;

public class ObjectSizeTests {
	static class B {
		int a;
		int b;
	}

	static class C {
		int ba;
		B[] as = new B[3];

		C() {
			for (int i = 0; i < as.length; i++) {
				as[i] = new B();
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(ClassSize.estimateBase(UnsafeAccess.class, false));
		System.out.println(ClassSize.estimateBase(Long.class, false));
		System.out.println(ClassSize.estimateBase(long.class, false));
		System.out.println(ClassSize.estimateBase(byte.class, false));
		System.out.println(ClassSize.estimateBase(
				org.base.spark.MemoryPool.class, false));
		System.out.println(ClassSize.estimateBase(
				B.class, false));
		System.out.println(ClassSize.estimateBase(
				C.class, false));

	}
}
