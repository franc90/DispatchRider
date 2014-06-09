package benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BenchmarkSaver {
	public static void save(String fileName, CommissionDesc base,
			List<CommissionDesc> comms, int load, int serviceTime)
			throws Exception {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName, false);
			writer.write("25\t200\t1\n");
			StringBuilder builder = new StringBuilder();
			builder.append("0\t");
			builder.append((int) base.getPoint().getX()).append("\t")
					.append((int) base.getPoint().getY()).append("\t");
			builder.append("0\t0\t").append(base.getEndOfTW())
					.append("\t0\t0\t0\n");
			writer.write(builder.toString());
			for (CommissionDesc com : comms) {
				builder = new StringBuilder();
				builder.append(com.getId()).append("\t");
				builder.append((int) com.getPoint().getX()).append("\t")
						.append((int) com.getPoint().getY()).append("\t");
				if (com.isPickup()) {
					builder.append(load).append("\t");
				} else {
					builder.append(-load).append("\t");
				}
				builder.append(com.getBeginOfTW()).append("\t")
						.append(com.getEndOfTW()).append("\t");
				builder.append(serviceTime).append("\t");
				if (com.isPickup()) {
					builder.append("0\t").append(com.getSecondPart().getId());
				} else {
					builder.append(com.getSecondPart().getId()).append("\t0");
				}
				builder.append("\n");
				writer.write(builder.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
