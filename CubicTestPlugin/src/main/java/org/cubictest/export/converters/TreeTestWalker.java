/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.export.converters;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.exception.UnknownExtensionPointException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.utils.TestWalkerUtils;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;

/**
 * Uses a connection point converter, a page converter and a transition
 * converter to convert a test into e.g. test steps.
 * 
 * @author SK
 * @author chr_schwarz
 * 
 */
public class TreeTestWalker<T> {

	private List<TransitionNode> convertedNodes = new ArrayList<TransitionNode>();

	private Class<? extends IUrlStartPointConverter<T>> urlStartPointConverter;

	private PageWalker<T> pageWalker;

	private Class<? extends ITransitionConverter<T>> transitionConverter;

	private Class<? extends ICustomTestStepConverter<T>> customTestStepConverter;

	
	/**
	 * Public constructor.
	 */
	public TreeTestWalker(Class<? extends IUrlStartPointConverter<T>> urlSpc,
			Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc,
			Class<? extends ITransitionConverter<T>> tc,
			Class<? extends ICustomTestStepConverter<T>> ctsc) {
		this.urlStartPointConverter = urlSpc;
		this.pageWalker = new PageWalker<T>(pec,cc);
		;
		this.transitionConverter = tc;
		this.customTestStepConverter = ctsc;
	}

	
	/**
	 * Convert a test to test steps and populate steps into e.g. a step list.
	 * Converts all paths in test (tree).
	 * 
	 * @param test
	 * @param t
	 */
	public void convertTest(Test test, T t) {
		
		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.
		
		try {
			//be sure to cover all paths:
			for (int path = 0; path < 42; path++) {
				convertTransitionNode(t, test.getStartPoint(), null);
			}
		} 
		catch (IllegalAccessException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		catch (InstantiationException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	
	
	/**
	 * Converts test node and its children. If targetExtensionPoint is non-null,
	 * only converts test on path to that ExtensionPoint. If node is subtest,
	 * only convert the used path in the subtest (the path leading to the
	 * extension point that is extended from)
	 * 
	 * @param t
	 * @param node
	 * @param targetExtensionPoint
	 *            if non-null, only convert node on path to this ExtensionPoint.
	 */
	protected boolean convertTransitionNode(T t, TransitionNode node, ConnectionPoint targetExtensionPoint)
			throws InstantiationException, IllegalAccessException {

		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.

		
		boolean nodeFinished = true; //init

		if (convertedNodes.contains(node)) {
			return true;
		}

		if (targetExtensionPoint == null || TestWalkerUtils.isOnExtensionPointPath(node, targetExtensionPoint)) {

			if (node instanceof UrlStartPoint) {
				urlStartPointConverter.newInstance().handleUrlStartPoint(t,
						(UrlStartPoint) node);
			} 
			else if (node instanceof ExtensionStartPoint) {
				convertTransitionNode(t, (((SubTest) node).getTest()).getStartPoint(), (ExtensionStartPoint) node);
			} 
			else if (node instanceof SubTest) {
				Test test = ((SubTest) node).getTest();
				List<Transition> out = node.getOutTransitions();
				// only convert path in subtest leading to the extension
				// point that is extended from
				convertTransitionNode(t, test.getStartPoint(), ((ExtensionTransition) out.get(0)).getExtensionPoint());
			} 
			else if (node instanceof Page) {
				pageWalker.handlePage(t, (Page) node);
			} 
			else if (node instanceof CustomTestStep) {
				customTestStepConverter.newInstance().handleCustomStep(t,
						(CustomTestStep) node);
			}
			else if (node instanceof ExtensionPoint) {
				//do nothing
			} 
			else {
				ErrorHandler.logAndShowErrorDialogAndThrow("Encountered unknown node type during export: " + node);
			}

			int pathNum = 0;
			for (Transition outTransition : node.getOutTransitions()) {
				pathNum++;
				TransitionNode endNode = (TransitionNode) outTransition.getEnd();

				if (convertedNodes.contains(endNode)) {
					continue;
				}
				else {
					if (targetExtensionPoint == null || TestWalkerUtils.isOnExtensionPointPath(endNode, targetExtensionPoint)) {
						if (outTransition instanceof UserInteractionsTransition) {
							transitionConverter.newInstance().handleUserInteractions(t,(UserInteractionsTransition) outTransition);
						}
						else {
							//only follow connection, no export
						}
						
						// convert end node recursively: 
						nodeFinished = convertTransitionNode(t, endNode, targetExtensionPoint);

						if (pathNum < node.getOutTransitions().size()) {
							//end node converted, skip to root of tree to start traversal of the other paths from there.
							nodeFinished = false;
							break;
						}
					}
				}
				
			}
			if (targetExtensionPoint != null) {
				//there possibly exists a tree test *after* this test, we do not know whether we are finished
				nodeFinished = false;
			}
			
			if (nodeFinished) {
				convertedNodes.add(node);
			}
			
		}
		else {
			String msg = "Target extension point connected to page not present in test: " + node + ", " + targetExtensionPoint;
			ErrorHandler.logAndShowErrorDialogAndRethrow(new UnknownExtensionPointException(msg), msg);;
		}
		
		return nodeFinished;
	}

	
}
