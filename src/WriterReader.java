import java.io.*;

public class WriterReader {

	// Serialization
	// Save object into a file.
	public static void writeObjectToFile(Model obj, File file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(obj);
			oos.flush();
		}
	}

	// Deserialization
	// Get object from a file.
	public static Model readObjectFromFile(File file) throws IOException, ClassNotFoundException {
		Model result = null;
		try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
			result = (Model) ois.readObject();
		}
		return result;
	}

}