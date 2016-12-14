package in.thekalinga.snippet.util.readme

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE
import static com.google.common.base.CaseFormat.UPPER_CAMEL


// current dir is src/util/groovy/in/thekalinga/snippet/util/readme
// navigating to bootstrap.xml in main/resources is ../../../../../../../main/resources
def bootstrapFile = new File("../../../../../../../main/resources/bootstrap.xml")
def root = new XmlParser().parse(bootstrapFile)

def templates = root.template

def components = templates.collect {
    new Component(text: it.'@description', code: it.'@name', context: findMainContext(it))
}

def sorted = components.sort { it.code }
def groups = splitInGroups(sorted)

printToc(groups)
printComponents(groups)

void printToc(groups) {
    groups.each {
        println "- [${it.name}](#${UPPER_CAMEL.to(LOWER_UNDERSCORE, it.name).replaceAll('/', '')})"
    }
}

void printComponents(groups) {
    groups.each { group ->
        println ""
        println "### ${group.name}"
        println ""
        println "| Component                      | Snippet code                   | Context |"
        println "|------------------------------- | -------------------------------| ------- |"

        group.components.each {
            println "| ${String.format("%-30s", it.text)} | ${String.format("%-30s", it.code)} | ${String.format("%-7s", it.context)} |"
        }
    }
}

def findMainContext(template) {
    template.context.option.'@name'.contains('HTML') ? "HTML" : "CSS"
}

private List<Group> splitInGroups(components) {
    def groups = []
    Group lastGroup = null

    components.each { component ->
        def groupName = component.code.split(':')[0][3..-1].capitalize()
        def group = new Group(name: groupName)

        if (lastGroup == null || group.name != lastGroup.name) {
            lastGroup = new Group(name: groupName)
            groups << lastGroup
        }

        lastGroup.components.add(component)
    }

    return groups
}
