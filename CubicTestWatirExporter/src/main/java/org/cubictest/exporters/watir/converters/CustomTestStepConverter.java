/*******************************************************************************
 * Copyright (c) 2005, 2010  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Watir custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<WatirHolder> {

	public String getDataKey() {
		return "org.cubictest.watirexporter";
	}

	public void handleCustomStep(WatirHolder t, CustomTestStepHolder cts,
			CustomTestStepData data) {
		throw new ExporterException("Custom test steps are not supported by the Watir exporter yet");
	}

}
