package com.evalon4j

/**
 * Evalon4J's Configuration File
 *
 * Read from evalon4j.json under your project, or using -c option to specify one.
 *
 * @author whitecosm0s_
 */
class Evalon4JConfiguration {
    /**
     * The Name of Module，You can define your own.
     */
    String name = ""

    String author = ""

    String version = ""

    String description = ""

    /**
     * Service Name or Service Qualified Name
     */
    List<String> includedServices = []

    /**
     * Service Name or Service Qualified Name
     */
    List<String> excludedServices = []

    /**
     * Dependency Source Jar Path
     */
    List<String> dependencies = []

    /**
     * Only Export Http API
     */
    boolean onlyHttpApi = false

    /**
     * Only Export Java RPC API
     */
    boolean onlyJavaApi = false

    /**
     * Export Locale, if set, will override system locale
     */
    String locale = null
}
