package gfads.cin.ufpe.maverick.ltl.core

import jhoafparser.parser.HOAFParser

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import jhoafparser.consumer.HOAConsumerStore
import jhoafparser.storage.StoredAutomaton

class LabeledTransitionSystemFactory {

	private static Logger LOG = LoggerFactory.getLogger(LabeledTransitionSystemFactory.class)
	
	public static LabeledTransitionSystem create(String ltlProperty, TransitionChecker checker) {
		Process process = isWin() ? createProcessWin(ltlProperty) : createProcessLinux(ltlProperty)
		
		PipedInputStream input = new PipedInputStream()
		PipedOutputStream out = new PipedOutputStream(input)
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream()
		
		process.waitForProcessOutput(out, outStream)
		// logging error to create automaton
		if(outStream.size() > 0) {
			LOG.error(outStream.toString().replaceAll("\n",""))
		} 
		
		StoredAutomaton storedAutomaton = createAutomaton(input)
		LabeledTransitionSystem lts = LabeledTransitionSystemImpl.getInstance()
		lts.init(storedAutomaton, checker)
		return lts
	}
	
	private static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0
	}
	
	private static Process createProcessWin(String property) {
		return new ProcessBuilder("bash", "-c", "ltl2tgba -B -D --lenient \'${property}\'").start()
	}
	
	private static Process createProcessLinux(String property) {
		return new ProcessBuilder("ltl2tgba", "-B", "-D", "--lenient", property).start()
	}
	
	private static StoredAutomaton createAutomaton(InputStream inputStream) {
		if(!inputStream.available()) {
			throw new RuntimeException("Impossible to create a StoredAutomaton -- HOA string is empty")
		}
		
		HOAConsumerStore consumerStore = new HOAConsumerStore()
		HOAFParser.parseHOA(inputStream, consumerStore)
		StoredAutomaton storedAutomaton = consumerStore.getStoredAutomaton()
		
		return storedAutomaton
	}
}