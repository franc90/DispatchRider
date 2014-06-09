package machineLearning.aggregator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import measure.Measure;

public class AggregatorsManager implements Serializable {

	private static final long serialVersionUID = 8053493794664528016L;
	private final List<MLAggregator> aggregators = new LinkedList<MLAggregator>();

	public AggregatorsManager() {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			assert classLoader != null;
			String packageName = "machineLearning.aggregator";
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			File dir = new File(resources.nextElement().getFile()
					.replace("%20", " "));
			Class<?> aggregator = null;
			try {
				aggregator = Class.forName(packageName + ".MLAggregator");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			if (dir.canRead()) {
				for (String file : dir.list()) {
					if (file.endsWith(".class")) {
						try {
							Class<?> clazz = Class.forName(packageName + "."
									+ file.substring(0, file.length() - 6));
							if (!clazz.getSuperclass().equals(aggregator))
								continue;
							Object obj = clazz.newInstance();
							if (obj instanceof MLAggregator) {
								aggregators.add((MLAggregator) obj);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				String jarName = dir.getPath();
				jarName = jarName.substring("file:".length(),
						jarName.indexOf(".jar") + 4);
				try {
					for (Class<?> clazz : getClasses(packageName, jarName)) {
						try {
							if (clazz.getSuperclass() == null
									|| !clazz.getSuperclass()
											.equals(aggregator))
								continue;
							Object obj = clazz.newInstance();
							if (obj instanceof MLAggregator) {
								aggregators.add((MLAggregator) obj);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Class> getClasses(String packageName, String jarName)
			throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();

		packageName = packageName.replaceAll("\\.", "/");
		File f = new File(jarName);
		if (f.exists()) {
			try {
				JarInputStream jarFile = new JarInputStream(
						new FileInputStream(jarName));
				JarEntry jarEntry;

				while (true) {
					jarEntry = jarFile.getNextJarEntry();
					if (jarEntry == null) {
						break;
					}
					if ((jarEntry.getName().startsWith(packageName))
							&& (jarEntry.getName().endsWith(".class"))) {
						classes.add(Class.forName(jarEntry.getName()
								.replaceAll("/", "\\.")
								.substring(0, jarEntry.getName().length() - 6)));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return classes;
		} else
			return null;
	}

	public void setMeasures(Map<String, Measure> measures) {
		for (MLAggregator aggregator : aggregators)
			aggregator.setMeasures(measures);
	}

	public void aggregationFinished() {
		for (MLAggregator aggregator : aggregators)
			aggregator.aggregationFinished();
	}

	public String insertAggregateValues(String fun) {
		for (MLAggregator aggregator : aggregators)
			fun = aggregator.replace(fun);
		return fun;
	}

	public List<MLAggregator> getAggregators() {
		return aggregators;
	}

}
