package in.thekalinga.snippet.util

import org.apache.commons.io.IOUtils

import static java.nio.charset.Charset.defaultCharset

class PluginXmlGenerator {

    static void main(String[] args) {
        StringWriter readmeTemplate = new StringWriter()
        IOUtils.copy(getClass().getResourceAsStream("/plugin-template.xml"), readmeTemplate, defaultCharset())
        String readmeContent = readmeTemplate.toString().replaceAll(/@@@VERSION@@@/,
                IOUtils.readLines(new FileInputStream(new File(".version")), defaultCharset()).get(0))

        File readme = new File("src/main/resources/META-INF/plugin.xml")
        IOUtils.write(readmeContent, new FileOutputStream(readme), defaultCharset())
        println "Regenerated plugin xml. Saved to ${readme.absolutePath}"
    }

}
