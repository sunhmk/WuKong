package org.base.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class Collections {
	public static final ImmutableSet<String> COLOR_NAMES = ImmutableSet.of(
			"red", "orange", "yellow", "green", "blue", "purple");

	class Foo {
		Set<String> bars;

		Foo(Set<String> bars) {
			this.bars = ImmutableSet.copyOf(bars); // defensive copy!
		}
	}

	/*
	 * copyOf方法，如ImmutableSet.copyOf(set); of方法，如ImmutableSet.of(“a”, “b”, “c”)或
	 * ImmutableMap.of(“a”, 1, “b”, 2); Builder工具，如 1 public static final
	 * ImmutableSet<Color> GOOGLE_COLORS = 2 ImmutableSet.<Color>builder() 3
	 * .addAll(WEBSAFE_COLORS) 4 .add(new Color(0, 191, 255)) 5 .build();
	 */

	public static void main(String[] args) {
		Map<String, Integer> counts = new HashMap<String, Integer>();

		for (String word : COLOR_NAMES) {
			Integer count = counts.get(word);
			if (count == null) {
				counts.put(word, 1);
			} else {
				counts.put(word, count + 1);
			}
		}
		String strWorld = "wer|dffd|ddsa|dfd|dreg|de|dr|ce|ghrt|cf|gt|ser|tg|ghrt|cf|gt|"
				+ "ser|tg|gt|kldf|dfg|vcd|fg|gt|ls|lser|dfr|wer|dffd|ddsa|dfd|dreg|de|dr|"
				+ "ce|ghrt|cf|gt|ser|tg|gt|kldf|dfg|vcd|fg|gt|ls|lser|dfr";
		String[] words = strWorld.split("\\|");

		List<String> wordList = new ArrayList<String>();
		for (String word : words) {
			wordList.add(word);
		}

		// 将数据集添加到Multiset中
		Multiset<String> wordsMultiset = HashMultiset.create();
		wordsMultiset.addAll(wordList);

		// elementSet(): 将不同的元素放入一个Set中
		// count(Object element)：返回给定参数元素的个数
		for (String key : wordsMultiset.elementSet()) {
			System.out.println(key + " count：" + wordsMultiset.count(key));
		}
		Iterator<String> itr = wordsMultiset.iterator();
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}

		Map<String, Integer> nameToId = Maps.newHashMap();
		Map<Integer, String> idToName = Maps.newHashMap();

		nameToId.put("Bob", 42);
		idToName.put(42, "Bob");
		// 如果"Bob"和42已经在map中了，会发生什么?
		// 如果我们忘了同步两个map，会有诡异的bug发生...

		List<String> list = Lists.newArrayList();
		Map<String, Integer> map = Maps.newLinkedHashMap();
		Set<String> copySet = Sets.newHashSet(wordsMultiset);
		List<String> theseElements = Lists.newArrayList("alpha", "beta",
				"gamma");
		/*
		 * List<Type> exactly100 = Lists.newArrayListWithCapacity(100);
		 * List<Type> approx100 = Lists.newArrayListWithExpectedSize(100);
		 * Set<Type> approx100Set = Sets.newHashSetWithExpectedSize(100);
		 */
		Multiset<String> multiset = HashMultiset.create();
		Iterable<Integer> concatenated = Iterables.concat(Ints.asList(1, 2, 3),
				Ints.asList(4, 5, 6)); // concatenated包括元素 1, 2, 3, 4, 5, 6
		String lastAdded = Iterables.getLast(theseElements);
		String theElement = Iterables.getOnlyElement(theseElements);
		// 如果set不是单元素集，就会出错了！
		List<?> countUp = Ints.asList(1, 2, 3, 4, 5);
		List<?> countDown = Lists.reverse(countUp); // {5, 4, 3, 2, 1}
		List<?> parts = Lists.partition(countUp, 2);// {{1,2}, {3,4}, {5}}
		Set<String> wordsWithPrimeLength = ImmutableSet.of("one", "two",
				"three", "six", "seven", "eight");
		Set<String> primes = ImmutableSet.of("two", "three", "five", "seven");
		SetView<String> intersection = Sets.intersection(primes,
				wordsWithPrimeLength);
		// intersection包含"two", "three", "seven"
		intersection.immutableCopy();// 可以使用交集，但不可变拷贝的读取效率更高

		// 有些键不需要刷新，并且我们希望刷新是异步完成的
		/*
		 * LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
		 * .maximumSize(1000) .refreshAfterWrite(1, TimeUnit.MINUTES) .build(
		 * new CacheLoader<Key, Graph>() { public Graph load(Key key) { // no
		 * checked exception return getGraphFromDatabase(key); }
		 * 
		 * public ListenableFuture<Key, Graph> reload(final Key key, Graph
		 * prevGraph) { if (neverNeedsRefresh(key)) { return
		 * Futures.immediateFuture(prevGraph); }else{ // asynchronous!
		 * ListenableFutureTask<Key, Graph> task=ListenableFutureTask.create(new
		 * Callable<Key, Graph>() { public Graph call() { return
		 * getGraphFromDatabase(key); } }); executor.execute(task); return task;
		 * } } });
		 */

		Cache<String, Boolean> memstoreReplicationEnabled = CacheBuilder
				.newBuilder().expireAfterWrite(5000, TimeUnit.MILLISECONDS)
				.initialCapacity(10).maximumSize(1000).build();
		memstoreReplicationEnabled.put("String1", true);
		memstoreReplicationEnabled.put("String2", false);
		memstoreReplicationEnabled.asMap().containsValue("String1");
		CacheLoader<String, Integer> loader = new CacheLoader<String, Integer>() {

			public ListenableFuture<Integer> reload(final String hri,
					Integer oldValue) throws Exception {
				return MoreExecutors.newDirectExecutorService().submit(
						new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return 0;
							}
						});
			}

			@Override
			public Integer load(String key) throws Exception {
				return 0;
			}
		};
		LoadingCache<String, Integer> cache = CacheBuilder.newBuilder()
				.expireAfterWrite(5000, TimeUnit.MILLISECONDS).build(loader);
		/*
		 * List<String> names; Map<String, Person> personWithName; List<Person>
		 * people = Lists.transform(names, Functions.forMap(personWithName));
		 * 
		 * ListMultimap<String, String> firstNameToLastNames; // maps first
		 * names to all last names of people with that first name
		 * 
		 * ListMultimap<String, String> firstNameToName =
		 * Multimaps.transformEntries(firstNameToLastNames, new
		 * EntryTransformer<String, String, String> () { public String
		 * transformEntry(String firstName, String lastName) { return firstName
		 * + " " + lastName; } });
		 */

		ListeningExecutorService service = MoreExecutors
				.listeningDecorator(Executors.newFixedThreadPool(10));
		ListenableFuture<String> explosion = service
				.submit(new Callable<String>() {
					public String call() {
						return "";
					}
				});
		Futures.addCallback(explosion, new FutureCallback<Object>() {
			public void onFailure(Throwable thrown) {

			}

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub

			}
		});
		Joiner joiner = Joiner.on("; ").skipNulls();
		joiner.join("Harry", null, "Ron", "Hermione");
		Joiner.on(",").join(Arrays.asList(1, 5, 7)); // returns "1,5,7"
		Splitter.on(',').trimResults().omitEmptyStrings()
				.split("foo,bar,,   qux");
		/*String noControl = CharMatcher.JAVA_ISO_CONTROL.removeFrom(string); //移除control字符
		String theDigits = CharMatcher.DIGIT.retainFrom(string); //只保留数字字符
		String spaced = CharMatcher.WHITESPACE.trimAndCollapseFrom(string, ' ');
		//去除两端的空格，并把中间的连续空格替换成单个空格
		String noDigits = CharMatcher.JAVA_DIGIT.replaceFrom(string, "*"); //用*号替换所有数字
		String lowerAndDigit = CharMatcher.JAVA_DIGIT.or(CharMatcher.JAVA_LOWER_CASE).retainFrom(string);
		// 只保留数字和小写字母
		 * 
		 */
			//是class与实例map
		   final ClassToInstanceMap<Myclass> interfaceToProxyCache;
		   interfaceToProxyCache = MutableClassToInstanceMap.create();
		   interfaceToProxyCache.putInstance(Myclass.class, new Myclass());
		
	}
	public static class Myclass{
		
	}
}
