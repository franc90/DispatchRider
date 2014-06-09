package machineLearning;

public class MLAlgorithmFactory {

	@SuppressWarnings("rawtypes")
	public static MLAlgorithm createAlgorithm(String name) {
		try {
			String packageName = "machineLearning.";
			Class clazz = Class.forName(packageName + name.toLowerCase() + "."
					+ name);
			return (MLAlgorithm) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
}
