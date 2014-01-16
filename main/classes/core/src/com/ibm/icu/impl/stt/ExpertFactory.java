/*
 *******************************************************************************
 *   Copyright (C) 2001-2014, International Business Machines
 *   Corporation and others.  All Rights Reserved.
 *******************************************************************************
 */

package com.ibm.icu.impl.stt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.icu.impl.stt.handlers.TypeHandler;
import com.ibm.icu.text.BidiStructuredProcessor;

/**
 * Obtains Expert instances. An {@link Expert} instance (called in short an
 * "expert") provides the advanced methods to process a certain type of
 * structured text, and is thus related to a specific {@link TypeHandler
 * structured text type handler}. There are two kinds of experts:
 * <ul>
 * <li>stateful, obtained by calling {@link #getStatefulExpert}.</li>
 * <li>not stateful, obtained by calling {@link #getExpert}.</li>
 * </ul>
 * <p>
 * Only the stateful kind can remember the state established by a call to a text
 * processing method and transmit it as initial state in the next call to a text
 * processing method.
 * <p>
 * In other words, the methods {@link Expert#getState()},
 * {@link Expert#setState} and {@link Expert#clearState()} of {@link Expert} are
 * inoperative for experts which are not stateful.
 * <p>
 * Using a stateful expert is more resource intensive, thus not stateful experts
 * should be used when feasible.
 * 
 * @author Matitiahu Allouche, updated by Lina Kemmel
 * 
 */
final public class ExpertFactory {

    /**
     * The default set of separators used to segment a string: dot, colon,
     * slash, backslash.
     */
    private static final String defaultSeparators = Processor
            .getDefaultSeparators();

    static private ConcurrentHashMap<BidiStructuredProcessor.StructuredTypes, Expert> sharedDefaultExperts = new ConcurrentHashMap<BidiStructuredProcessor.StructuredTypes, Expert>();

    // String type -> map of { environment -> expert }
    static private ConcurrentHashMap<BidiStructuredProcessor.StructuredTypes, Map<Environment, Expert>> sharedExperts = new ConcurrentHashMap<BidiStructuredProcessor.StructuredTypes, Map<Environment, Expert>>();

    static private Expert defaultExpert;

    private ExpertFactory() {
        // prevents instantiation
    }

    /**
     * Obtains a Expert instance for processing structured text with a default
     * type handler segmenting the text according to default separators. This
     * expert instance does not handle states.
     * 
     * @return the Expert instance.
     * @see Processor#getDefaultSeparators()
     */
    static public Expert getExpert() {
        if (defaultExpert == null) {
            TypeHandler handler = new TypeHandler(defaultSeparators);
            defaultExpert = new ExpertImpl(handler, Environment.DEFAULT, false);
        }
        return defaultExpert;
    }

    /**
     * Obtains a Expert instance for processing structured text with the
     * specified type handler. This expert instance does not handle states.
     * 
     * @param type
     *            the identifier for the required type handler. This identifier
     *            must be one of those listed in {@link TypeHandlerFactory} .
     * @return the Expert instance.
     */
    static public Expert getExpert(BidiStructuredProcessor.StructuredTypes type) {
        Expert expert = sharedDefaultExperts.get(type);
        if (expert == null) {
            TypeHandler handler = type.getInstance();
            expert = new ExpertImpl(handler, Environment.DEFAULT, false);
            sharedDefaultExperts.putIfAbsent(type, expert);
        }
        return expert;
    }

    /**
     * Obtains a Expert instance for processing structured text with the
     * specified type handler and the specified environment. This expert
     * instance does not handle states.
     * 
     * @param type
     *            the identifier for the required type handler. This identifier
     *            must be one of those listed in {@link TypeHandlerFactory} .
     * @param environment
     *            the current environment, which may affect the behavior of the
     *            expert. This parameter may be specified as <code>null</code>,
     *            in which case the {@link Environment#DEFAULT} environment
     *            should be assumed.
     * @return the Expert instance.
     */
    static public Expert getExpert(BidiStructuredProcessor.StructuredTypes type, Environment environment) {
        Expert expert;
        if (environment == null)
            environment = Environment.DEFAULT;
        ConcurrentHashMap<Environment, Expert> experts = (ConcurrentHashMap<Environment, Expert>) sharedExperts.get(type);
        if (experts == null) {
            experts = new ConcurrentHashMap<Environment, Expert>();
            sharedExperts.putIfAbsent(type, experts);
        }
        expert = (Expert) experts.get(environment);
        if (expert == null) {
            TypeHandler handler = type.getInstance();
            expert = new ExpertImpl(handler, environment, false);
            experts.putIfAbsent(environment, expert);
        }
        return expert;
    }

    /**
     * Obtains a Expert instance for processing structured text with the
     * specified type handler. This expert instance can handle states.
     * 
     * @param type
     *            the identifier for the required type handler. This identifier
     *            must be one of those listed in {@link TypeHandlerFactory} .
     * @return the Expert instance.
     */
    static public Expert getStatefulExpert(BidiStructuredProcessor.StructuredTypes type) {
        return getStatefulExpert(type, Environment.DEFAULT);
    }

    /**
     * Obtains a Expert instance for processing structured text with the
     * specified type handler and the specified environment. This expert
     * instance can handle states.
     * 
     * @param type
     *            the identifier for the required type handler. This identifier
     *            must be one of those listed in {@link TypeHandlerFactory} .
     * @param environment
     *            the current environment, which may affect the behavior of the
     *            expert. This parameter may be specified as <code>null</code>,
     *            in which case the {@link Environment#DEFAULT} environment
     *            should be assumed.
     * @return the Expert instance.
     */
    static public Expert getStatefulExpert(BidiStructuredProcessor.StructuredTypes type, Environment environment) {
        TypeHandler handler = type.getInstance();
        return getStatefulExpert(handler, environment);
    }

    /**
     * Obtains a Expert instance for processing structured text with the
     * specified type handler and the specified environment. This expert
     * instance can handle states.
     * 
     * @param handler
     *            the type handler instance. It may have been obtained using
     *            {@link TypeHandlerFactory#getHandler(String)} or by
     *            instantiating a type handler.
     * @param environment
     *            the current environment, which may affect the behavior of the
     *            expert. This parameter may be specified as <code>null</code>,
     *            in which case the {@link Environment#DEFAULT} environment
     *            should be assumed.
     * @return the Expert instance.
     */
    static public Expert getStatefulExpert(TypeHandler handler,
            Environment environment) {
        if (environment == null)
            environment = Environment.DEFAULT;
        return new ExpertImpl(handler, environment, true);
    }

}
