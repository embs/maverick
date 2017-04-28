package gfads.cin.ufpe.maverick.ltl.core

import gfads.cin.ufpe.maverick.events.MaverickSymptom

/**
 * The implmentation of this interface must be thread-safe!
 * @author adalrjr1
 *
 */
interface TransitionChecker {
	/**
	 * Must be thread-safe
	 * @param transitionFormula
	 * @param symptom
	 * @return
	 */
	boolean check(String transitionFormula, MaverickSymptom symptom)
}