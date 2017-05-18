package org.base.spark.maptest
import org.base.spark.map.AppendOnlyMap
import java.util.Comparator

object MapTest {
  def main(argStrings: Array[String]) {
    val map = new AppendOnlyMap[String, String]()
    for (i <- 1 to 100) {
      map("" + i) = "" + i
    }
    assert(map.size == 100)
    for (i <- 1 to 100) {
      val res = map.changeValue("" + i, (hadValue, oldValue) => {
        assert(hadValue == true)
        assert(oldValue == "" + i)
        oldValue + "!"
      })
      assert(res == i + "!")
    }
    // Iterate from 101 to 400 to make sure the map grows a couple of times, because we had a
    // bug where changeValue would return the wrong result when the map grew on that insert
    for (i <- 101 to 400) {
      val res = map.changeValue("" + i, (hadValue, oldValue) => {
        assert(hadValue == false)
        i + "!"
      })
      assert(res == i + "!")
    }
    
    for(i<-1 to 400)
    {
      System.out.println(map("" + i));
    }
    val it = map.destructiveSortedIterator(new Comparator[String] {
      def compare(key1: String, key2: String): Int = {
        val x = if (key1 != null) key1.toInt else Int.MinValue
        val y = if (key2 != null) key2.toInt else Int.MinValue
        x.compareTo(y)
      }
    })
    while(it.hasNext)
    {
      System.out.println(it.next())
    }
  }
}