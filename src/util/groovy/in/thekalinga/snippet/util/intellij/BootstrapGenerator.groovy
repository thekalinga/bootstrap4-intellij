package in.thekalinga.snippet.util.intellij

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.apache.commons.io.IOUtils
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import static java.nio.charset.Charset.defaultCharset
import static java.util.regex.Pattern.compile
import static java.util.stream.Collectors.toList

class BootstrapGenerator {

    static LONG_FORM_PATTERN = compile(/\$\s*\{\s*(\d+)\s*:\s*([^}]*)\s*}/)
    static SHORT_FORM_PATTERN = compile(/\$\s*(\d+)/)

    static void main(String[] args) {

        List<Template> templates = new ArrayList<>()

        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        def snippetResources = Arrays.asList(resourceLoader.getResources("/bootstrap/**/*.html"))
        snippetResources.forEach({ snippetResource ->
            StringWriter writer = new StringWriter()
            IOUtils.copy(snippetResource.getInputStream(), writer, defaultCharset())
            def contentUntouched = writer.toString()
            def fileParent = new File(snippetResource.file.parent).name
            def fileBaseName = snippetResource.file.name.take(snippetResource.getFile().name.lastIndexOf('.'))

            def templateStr = contentUntouched

            def matches = parseAndGetMatches(contentUntouched)
            def variables = matches.stream().map({ match ->
                templateStr = replaceWithVarFormat(templateStr, match.index)
                return TemplateVariable.from(match)
            }).collect(toList())

            def template = Template.builder()
                    .name(fileBaseName != '$' ? "b4-${fileParent}-${fileBaseName}" : 'b4-$')
                    .value(templateStr.contains('$END$') ? templateStr.trim() : "${templateStr.trim()}\$END\$")
                    .description(fileBaseName != '$' ? "${fileParent} ${fileBaseName}".replace(/-/, ' ') : 'Bootstrap master template')
                    .toReformat(true)
                    .variables(variables)
                    .options(ContextOption.ALL)
                    .build()

            templates.add(template)
        })
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(INDENT_OUTPUT);
        File file = new File("src/main/resources/bootstrap.xml");

        TemplateSet templateSet = TemplateSet.builder().group("Bootstrap 4")
                .templates(templates).build()

        xmlMapper.writeValue(file, templateSet)
        println file.absolutePath
    }

    static String replaceWithVarFormat(String text, int index) {
        String modifiedText = replaceWithVarFormat(text, index, true)
        if (modifiedText == text) {
            return replaceWithVarFormat(text, index, false)
        }
        return modifiedText
    }

    static String replaceWithVarFormat(String text, int index, boolean longForm) {
        if (longForm) {
            def pattern = compile(/\$\s*\{\s*/ + index + /\s*:\s*([^}]*)\s*}/)
            return text.replaceAll(pattern, /\$/ + index + /\$/)
        } else {
            def pattern = compile(/\$\s*/+index)
            return text.replaceAll(pattern, /\$/ + index + /\$/)
        }
    }

    static Set<Match> parseAndGetMatches(String text) {
        def matches = parseAndGetMatches(text, true)
        matches.addAll(parseAndGetMatches(text, false))
        return matches
    }

    static Set<Match> parseAndGetMatches(String text, boolean longForm) {
        Set<Match> matches = new TreeSet<>()
        if (longForm) {
            def matcher = LONG_FORM_PATTERN.matcher(text)
            while (matcher.find()) {
                def index = Integer.parseInt(matcher.group(1))
                def suggestion = matcher.group(2)
                matches << Match.builder().index(index).suggestion(suggestion).build()
            }
        } else {
            def matcher = SHORT_FORM_PATTERN.matcher(text)
            while (matcher.find()) {
                def index = Integer.parseInt(matcher.group(1))
                matches << Match.builder().index(index).build()
            }
        }
        return matches
    }
}
