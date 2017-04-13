package org.base.io.files;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;

public class FileStreamExample {
	public static void main(String[] args) throws Exception {
        // DataOutputStream装饰FileOutputStream
        FileOutputStream out = new FileOutputStream("e:/io.txt");
        BufferedOutputStream bout = new BufferedOutputStream(out);
        DataOutputStream dout = new DataOutputStream(bout);
        dout.writeByte(-12);
        dout.writeLong(12);
        dout.writeChar('1');
        dout.writeFloat(1.01f);
        dout.writeUTF("你好FileOutputStream和DataOutputStream");
        dout.close();

        FileInputStream in = new FileInputStream("e:/io.txt");
        BufferedInputStream bin = new BufferedInputStream(in);
        DataInputStream din = new DataInputStream(bin);
        System.out.println(din.readByte());
        System.out.println(din.readLong());
        System.out.println(din.readChar());
        System.out.println(din.readFloat());
        System.out.println(din.readUTF());
        din.close();

        // DataOutputStream装饰ByteArrayOutputStream
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF("你好ByteArrayOutputStream和DataOutputStream");
            dos.close();

            byte[] data = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            System.out.println(dis.readUTF());
            dis.close();
        } catch (Exception e) {

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FilterOutputStream stream = new FilterOutputStream(baos);
	}
}
