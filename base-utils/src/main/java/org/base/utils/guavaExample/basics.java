package org.base.utils.guavaExample;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

public class basics {
	
	static class Person implements Comparable<Person> {
		  private String lastName;
		  private String firstName;
		  private int zipCode;
		  private etype type;
		  public enum etype{
			  http(2),
			  http2(1);
			  private int a;
			  etype(int a)
			  {
				  this.a = a;
			  }
		  }
		  public int compareTo(Person that) {
			  return ComparisonChain.start()
					              .compare(this.firstName, that.firstName)
					              .compare(this.zipCode, that.zipCode)
					              .compare(this.type, that.type, Ordering.natural().nullsLast())
					              .result();

		    /*int cmp = lastName.compareTo(other.lastName);
		    if (cmp != 0) {
		      return cmp;
		    }
		    cmp = firstName.compareTo(other.firstName);
		    if (cmp != 0) {
		      return cmp;
		    }
		    return Integer.compare(zipCode, other.zipCode);*/
		  }
	}
	public static void main(String[]args)
	{
		Optional<Integer> possible = Optional.of(5);
		possible.isPresent(); // returns true
		System.out.print(possible.get()); // returns 5
		int i = 0;
		Preconditions.checkArgument(i >= 0, "Argument was %s but expected nonnegative", i);
		Preconditions.checkState(true, "not null");
		Preconditions.checkState(3>2,"check state %d,%s",1,"test");
		Preconditions.checkNotNull(true,  1);
		//Predicates.assertEquals("阿里巴巴测试公司", "阿里巴巴测试公司");
		Objects.equal("a", "a"); // returns true
		Objects.equal(null, "a"); // returns false
		Objects.equal("a", null); // returns false
		Objects.equal(null, null); // returns true
		int a = Objects.hashCode(possible,i);
		System.out.println(MoreObjects.toStringHelper(possible).add("x", 1).toString());
		// Returns "MyObject{x=1}"
		System.out.println(MoreObjects.toStringHelper("MyObject").add("x", 1).toString());

	}
}
