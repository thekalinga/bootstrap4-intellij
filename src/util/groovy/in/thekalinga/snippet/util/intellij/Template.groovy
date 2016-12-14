package in.thekalinga.snippet.util.intellij

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.builder.Builder

@Builder
class Template {
    @JacksonXmlProperty(isAttribute = true)
    String name
    @JacksonXmlProperty(isAttribute = true)
    String value
    @JacksonXmlProperty(isAttribute = true)
    String description
    @JacksonXmlProperty(isAttribute = true)
    boolean toReformat

    @JacksonXmlProperty(localName = "variable")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<TemplateVariable> variables

    @JacksonXmlElementWrapper(localName = "context", useWrapping = true)
    @JacksonXmlProperty(localName = "option")
    List<ContextOption> options
}

@Builder
class TemplateVariable {
    @JacksonXmlProperty(isAttribute = true)
    String name
    @JacksonXmlProperty(isAttribute = true)
    String expression
    @JacksonXmlProperty(isAttribute = true)
    String defaultValue
    @JacksonXmlProperty(isAttribute = true)
    boolean alwaysStopAt

    static TemplateVariable from(Match match) {
        def builder = TemplateVariable.builder()
                .name(String.valueOf(match.index))
                .alwaysStopAt(true)
        if (match.suggestion) {
            builder.expression("\"${match.suggestion.replaceAll(/"/, /\\"/)}\"")
        }
        return builder.build()
    }
}

@Builder
class ContextOption {

    static final List<ContextOption> ALL = Collections.unmodifiableList(Arrays.asList(
        ContextOption.builder().name("HTML_TEXT").value(true).build(),
        ContextOption.builder().name("HTML").value(true).build(),
        ContextOption.builder().name("PHP").value(true).build(),
        ContextOption.builder().name("GSP").value(true).build(),
        ContextOption.builder().name("JSP").value(true).build(),
        ContextOption.builder().name("JSX_HTML").value(true).build()
    ))

    @JacksonXmlProperty(isAttribute = true)
    String name
    @JacksonXmlProperty(isAttribute = true)
    boolean value
}
