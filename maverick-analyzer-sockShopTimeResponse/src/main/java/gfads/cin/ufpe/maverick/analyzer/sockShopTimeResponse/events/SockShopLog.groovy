package gfads.cin.ufpe.maverick.analyzer.sockShopTimeResponse.events

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Map
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap.MapEntry

import com.google.common.base.Strings 

import gfads.cin.ufpe.maverick.events.MaverickSymptom
import groovy.json.JsonException
import groovy.json.JsonSlurper

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class SockShopLog {
	@JsonProperty("timestamp")
	private long timestamp = -1L
	@JsonProperty("logLevel")
	private String logLevel = ""
	@JsonProperty("className")
	private String className = ""
	
	@JsonProperty("text")
	private String text = ""
	
	public SockShopLog(String text) {
		this.text = text
		splitLogMessage(this.text)
		
	}
	
	private def splitLogMessage(String text) {
		if(Strings.isNullOrEmpty(text)) {
			return
		}
		
		int count = 1
		String aux = ""
		def result = text.split(/\s+/).each { token ->
			String s = sanitizeColor(token)
			if(isDate(s)) {
				aux += s + " "
			}
			else if(isLogLevel(s)) {
				logLevel = s
			}
			else if(isClassName(s)) {
				className = s
			}
		}
		timestamp = dateStrToLong(aux)
		return result
	}
	
	private String sanitizeColor(String token) {
		String[] s = token.split(/(\u001b\[[0-9]+m){1}/)
		return s.length > 1 ? s[1] : s[0] 
	}
	
	private boolean isDate(String token) {
		def date = token ==~ /([0-9]{4}(-[0-9]{2}){2})+\s*/
		def time = token ==~ /\s*([0-9]{2}(:[0-9]{2}){2}(\.[0-9]+)?)+\s*/
		def total = token ==~ /[0-9]{4}(-[0-9]{2}){2} [0-9]{2}(:[0-9]{2}){2}\.[0-9]+\s*/
		return date || time || total
	}
	
	private boolean isLogLevel(String token) {
		token.matches(/\s*(ERROR|INFO|DEBUG|WARN|FATAL|TRACE)\s*/)
	}
	
	private boolean isJson(String token) {
		JsonSlurper json = new JsonSlurper()
		try {
			json.parseText(token) != null
		}
		catch(JsonException e) {
			return false
		}
	}
	
	private boolean isClassName(String token) {
		token.matches(/\s*(\w\.)+\w+\s*/)
	}
	
	private Long dateStrToLong(String token) {
		if(Strings.isNullOrEmpty(token)) {
			return -1L
		}
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
		
		DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
		
		String dateStr = localFormat.format(utcFormat.parse(token))
		localFormat.parse(dateStr).getTime()
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public String getClassName() {
		return className;
	}

	public String getText() {
		return text;
	}

	public Map getLogAsMap() {
		this.class.declaredFields.findAll { !it.synthetic }.inject([:]) { result, entry ->
			result[ (entry.name) ] = this."$entry.name" 
			result
	    }
	}
	
	public def get(String property) {
		def methods = this.getClass().getMethods()
		Method method = methods.find { m ->
			m.getName().equalsIgnoreCase("get"+property)
		}
		
		if(Objects.nonNull(method)) {
			try {
				return method.invoke(this, null)
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace()
			}
		}
		
		return null
	}

	@Override
	public String toString() {
		return "SockShopLog [timestamp=" + timestamp + ", logLevel=" + logLevel + ", className=" + className + ", text="
				+ text + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(timestamp, className, logLevel, text);
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true
		if(! (o instanceof SockShopLog)) return false
		
		SockShopLog sockShopLog = (SockShopLog) o
		
		Objects.equals(timestamp, sockShopLog.timestamp) &&
	    Objects.equals(className, sockShopLog.className) &&
		Objects.equals(logLevel, sockShopLog.logLevel) &&
		Objects.equals(text, sockShopLog.text)
	}
}
