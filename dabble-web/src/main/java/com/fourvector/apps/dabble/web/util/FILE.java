package com.fourvector.apps.dabble.web.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FILE {

	public static String text(Path path) {
		try {
			return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("exception whil reading text from file: " + path);
		}
	}

	public static List<String> lines(Path path) {
		
		return null;
		/*try {
			return Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException("exception whil reading lines from file: " + path);
		}*/
	}

	public static Path getPath(String filePath) {
		try {
			return Paths.get(FILE.class.getResource(filePath).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to find a file at " + filePath);
		}
	}

}
