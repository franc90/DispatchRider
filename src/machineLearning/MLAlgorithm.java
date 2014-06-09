package machineLearning;

import jade.core.AID;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorsHolder;
import measure.configuration.GlobalConfiguration;
import measure.configuration.HolonConfiguration;
import algorithm.Schedule;
import dtp.jade.algorithm.agent.AlgorithmAgentParent;
import dtp.simmulation.SimInfo;

/**
 * Base class for all machine learning algorithms. To add new algorithm, you
 * have to create new class, which: extends this class, will be created in
 * package 'machinelearning.<your class name (down case)>'. Then you can use it
 * in configuration file (mlAlgorithm element), by setting value of 'algorithm'
 * attribute to the name of your class (it is case sensitive!)
 * 
 */
public abstract class MLAlgorithm extends MeasureCalculatorsHolder implements
		Serializable {

	private static final long serialVersionUID = 137201215927440304L;

	@SuppressWarnings("rawtypes")
	public MLAlgorithm() {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			assert classLoader != null;
			String packageName = "measure";
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			File dir = new File(resources.nextElement().getFile()
					.replace("%20", " "));
			Class measureCalculator = null;
			try {
				measureCalculator = Class.forName(packageName
						+ ".MeasureCalculator");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			if (dir.canRead()) {
				for (String file : dir.list()) {
					if (file.endsWith(".class")) {
						try {
							Class clazz = Class.forName(packageName + "."
									+ file.substring(0, file.length() - 6));
							if (!clazz.getSuperclass()
									.equals(measureCalculator))
								continue;
							Object obj = clazz.newInstance();
							if (obj instanceof MeasureCalculator) {
								addCalculator(file.substring(0,
										file.length() - 6));
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
					for (Class clazz : getClasses(packageName, jarName)) {
						try {
							if (clazz.getSuperclass() == null
									|| !clazz.getSuperclass().equals(
											measureCalculator))
								continue;
							Object obj = clazz.newInstance();
							if (obj instanceof MeasureCalculator) {
								addCalculator(clazz.getName().substring(
										packageName.length() + 1));
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

	protected Map<String, Measure> calculateMeasures(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			AlgorithmAgentParent agent) {
		if (newSchedules != null) {
			for (AID key : oldSchedules.keySet()) {
				if (newSchedules.get(key) == null)
					newSchedules.put(key, oldSchedules.get(key));
			}
		}
		Map<String, Measure> measures = new HashMap<String, Measure>();
		for (MeasureCalculator calc : getCalculators()) {
			measures.put(calc.getName(),
					calc.calculateMeasure(oldSchedules, newSchedules, agent));
		}
		return measures;
	}

	/**
	 * @param newSchedule
	 *            can be null - if STLike algorithm isn't invoked
	 */
	public abstract GlobalConfiguration getGlobalConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent);

	/**
	 * @param newSchedule
	 *            can be null - if STLike algorithm isn't invoked
	 */
	public abstract Map<AID, HolonConfiguration> getHolonsConfiguration(
			Map<AID, Schedule> oldSchedules, Map<AID, Schedule> newSchedules,
			SimInfo info, boolean exploration, AlgorithmAgentParent agent);

	/**
	 * @param parameters
	 *            - contains parameters set in test configuration file (element
	 *            param). You are responsible for parsing (casting) them
	 */
	public abstract void setAlgorithmParameters(Map<String, String> parameters);

	/**
	 * This method is invoked after simulation. You should save machine learning
	 * knowledge representation (for example QLearinng table)
	 * 
	 * @param fileName
	 *            - file from which knowledge representation was read
	 * @param saveFileName
	 *            - simulation result file name (you have to change it, for
	 *            example by add ending string)
	 * @throws Exception
	 */
	public abstract void save(String fileName, String saveFileName)
			throws Exception;

	/**
	 * You should read knowledge representation here
	 */
	public abstract void init(String fileName);
}
