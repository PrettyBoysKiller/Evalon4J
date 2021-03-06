package com.evalon4j.transformer

import com.evalon4j.java.JavaField
import com.evalon4j.java.types.*
import com.evalon4j.json.JsonStruct
import com.evalon4j.visitors.DependencyTree

class JsonStructTransformer {
    static JsonStruct transformJavaFieldToJsonStruct(JavaField javaField, DependencyTree dependencyTree = null) {
        def jsonStruct = transformJavaAbstractTypeToJsonStruct(javaField.fieldType, dependencyTree)

        // Basic

        jsonStruct.fieldName = javaField.fieldName

        jsonStruct.fieldTypeName = javaField.fieldType.simpleName

        jsonStruct.isRequired = javaField.isRequired

        jsonStruct.isDeprecated = javaField.isDeprecated

        jsonStruct.isIgnore = javaField.isIgnore

        // Javadoc

        jsonStruct.javadocTitle = javaField.javadocTitle

        jsonStruct.javadocContent = javaField.javadocContent

        jsonStruct.javadocTags = javaField.javadocTags

        // Comment

        javaField.javadocTitle && (jsonStruct.fieldSummary = javaField.javadocTitle)

        // Restful Annotations

        jsonStruct.springAnnotations = javaField.springAnnotations

        jsonStruct.jaxRSAnnotations = javaField.jaxRSAnnotations

        jsonStruct.swaggerAnnotations = javaField.swaggerAnnotations

        jsonStruct.openAPIAnnotations = javaField.openAPIAnnotations

        jsonStruct.validationAnnotations = javaField.validationAnnotations

        // Jackson Ignore

        def jacksonAnnotations = javaField.jacksonAnnotations

        if (jacksonAnnotations && jacksonAnnotations.jsonIgnore) {
            jsonStruct.isIgnore = true
        }

        def swaggerAnnotations = javaField.swaggerAnnotations

        // Swagger Annotations Like @ApiModelProperty

        if (swaggerAnnotations && swaggerAnnotations.apiModelProperty) {
            def apiModelProperty = swaggerAnnotations.apiModelProperty

            apiModelProperty.name && (jsonStruct.fieldName = apiModelProperty.name)

            apiModelProperty.value && (jsonStruct.fieldSummary = apiModelProperty.value)
        }

        return jsonStruct
    }

    static JsonStruct transformJavaAbstractTypeToJsonStruct(JavaAbstractType javaAbstractType, DependencyTree dependencyTree = null) {
        if (javaAbstractType instanceof JavaPrimitiveType) {
            return new JsonStruct(javaAbstractType)
        }

        if (javaAbstractType instanceof JavaListType || javaAbstractType instanceof JavaSetType || javaAbstractType instanceof JavaArrayType) {
            //noinspection GroovyAssignabilityCheck
            def jsonStruct = new JsonStruct(javaAbstractType)

            jsonStruct.children << transformJavaAbstractTypeToJsonStruct(javaAbstractType.typeArgument, dependencyTree)

            return jsonStruct
        }

        if (javaAbstractType instanceof JavaMapType) {
            def jsonStruct = new JsonStruct(javaAbstractType)

            def keyStruct = transformJavaAbstractTypeToJsonStruct(javaAbstractType.keyTypeArgument, dependencyTree)

            keyStruct && (keyStruct.isMapKey = true)

            def valueStruct = transformJavaAbstractTypeToJsonStruct(javaAbstractType.valueTypeArgument, dependencyTree)

            valueStruct && (valueStruct.isMapValue = true)

            jsonStruct.children << keyStruct

            jsonStruct.children << valueStruct

            return jsonStruct
        }

        if (javaAbstractType instanceof JavaGenericType) {
            def actualType = javaAbstractType.build()

            if (dependencyTree) {
                dependencyTree = new DependencyTree(qualifiedName: actualType.qualifiedName, parent: dependencyTree)
            } else {
                dependencyTree = new DependencyTree(qualifiedName: actualType.qualifiedName)
            }

            return transformJavaAbstractTypeToJsonStruct(actualType, dependencyTree)
        }

        if (javaAbstractType instanceof JavaReferenceType) {
            !dependencyTree && (dependencyTree = new DependencyTree(qualifiedName: javaAbstractType.qualifiedName))

            def jsonStruct = new JsonStruct(javaAbstractType)

            if (javaAbstractType.isEnum) {
                javaAbstractType.enumValues.each { v ->
                    def value = new JsonStruct()

                    value.fieldName = v.name

                    value.fieldTypeName = "ENUM"

                    value.isEnumValue = true

                    value.enumValue = v.value

                    value.fieldSummary = v.javadocTitle

                    value.javadocTitle = v.javadocTitle

                    value.javadocContent = v.javadocContent

                    value.javadocTags = v.javadocTags

                    jsonStruct.children << value
                }

                jsonStruct.children.sort({ // 枚举默认排序
                    return it.fieldName
                })

                jsonStruct.isEnumType = true

                jsonStruct.isStructType = false
            } else {
                def flag = dependencyTree.isRecursive(javaAbstractType.qualifiedName)

                if (flag) {
                    jsonStruct.isRecursive = true

                    return jsonStruct
                } else {
                    dependencyTree = new DependencyTree(qualifiedName: javaAbstractType.qualifiedName, parent: dependencyTree)
                }

                // Set field to ignore from @JsonIgnoreProperties annotation

                def jsonIgnoreProperties = javaAbstractType?.jacksonAnnotations?.jsonIgnoreProperties

                javaAbstractType.fields.each { javaField ->
                    if (jsonIgnoreProperties && jsonIgnoreProperties.value.contains(javaField.fieldName)) {
                        javaField.isIgnore = true
                    }

                    jsonStruct.children << transformJavaFieldToJsonStruct(javaField, dependencyTree)
                }

                javaAbstractType.extensions.each { extensionType ->
                    def extension = transformJavaAbstractTypeToJsonStruct(extensionType, dependencyTree)

                    jsonStruct.children.addAll(extension.children)
                }
            }

            jsonStruct.children.unique {
                return it.fieldName
            }

            // Swagger Annotations Like @ApiModel

            if (javaAbstractType.swaggerAnnotations && javaAbstractType.swaggerAnnotations.apiModel) {
                def apiModel = javaAbstractType.swaggerAnnotations.apiModel

                apiModel.value && (jsonStruct.fieldSummary = apiModel.value)

                apiModel.description && (jsonStruct.fieldDescription = apiModel.description)
            }

            return jsonStruct
        }

        return new JsonStruct(new JavaPrimitiveType())
    }
}
