/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

/**
 * @author asharma
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fourvector.apps.dabble.common.dto.AddressDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Controller
@RequestMapping("/v0/service")
public class RestWadlController extends BaseRestController {

	private String							jsonString;
	@Autowired
	private RequestMappingHandlerMapping	handlerMapping;
	@Autowired
	private ApplicationContext				webApplicationContext;
	private static final String				SERVICE_PATH	= "/wadl/json/";

	@RequestMapping(value = SERVICE_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String generateJsonWadl(HttpServletRequest request) {
		if (jsonString == null) {
			JsonObject json = new JsonObject();
			json.addProperty("description", "Restful Service Definitions");
			json.addProperty("baseUrl", getBaseUrl(request));
			Map<String, JsonArray> serviceClasses = new LinkedHashMap<>();
			Map<RequestMappingInfo, HandlerMethod> handletMethods = handlerMapping.getHandlerMethods();
			for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handletMethods.entrySet()) {

				RequestMappingInfo mappingInfo = entry.getKey();
				HandlerMethod handlerMethod = entry.getValue();

				Object object = handlerMethod.getBean();
				Object bean = webApplicationContext.getBean(object.toString());

				boolean isRestContoller = bean.getClass().isAnnotationPresent(RestController.class);
				if (!isRestContoller) {
					continue;
				}
				String serviceName = bean.getClass().getSimpleName();
				RequestMapping requestMapping = bean.getClass().getAnnotation(RequestMapping.class);
				String baseUrl = requestMapping.value()[0];
				Set<RequestMethod> httpMethods = mappingInfo.getMethodsCondition().getMethods();
				for (RequestMethod httpMethod : httpMethods) {
					JsonObject httpRequestMethod = new JsonObject();
					Method javaMethod = handlerMethod.getMethod();

					for (Annotation annotation : javaMethod.getAnnotations()) {
						if (annotation instanceof RequestMapping) {
							RequestMapping rm = (RequestMapping) annotation;
							List<String> supportedHttpMethods = new ArrayList<>();
							for (RequestMethod reqMethod : rm.method()) {
								supportedHttpMethods.add(reqMethod.name());
							}
							httpRequestMethod.addProperty("method", gson.toJsonTree(supportedHttpMethods.toArray()).getAsString());
							httpRequestMethod.addProperty("path", baseUrl + rm.value()[0]);
							httpRequestMethod.add("produces", cleanArray(rm.produces()));
							httpRequestMethod.add("consumes", cleanArray(rm.consumes()));
						}
					}
					Annotation[][] parameterAnnotations = javaMethod.getParameterAnnotations();
					Class<?>[] paramTypes = javaMethod.getParameterTypes();
					JsonArray paramsArray = new JsonArray();
					for (int i = 0; i < parameterAnnotations.length; i++) {
						Annotation[] parameterAnnotation = parameterAnnotations[i];
						Class<?> paramType = paramTypes[i];
						for (Annotation annotation2 : parameterAnnotation) {
							JsonObject param = new JsonObject();
							if (annotation2 instanceof RequestParam) {
								RequestParam param2 = (RequestParam) annotation2;
								param.addProperty("name", param2.value());
								param.addProperty("location", "RequestBody");
								param.addProperty("mandatory", param2.required());
								param.addProperty("type", paramType.getSimpleName());
							}
							if (annotation2 instanceof PathVariable) {
								PathVariable param2 = (PathVariable) annotation2;
								param.addProperty("name", param2.value());
								param.addProperty("location", "QueryString");
								param.addProperty("mandatory", true);
								param.addProperty("type", paramType.getSimpleName());
							}
							if (annotation2 instanceof RequestHeader) {
								RequestHeader param2 = (RequestHeader) annotation2;
								param.addProperty("name", param2.value());
								param.addProperty("location", "RequestHeader");
								param.addProperty("mandatory", true);
								param.addProperty("type", paramType.getSimpleName());
							}
							paramsArray.add(param);
						}
						httpRequestMethod.add("params", paramsArray);
					}
					JsonArray array = serviceClasses.get(serviceName);
					if (array == null) {
						array = new JsonArray();
					}
					array.add(httpRequestMethod);
					serviceClasses.put(serviceName, array); // why not!
				}

			}

			for (Map.Entry<String, JsonArray> entry : serviceClasses.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}
			json.add("userDTO", gson.toJsonTree(new UserDTO()));
			json.add("addressDTO", gson.toJsonTree(new AddressDTO()));
			json.add("jobDTO", gson.toJsonTree(new JobDTO()));
			json.add("categoryDTO", gson.toJsonTree(new SubscriptionDTO()));
			jsonString = gson.toJson(json);
		}
		return jsonString;
	}

	private JsonArray cleanArray(String[] array) {
		JsonArray result = new JsonArray();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				result.add(new JsonPrimitive(array[i].trim()));
			}
		}
		return result;
	}

	private String getBaseUrl(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + requestUri.replace(SERVICE_PATH, "");
	}
}