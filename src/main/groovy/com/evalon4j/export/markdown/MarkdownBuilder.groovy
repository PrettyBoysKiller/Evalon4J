package com.evalon4j.export.markdown

import com.evalon4j.Evalon4JConfiguration
import com.evalon4j.Evalon4Ji18n
import com.evalon4j.export.ExportHelper
import com.evalon4j.json.JsonModule
import com.evalon4j.json.JsonService

/**
 * comment
 *
 * @author whitecosm0s_
 */
class MarkdownBuilder {
    static i18n = new Evalon4Ji18n()

    static String buildFromJsonModule(JsonModule jsonModule) {
        Evalon4JConfiguration cfg = jsonModule.cfg

        def md = ""

        md += Markdown.h1(jsonModule.moduleName)

        md += Markdown.infoTable(cfg)

        if (cfg.onlyHttpApi) {
            md += transformRestfulAPIsToMarkdown(jsonModule, cfg)

            return md
        }

        if (cfg.onlyJavaApi) {
            md += transformJavaAPIsToMarkdown(jsonModule, cfg)

            return md
        }

        md += transformRestfulAPIsToMarkdown(jsonModule, cfg)

        md += transformJavaAPIsToMarkdown(jsonModule, cfg)

        return md
    }

    static String transformRestfulAPIsToMarkdown(JsonModule jsonModule, Evalon4JConfiguration cfg) {
        String md = ""

        if (jsonModule.restfulApis) {
            md += Markdown.h2("HTTP API")
        } else {
            return md
        }

        jsonModule.restfulApis.each { jsonService ->
            if (isExcludedService(cfg, jsonService)) {
                return
            }

            if (!isIncludedService(cfg, jsonService)) {
                return
            }

            md += Markdown.h3(ExportHelper.getServiceSummary(jsonService))

            md += Markdown.html(ExportHelper.getServiceDescription(jsonService))

            jsonService.methods.each { jsonMethod ->
                md += Markdown.h4(ExportHelper.getMethodSummary(jsonMethod))

                md += Markdown.html(ExportHelper.getMethodDescription(jsonMethod))

                md += Markdown.tags(jsonMethod.tags)

                md += Markdown.h5(i18n.MarkdownBuilder().requestMethod)

                md += Markdown.list(jsonMethod.requestMethod)

                md += Markdown.h5(i18n.MarkdownBuilder().requestPath)

                md += Markdown.list(jsonMethod.fullRequestPath)

                if (jsonMethod.consumes) {
                    md += Markdown.h5(i18n.MarkdownBuilder().consumes)

                    md += Markdown.list(jsonMethod.consumes)
                }

                if (jsonMethod.headers) {
                    md += Markdown.h5(i18n.MarkdownBuilder().requestHeaders)

                    md += Markdown.parametersTable(jsonMethod.headers)
                }

                if (jsonMethod.cookies) {
                    md += Markdown.h5(i18n.MarkdownBuilder().requestCookies)

                    md += Markdown.parametersTable(jsonMethod.cookies)
                }

                md += Markdown.h5(i18n.MarkdownBuilder().parameters)

                if (!jsonMethod.parameters) {
                    md += Markdown.h5(i18n.MarkdownBuilder().noParameter)
                } else {
                    md += Markdown.parametersTable(jsonMethod.parameters)
                }

                if (jsonMethod.parameterConstraints) {
                    md += Markdown.h5(i18n.MarkdownBuilder().constraints)

                    md += Markdown.constraintsTable(jsonMethod.parameterConstraints)
                }

                if (jsonMethod.produces) {
                    md += Markdown.h5(i18n.MarkdownBuilder().produces)

                    md += Markdown.list(jsonMethod.produces)
                }

                md += Markdown.h5(i18n.MarkdownBuilder().response)

                if (!jsonMethod.responses) {
                    md += Markdown.h5(i18n.MarkdownBuilder().noResponse)

                } else {
                    md += Markdown.responseTable(jsonMethod.responses)
                }

                if (jsonMethod.responseHeaders) {
                    md += Markdown.h5(i18n.MarkdownBuilder().responseHeaders)

                    md += Markdown.responseTable(jsonMethod.responseHeaders)
                }
            }
        }

        return md
    }

    static String transformJavaAPIsToMarkdown(JsonModule jsonModule, Evalon4JConfiguration cfg) {
        String md = ""

        if (jsonModule.javaServices) {
            md += Markdown.h2("JAVA API")
        } else {
            return md
        }

        jsonModule.javaServices.each { jsonService ->
            if (isExcludedService(cfg, jsonService)) {
                return
            }

            if (!isIncludedService(cfg, jsonService)) {
                return
            }

            md += Markdown.h3(ExportHelper.getServiceSummary(jsonService))

            md += Markdown.html(ExportHelper.getServiceDescription(jsonService))

            jsonService.methods.each { jsonMethod ->
                md += Markdown.h4(ExportHelper.getMethodSummary(jsonMethod))

                md += Markdown.html(ExportHelper.getMethodDescription(jsonMethod))

                md += Markdown.tags(jsonMethod.tags)

                md += Markdown.h5(i18n.MarkdownBuilder().parameters)

                if (!jsonMethod.parameters) {
                    md += Markdown.h5(i18n.MarkdownBuilder().noParameter)
                } else {
                    md += Markdown.parametersTable(jsonMethod.parameters)
                }

                if (jsonMethod.parameterConstraints) {
                    md += Markdown.h5(i18n.MarkdownBuilder().constraints)

                    md += Markdown.constraintsTable(jsonMethod.parameterConstraints)
                }

                md += Markdown.h5(i18n.MarkdownBuilder().response)

                if (!jsonMethod.responses) {
                    md += Markdown.h5(i18n.MarkdownBuilder().noResponse)
                } else {
                    md += Markdown.responseTable(jsonMethod.responses)
                }

                if (jsonMethod.exceptions) {
                    md += Markdown.h5(i18n.MarkdownBuilder().exceptions)

                    md += Markdown.responseTable(jsonMethod.exceptions)
                }
            }
        }

        return md
    }

    static boolean isExcludedService(Evalon4JConfiguration cfg, JsonService jsonService) {
        if (!cfg.excludedServices) {
            return false
        }

        return cfg.excludedServices.contains(jsonService.serviceName) || cfg.excludedServices.contains(jsonService.serviceQualifiedName)
    }

    static boolean isIncludedService(Evalon4JConfiguration cfg, JsonService jsonService) {
        if (!cfg.includedServices) {
            return true
        }

        return cfg.includedServices.contains(jsonService.serviceName) || cfg.includedServices.contains(jsonService.serviceQualifiedName)
    }
}
