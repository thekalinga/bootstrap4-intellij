package in.thekalinga.snippet.util.readme

import com.fasterxml.jackson.databind.ObjectMapper
import in.thekalinga.snippet.util.intellij.Icons
import org.apache.commons.io.IOUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import static java.nio.charset.Charset.defaultCharset
import static java.util.Arrays.asList

class ReadmeGenerator {

    static void main(String[] args) {
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        def snippetResources = asList(resourceLoader.getResources("/bootstrap/**/*.html"))

        printSnippets(snippetResources)
    }

    static void printSnippets(List<Resource> snippetResources) {
        Map<String, List<SnippetHelp>> folderToSnippets = new LinkedHashMap<>()
        snippetResources.each { snippetResource ->
            def fileBaseName = snippetResource.filename.take(snippetResource.filename.lastIndexOf('.'))
            def parentDirName = snippetResource.file.parentFile.name

            if (fileBaseName != '$') {
                String sectionName = parentDirName
                if (parentDirName == 'bgroup') {
                    sectionName = 'Button group'
                }

                if (parentDirName == 'igroup') {
                    sectionName = 'Input group'
                }

                sectionName = sectionName.capitalize()

                def description = "${sectionName}-${fileBaseName}"

                if (description ==~ /.*-a\b.*/) {
                    description = description.replaceAll(/-a\b/, " with link")
                }

                if (description ==~ /.*-ul\b.*/) {
                    description = description.replaceAll(/-ul\b/, " with unordered list")
                }

                if (description ==~ /.*-col$/) {
                    description = description.replaceAll(/-col$/, " column")
                }

                description = description.replaceAll(/-/, ' ')

                def snippetHelp = SnippetHelp.builder()
                        .trigger("${parentDirName}-${fileBaseName}")
                        .description(description)
                        .build()
                List<SnippetHelp> helps = folderToSnippets.get(sectionName)
                if (!helps) {
                    helps = new ArrayList<>()
                }
                helps.add(snippetHelp)
                folderToSnippets.put(sectionName, helps)
            } else {
                def snippetHelp = SnippetHelp.builder()
                        .trigger('\\$')
                        .description('Bootstrap master template')
                        .build()
                folderToSnippets.put('Bootstrap master template', asList(snippetHelp))
            }
        }

        StringBuilder builder = new StringBuilder()

        folderToSnippets.forEach({ String folderName, List<SnippetHelp> snippetHelps ->
            builder << "\n"
            builder << "### ${folderName}\n\n"
            builder << "Trigger | Description\n"
            builder << "--- | ---\n"
            snippetHelps.forEach({snippetHelp ->
                builder << "${snippetHelp}\n"
            })
        })

        // Handle font awesome
        builder << "\n"
        builder << "### Font awesome\n\n"
        builder << "  Trigger\n"
        builder << "  ---\n"
        ObjectMapper mapper = new ObjectMapper()
        def iconsContainer = mapper.readValue(getClass().getResourceAsStream('/font-awesome.json'), Icons.class)
        iconsContainer.icons.forEach({ iconStr ->
            builder << "  ${iconStr}\n"
        })

        StringWriter readmeTemplate = new StringWriter()
        IOUtils.copy(getClass().getResourceAsStream("/readme-template.md"), readmeTemplate, defaultCharset())
        String readmeContent = readmeTemplate.toString().replaceAll(/@@@GENERATED_DOCUMENTATION_PLACEHOLDER@@@/, builder.toString())

        File readme = new File("README.md")
        IOUtils.write(readmeContent, new FileOutputStream(readme), defaultCharset())
        println readme.absolutePath
    }

}
