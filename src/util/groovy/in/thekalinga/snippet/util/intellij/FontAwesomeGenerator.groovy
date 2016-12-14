package in.thekalinga.snippet.util.intellij

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT

class FontAwesomeGenerator {

    static void main(String[] args) {

        List<Template> templates = new ArrayList<>()

        def variables = Arrays.asList(
            TemplateVariable.builder()
                .name('1')
                .alwaysStopAt(true)
                .expression('\"https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\"')
                .build()
        )

        def template = Template.builder()
                .name('fa/\$')
                .value('<link rel=\"stylesheet\" href=\"$1$\">\$END\$')
                .description("Font awesome css link")
                .toReformat(true)
                .variables(variables)
                .options(ContextOption.ALL)
                .build()

        templates.add(template)

        ObjectMapper mapper = new ObjectMapper()
        def iconsContainer = mapper.readValue(getClass().getResourceAsStream('/font-awesome.json'), Icons.class)
        iconsContainer.icons.forEach({ iconStr ->

            variables = Arrays.asList(
                TemplateVariable.builder()
                    .name('1')
                    .alwaysStopAt(true)
                    .build()
            )

            template = Template.builder()
                    .name("fa/${iconStr}")
                    .value("<i class=\"fa fa-${iconStr}" + '$1$' +"\" aria-hidden=\"true\"></i>\$END\$")
                    .toReformat(false)
                    .variables(variables)
                    .options(ContextOption.ALL)
                    .build()

            templates.add(template)
        })
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(INDENT_OUTPUT);
        File file = new File("font-awesome.xml");

        TemplateSet templateSet = TemplateSet.builder().group("Font awesome")
                .templates(templates).build()

        xmlMapper.writeValue(file, templateSet)
        println file.absolutePath
    }

}
