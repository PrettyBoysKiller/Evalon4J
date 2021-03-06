package com.evalon4j

/**
 * 文档导出类型
 *
 * @author whitecosm0s_
 */
class Evalon4JExportType {
    /**
     * 供产品RabiAPI使用的内部格式，默认
     */
    static final String EVALON = "evalon"

    /**
     * 标准Markdown格式
     */
    static final String MARKDOWN = "markdown"

    /**
     * 标准Asciidoc格式
     */
    static final String ASCIIDOC = "asciidoc"

    /**
     * 旧版 Swagger 2.0 Json 文件格式
     */
    static final String SWAGGER = "swagger"

    /**
     * 新版 OpenAPI 3.x Json 文件格式
     */
    static final String OPENAPI = "openapi"

    static EXPORT_TYPES = [
            EVALON,
            MARKDOWN,
            ASCIIDOC,
            SWAGGER,
            OPENAPI,
    ]
}
