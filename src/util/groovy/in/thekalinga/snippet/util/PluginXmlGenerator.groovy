package in.thekalinga.snippet.util

import org.apache.commons.io.IOUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import static java.nio.charset.Charset.defaultCharset
import static java.util.Arrays.asList

class PluginXmlGenerator {

    static void main(String[] args) {
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        def snippetResources = asList(resourceLoader.getResources("/bootstrap/**/*.html"))

        printSnippets(snippetResources)
    }

    static void printSnippets(List<Resource> snippetResources) {
        StringWriter readmeTemplate = new StringWriter()
        IOUtils.copy(getClass().getResourceAsStream("/plugin-template.xml"), readmeTemplate, defaultCharset())
        String readmeContent = readmeTemplate.toString().replaceAll(/@@@VERSION@@@/,
                IOUtils.readLines(new FileInputStream(new File(".version")), defaultCharset()).get(0))

        File readme = new File("src/main/resources/META-INF/plugin.xml")
        IOUtils.write(readmeContent, new FileOutputStream(readme), defaultCharset())
        println readme.absolutePath
    }

}
