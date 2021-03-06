/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.config.core.ConfigDescription;
import org.eclipse.smarthome.config.core.ConfigDescriptionParameter;
import org.eclipse.smarthome.config.xml.util.ConverterAssertion;
import org.eclipse.smarthome.config.xml.util.ConverterAttributeMapValidator;
import org.eclipse.smarthome.config.xml.util.GenericUnmarshaller;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


/**
 * The {@link ConfigDescriptionConverter} is a concrete implementation of the {@code XStream}
 * {@link Converter} interface used to convert config description information within an XML
 * document into a {@link ConfigDescription} object.
 * <p>
 * This converter converts {@code config-description} XML tags.
 *
 * @author Michael Grammling - Initial Contribution
 */
public class ConfigDescriptionConverter extends GenericUnmarshaller<ConfigDescription> {

    private ConverterAttributeMapValidator attributeMapValidator;


    public ConfigDescriptionConverter() {
        super(ConfigDescription.class);

        this.attributeMapValidator = new ConverterAttributeMapValidator(new String[][] {
                { "uri", "false" }});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ConfigDescription configDescription = null;

        // read attributes
        Map<String, String> attributes = this.attributeMapValidator.readValidatedAttributes(reader);
        String uriText = attributes.get("uri");
        if (uriText == null) {
            // the URI could be overridden by a context field if it could be automatically extracted
            uriText = (String) context.get("config-description.uri");
        }

        URI uri = null;
        try {
            uri = new URI(uriText);
        } catch (NullPointerException | URISyntaxException ex) {
            throw new ConversionException("The URI '" + uriText + "' in node '"
                    + reader.getNodeName() + "' is invalid!", ex);
        }

        // read values
        List<ConfigDescriptionParameter> configDescriptionParams =
                (List<ConfigDescriptionParameter>) context.convertAnother(context, List.class);

        ConverterAssertion.assertEndOfType(reader);

        // create object
        configDescription = new ConfigDescription(uri, configDescriptionParams);

        return configDescription;
    }

}
