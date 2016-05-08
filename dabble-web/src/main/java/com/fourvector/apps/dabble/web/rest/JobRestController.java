/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IJobService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.web.model.ResponseHolder;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/v0/service/job")
public class JobRestController extends BaseRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JobRestController.class);

	@Autowired
	private IJobService jobService;
	// Constants ----------------------------------------------------------------------------------

	private static final int	DEFAULT_BUFFER_SIZE	= 10240;					// ..bytes = 10KB.
	private static final long	DEFAULT_EXPIRE_TIME	= 604800000L;				// ..ms = 1 week.
	private static final String	MULTIPART_BOUNDARY	= "MULTIPART_BYTERANGES";

	/**
	 * 
	 */
	public JobRestController() {
		super();
	}

	@RequestMapping(value = "{jobId}/resource/{resourceId}/", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void getResource(@PathVariable("jobId") Integer jobId, @PathVariable("resourceId") String resourceId, HttpServletResponse response, HttpServletRequest request) {
		Thread.currentThread().setName("get-resource-for-job#" + jobId);
		LOG.trace("Method [getResource]: Called Data: {}, {}", jobId, resourceId);
		File file = null;
		InputStream is = null;
		try {
			file = jobService.getResource(jobId, resourceId);
			is = FileUtils.openInputStream(file);
			String contentType = HttpURLConnection.guessContentTypeFromStream(FileUtils.openInputStream(file));
			response.setContentType(contentType);
			String extn = FilenameUtils.getExtension(file.getName()).toLowerCase();
			if (extn.contains("png") || extn.contains("jpg") || extn.contains("jpeg")) {
				contentType = "image/" + extn;
				LOG.info("Found Image type");
				IOUtils.copyLarge(is, response.getOutputStream());
				LOG.info("Returning {} Response.", contentType);
				return;
			} else {
				contentType = "video/" + extn;
			}

			// Prepare some variables. The ETag is an unique identifier of the file.
			String fileName = file.getName();
			long length = file.length();
			long lastModified = file.lastModified();
			String eTag = fileName + "_" + length + "_" + lastModified;
			long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

			// Validate request headers for caching ---------------------------------------------------

			// If-None-Match header should contain "*" or ETag. If so, then return 304.
			String ifNoneMatch = request.getHeader("If-None-Match");
			if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", eTag); // Required in 304.
				response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
				return;
			}

			// If-Modified-Since header should be greater than LastModified. If so, then return 304.
			// This header is ignored if any If-None-Match header is specified.
			long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", eTag); // Required in 304.
				response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
				return;
			}

			// Validate request headers for resume ----------------------------------------------------

			// If-Match header should contain "*" or ETag. If not, then return 412.
			String ifMatch = request.getHeader("If-Match");
			if (ifMatch != null && !matches(ifMatch, eTag)) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}

			// If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
			long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
			if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}

			// Validate and process range -------------------------------------------------------------

			// Prepare some variables. The full Range represents the complete file.
			Range full = new Range(0, length - 1, length);
			List<Range> ranges = new ArrayList<Range>();

			// Validate and process Range and If-Range headers.
			String range = request.getHeader("Range");
			if (range != null) {

				// Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
				if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
					response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return;
				}

				// If-Range header should either match ETag or be greater then LastModified. If not,
				// then return full file.
				String ifRange = request.getHeader("If-Range");
				if (ifRange != null && !ifRange.equals(eTag)) {
					try {
						long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
						if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
							ranges.add(full);
						}
					} catch (IllegalArgumentException ignore) {
						ranges.add(full);
					}
				}

				// If any valid If-Range header, then process each part of byte range.
				if (ranges.isEmpty()) {
					for (String part : range.substring(6).split(",")) {
						// Assuming a file with length of 100, the following examples returns bytes at:
						// 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
						long start = sublong(part, 0, part.indexOf("-"));
						long end = sublong(part, part.indexOf("-") + 1, part.length());

						if (start == -1) {
							start = length - end;
							end = length - 1;
						} else if (end == -1 || end > length - 1) {
							end = length - 1;
						}

						// Check if Range is syntactically valid. If not, then return 416.
						if (start > end) {
							response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
							response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							return;
						}

						// Add range.
						ranges.add(new Range(start, end, length));
					}
				}
			}

			// Prepare and initialize response --------------------------------------------------------

			// Get content type by file name and set default GZIP support and content disposition.
			boolean acceptsGzip = false;
			String disposition = "inline";

			// If content type is text, then determine whether GZIP content encoding is supported by
			// the browser and expand content type with the one and right character encoding.
			if (contentType.startsWith("text")) {
				String acceptEncoding = request.getHeader("Accept-Encoding");
				acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
				contentType += ";charset=UTF-8";
			}

			// Else, expect for images, determine content disposition. If content type is supported by
			// the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
			else if (!contentType.startsWith("image")) {
				String accept = request.getHeader("Accept");
				disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
			}

			// Initialize response.
			response.reset();
			response.setBufferSize(DEFAULT_BUFFER_SIZE);
			response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("ETag", eTag);
			response.setDateHeader("Last-Modified", lastModified);
			response.setDateHeader("Expires", expires);

			// Send requested file (part(s)) to client ------------------------------------------------

			// Prepare streams.
			RandomAccessFile input = null;
			OutputStream output = null;

			try {
				// Open streams.
				input = new RandomAccessFile(file, "r");
				output = response.getOutputStream();

				if (ranges.isEmpty() || ranges.get(0) == full) {

					// Return full file.
					Range r = full;
					response.setContentType(contentType);
					response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

					if (acceptsGzip) {
						// The browser accepts GZIP, so GZIP the content.
						response.setHeader("Content-Encoding", "gzip");
						output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
					} else {
						// Content length is not directly predictable in case of GZIP.
						// So only add it if there is no means of GZIP, else browser will hang.
						response.setHeader("Content-Length", String.valueOf(r.length));
					}

					// Copy full range.
					copy(input, output, r.start, r.length);

				} else if (ranges.size() == 1) {

					// Return single part of file.
					Range r = ranges.get(0);
					response.setContentType(contentType);
					response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
					response.setHeader("Content-Length", String.valueOf(r.length));
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

					// Copy single part range.
					copy(input, output, r.start, r.length);

				} else {

					// Return multiple parts of file.
					response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

					// Cast back to ServletOutputStream to get the easy println methods.
					ServletOutputStream sos = (ServletOutputStream) output;

					// Copy multi part range.
					for (Range r : ranges) {
						// Add multipart boundary and header fields for every range.
						sos.println();
						sos.println("--" + MULTIPART_BOUNDARY);
						sos.println("Content-Type: " + contentType);
						sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

						// Copy single part range of multi part range.
						copy(input, output, r.start, r.length);
					}

					// End with multipart boundary.
					sos.println();
					sos.println("--" + MULTIPART_BOUNDARY + "--");
				}
			} finally {
				// Gently close streams.
				close(output);
				close(input);
			}

			IOUtils.copyLarge(is, response.getOutputStream());
		} catch (IOException e) {
			LOG.error("Found exception IOException in method getResource", e);
			IOUtils.closeQuietly(is);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getResource", e);
		}
		LOG.trace("Method [getResource]: Returning.");
	}

	// Helpers (can be refactored to public utility class) ----------------------------------------

	/**
	 * Returns true if the given accept header accepts the given value.
	 * 
	 * @param acceptHeader
	 *            The accept header.
	 * @param toAccept
	 *            The value to be accepted.
	 * @return True if the given accept header accepts the given value.
	 */
	private static boolean accepts(String acceptHeader, String toAccept) {
		String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
		Arrays.sort(acceptValues);
		return Arrays.binarySearch(acceptValues, toAccept) > -1 || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1 || Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	/**
	 * Returns true if the given match header matches the given value.
	 * 
	 * @param matchHeader
	 *            The match header.
	 * @param toMatch
	 *            The value to be matched.
	 * @return True if the given match header matches the given value.
	 */
	private static boolean matches(String matchHeader, String toMatch) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

	/**
	 * Returns a substring of the given string value from the given begin index to the given end
	 * index as a long. If the substring is empty, then -1 will be returned
	 * 
	 * @param value
	 *            The string value to return a substring as long for.
	 * @param beginIndex
	 *            The begin index of the substring to be returned as long.
	 * @param endIndex
	 *            The end index of the substring to be returned as long.
	 * @return A substring of the given string value as long or -1 if substring is empty.
	 */
	private static long sublong(String value, int beginIndex, int endIndex) {
		String substring = value.substring(beginIndex, endIndex);
		return (substring.length() > 0) ? Long.parseLong(substring) : -1;
	}

	/**
	 * Copy the given byte range of the given input to the given output.
	 * 
	 * @param input
	 *            The input to copy the given range to the given output for.
	 * @param output
	 *            The output to copy the given range from the given input for.
	 * @param start
	 *            Start of the byte range.
	 * @param length
	 *            Length of the byte range.
	 * @throws IOException
	 *             If something fails at I/O level.
	 */
	private static void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int read;

		if (input.length() == length) {
			// Write full range.
			while ((read = input.read(buffer)) > 0) {
				output.write(buffer, 0, read);
			}
		} else {
			// Write partial range.
			input.seek(start);
			long toRead = length;

			while ((read = input.read(buffer)) > 0) {
				if ((toRead -= read) > 0) {
					output.write(buffer, 0, read);
				} else {
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	/**
	 * Close the given resource.
	 * 
	 * @param resource
	 *            The resource to be closed.
	 */
	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException ignore) {
				// Ignore IOException. If you want to handle this anyway, it might be useful to know
				// that this will generally only be thrown when the client aborted the request.
			}
		}
	}

	@RequestMapping(value = "/my-jobs/user/{userId}/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getMyJobs(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("get-my-jobs-user#" + userId);
		LOG.trace("Method [getMyJobs]: Called Data: {}", userId);
		ResponseHolder response = null;
		try {
			List<JobDTO> resp = jobService.getMyJobs(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [getMyJobs]: Returning {}", resp.size());
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getMyJobs", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getMyJobs", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getMyJobs]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{jobId}/user/{userId}/edit/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> editJob(@PathVariable("userId") Integer userId, @PathVariable("jobId") Integer jobId, @RequestHeader("payload") String jobDTOPayloadAsJson) {
		Thread.currentThread().setName("edit-job#" + jobId + "-by-user#" + userId);
		LOG.trace("Method [editJob]: Called Data: {Job:{}, user:{}, payload:{}}", jobId, userId, jobDTOPayloadAsJson);
		ResponseHolder response = null;
		try {
			JobDTO jobDTO = gson.fromJson(jobDTOPayloadAsJson, JobDTO.class);
			JobDTO resp = jobService.editJob(userId, jobId, jobDTO);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [editJob]: Returning {}", resp);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method editJob", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method editJob", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [editJob]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{jobId}/resource/{resourceId}/remove/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> removeFile(@PathVariable("resourceId") String resourceId, @PathVariable("jobId") Integer jobId) {
		Thread.currentThread().setName("remove-file-for-job#" + jobId);
		LOG.trace("Method [removeFile]: Called Data: {Job:{}, resource:{}}", jobId, resourceId);
		ResponseHolder response = null;
		try {
			jobService.removeFiles(jobId, resourceId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
			LOG.trace("Method [removeFile]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method removeFile", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method removeFile", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [removeFile]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/create/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> createJob(@PathVariable("userId") Integer userId, @RequestHeader("nonce") String nonce, @RequestHeader("payload") String jobDtoJson, @RequestParam(value = "images", required = false) List<MultipartFile> images, @RequestParam(value = "audios", required = false) List<MultipartFile> audios,
			@RequestParam(value = "videos", required = false) List<MultipartFile> videos) {
		Thread.currentThread().setName("create-job-by-user#" + userId);
		LOG.trace("Method [createJob]: Called (UserId:{}, json:{})", userId, jobDtoJson);
		JobDTO jobDto = gson.fromJson(jobDtoJson, JobDTO.class);
		ResponseHolder response = null;
		try {
			for (MultipartFile video : videos) {
				try {
					jobDto.getVideos().add(jobService.saveVideoFile(video.getInputStream(), video.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method createJob; while saving video files", e);
				}
			}
			for (MultipartFile audio : audios) {
				try {
					jobDto.getAudios().add(jobService.saveAudioFile(audio.getInputStream(), audio.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method createJob; while saving audio files", e);
				}
			}
			for (MultipartFile image : images) {
				try {
					jobDto.getImages().add(jobService.saveImageFile(image.getInputStream(), image.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method createJob; while saving image files", e);
				}
			}
			jobDto.setPosterId(userId);
			Integer result = jobService.createJob(jobDto, nonce);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
			LOG.trace("Method [createJob]: Returning {}", result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method createJob", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method createJob", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [createJob]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{jobId}/add-files/user/{userId}/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> addFilesToJob(@PathVariable("jobId") Integer jobId, @PathVariable Integer userId, @RequestParam(value = "images", required = false) List<MultipartFile> images, @RequestParam(value = "audios", required = false) List<MultipartFile> audios, @RequestParam(value = "videos", required = false) List<MultipartFile> videos) {
		Thread.currentThread().setName("add-files-to-job#" + jobId + "-by-user#" + userId);
		LOG.trace("Method [addFilesToJob]: Called ");
		JobDTO jobDto = new JobDTO();
		ResponseHolder response = null;
		Map<String, List<String>> resp = new LinkedHashMap<>();
		try {
			for (MultipartFile video : videos) {
				try {
					jobDto.getVideos().add(jobService.saveVideoFile(video.getInputStream(), video.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method addFilesToJob; while saving video files", e);
				}
			}
			for (MultipartFile audio : audios) {
				try {
					jobDto.getVideos().add(jobService.saveAudioFile(audio.getInputStream(), audio.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method addFilesToJob; while saving audio files", e);
				}
			}
			for (MultipartFile image : images) {
				try {
					jobDto.getImages().add(jobService.saveImageFile(image.getInputStream(), image.getOriginalFilename()));
				} catch (IOException e) {
					LOG.error("Found IOException in method addFilesToJob; while saving image files", e);
				}
			}
			resp.put("videos", jobDto.getVideos());
			resp.put("audios", jobDto.getAudios());
			resp.put("images", jobDto.getImages());

			jobService.addFiles(jobId, jobDto);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [addFilesToJob]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method addFilesToJob", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method addFilesToJob", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [addFilesToJob]: Returning.");

		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{jobId}/user/{userId}/retract/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> retractJob(@PathVariable("jobId") Integer jobId, @PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("retract-job#" + jobId + "-by-user#" + userId);
		LOG.trace("Method [retractJob]: Called ");
		ResponseHolder response = null;
		try {
			JobDTO result = jobService.retractJob(jobId, userId);
			Object jobList = jobService.getMyJobs(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, jobList);
			LOG.trace("Method [retractJob]: Returning {}", result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method retractJob", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method retractJob", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [retractJob]: Returning.");

		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/list/user/{userId}/range/{range}/q/{searchText}/timezone/{timezone}/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> listJobs(@PathVariable("timezone") String timezone, @PathVariable("userId") Integer userId, @RequestHeader("lng") Double lng, @RequestHeader("lat") Double lat, @PathVariable("searchText") String searchText, @PathVariable("range") Integer range) {
		Thread.currentThread().setName("list-jobs-@(" + lat + "," + lng + ")-range#" + range + "-by-user#" + userId + "-tz-" + timezone);
		LOG.trace("Method [listJobs]: Called location({},{}), user: {}, SearchText: {}, Range: {},timezone: {}", lat, lng, userId, searchText, range, timezone);
		ResponseHolder response = null;
		try {
			if (range == null || range <= 0) {
				range = null;
			}
			if (searchText.trim().equalsIgnoreCase("null")) {
				searchText = null;
				// this is to allow someone to enter a blank value and see results without considering search string.
			}
			List<JobDTO> jobList = jobService.listJobs(userId, lat, lng, range, searchText, timezone);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, jobList);
			LOG.trace("Method [listJobs]: Returning {} elements", jobList.size());
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method listJobs", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method listJobs", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [listJobs]: Returning.");

		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{jobId}/userId/{userId}/feedback-bidder/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> storeFeedbackFromBidder(@PathVariable("jobId") Integer jobId, @PathVariable("userId") Integer userId, @RequestHeader("comment") String comment, @RequestHeader("commentee") Integer commentee, @RequestHeader("rating") Integer rating) {
		Thread.currentThread().setName("bidder-store-feedback-for-seller#" + commentee + "for-job#" + jobId);
		LOG.trace("Method [storeFeedbackFromBidder]: Called user: {}->{}, Job: {}", userId, commentee, jobId);
		ResponseHolder response = null;
		try {
			jobService.storeFeedbackFromBidder(userId, jobId, commentee, comment, rating);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
			LOG.trace("Method [storeFeedbackFromBidder]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method storeFeedbackFromBidder", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method storeFeedbackFromBidder", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [storeFeedbackFromBidder]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{jobId}/userId/{userId}/feedback-seller/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> storeFeedbackFromSeller(@PathVariable("jobId") Integer jobId, @PathVariable("userId") Integer userId, @RequestHeader("comment") String comment, @RequestHeader("commentee") Integer commentee, @RequestHeader("rating") Integer rating) {
		Thread.currentThread().setName("seller-store-feedback-for-bidder#" + commentee + "for-job#" + jobId);
		LOG.trace("Method [storeFeedbackFromSeller]: Called user: {}->{}, Job: {}", userId, commentee, jobId);
		ResponseHolder response = null;
		try {
			jobService.storeFeedbackFromSeller(userId, jobId, commentee, comment, rating);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
			LOG.trace("Method [storeFeedbackFromSeller]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method storeFeedbackFromSeller", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method storeFeedbackFromSeller", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [storeFeedbackFromSeller]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * This class represents a byte range.
	 */
	protected class Range {
		long	start;
		long	end;
		long	length;
		long	total;

		/**
		 * Construct a byte range.
		 * 
		 * @param start
		 *            Start of the byte range.
		 * @param end
		 *            End of the byte range.
		 * @param total
		 *            Total length of the byte source.
		 */
		public Range(long start, long end, long total) {
			this.start = start;
			this.end = end;
			this.length = end - start + 1;
			this.total = total;
		}

	}
}
