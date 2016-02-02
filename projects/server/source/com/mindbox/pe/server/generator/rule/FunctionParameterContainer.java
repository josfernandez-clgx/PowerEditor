/**
 * 
 */
package com.mindbox.pe.server.generator.rule;

import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;

interface FunctionParameterContainer {

	FunctionParameter getParameterAt(int paramNo);

	FunctionTypeDefinition getFunctionTypeDefinition();
}