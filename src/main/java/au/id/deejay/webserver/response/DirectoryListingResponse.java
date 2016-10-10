package au.id.deejay.webserver.response;

import au.id.deejay.webserver.api.HttpStatus;
import au.id.deejay.webserver.api.HttpVersion;
import au.id.deejay.webserver.exception.ResponseException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author David Jessup
 */
public class DirectoryListingResponse extends HttpResponse {

	private final File directory;
	private String html;

	public DirectoryListingResponse(File directory, HttpVersion version) {
		super(HttpStatus.OK_200, version);
		this.directory = directory;

		if (!directory.canRead()) {
			throw new ResponseException("Requested directory does not exist or cannot be read: " + directory.getPath());
		}

		html = listing();
		headers().set("Content-length", String.valueOf(html.length()));
		headers().set("Content-type", "text/html");
	}

	private String listing() {

		StringBuilder html = new StringBuilder();

		html.append("<h1>Directory listing for: ")
			.append(directory.getPath())
			.append("</h1>")
			.append("<ol>");

		listFile(html, "[ Up ]", "../");
		for (File file : directory.listFiles()) {
			listFile(html, file.getName(), file.getName() + (file.isDirectory() ? "/" : ""));
		}

		html.append("</ol>");

		return html.toString();
	}

	private void listFile(StringBuilder html, String name, String path) {
		html.append("<li><a href=\"./")
			.append(path)
			.append("\">")
			.append(name)
			.append("</a></li>");
	}

	@Override
	public InputStream stream() {
		return new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
	}
}