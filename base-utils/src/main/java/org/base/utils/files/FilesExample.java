package org.base.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class FilesExample {
	public static void main(String[]args)
	{
		FileInputStream input = null;
		byte[] bytes = null;
		try {
			 input = new FileInputStream("/work/code/pic/3.jpg");
			 
			 try {
				 bytes = ByteStreams.toByteArray(input);
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
				int a = input.available();
				ByteStreams.copy(input, new FileOutputStream("/work/code/pic/test.jpg"));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			bytes = Files.toByteArray(new File("/work/code/pic/3.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteSource source = Files.asByteSource(new File("/work/code/pic/3.jpg"));
		ByteSink sink = Files.asByteSink(new File("/work/code/pic/test1.jpg"), FileWriteMode.APPEND);
		try {
			source.copyTo(sink);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Funnel<Person> personFunnel = new Funnel<Person>() {

			@Override
			public void funnel(Person person, PrimitiveSink into) {
				// TODO Auto-generated method stub
				into
	            .putInt(person.id)
	            .putString(person.firstName, Charsets.UTF_8)
	            .putString(person.lastName, Charsets.UTF_8)
	            .putInt(person.birthYear);
			}
		};
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher()
		        .putLong(1)
		        .putString("test", Charsets.UTF_8)
		        .putObject(new Person(), personFunnel)
		        .hash();
		
	}
	
	static class Person {
	     int id = 1;
	     String firstName = "sua"; 
	     String lastName = "lin";
	     int birthYear = 1917;
	}
}
